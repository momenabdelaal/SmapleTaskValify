package com.valify.registrationsdk.data.repository

import com.valify.registrationsdk.data.local.dao.UserRegistrationDao
import com.valify.registrationsdk.data.local.entity.UserRegistrationEntity
import com.valify.registrationsdk.domain.model.UserRegistration
import com.valify.registrationsdk.domain.repository.RegistrationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RegistrationRepositoryImpl @Inject constructor(
    private val dao: UserRegistrationDao
) : RegistrationRepository {

    override suspend fun saveRegistration(registration: UserRegistration): Long {
        return dao.insertRegistration(registration.toEntity())
    }

    override fun getLatestIncompleteRegistration(): Flow<UserRegistration?> {
        return dao.getLatestIncompleteRegistration().map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun isUsernameTaken(username: String): Boolean {
        return dao.isUsernameTaken(username)
    }

    override suspend fun isEmailTaken(email: String): Boolean {
        return dao.isEmailTaken(email)
    }

    override suspend fun getRegistrationByDeviceId(deviceId: String): UserRegistration? {
        return dao.getRegistrationByDeviceId(deviceId)?.toDomain()
    }

    override suspend fun updateSelfieImage(registrationId: Long, imagePath: String) {
        val existingRegistration = dao.getRegistrationById(registrationId)
        existingRegistration?.let { registration ->
            dao.updateRegistration(
                registration.copy(
                    selfieImagePath = imagePath,
                    registrationCompleted = true
                )
            )
        }
    }

    private fun UserRegistration.toEntity(): UserRegistrationEntity {
        return UserRegistrationEntity(
            username = username,
            email = email,
            phoneNumber = phone,
            password = password,
            deviceId = deviceId,
            selfieImagePath = selfieImagePath,
            registrationCompleted = false
        )
    }

    private fun UserRegistrationEntity.toDomain(): UserRegistration {
        return UserRegistration(
            id = id,
            username = username,
            email = email,
            phone = phoneNumber,
            password = password,
            deviceId = deviceId,
            selfieImagePath = selfieImagePath
        )
    }
}
