package com.example.currency

import DarkColors
import LightColors
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.example.currency.presentation.screen.HomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview



@Composable
fun App() {
    val colors = if (!isSystemInDarkTheme()) LightColors else DarkColors
    MaterialTheme(colorScheme = colors) {
        Navigator(HomeScreen())

    }

}
