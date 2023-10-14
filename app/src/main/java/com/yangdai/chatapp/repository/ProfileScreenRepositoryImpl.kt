package com.yangdai.chatapp.repository

import android.net.Uri

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.onesignal.OneSignal
import com.yangdai.chatapp.core.Constants.ERROR_MESSAGE
import com.yangdai.chatapp.domain.model.User
import com.yangdai.chatapp.domain.model.UserStatus
import com.yangdai.chatapp.domain.repository.ProfileScreenRepository
import com.yangdai.chatapp.core.Response
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileScreenRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage
) : ProfileScreenRepository {
    override suspend fun signOut(): Flow<Response<Boolean>> = callbackFlow {
        try {
            this@callbackFlow.trySendBlocking(Response.Loading)
            auth.signOut().apply {
                this@callbackFlow.trySendBlocking(Response.Success(true))
            }
        } catch (e: Exception) {
            this@callbackFlow.trySendBlocking(Response.Error(e.message ?: ERROR_MESSAGE))
        }
        awaitClose {
            channel.close()
            cancel()
        }
    }

    override suspend fun uploadPictureToFirebase(url: Uri): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            val uuidImage = UUID.randomUUID()
            val imageName = "images/$uuidImage.jpg"
            val storageRef = storage.reference.child(imageName)

            storageRef.putFile(url).apply {}.await()
            var downloadUrl = ""
            storageRef.downloadUrl.addOnSuccessListener {
                downloadUrl = it.toString()
            }.await()
            emit(Response.Success(downloadUrl))

        } catch (e: Exception) {
            emit(Response.Error(e.message ?: ERROR_MESSAGE))
        }
    }

    override suspend fun createOrUpdateProfileToFirebase(user: User): Flow<Response<Boolean>> =
        flow {
            try {
                emit(Response.Loading)
                val userUUID = auth.currentUser?.uid.toString()
                val userEmail = auth.currentUser?.email.toString()
                val oneSignalUserId = OneSignal.getDeviceState()?.userId.toString()

                val databaseReference =
                    database.getReference("Profiles").child(userUUID).child("profile")

                val childUpdates = mutableMapOf<String, Any>()

                childUpdates["/profileUUID/"] = userUUID
                childUpdates["/userEmail/"] = userEmail
                childUpdates["/oneSignalUserId/"] = oneSignalUserId

                if (user.userName != "") childUpdates["/userName/"] = user.userName
                if (user.userProfilePictureUrl != "") childUpdates["/userProfilePictureUrl/"] =
                    user.userProfilePictureUrl
                if (user.userSurName != "") childUpdates["/userSurName/"] = user.userSurName
                if (user.userBio != "") childUpdates["/userBio/"] = user.userBio
                if (user.userPhoneNumber != "") childUpdates["/userPhoneNumber/"] =
                    user.userPhoneNumber
                childUpdates["/status/"] = UserStatus.ONLINE.toString()

                databaseReference.updateChildren(childUpdates).await()
                emit(Response.Success(true))
            } catch (e: Exception) {
                emit(Response.Error(e.message ?: ERROR_MESSAGE))
            }
        }

    override suspend fun loadProfileFromFirebase(): Flow<Response<User>> = callbackFlow {
        try {
            this@callbackFlow.trySendBlocking(Response.Loading)
            val userUUID = auth.currentUser?.uid
            val databaseReference = database.getReference("Profiles")
            val postListener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userFromFirebaseDatabase =
                        snapshot.child(userUUID!!).child("profile").getValue(User::class.java)
                            ?: User()
                    this@callbackFlow.trySendBlocking(Response.Success(userFromFirebaseDatabase))
                }

                override fun onCancelled(error: DatabaseError) {
                    this@callbackFlow.trySendBlocking(Response.Error(error.message))
                }
            })
            databaseReference.addValueEventListener(postListener)
            awaitClose {
                databaseReference.removeEventListener(postListener)
                channel.close()
                cancel()
            }
        } catch (e: Exception) {
            this@callbackFlow.trySendBlocking(Response.Error(e.message ?: ERROR_MESSAGE))
        }
    }

    override suspend fun setUserStatusToFirebase(userStatus: UserStatus): Flow<Response<Boolean>> =
        flow {
            try {
                emit(Response.Loading)
                if (auth.currentUser != null) {
                    val userUUID = auth.currentUser?.uid.toString()

                    val databaseReference =
                        database.getReference("Profiles").child(userUUID).child("profile")
                            .child("status")
                    databaseReference.setValue(userStatus.toString()).await()
                    emit(Response.Success(true))
                } else {
                    emit(Response.Success(false))
                }
            } catch (e: Exception) {
                emit(Response.Error(e.message ?: ERROR_MESSAGE))
            }
        }

}