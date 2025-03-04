package com.example.data.repository.home

import android.util.Log
import com.example.data.models.Resource
import com.example.data.models.sale_ads.SalesAdModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SalesAdsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SalesAdsRepository {
    override   fun getSalesAds() = flow {
        try {
            Log.d(TAG, "getSalesAds: ")
            emit(Resource.Loading())
            val salesAds =
                firestore.collection("sales_ads")
                    .get().await().toObjects(SalesAdModel::class.java)
            emit(Resource.Success(salesAds.map { it.toUIModel() }))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    companion object {
        private const val TAG = "SalesAdsRepositoryImpl"
    }
    suspend fun getPagingSales(){
        val salesAds =  firestore.collection("sales_ads").limit(10).get().await()


        val lstDocument = salesAds.documents.last()

        getNextPage(lstDocument)
    }

    suspend fun getNextPage(lastDocument: DocumentSnapshot){
        val salesAds =  firestore.collection("sales_ads").startAfter(lastDocument).limit(10).get().await()
    }
}