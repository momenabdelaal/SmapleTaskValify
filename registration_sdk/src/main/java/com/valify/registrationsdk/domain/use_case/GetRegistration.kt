package com.valify.registrationsdk.domain.use_case

import com.valify.registrationsdk.domain.model.UserRegistration
import com.valify.registrationsdk.domain.repository.RegistrationRepository
import com.valify.registrationsdk.util.DeviceUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRegistration @Inject constructor(
    private val repository: RegistrationRepository,
    private val deviceUtils: DeviceUtils
) {
    operator fun invoke(): Flow<UserRegistration?> = flow {
        val deviceId = deviceUtils.getDeviceId()
        val registration = repository.getRegistrationByDeviceId(deviceId)
        emit(registration)
    }
}
