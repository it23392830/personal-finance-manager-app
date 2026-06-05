package com.example.financeflow.di

import com.example.financeflow.data.remote.ProfileFirestoreService
import com.example.financeflow.repository.ProfileRepository
import com.example.financeflow.repository.ProfileRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt providers for the Profile module.
 */
@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    /** Provides Firebase Storage for profile image uploads. */
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    /** Provides ProfileFirestoreService. */
    @Provides
    @Singleton
    fun provideProfileFirestoreService(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ): ProfileFirestoreService {
        return ProfileFirestoreService(firestore, auth, storage)
    }

    /** Provides ProfileRepository. */
    @Provides
    @Singleton
    fun provideProfileRepository(
        service: ProfileFirestoreService
    ): ProfileRepository {
        return ProfileRepositoryImpl(service)
    }
}
