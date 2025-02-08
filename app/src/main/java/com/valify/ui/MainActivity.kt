package com.valify.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.valify.registrationsdk.data.ValifySDKWrapper
import com.valify.registrationsdk.presentation.registration.RegistrationScreen
import com.valify.registrationsdk.presentation.selfie.SelfieScreen
import com.valify.ui.theme.ValifyTheme
import dagger.hilt.android.AndroidEntryPoint
import me.vidv.vidvocrsdk.sdk.VIDVOCRListener
import me.vidv.vidvocrsdk.sdk.VIDVOCRResponse
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var valifySDK: ValifySDKWrapper

    private val sdkListener = object : VIDVOCRListener {
        override fun onOCRResult(response: VIDVOCRResponse?) {
            response?.let { result ->
                Log.d("ValifySDK", "Registration completed successfully")
                Log.d("ValifySDK", "Response data: $result")
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Registration completed successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                    // You can navigate to a success screen here if needed
                }
            } ?: run {
                Log.e("ValifySDK", "Registration failed - no response received")
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Registration failed. Please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SDK with the listener
        valifySDK.initialize(sdkListener.toString())

        setContent {
            ValifyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = "registration"
                    ) {
                        composable("registration") {
                            RegistrationScreen(
                                onRegistrationComplete = { registrationId ->
                                    navController.navigate("selfie/$registrationId")
                                }
                            )
                        }
                        composable(
                            route = "selfie/{registrationId}",
                            arguments = listOf(
                                navArgument("registrationId") { type = NavType.LongType }
                            )
                        ) { backStackEntry ->
                            val registrationId = backStackEntry.arguments?.getLong("registrationId") ?: return@composable
                            SelfieScreen(
                                registrationId = registrationId,
                                onNavigateToRegister = { id ->
                                    try {
                                        // Start Valify SDK registration process
                                        valifySDK.startRegistration(
                                            activity = this@MainActivity,
                                            listener = sdkListener
                                        )
                                        // Show loading message
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Starting registration process...",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } catch (e: Exception) {
                                        Log.e("ValifySDK", "Failed to start registration", e)
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Failed to start registration: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
