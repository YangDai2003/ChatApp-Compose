package com.yangdai.chatapp.app

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yangdai.chatapp.domain.repository.AuthScreenRepository
import com.yangdai.chatapp.domain.repository.ChatScreenRepository
import com.yangdai.chatapp.domain.repository.ProfileScreenRepository
import com.yangdai.chatapp.domain.repository.UserListScreenRepository
import com.yangdai.chatapp.domain.usecase.authScreen.AuthUseCases
import com.yangdai.chatapp.domain.usecase.authScreen.IsUserAuthenticatedInFirebase
import com.yangdai.chatapp.domain.usecase.authScreen.SignIn
import com.yangdai.chatapp.domain.usecase.authScreen.SignUp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.yangdai.chatapp.domain.usecase.chatScreen.BlockFriendToFirebase
import com.yangdai.chatapp.domain.usecase.chatScreen.ChatScreenUseCases
import com.yangdai.chatapp.domain.usecase.chatScreen.InsertMessageToFirebase
import com.yangdai.chatapp.domain.usecase.chatScreen.LoadMessageFromFirebase
import com.yangdai.chatapp.domain.usecase.chatScreen.LoadOpponentProfileFromFirebase
import com.yangdai.chatapp.domain.usecase.profileScreen.CreateOrUpdateProfileToFirebase
import com.yangdai.chatapp.domain.usecase.profileScreen.LoadProfileFromFirebase
import com.yangdai.chatapp.domain.usecase.profileScreen.ProfileScreenUseCases
import com.yangdai.chatapp.domain.usecase.profileScreen.SetUserStatusToFirebase
import com.yangdai.chatapp.domain.usecase.profileScreen.SignOut
import com.yangdai.chatapp.domain.usecase.profileScreen.UploadPictureToFirebase
import com.yangdai.chatapp.domain.usecase.userListScreen.AcceptPendingFriendRequestToFirebase
import com.yangdai.chatapp.domain.usecase.userListScreen.CheckChatRoomExistedFromFirebase
import com.yangdai.chatapp.domain.usecase.userListScreen.CheckFriendListRegisterIsExistedFromFirebase
import com.yangdai.chatapp.domain.usecase.userListScreen.CreateChatRoomToFirebase
import com.yangdai.chatapp.domain.usecase.userListScreen.CreateFriendListRegisterToFirebase
import com.yangdai.chatapp.domain.usecase.userListScreen.LoadAcceptedFriendRequestListFromFirebase
import com.yangdai.chatapp.domain.usecase.userListScreen.LoadPendingFriendRequestListFromFirebase
import com.yangdai.chatapp.domain.usecase.userListScreen.OpenBlockedFriendToFirebase
import com.yangdai.chatapp.domain.usecase.userListScreen.RejectPendingFriendRequestToFirebase
import com.yangdai.chatapp.domain.usecase.userListScreen.SearchUserFromFirebase
import com.yangdai.chatapp.domain.usecase.userListScreen.UserListScreenUseCases
import com.yangdai.chatapp.repository.AuthScreenRepositoryImpl
import com.yangdai.chatapp.repository.ChatScreenRepositoryImpl
import com.yangdai.chatapp.repository.ProfileScreenRepositoryImpl
import com.yangdai.chatapp.repository.UserListScreenRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("login")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideFirebaseAuthInstance() = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseStorageInstance() = FirebaseStorage.getInstance()

    @Provides
    fun provideFirebaseDatabaseInstance() = FirebaseDatabase.getInstance()

    @Provides
    fun providesDataStore(application: Application) = application.dataStore

    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth,
    ): AuthScreenRepository = AuthScreenRepositoryImpl(auth)

    @Provides
    fun provideChatScreenRepository(
        auth: FirebaseAuth,
        database: FirebaseDatabase
    ): ChatScreenRepository = ChatScreenRepositoryImpl(auth, database)

    @Provides
    fun provideProfileScreenRepository(
        auth: FirebaseAuth,
        database: FirebaseDatabase,
        storage: FirebaseStorage
    ): ProfileScreenRepository = ProfileScreenRepositoryImpl(auth, database, storage)

    @Provides
    fun provideUserListScreenRepository(
        auth: FirebaseAuth,
        database: FirebaseDatabase
    ): UserListScreenRepository = UserListScreenRepositoryImpl(auth, database)

    @Provides
    fun provideAuthScreenUseCase(authRepository: AuthScreenRepository) = AuthUseCases(
        isUserAuthenticated = IsUserAuthenticatedInFirebase(authRepository),
        signIn = SignIn(authRepository),
        signUp = SignUp(authRepository)
    )

    @Provides
    fun provideChatScreenUseCase(chatScreenRepository: ChatScreenRepository) = ChatScreenUseCases(
        blockFriendToFirebase = BlockFriendToFirebase(chatScreenRepository),
        insertMessageToFirebase = InsertMessageToFirebase(chatScreenRepository),
        loadMessageFromFirebase = LoadMessageFromFirebase(chatScreenRepository),
        opponentProfileFromFirebase = LoadOpponentProfileFromFirebase(chatScreenRepository)
    )

    @Provides
    fun provideProfileScreenUseCase(profileScreenRepository: ProfileScreenRepository) =
        ProfileScreenUseCases(
            createOrUpdateProfileToFirebase = CreateOrUpdateProfileToFirebase(
                profileScreenRepository
            ),
            loadProfileFromFirebase = LoadProfileFromFirebase(profileScreenRepository),
            setUserStatusToFirebase = SetUserStatusToFirebase(profileScreenRepository),
            signOut = SignOut(profileScreenRepository),
            uploadPictureToFirebase = UploadPictureToFirebase(profileScreenRepository)
        )

    @Provides
    fun provideUserListScreenUseCase(userListScreenRepository: UserListScreenRepository) =
        UserListScreenUseCases(
            acceptPendingFriendRequestToFirebase = AcceptPendingFriendRequestToFirebase(
                userListScreenRepository
            ),
            checkChatRoomExistedFromFirebase = CheckChatRoomExistedFromFirebase(
                userListScreenRepository
            ),
            checkFriendListRegisterIsExistedFromFirebase = CheckFriendListRegisterIsExistedFromFirebase(
                userListScreenRepository
            ),
            createChatRoomToFirebase = CreateChatRoomToFirebase(userListScreenRepository),
            createFriendListRegisterToFirebase = CreateFriendListRegisterToFirebase(
                userListScreenRepository
            ),
            loadAcceptedFriendRequestListFromFirebase = LoadAcceptedFriendRequestListFromFirebase(
                userListScreenRepository
            ),
            loadPendingFriendRequestListFromFirebase = LoadPendingFriendRequestListFromFirebase(
                userListScreenRepository
            ),
            openBlockedFriendToFirebase = OpenBlockedFriendToFirebase(userListScreenRepository),
            rejectPendingFriendRequestToFirebase = RejectPendingFriendRequestToFirebase(
                userListScreenRepository
            ),
            searchUserFromFirebase = SearchUserFromFirebase(userListScreenRepository),
        )
}