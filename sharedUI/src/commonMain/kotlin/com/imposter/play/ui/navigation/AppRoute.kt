package com.imposter.play.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : NavKey

@Serializable
data object CustomizeRoute : NavKey

@Serializable
data object SettingsRoute : NavKey

@Serializable
data class RoleRevealRoute(val playerIndex: Int) : NavKey

@Serializable
data object DiscussionRoute : NavKey

@Serializable
data object VoteRoute : NavKey

@Serializable
data object ResultRoute : NavKey
