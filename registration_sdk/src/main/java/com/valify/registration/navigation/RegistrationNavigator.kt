package com.valify.registration.navigation

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.valify.registration.config.ValifyRegistrationConfig
import com.valify.registrationsdk.domain.repository.RegistrationRepository
import com.valify.registrationsdk.presentation.registration.RegistrationScreen
import com.valify.registrationsdk.presentation.selfie.SelfieScreen

class RegistrationNavigator(
    private val config: ValifyRegistrationConfig
) {

    @Composable
    fun RegistrationNavigation(
        navController: NavHostController = rememberNavController()
    ) {

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    SelfieScreen(
                        registrationId = registrationId,
                        onNavigateToRegister = { id ->
                            config.onRegistrationComplete(id.toString())
                            navController.navigate("home") {
                                popUpTo("registration") { inclusive = true }
                            }
                        }
                    )
                }
            }


        }
    }


}


