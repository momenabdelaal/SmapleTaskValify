package com.valify.di

import android.content.Context
import androidx.room.Room
import com.valify.data.local.RegistrationDatabase
import com.valify.data.local.dao.UserRegistrationDao
import com.valify.data.repository.RegistrationRepositoryImpl
import com.valify.data.ValifySDKWrapper
import com.valify.domain.repository.RegistrationRepository
import com.valify.domain.use_case.SaveRegistration
import com.valify.presentation.registration.validation.ValidateEmail
import com.valify.presentation.registration.validation.ValidatePassword
import com.valify.presentation.registration.validation.ValidatePhoneNumber
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
    fun provideSaveRegistration(
        repository: RegistrationRepository
    ): SaveRegistration {
        return SaveRegistration(repository)
    }

    @Provides
    fun provideValidateEmail(): ValidateEmail {
        return ValidateEmail()
    }

    @Provides
    fun provideValidatePassword(): ValidatePassword {
        return ValidatePassword()
    }

    @Provides
    fun provideValidatePhoneNumber(): ValidatePhoneNumber {
        return ValidatePhoneNumber()
    }
}
