package com.valify.registrationsdk.domain.use_case

import com.valify.registrationsdk.domain.model.UserRegistration
import com.valify.registrationsdk.domain.repository.RegistrationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRegistration @Inject constructor(
    private val repository: RegistrationRepository
) {
    operator fun invoke(): Flow<UserRegistration?> {
        return repository.getLatestIncompleteRegistration()
    }
}
