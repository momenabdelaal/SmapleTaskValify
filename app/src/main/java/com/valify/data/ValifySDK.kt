package com.valify.data

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import me.vidv.vidvocrsdk.sdk.VIDVOCRConfig
import me.vidv.vidvocrsdk.sdk.VIDVOCRListener
import me.vidv.vidvocrsdk.viewmodel.VIDVError
import me.vidv.vidvocrsdk.viewmodel.VIDVEvent
import me.vidv.vidvocrsdk.viewmodel.VIDVLogListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValifySDKWrapper @Inject constructor(
    private val context: Context
) {
    private lateinit var vidvOcrBuilder: VIDVOCRConfig.Builder

    fun initialize(accessToken: String) {
        vidvOcrBuilder = VIDVOCRConfig.Builder().apply {
            // Required configurations
            setBaseUrl(BASE_URL)
            setAccessToken(accessToken)
            setBundleKey(BUNDLE_KEY)

            // Optional configurations
            setLanguage("en")
            setDataValidation(true)
            setReturnValidationError(true)
            setReviewData(true)
            setPreviewCapturedImage(true)
            setManualCaptureMode(false)
            setCaptureOnlyMode(false)
            setPrimaryColor(Color.parseColor("#FF2196F3"))
            setDocumentVerificationPlus(false)
            setCollectUserInfo(true)
            setAdvancedConfidence(true)

            // Set logs listener
            setLogsListener(object : VIDVLogListener {
                override fun onLog(log: VIDVEvent) {
                    Log.d(
                        "VIDV-Logs",
                        "Key: ${log.key}, " +
                        "Session ID: ${log.sessionID}, " +
                        "Date: ${log.date}, " +
                        "Time Stamp: ${log.timestamp}, " +
                        "Type: ${log.type}, " +
                        "Screen: ${log.screen}"
                    )
                }

                override fun onLog(log: VIDVError) {
                    Log.d(
                        "VIDV-Error",
                        "Code: ${log.code}, " +
                        "Message: ${log.message}, " +
                        "Session ID: ${log.sessionId}, " +
                        "Date: ${log.date}, " +
                        "Time Stamp: ${log.timestamp}, " +
                        "Type: ${log.type}, " +
                        "Screen: ${log.screen}"
                    )
                }
            })
        }
    }

    fun startRegistration(activity: Activity, listener: VIDVOCRListener) {
        vidvOcrBuilder.start(activity, listener)
    }

    companion object {
        private const val BASE_URL = "https://api.valifystage.com"
        private const val BUNDLE_KEY = "YOUR_BUNDLE_KEY" // Replace with actual bundle key
    }
}
