package com.valify.registrationsdk.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.valify.registrationsdk.domain.repository.SelfieRepository

import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelfieRepositoryImpl @Inject constructor(
    private val context: Context
) : SelfieRepository {

    companion object {
        private const val SELFIE_FILE_NAME = "selfie_%d.jpg"
    }

    override suspend fun saveSelfie(registrationId: Long, bitmap: Bitmap): Result<Uri> = runCatching {
        val fileName = SELFIE_FILE_NAME.format(registrationId)
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        Uri.fromFile(file)
    }

    override suspend fun getSelfie(registrationId: Long): Result<Uri> = runCatching {
        val fileName = SELFIE_FILE_NAME.format(registrationId)
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            throw Exception("Selfie not found")
        }
        Uri.fromFile(file)
    }

    override suspend fun deleteSelfie(registrationId: Long): Result<Unit> = runCatching {
        val fileName = SELFIE_FILE_NAME.format(registrationId)
        val file = File(context.filesDir, fileName)
        if (file.exists() && !file.delete()) {
            throw Exception("Failed to delete selfie")
        }
    }

//    override fun observeSelfieStatus(registrationId: Long): Flow<SelfieStatus> = flow {
//        emit(SelfieStatus.Processing)
//        val fileName = SELFIE_FILE_NAME.format(registrationId)
//        val file = File(context.filesDir, fileName)
//        if (!file.exists()) {
//            emit(SelfieStatus.NotTaken)
//        } else {
//            try {
//                emit(SelfieStatus.Taken(Uri.fromFile(file)))
//            } catch (e: Exception) {
//                emit(SelfieStatus.Error(e.message ?: "Unknown error"))
//            }
//        }
//    }
}
