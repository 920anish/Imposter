package com.imposter.play.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.imposter.play.engine.GameState
import com.imposter.play.engine.GameSession
import com.imposter.play.engine.PlayerRole
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.MonoBadge
import com.imposter.play.ui.components.PrimaryButton
import com.imposter.play.ui.components.RoleRevealCard
import com.imposter.play.theme.ColorBorder
import com.imposter.play.theme.ColorCrew
import com.imposter.play.theme.ColorCrewDim
import com.imposter.play.theme.ColorImp
import com.imposter.play.theme.ColorImpDim
import com.imposter.play.theme.ColorMuted
import com.imposter.play.theme.ColorText
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_role_classified
import imposter.sharedui.generated.resources.nav_role_crew_member
import imposter.sharedui.generated.resources.nav_role_crew_tip
import imposter.sharedui.generated.resources.nav_role_done
import imposter.sharedui.generated.resources.nav_role_dont_peek
import imposter.sharedui.generated.resources.nav_role_reveal
import imposter.sharedui.generated.resources.nav_role_hint_title
import imposter.sharedui.generated.resources.nav_role_imp_tip_1
import imposter.sharedui.generated.resources.nav_role_imp_tip_2
import imposter.sharedui.generated.resources.nav_role_imposter_member
import imposter.sharedui.generated.resources.nav_role_pass_phone
import imposter.sharedui.generated.resources.nav_role_secret_word
import imposter.sharedui.generated.resources.nav_role_state_hidden
import org.jetbrains.compose.resources.stringResource

@Composable
fun RoleRevealScreen(
    session: GameSession,
    role: PlayerRole,
    onReveal: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val revealState = session.state as? GameState.RoleReveal
    val isCardRevealed = revealState?.isRevealed == true
    val playerName = session.normalizedPlayerNames.getOrNull(revealState?.playerIndex ?: 0) ?: "Player 1"

    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize()) {
        GridBackground(tint = ColorBorder, opacity = 0.32f)
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) {
                MonoBadge(
                    text = "${(revealState?.playerIndex ?: 0) + 1} / ${session.config.playerCount}",
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(16.dp))
                AnimatedContent(
                    targetState = Pair(isCardRevealed, role),
                    transitionSpec = {
                        slideInVertically(animationSpec = spring()) + fadeIn() togetherWith fadeOut()
                    },
                    label = "roleRevealCard",
                ) { state ->
                    if (!state.first) {
                        RoleRevealCover(
                            playerName = playerName,
                        )
                    } else {
                        RoleCard(role = state.second)
                    }
                }
            }
            PrimaryButton(
                text = if (isCardRevealed) stringResource(Res.string.nav_role_done) else stringResource(Res.string.nav_role_reveal),
                onClick = if (isCardRevealed) onNext else onReveal,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun RoleRevealCover(
    playerName: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "PASS",
            style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
            color = ColorText,
        )
        Spacer(Modifier.height(12.dp))
        MonoBadge(text = stringResource(Res.string.nav_role_pass_phone))
        Spacer(Modifier.height(12.dp))
        Text(
            text = playerName,
            style = androidx.compose.material3.MaterialTheme.typography.displayMedium,
            color = ColorText,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.nav_role_dont_peek),
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            color = ColorMuted,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RoleCard(role: PlayerRole) {
    val isCrew = role is PlayerRole.Crew
    val accent = if (isCrew) ColorCrew else ColorImp
    val accentDim = if (isCrew) ColorCrewDim else ColorImpDim

    RoleRevealCard(
        accent = accent,
        accentDim = accentDim,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            MonoBadge(
                text = stringResource(Res.string.nav_role_classified),
                color = accent,
                border = accent,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (isCrew) stringResource(Res.string.nav_role_crew_member) else stringResource(Res.string.nav_role_imposter_member),
                style = androidx.compose.material3.MaterialTheme.typography.displayMedium,
                color = accent,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(accent.copy(alpha = 0.35f)))
            Spacer(Modifier.height(16.dp))
            Text(
                text = if (isCrew) stringResource(Res.string.nav_role_secret_word) else stringResource(Res.string.nav_role_hint_title),
                style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                color = ColorMuted,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = when (role) {
                    is PlayerRole.Crew -> role.word
                    is PlayerRole.Imposter -> role.hint
                    PlayerRole.Unknown -> stringResource(Res.string.nav_role_state_hidden)
                },
                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
                color = ColorText,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = if (isCrew) stringResource(Res.string.nav_role_crew_tip) else stringResource(Res.string.nav_role_imp_tip_1),
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = accent.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
            )
            if (!isCrew) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(Res.string.nav_role_imp_tip_2),
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = accent.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
