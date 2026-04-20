package com.imposter.play.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import com.imposter.play.engine.GameIntent
import com.imposter.play.engine.GameState
import com.imposter.play.engine.GameViewModel
import com.imposter.play.ui.screens.AddWordsScreen
import com.imposter.play.ui.screens.CustomizeScreen
import com.imposter.play.ui.screens.DiscussionScreen
import com.imposter.play.ui.screens.HomeScreen
import com.imposter.play.ui.screens.ResultScreen
import com.imposter.play.ui.screens.RoleRevealScreen
import com.imposter.play.ui.screens.SettingsScreen
import com.imposter.play.ui.screens.VoteScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AppNavigator(
    viewModel: GameViewModel = koinInject(),
) {
    val session by viewModel.session.collectAsState()
    val backStack = remember { mutableStateListOf<NavKey>(HomeRoute) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadPrefs()
    }

    NavDisplay(
        backStack = backStack,
        transitionSpec = {
            if (isGameplayToGameplay()) {
                gameplayTransform()
            } else {
                setupForwardTransform()
            }
        },
        popTransitionSpec = {
            if (isGameplayToGameplay()) {
                gameplayTransform()
            } else {
                setupBackTransform()
            }
        },
        onBack = {
            when (backStack.lastOrNull()) {
                is RoleRevealRoute -> {
                    // back from reveal = go home, reset
                    viewModel.onIntent(GameIntent.PlayAgain)
                    while (backStack.size > 1) backStack.removeLastOrNull()
                }
                is HomeRoute -> return@NavDisplay
                is CustomizeRoute, is SettingsRoute, is AddWordsRoute -> backStack.removeLastOrNull()
                else -> {
                    // discussion, vote, result — back goes home
                    viewModel.onIntent(GameIntent.PlayAgain)
                    while (backStack.size > 1) backStack.removeLastOrNull()
                }
            }
        },
        entryProvider = entryProvider {
            entry<HomeRoute>(metadata = navGroupMetadata(NAV_GROUP_HOME)) {
                HomeScreen(
                    config = session.config,
                    onDecreasePlayers = { viewModel.onIntent(GameIntent.DecreasePlayerCount) },
                    onIncreasePlayers = { viewModel.onIntent(GameIntent.IncreasePlayerCount) },
                    onPlayNow = {
                        scope.launch {
                            if (viewModel.startGameAndAwait(it)) {
                                backStack.add(RoleRevealRoute(0))
                            }
                        }
                    },
                    onCustomize = { backStack.add(CustomizeRoute) },
                    onAddWords = { backStack.add(AddWordsRoute) },
                    onSettings = { backStack.add(SettingsRoute) },
                )
            }
            entry<AddWordsRoute>(metadata = navGroupMetadata(NAV_GROUP_SETUP)) {
                AddWordsScreen(
                    onClose = { backStack.removeLastOrNull() },
                )
            }
            entry<CustomizeRoute>(metadata = navGroupMetadata(NAV_GROUP_SETUP)) {
                CustomizeScreen(
                    config = session.config,
                    onPlay = {
                        scope.launch {
                            if (viewModel.startGameAndAwait(it)) {
                                backStack.add(RoleRevealRoute(0))
                            }
                        }
                    },
                    onClose = { backStack.removeLastOrNull() },
                )
            }
            entry<SettingsRoute>(metadata = navGroupMetadata(NAV_GROUP_SETUP)) {
                SettingsScreen(
                    config = session.config,
                    onConfigChange = { updated ->
                        viewModel.onIntent(GameIntent.UpdateSetupConfig(updated))
                    },
                    onClose = { backStack.removeLastOrNull() },
                )
            }
            entry<RoleRevealRoute>(metadata = navGroupMetadata(NAV_GROUP_GAMEPLAY)) {
                RoleRevealScreen(
                    session = session,
                    role = viewModel.currentPlayerRole(),
                    onReveal = { viewModel.onIntent(GameIntent.RevealCard) },
                    onNext = {
                        viewModel.onIntent(GameIntent.NextPlayer)
                        when (val state = viewModel.session.value.state) {
                            is GameState.RoleReveal -> {
                                backStack.removeLastOrNull() // remove current reveal
                                backStack.add(RoleRevealRoute(state.playerIndex)) // replace with next
                            }
                            is GameState.Discussion -> {
                                backStack.removeLastOrNull() // remove last reveal
                                backStack.add(DiscussionRoute)
                            }
                            else -> Unit
                        }
                    },
                )
            }
            entry<DiscussionRoute>(metadata = navGroupMetadata(NAV_GROUP_GAMEPLAY)) {
                DiscussionScreen(
                    session = session,
                    onToggleTimer = { viewModel.onIntent(GameIntent.ToggleTimer) },
                    onVoteNow = {
                        viewModel.onIntent(GameIntent.StartVoting)
                        backStack.add(VoteRoute)
                    },
                    onSkipToResult = {
                        viewModel.onIntent(GameIntent.SkipToResult)
                        backStack.add(ResultRoute)
                    },
                    onPlayAgain = {
                        viewModel.onIntent(GameIntent.PlayAgain)
                        while (backStack.size > 1) {
                            backStack.removeLastOrNull()
                        }
                    },
                )
            }
            entry<VoteRoute>(metadata = navGroupMetadata(NAV_GROUP_GAMEPLAY)) {
                VoteScreen(
                    session = session,
                    onCastVote = { index -> viewModel.onIntent(GameIntent.CastVote(index)) },
                    onReveal = {
                        viewModel.onIntent(GameIntent.RevealResult)
                        backStack.add(ResultRoute)
                    },
                )
            }
            entry<ResultRoute>(metadata = navGroupMetadata(NAV_GROUP_GAMEPLAY)) {
                ResultScreen(
                    session = session,
                    onPlayAgain = {
                        viewModel.onIntent(GameIntent.PlayAgain)
                        while (backStack.size > 1) {
                            backStack.removeLastOrNull()
                        }
                    },
                )
            }
        },
    )

//    LaunchedEffect(session.state) {
//        if (session.state is GameState.Idle && backStack.lastOrNull() != HomeRoute) {
//            while (backStack.size > 1) {
//                backStack.removeLastOrNull()
//            }
//        }
//    }
}

