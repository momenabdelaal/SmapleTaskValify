package com.valify.registrationsdk.domain.repository

import com.valify.registrationsdk.domain.model.UserRegistration
import kotlinx.coroutines.flow.Flow

interface RegistrationRepository {
    suspend fun saveRegistration(registration: UserRegistration): Long
    fun getLatestIncompleteRegistration(): Flow<UserRegistration?>
    suspend fun isUsernameTaken(username: String): Boolean
    suspend fun isEmailTaken(email: String): Boolean
    suspend fun getRegistrationByDeviceId(deviceId: String): UserRegistration?
    suspend fun updateSelfieImage(registrationId: Long, imagePath: String)
}
