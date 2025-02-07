package com.valify.domain.repository

import com.valify.domain.model.UserRegistration
import kotlinx.coroutines.flow.Flow


interface RegistrationRepository {

    suspend fun saveRegistration(registration: UserRegistration): Long


    suspend fun updateSelfieImage(registrationId: Long, imagePath: String)


    fun getLatestIncompleteRegistration(): Flow<UserRegistration?>


    suspend fun isUsernameTaken(username: String): Boolean

    suspend fun isEmailTaken(email: String): Boolean
}
