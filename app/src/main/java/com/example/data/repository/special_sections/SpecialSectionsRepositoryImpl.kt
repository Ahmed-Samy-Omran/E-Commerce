package com.example.data.repository.special_sections

import android.util.Log
import com.example.data.models.SpecialSectionModel
import com.example.data.models.SpecialSections
import com.example.utils.CrashlyticsUtils
import com.example.utils.SpecialSectionsException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SpecialSectionsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SpecialSectionsRepository {
    override fun recommendProductsSection() = flow {
        try {
            val specialSection = firestore.collection("special_sections")
                .document(SpecialSections.RECOMMENDED_PRODUCTS.id).get().await()
                .toObject(SpecialSectionModel::class.java)
            Log.d(TAG, "recommendProductsSection: success ${specialSection}")

            emit(specialSection)
        } catch (e: Exception) {

            Log.d(TAG, "recommendProductsSection: Error fetching recommended products ${e.message}")
            val msg = e.message ?: "Error fetching recommended products"
            CrashlyticsUtils.sendCustomLogToCrashlytics<SpecialSectionsException>(
                msg, Pair(CrashlyticsUtils.SPECIAL_SECTIONS, msg)
            )
            emit(null)
        }
    }

    companion object {
        private const val TAG = "SpecialSectionsRepositoryImpl"
    }
}