package com.example.data.repository.products

import android.util.Log
import com.example.data.models.products.ProductModel
import com.example.domain.models.toProductUIModel
import com.example.ui.products.model.ProductUIModel
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
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
}