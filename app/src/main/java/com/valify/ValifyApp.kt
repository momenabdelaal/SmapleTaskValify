package com.valify

import android.app.Application
import android.graphics.Color
import android.widget.Toast
import com.valify.registration.ValifyRegistrationSDK
import com.valify.registration.config.ValifyRegistrationConfig
import com.valify.registrationsdk.domain.repository.RegistrationRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ValifyApp : Application() {

    @Inject
    lateinit var registrationRepository: RegistrationRepository

    override fun onCreate() {
        super.onCreate()
        
        ValifyRegistrationSDK.initialize(
            ValifyRegistrationConfig(
                context = applicationContext,
                onRegistrationComplete = { userId ->
                    Toast.makeText(
                        applicationContext,
                        "Registration completed successfully! User ID: $userId",
                        Toast.LENGTH_LONG
                    ).show()
                },
                onRegistrationError = { error ->
                    Toast.makeText(
                        applicationContext,
                        "Registration failed: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                },
                enableAutoNavigation = true
            )
        )
    }


}
