package com.imposter.play.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.imposter.play.engine.GameSession
import com.imposter.play.ui.components.DangerButton
import com.imposter.play.ui.components.GhostButton
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.MonoBadge
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_vote_reveal
import imposter.sharedui.generated.resources.nav_vote_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun VoteScreen(
    session: GameSession,
    onCastVote: (Int) -> Unit,
    onReveal: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize()) {
        GridBackground()
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            MonoBadge(text = stringResource(Res.string.nav_vote_title))
            Spacer(Modifier.padding(8.dp))
            Text(
                text = stringResource(Res.string.nav_vote_title),
                style = androidx.compose.material3.MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.padding(12.dp))
            session.normalizedPlayerNames.forEachIndexed { index, player ->
                GhostButton(
                    text = player,
                    onClick = { onCastVote(index) },
                )
                Spacer(Modifier.padding(4.dp))
            }
            DangerButton(
                text = stringResource(Res.string.nav_vote_reveal),
                onClick = onReveal,
            )
        }
    }
}

