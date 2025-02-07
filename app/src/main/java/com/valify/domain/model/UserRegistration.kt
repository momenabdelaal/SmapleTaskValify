package com.valify.domain.model


data class UserRegistration(
    val username: String,
    val phoneNumber: String,
    val email: String,
    val password: String,
    val selfieImagePath: String? = null
)
