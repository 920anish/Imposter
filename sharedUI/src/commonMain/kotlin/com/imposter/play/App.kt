package com.imposter.play

import com.imposter.play.theme.AppTheme
import androidx.compose.runtime.Composable
import com.imposter.play.ui.navigation.AppNavigator

@Composable
fun App() = AppTheme {
    AppNavigator()
}
