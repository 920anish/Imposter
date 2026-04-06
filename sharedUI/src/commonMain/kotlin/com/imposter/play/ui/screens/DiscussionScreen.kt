package com.imposter.play.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.imposter.play.engine.GameSession
import com.imposter.play.engine.GameState
import com.imposter.play.theme.ColorBorder
import com.imposter.play.theme.ColorDim
import com.imposter.play.theme.ColorImp
import com.imposter.play.theme.ColorMuted
import com.imposter.play.theme.ColorSurface
import com.imposter.play.theme.ColorText
import com.imposter.play.ui.components.DangerButton
import com.imposter.play.ui.components.GhostButton
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.MonoBadge
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_discussion_pause
import imposter.sharedui.generated.resources.nav_discussion_players
import imposter.sharedui.generated.resources.nav_discussion_resume
import imposter.sharedui.generated.resources.nav_discussion_subtitle
import imposter.sharedui.generated.resources.nav_discussion_title
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
    val lastSecondsLeft = remember { mutableIntStateOf(discussion?.secondsLeft ?: 180) }
    val lastIsRunning = remember { mutableStateOf(discussion?.isRunning ?: false) }

    if (discussion != null) {
        lastSecondsLeft.intValue = discussion.secondsLeft
        lastIsRunning.value = discussion.isRunning
    }

    val secondsLeft = lastSecondsLeft.intValue
    val isRunning = lastIsRunning.value
    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val scrollState = rememberScrollState()

    val urgent = secondsLeft < 30
    val timerColor = animateColorAsState(
        targetValue = if (urgent) ColorImp else ColorText,
        animationSpec = spring(),
        label = "discussionTimerColor",
    )
    val pulse = rememberInfiniteTransition(label = "discussionUrgentPulse")
    val urgentAlpha = pulse.animateFloat(
        initialValue = 1f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(550),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "discussionUrgentAlpha",
    )

    Box(modifier = modifier.fillMaxSize()) {
        GridBackground(
            tint = if (urgent) ColorImp else ColorBorder,
            opacity = if (urgent) 0.1f else 0.32f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Top spacer for centering
            Spacer(Modifier.weight(1f))

            MonoBadge(text = stringResource(Res.string.nav_discussion_title))
            Spacer(Modifier.height(20.dp))
            Text(
                text = "$minutes:${seconds.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.displayLarge,
                color = timerColor.value,
                modifier = Modifier.alpha(if (urgent) urgentAlpha.value else 1f),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.nav_discussion_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = ColorMuted,
            )
            Spacer(Modifier.height(16.dp))
            GhostButton(
                text = if (isRunning) stringResource(Res.string.nav_discussion_pause) else stringResource(
                    Res.string.nav_discussion_resume
                ),
                onClick = onToggleTimer,
            )
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ColorBorder)
                    .padding(18.dp),
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.nav_discussion_players),
                        style = MaterialTheme.typography.labelSmall,
                        color = ColorDim,
                    )
                    Spacer(Modifier.height(16.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        session.normalizedPlayerNames.forEach { player ->
                            Box(
                                modifier = Modifier
                                    .background(ColorSurface)
                                    .border(1.dp, ColorBorder)
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    text = player,
                                    color = ColorText,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            DangerButton(
                text = stringResource(Res.string.nav_discussion_vote_now),
                onClick = onVoteNow,
            )

            // Bottom spacer for centering
            Spacer(Modifier.weight(1f))
        }
    }
}
