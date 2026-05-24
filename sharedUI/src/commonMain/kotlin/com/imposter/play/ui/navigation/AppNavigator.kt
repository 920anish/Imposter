package com.imposter.play.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.imposter.play.engine.GameIntent
import com.imposter.play.engine.GameState
import com.imposter.play.engine.GameViewModel
import com.imposter.play.ui.screens.addwords.AddWordsScreen
import com.imposter.play.ui.screens.customize.CustomizeScreen
import com.imposter.play.ui.screens.customize.CustomizeViewModel
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
    val customizeViewModel = koinInject<CustomizeViewModel>()
    val backStack = remember { mutableStateListOf<NavKey>(HomeRoute) }
    val scope = rememberCoroutineScope()

    // Centralized navigation reducers to ensure single source of truth and valid state transitions
    val safePop: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    val resetToHome: () -> Unit = {
        viewModel.onIntent(GameIntent.PlayAgain)
        while (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadPrefs()
    }

    NavDisplay(
        backStack = backStack,
        transitionSpec = {
            if (isGameplayToGameplay()) {
                gameplayForwardTransform()
            } else {
                setupForwardTransform()
            }
        },
        popTransitionSpec = {
            if (isGameplayToGameplay()) {
                gameplayBackTransform()
            } else {
                setupBackTransform()
            }
        },
        onBack = {
            when (backStack.lastOrNull()) {
                is RoleRevealRoute -> resetToHome()
                is HomeRoute -> return@NavDisplay
                is CustomizeRoute, is SettingsRoute, is AddWordsRoute -> safePop()
                else -> resetToHome()
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
                    onClose = safePop,
                )
            }
            entry<CustomizeRoute>(metadata = navGroupMetadata(NAV_GROUP_SETUP)) {
                CustomizeScreen(
                    config = session.config,
                    viewModel = customizeViewModel,
                    onPlay = {
                        scope.launch {
                            if (viewModel.startGameAndAwait(it)) {
                                backStack.add(RoleRevealRoute(0))
                            }
                        }
                    },
                    onClose = safePop,
                )
            }
            entry<SettingsRoute>(metadata = navGroupMetadata(NAV_GROUP_SETUP)) {
                SettingsScreen(
                    config = session.config,
                    viewModel = customizeViewModel,
                    onConfigChange = { updated ->
                        viewModel.onIntent(GameIntent.UpdateSetupConfig(updated))
                    },
                    onClose = safePop,
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
                                safePop() // remove current reveal
                                backStack.add(RoleRevealRoute(state.playerIndex)) // replace with next
                            }
                            is GameState.Discussion -> {
                                safePop() // remove last reveal
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
                    onPlayAgain = resetToHome,
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
                    onPlayAgain = resetToHome,
                )
            }
        },
    )
}