private const val NAV_GROUP_KEY = "nav_group"
private const val NAV_GROUP_HOME = "home"
private const val NAV_GROUP_SETUP = "setup"
private const val NAV_GROUP_GAMEPLAY = "gameplay"

private fun navGroupMetadata(group: String): Map<String, Any> = mapOf(NAV_GROUP_KEY to group)

private fun Scene<NavKey>.navGroupOrDefault(): String =
    metadata[NAV_GROUP_KEY] as? String ?: NAV_GROUP_SETUP

private fun AnimatedContentTransitionScope<Scene<NavKey>>.isGameplayToGameplay(): Boolean {
    return initialState.navGroupOrDefault() == NAV_GROUP_GAMEPLAY &&
        targetState.navGroupOrDefault() == NAV_GROUP_GAMEPLAY
}

private fun AnimatedContentTransitionScope<Scene<NavKey>>.setupForwardTransform(): ContentTransform {
    return (slideInHorizontally(animationSpec = tween(210)) { width -> width / 7 } +
        fadeIn(animationSpec = tween(210))) togetherWith
        (slideOutHorizontally(animationSpec = tween(190)) { width -> -width / 10 } +
            fadeOut(animationSpec = tween(170)))
}

private fun AnimatedContentTransitionScope<Scene<NavKey>>.setupBackTransform(): ContentTransform {
    return (slideInHorizontally(animationSpec = tween(210)) { width -> -width / 7 } +
        fadeIn(animationSpec = tween(210))) togetherWith
        (slideOutHorizontally(animationSpec = tween(190)) { width -> width / 10 } +
            fadeOut(animationSpec = tween(170)))
}

private fun AnimatedContentTransitionScope<Scene<NavKey>>.gameplayTransform(): ContentTransform {
    return fadeIn(animationSpec = tween(105)) togetherWith fadeOut(animationSpec = tween(95))
}
