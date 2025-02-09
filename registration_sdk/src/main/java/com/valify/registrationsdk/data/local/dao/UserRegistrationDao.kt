package com.valify.registrationsdk.data.local.dao

import androidx.room.*
import com.valify.registrationsdk.data.local.entity.UserRegistrationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserRegistrationDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRegistration(registration: UserRegistrationEntity): Long

    @Update
    suspend fun updateRegistration(registration: UserRegistrationEntity)

    @Query("SELECT * FROM user_registrations WHERE registrationCompleted = 0 ORDER BY id DESC LIMIT 1")
    fun getLatestIncompleteRegistration(): Flow<UserRegistrationEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM user_registrations WHERE username = :username)")
    suspend fun isUsernameTaken(username: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM user_registrations WHERE email = :email)")
    suspend fun isEmailTaken(email: String): Boolean
}
