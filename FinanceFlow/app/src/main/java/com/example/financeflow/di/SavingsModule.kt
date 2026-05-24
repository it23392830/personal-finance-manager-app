package com.example.financeflow.di

import com.example.financeflow.data.remote.SavingsFirestoreService
import com.example.financeflow.data.local.dao.SavingDao
import com.example.financeflow.data.local.dao.SavingGoalDao
import com.example.financeflow.repository.savings.SavingsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt providers for the Savings module.
 *
 * FirebaseAuth and FirebaseFirestore are provided by FirebaseModule and injected
 * here so we avoid duplicate app-wide Firebase bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
object SavingsModule {

    /** Provides the Firestore service that owns savings collection calls. */
    @Provides
    @Singleton
    fun provideSavingsFirestoreService(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): SavingsFirestoreService {
        return SavingsFirestoreService(firestore, auth)
    }

    /** Provides the repository used by SavingsViewModel. */
    @Provides
    @Singleton
    fun provideSavingsRepository(
        service: SavingsFirestoreService,
        savingDao: SavingDao,
        savingGoalDao: SavingGoalDao
    ): SavingsRepository {
        return SavingsRepository(service, savingDao, savingGoalDao)
    }
}
