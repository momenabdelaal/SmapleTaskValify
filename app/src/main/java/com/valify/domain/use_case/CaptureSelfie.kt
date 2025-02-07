package com.valify.domain.use_case

import com.valify.domain.repository.RegistrationRepository
import javax.inject.Inject

class CaptureSelfie @Inject constructor(
    private val repository: RegistrationRepository
) {
    suspend operator fun invoke(registrationId: Long, imagePath: String) {
        repository.updateSelfieImage(registrationId, imagePath)
    }
}
