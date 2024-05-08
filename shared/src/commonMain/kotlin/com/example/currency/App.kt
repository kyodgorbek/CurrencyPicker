package com.example.currency

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.example.currency.di.initializeKoin
import com.example.currency.presentation.screen.HomeScreen


@Composable
fun App() {
    //val colors = if (!isSystemInDarkTheme()) LightColors else DarkColors
    initializeKoin()
    MaterialTheme {
        Navigator(HomeScreen())

    }

}
