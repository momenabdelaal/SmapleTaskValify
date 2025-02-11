package com.valify.registration

import android.annotation.SuppressLint
import androidx.annotation.Keep
import com.valify.registration.config.ValifyRegistrationConfig

@Keep
object ValifyRegistrationSDK {
    @SuppressLint("StaticFieldLeak")
    private var config: ValifyRegistrationConfig? = null

    @JvmStatic
    fun initialize(config: ValifyRegistrationConfig) {
        this.config = config
    }

    @JvmStatic
    fun getConfig(): ValifyRegistrationConfig = 
        config ?: throw IllegalStateException("ValifyRegistrationSDK must be initialized first")

    internal fun checkInitialized() {
        if (config == null) {
            throw IllegalStateException("ValifyRegistrationSDK must be initialized first")
        }
    }
}
