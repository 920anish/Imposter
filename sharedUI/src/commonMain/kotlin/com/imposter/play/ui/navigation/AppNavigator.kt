package com.imposter.play.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.imposter.play.engine.GameIntent
import com.imposter.play.engine.GameState
import com.imposter.play.engine.GameViewModel
import com.imposter.play.ui.screens.CustomizeScreen
import com.imposter.play.ui.screens.DiscussionScreen
import com.imposter.play.ui.screens.HomeScreen
import com.imposter.play.ui.screens.ResultScreen
import com.imposter.play.ui.screens.RoleRevealScreen
import com.imposter.play.ui.screens.SettingsScreen
import com.imposter.play.ui.screens.VoteScreen
import org.koin.compose.koinInject

@Composable
fun AppNavigator(
    viewModel: GameViewModel = koinInject(),
) {
    val session by viewModel.session.collectAsState()
    val backStack = remember { mutableStateListOf<NavKey>(HomeRoute) }

    LaunchedEffect(Unit) {
        viewModel.loadPrefs()
    }

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                while (backStack.size > 1) {
                    backStack.removeLastOrNull()
                }
                viewModel.onIntent(GameIntent.PlayAgain)
            }
        },
        entryProvider = entryProvider {
            entry<HomeRoute> {
                HomeScreen(
                    config = session.config,
                    onDecreasePlayers = { viewModel.onIntent(GameIntent.DecreasePlayerCount) },
                    onIncreasePlayers = { viewModel.onIntent(GameIntent.IncreasePlayerCount) },
                    onPlayNow = {
                        viewModel.onIntent(GameIntent.StartGame(it))
                        backStack.add(RoleRevealRoute(0))
                    },
                    onCustomize = { backStack.add(CustomizeRoute) },
                )
            }
            entry<CustomizeRoute> {
                CustomizeScreen(
                    config = session.config,
                    onConfigChange = { updated ->
                        viewModel.onIntent(
                            GameIntent.UpdateSetupConfig(
                                updated
                            )
                        )
                    },
                    onPlay = {
                        viewModel.onIntent(GameIntent.StartGame(it))
                        backStack.add(RoleRevealRoute(0))
                    },
                    onOpenSettings = { backStack.add(SettingsRoute) },
                    onClose = { backStack.removeLastOrNull() },
                )
            }
            entry<SettingsRoute> {
                SettingsScreen(
                    config = session.config,
                    onConfigChange = { updated ->
                        viewModel.onIntent(GameIntent.UpdateSetupConfig(updated))
                    },
                    onClose = { backStack.removeLastOrNull() },
                )
            }
            entry<RoleRevealRoute> {
                RoleRevealScreen(
                    session = session,
                    role = viewModel.currentPlayerRole(),
                    onReveal = { viewModel.onIntent(GameIntent.RevealCard) },
                    onNext = {
                        viewModel.onIntent(GameIntent.NextPlayer)
                        when (val state = viewModel.session.value.state) {
                            is GameState.RoleReveal -> backStack.add(RoleRevealRoute(state.playerIndex))
                            is GameState.Discussion -> backStack.add(DiscussionRoute)
                            else -> Unit
                        }
                    },
                )
            }
            entry<DiscussionRoute> {
                DiscussionScreen(
                    session = session,
                    onToggleTimer = { viewModel.onIntent(GameIntent.ToggleTimer) },
                    onVoteNow = {
                        viewModel.onIntent(GameIntent.StartVoting)
                        backStack.add(VoteRoute)
                    },
                )
            }
            entry<VoteRoute> {
                VoteScreen(
                    session = session,
                    onCastVote = { index -> viewModel.onIntent(GameIntent.CastVote(index)) },
                    onReveal = {
                        viewModel.onIntent(GameIntent.RevealResult)
                        backStack.add(ResultRoute)
                    },
                )
            }
            entry<ResultRoute> {
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

    LaunchedEffect(session.state) {
        if (session.state is GameState.Idle && backStack.lastOrNull() != HomeRoute) {
            while (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        }
    }
}
