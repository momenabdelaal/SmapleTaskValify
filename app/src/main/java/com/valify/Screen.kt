package com.valify

sealed class Screen(val route: String) {
    object Registration : Screen("registration")
    object Selfie : Screen("selfie/{registrationId}")
}
