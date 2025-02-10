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

    override suspend fun updateSelfieImage(registrationId: Long, imagePath: String) {
        dao.getLatestIncompleteRegistration().collect { entity ->
            entity?.let {
                dao.updateRegistration(it.copy(
                    selfieImagePath = imagePath,
                    registrationCompleted = true
                ))
            }
        }
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


    private fun UserRegistration.toEntity(): UserRegistrationEntity {
        return UserRegistrationEntity(
            username = username,
            phoneNumber = phoneNumber,
            email = email,
            password = password,
            selfieImagePath = selfieImagePath
        )
    }

    /**
     * Extension function to convert entity to domain model
     */
    private fun UserRegistrationEntity.toDomain(): UserRegistration {
        return UserRegistration(
            username = username,
            phoneNumber = phoneNumber,
            email = email,
            password = password,
            selfieImagePath = selfieImagePath
        )
    }
}
