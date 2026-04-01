package com.imposter.play.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.imposter.play.engine.GameSession
import com.imposter.play.engine.PlayerRole
import com.imposter.play.ui.components.CornerBrackets
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.MonoBadge
import com.imposter.play.ui.components.PrimaryButton
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_role_done
import imposter.sharedui.generated.resources.nav_role_reveal
import imposter.sharedui.generated.resources.nav_role_state_crew
import imposter.sharedui.generated.resources.nav_role_state_hidden
import imposter.sharedui.generated.resources.nav_role_state_imposter
import imposter.sharedui.generated.resources.nav_role_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun RoleRevealScreen(
    session: GameSession,
    role: PlayerRole,
    onReveal: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize()) {
        GridBackground()
        CornerBrackets(modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(Res.string.nav_role_title),
                style = androidx.compose.material3.MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            MonoBadge(
                text = "${(session.state as? com.imposter.play.engine.GameState.RoleReveal)?.playerIndex?.plus(1) ?: 1} / ${session.config.playerCount}",
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = when (role) {
                    is PlayerRole.Crew -> "${stringResource(Res.string.nav_role_state_crew)} · ${role.word}"
                    is PlayerRole.Imposter -> "${stringResource(Res.string.nav_role_state_imposter)} · ${role.hint}"
                    PlayerRole.Unknown -> stringResource(Res.string.nav_role_state_hidden)
                },
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            PrimaryButton(
                text = stringResource(Res.string.nav_role_reveal),
                onClick = onReveal,
            )
            Spacer(Modifier.height(10.dp))
            PrimaryButton(
                text = stringResource(Res.string.nav_role_done),
                onClick = onNext,
            )
        }
    }
}
