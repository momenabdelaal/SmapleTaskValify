package com.valify.sdk

import android.content.Context
import androidx.activity.ComponentActivity
import com.valify.sdk.di.DaggerValifyComponent
import com.valify.sdk.di.ValifyComponent
import com.valify.sdk.domain.model.RegistrationResult
import com.valify.sdk.presentation.registration.RegistrationActivity

/**
 * Main entry point for the Valify SDK.
 * This class handles initialization and provides methods to start the registration process.
 */
class ValifySDK private constructor(
    private val context: Context,
    private val component: ValifyComponent
) {
    companion object {
        @Volatile
        private var instance: ValifySDK? = null

        /**
         * Initialize the SDK with the application context.
         * This should be called before using any SDK features.
         */
        fun initialize(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        val component = DaggerValifyComponent.factory().create(context)
                        instance = ValifySDK(context.applicationContext, component)
                    }
                }
            }
        }

        fun getInstance(): ValifySDK {
            return instance ?: throw IllegalStateException(
                "ValifySDK not initialized. Call initialize(context) first."
            )
        }
    }

    /**
     * Start the registration process.
     * @param activity The activity from which to start the registration
     * @param callback Callback to receive registration result
     */
    fun startRegistration(
        activity: ComponentActivity,
        callback: (RegistrationResult) -> Unit
    ) {
        RegistrationActivity.start(activity, callback)
    }

    /**
     * Get the Dagger component for dependency injection.
     * This is internal and should not be exposed to SDK users.
     */
    internal fun getComponent(): ValifyComponent = component
}
