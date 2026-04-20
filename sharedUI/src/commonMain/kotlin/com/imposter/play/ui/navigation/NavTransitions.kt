package com.imposter.play.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

internal const val NAV_GROUP_KEY = "nav_group"
internal const val NAV_GROUP_HOME = "home"
internal const val NAV_GROUP_SETUP = "setup"
internal const val NAV_GROUP_GAMEPLAY = "gameplay"

internal fun navGroupMetadata(group: String): Map<String, Any> = mapOf(NAV_GROUP_KEY to group)

private fun Scene<NavKey>.navGroupOrDefault(): String =
    metadata[NAV_GROUP_KEY] as? String ?: NAV_GROUP_SETUP

internal fun AnimatedContentTransitionScope<Scene<NavKey>>.isGameplayToGameplay(): Boolean {
    return initialState.navGroupOrDefault() == NAV_GROUP_GAMEPLAY &&
        targetState.navGroupOrDefault() == NAV_GROUP_GAMEPLAY
}

internal fun setupForwardTransform(): ContentTransform {
    return (slideInHorizontally(animationSpec = tween(210)) { width -> width / 7 } +
        fadeIn(animationSpec = tween(210))) togetherWith
        (slideOutHorizontally(animationSpec = tween(190)) { width -> -width / 10 } +
            fadeOut(animationSpec = tween(170)))
}

internal fun setupBackTransform(): ContentTransform {
    return (slideInHorizontally(animationSpec = tween(210)) { width -> -width / 7 } +
        fadeIn(animationSpec = tween(210))) togetherWith
        (slideOutHorizontally(animationSpec = tween(190)) { width -> width / 10 } +
            fadeOut(animationSpec = tween(170)))
}

internal fun gameplayForwardTransform(): ContentTransform {
    return (slideInHorizontally(animationSpec = tween(175)) { width -> width / 18 } +
        fadeIn(animationSpec = tween(175))) togetherWith
        (slideOutHorizontally(animationSpec = tween(155)) { width -> -width / 22 } +
            fadeOut(animationSpec = tween(145)))
}

internal fun gameplayBackTransform(): ContentTransform {
    return (slideInHorizontally(animationSpec = tween(175)) { width -> -width / 18 } +
        fadeIn(animationSpec = tween(175))) togetherWith
        (slideOutHorizontally(animationSpec = tween(155)) { width -> width / 22 } +
            fadeOut(animationSpec = tween(145)))
}
