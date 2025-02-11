package com.valify.registration.config

import android.content.Context
import androidx.annotation.Keep
import com.valify.registration.navigation.RegistrationNavigator

@Keep
data class ValifyRegistrationConfig(
    val context: Context,
    val onRegistrationComplete: (String) -> Unit = {},
    val onRegistrationError: (Throwable) -> Unit = {},
    val enableAutoNavigation: Boolean = true,
    val theme: ValifyTheme = ValifyTheme()
) {
    val navigator: RegistrationNavigator by lazy {
        RegistrationNavigator(this)
    }
}

@Keep
data class ValifyTheme(
    val primaryColor: Int? = null,
    val secondaryColor: Int? = null,
    val backgroundColor: Int? = null,
    val textColor: Int? = null
)
