package com.valify.registrationsdk

sealed class Screen(val route: String) {
    object Registration : Screen("registration")
    object Selfie : Screen("selfie/{registrationId}")
}
