package com.valify.registrationsdk.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.valify.registrationsdk.data.local.dao.UserRegistrationDao
import com.valify.registrationsdk.data.local.entity.UserRegistrationEntity


@Database(
    entities = [UserRegistrationEntity::class],
    version = 1
)
abstract class RegistrationDatabase : RoomDatabase() {
    abstract fun userRegistrationDao(): UserRegistrationDao

    companion object {
        const val DATABASE_NAME = "registration_db"
    }
}
