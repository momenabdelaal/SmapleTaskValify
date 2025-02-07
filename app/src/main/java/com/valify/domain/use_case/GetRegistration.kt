package com.valify.domain.use_case

import com.valify.domain.model.UserRegistration
import com.valify.domain.repository.RegistrationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRegistration @Inject constructor(
    private val repository: RegistrationRepository
) {
    operator fun invoke(): Flow<UserRegistration?> {
        return repository.getLatestIncompleteRegistration()
    }
}
