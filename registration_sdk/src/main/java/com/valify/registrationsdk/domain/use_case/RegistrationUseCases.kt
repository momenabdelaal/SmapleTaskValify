package com.valify.registrationsdk.domain.use_case

import javax.inject.Inject

data class RegistrationUseCases @Inject constructor(
    val saveRegistration: SaveRegistration,
    val getRegistration: GetRegistration,
    val captureSelfie: CaptureSelfie
)
