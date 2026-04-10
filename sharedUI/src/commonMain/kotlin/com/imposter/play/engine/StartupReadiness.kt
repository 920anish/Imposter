package com.imposter.play.engine

import kotlinx.coroutines.flow.MutableStateFlow

object StartupReadiness {
    val isReady = MutableStateFlow(false)
}

