package com.valify.registrationsdk.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_registrations",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class UserRegistrationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val phoneNumber: String,
    val email: String,
    val password: String,
    val deviceId: String,
    val selfieImagePath: String? = null,
    val registrationCompleted: Boolean = false
)
