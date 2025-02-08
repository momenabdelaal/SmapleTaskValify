package com.valify.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.valify.data.ValifySDKWrapper
import com.valify.presentation.registration.RegistrationScreen
import com.valify.presentation.selfie.SelfieScreen
import com.valify.presentation.ui.theme.ValifyTheme
import dagger.hilt.android.AndroidEntryPoint
import me.vidv.vidvocrsdk.sdk.VIDVOCRListener
import me.vidv.vidvocrsdk.sdk.VIDVOCRResponse
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var valifySDK: ValifySDKWrapper

    private val sdkListener =
        VIDVOCRListener { response -> Log.d("ValifySDK", "Success , Data: $response") }

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SDK with access token (get this from your authentication service)
        valifySDK.initialize("YOUR_ACCESS_TOKEN")

        setupCameraLauncher()

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
                                onRegistrationComplete = {
                                    // Start Valify SDK registration process
                                    valifySDK.startRegistration(
                                        activity = this@MainActivity,
                                        listener = sdkListener
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setupCameraLauncher() {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imagePath = result.data?.getStringExtra("imagePath")
                // Handle the captured image path (e.g., save it or display it)
            }
        }
    }

    private fun navigateToCamera() {
//        val intent = Intent(this, CameraActivity::class.java)
//        cameraLauncher.launch(intent)
    }
}
