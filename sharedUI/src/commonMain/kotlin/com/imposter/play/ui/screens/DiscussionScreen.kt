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
import com.imposter.play.engine.GameState
import com.imposter.play.ui.components.DangerButton
import com.imposter.play.ui.components.GhostButton
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.MonoBadge
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_discussion_title
import imposter.sharedui.generated.resources.nav_discussion_toggle
import imposter.sharedui.generated.resources.nav_discussion_vote_now
import org.jetbrains.compose.resources.stringResource

@Composable
fun DiscussionScreen(
    session: GameSession,
    onToggleTimer: () -> Unit,
    onVoteNow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val discussion = session.state as? GameState.Discussion
    val secondsLeft = discussion?.secondsLeft ?: 0
    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60

    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize()) {
        GridBackground()
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            MonoBadge(text = stringResource(Res.string.nav_discussion_title))
            Spacer(Modifier.padding(8.dp))
            Text(
                text = "$minutes:${seconds.toString().padStart(2, '0')}",
                style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.padding(8.dp))
            GhostButton(
                text = stringResource(Res.string.nav_discussion_toggle),
                onClick = onToggleTimer,
            )
            Spacer(Modifier.padding(8.dp))
            DangerButton(
                text = stringResource(Res.string.nav_discussion_vote_now),
                onClick = onVoteNow,
            )
        }
    }
}

