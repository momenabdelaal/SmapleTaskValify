package com.valify.registrationsdk.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.valify.registrationsdk.presentation.selfie.SelfieState
import kotlinx.coroutines.flow.Flow

interface SelfieRepository {
    suspend fun saveSelfie(registrationId: Long, bitmap: Bitmap): Result<Uri>
    suspend fun getSelfie(registrationId: Long): Result<Uri>
    suspend fun deleteSelfie(registrationId: Long): Result<Unit>
//    fun observeSelfieStatus(registrationId: Long): Flow<SelfieState>
}

