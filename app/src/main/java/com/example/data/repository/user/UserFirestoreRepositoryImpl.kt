package com.example.data.repository.user

import android.system.Os.close
import androidx.core.app.PendingIntentCompat.send
import com.example.data.models.Resource
import com.example.data.models.user.UserDetailsModel
import kotlinx.coroutines.flow.Flow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class UserFirestoreRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserFirestoreRepository {
    override suspend fun getUserDetails(userId: String): Flow<Resource<UserDetailsModel>> =
        callbackFlow {
            send(Resource.Loading())
            val documentPath = "users/$userId"
            val document = firestore.document(documentPath)
            val listener = document.addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                value?.toObject(UserDetailsModel::class.java)?.let {
                    if (it.disabled == true) {
                        close(UserNotFoundException("Account disabled"))
                        return@addSnapshotListener}
                    trySend(Resource.Success(it))
                }
            }
            awaitClose {
                listener.remove()
            }
        }
}

class UserNotFoundException(message: String) : Exception(message)
class AccountDisabledException(message: String) : Exception(message) // account disabled