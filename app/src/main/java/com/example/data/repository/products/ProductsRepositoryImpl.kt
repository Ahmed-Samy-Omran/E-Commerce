package com.example.data.repository.products

import android.util.Log
import com.example.data.models.Resource
import com.example.data.models.products.ProductModel
import com.example.domain.models.toProductUIModel
import com.example.ui.products.model.ProductUIModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductsRepository {

    override fun getCategoryProducts(categoryID: String, pageLimit: Int): Flow<List<ProductUIModel>> = flow {
        try {
            val snapshot = firestore.collection("products")
                .whereArrayContains("categories_ids", categoryID)
                .limit(pageLimit.toLong())
                .get()
                .await()

            val products = snapshot.documents.mapNotNull { it.toObject(ProductModel::class.java)?.toProductUIModel() }

            emit(products)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getSaleProducts(countryID: String, saleType: String, pageLimit: Int): Flow<List<ProductUIModel>> = flow {
        try {
            val snapshot = firestore.collection("products")
                .whereEqualTo("sale_type", saleType)
                .limit(pageLimit.toLong())
                .get()
                .await()

            val products = snapshot.documents.mapNotNull { it.toObject(ProductModel::class.java)?.toProductUIModel() }

            val updatedProducts = getOffersForProducts(countryID, products)

            emit(updatedProducts)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun getOffersForProducts(countryID: String, products: List<ProductUIModel>): List<ProductUIModel> {
        if (products.isEmpty()) return emptyList()

        val productIDs = products.map { it.id }

        return try {
            val snapshot = firestore.collection("product_offers")
                .whereArrayContains("countries", countryID)
                .whereIn("product_id", productIDs)
                .get()
                .await()

            val offerMap = snapshot.documents.associateBy(
                { it.getString("product_id") ?: "" },
                { it.getDouble("offer_percentage")?.toInt() ?: 0 }
            )

            products.map { product ->
                val offerPercentage = offerMap[product.id]
                val discountedPrice = offerPercentage?.let { product.price - (product.price * it / 100) }

                product.copy(
                    salePercentage = offerPercentage,
                    priceAfterSale = discountedPrice
                )
            }
        } catch (e: Exception) {
            products // Return original products if fetching offers fails
        }
    }


    override suspend fun getAllProductsPaging(
        countryID: String, pageLimit: Long, lastDocument: DocumentSnapshot?
    ) = flow<Resource<QuerySnapshot>> {
        try {

            emit(Resource.Loading())

            var firstQuery = firestore.collection("products").orderBy("price")

            if (lastDocument != null) {
                firstQuery = firstQuery.startAfter(lastDocument)
            }

            firstQuery = firstQuery.limit(pageLimit)

            val products = firstQuery.get().await()
            emit(Resource.Success(products))
        } catch (e: Exception) {
            Log.d(TAG, "getAllProductsPaging: ${e.message}")
            emit(Resource.Error(e))
        }
    }

    override fun listenToProductDetails(productID: String): Flow<ProductModel> {
        return callbackFlow {
            val listener = firestore.collection("products").document(productID)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.d(TAG, "listenToProductDetails: ${error.message}")
                        close(error)
                        return@addSnapshotListener
                    }

                    val product = value?.toObject(ProductModel::class.java)
                    if (product != null) {
                        trySend(product)
                    } else {
                        close()
                    }
                }

            awaitClose { listener.remove() }
        }
    }

    override suspend fun searchProducts(query: String): List<ProductUIModel> {
        return try {
            val snapshot = firestore.collection("products")

                // that code get every thing that starts with query(nike for example)
                // query + '\uf8ff and that Unicode character make the search get all text start with that query
                .whereGreaterThanOrEqualTo("name_lowercase", query)
                .whereLessThanOrEqualTo("name_lowercase", query + '\uf8ff')


                .get()
                .await()
            // here we convert the documents to ProductModel and then to ProductUIModel to represent it in UI.
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(ProductModel::class.java)?.toProductUIModel()
            }
        } catch (e: Exception) {
            //that mean when the search failed that print error in log cat and then return empty list
            e.printStackTrace()
            emptyList()
        }
    }


    companion object {
        private const val TAG = "ProductsRepositoryImpl"
    }
}

