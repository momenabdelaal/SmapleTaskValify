package com.valify.registrationsdk.di

import android.content.Context
import androidx.room.Room
import com.valify.registrationsdk.data.ValifySDKWrapper
import com.valify.registrationsdk.data.local.RegistrationDatabase
import com.valify.registrationsdk.data.local.dao.UserRegistrationDao
import com.valify.registrationsdk.data.repository.RegistrationRepositoryImpl
import com.valify.registrationsdk.data.repository.SelfieRepositoryImpl
import com.valify.registrationsdk.data.validation.DefaultValidationService
import com.valify.registrationsdk.domain.repository.RegistrationRepository
import com.valify.registrationsdk.domain.repository.SelfieRepository
import com.valify.registrationsdk.domain.use_case.SaveRegistration
import com.valify.registrationsdk.domain.validation.ValidationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideValifySDK(
        @ApplicationContext context: Context
    ): ValifySDKWrapper {
        return ValifySDKWrapper(context)
    }

    @Provides
    @Singleton
    fun provideRegistrationDatabase(
        @ApplicationContext context: Context
    ): RegistrationDatabase {
        return Room.databaseBuilder(
            context,
            RegistrationDatabase::class.java,
            RegistrationDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserRegistrationDao(database: RegistrationDatabase): UserRegistrationDao {
        return database.userRegistrationDao()
    }

    @Provides
    @Singleton
    fun provideRegistrationRepository(
        dao: UserRegistrationDao
    ): RegistrationRepository {
        return RegistrationRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideSaveRegistration(
        repository: RegistrationRepository
    ): SaveRegistration {
        return SaveRegistration(repository)
    }

    @Provides
    @Singleton
    fun provideSelfieRepository(
        @ApplicationContext context: Context
    ): SelfieRepository {
        return SelfieRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideValidationService(): ValidationService {
        return DefaultValidationService()
    }
}
