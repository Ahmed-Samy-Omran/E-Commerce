package com.example.data.repository.products

import com.example.data.models.Resource
import com.example.data.models.products.ProductModel
import com.example.ui.products.model.ProductUIModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {

//    fun getCategoryProducts(categoryID: String, pageLimit: Int): Flow<List<ProductModel>>
//
//    fun getSaleProducts(countryID: String, saleType: String, pageLimit: Int): Flow<List<ProductModel>>

    fun getCategoryProducts(categoryID: String, pageLimit: Int): Flow<List<ProductUIModel>>

    fun getSaleProducts(countryID: String, saleType: String, pageLimit: Int): Flow<List<ProductUIModel>>

    suspend fun getOffersForProducts(countryID: String, products: List<ProductUIModel>): List<ProductUIModel>

    suspend fun getAllProductsPaging(
        countryID: String, pageLimit: Long, lastDocument: DocumentSnapshot? = null
    ): Flow<Resource<QuerySnapshot>>
}