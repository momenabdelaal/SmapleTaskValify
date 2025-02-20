package com.valify.registrationsdk.domain.use_case

import com.valify.registrationsdk.domain.model.UserRegistration
import com.valify.registrationsdk.domain.repository.RegistrationRepository
import javax.inject.Inject

class SaveRegistration @Inject constructor(
    private val repository: RegistrationRepository
) {
    suspend operator fun invoke(registration: UserRegistration): Long {
        if (repository.isUsernameTaken(registration.username)) {
            throw IllegalArgumentException("Username is already taken")
        }
        if (repository.isEmailTaken(registration.email)) {
            throw IllegalArgumentException("Email is already registered")
        }
        
        return repository.saveRegistration(registration)
    }
}
