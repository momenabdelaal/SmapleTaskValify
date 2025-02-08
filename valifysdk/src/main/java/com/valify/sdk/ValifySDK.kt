package com.valify.sdk

import android.content.Context
import androidx.activity.ComponentActivity
import com.valify.sdk.di.DaggerValifyComponent
import com.valify.sdk.di.ValifyComponent
import com.valify.sdk.domain.model.RegistrationResult
import com.valify.sdk.presentation.registration.RegistrationActivity


class ValifySDK private constructor(
    private val context: Context,
    private val component: ValifyComponent
) {
    companion object {
        @Volatile
        private var instance: ValifySDK? = null


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


    fun startRegistration(
        activity: ComponentActivity,
        callback: (RegistrationResult) -> Unit
    ) {
        RegistrationActivity.start(activity, callback)
    }


    internal fun getComponent(): ValifyComponent = component
}
