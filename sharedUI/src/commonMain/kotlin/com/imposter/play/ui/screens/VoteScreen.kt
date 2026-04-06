package com.imposter.play.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.imposter.play.engine.GameSession
import com.imposter.play.theme.ColorBorder
import com.imposter.play.theme.ColorDim
import com.imposter.play.theme.ColorImp
import com.imposter.play.theme.ColorImpDim
import com.imposter.play.theme.ColorMuted
import com.imposter.play.theme.ColorSurface
import com.imposter.play.theme.ColorText
import com.imposter.play.ui.components.DangerButton
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.MonoBadge
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_vote_cast
import imposter.sharedui.generated.resources.nav_vote_count
import imposter.sharedui.generated.resources.nav_vote_headline
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
    val totalVotes = session.votes.sumOf { it.votes }
    val maxVotes = session.votes.maxOfOrNull { it.votes } ?: 0
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        GridBackground(tint = ColorBorder, opacity = 0.32f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Top spacer for centering when content fits
            Spacer(Modifier.weight(1f))

            MonoBadge(text = stringResource(Res.string.nav_vote_title))
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.nav_vote_headline),
                style = MaterialTheme.typography.displayMedium,
                color = ColorText,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "$totalVotes ${stringResource(Res.string.nav_vote_cast)}",
                style = MaterialTheme.typography.labelMedium,
                color = ColorMuted,
            )
            Spacer(Modifier.height(24.dp))

            // Player vote rows
            session.normalizedPlayerNames.forEachIndexed { index, player ->
                val voteCount = session.votes.firstOrNull { it.playerIndex == index }?.votes ?: 0
                val isHot = voteCount > 0 && voteCount == maxVotes
                val voteRatio = if (maxVotes == 0) 0f else voteCount.toFloat() / maxVotes.toFloat()
                val barWidth = animateFloatAsState(
                    targetValue = voteRatio,
                    animationSpec = spring(),
                    label = "voteBarWidth",
                )
                val rowColor = animateColorAsState(
                    targetValue = if (isHot) ColorImpDim else ColorSurface,
                    animationSpec = spring(),
                    label = "voteRowColor",
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rowColor.value)
                        .border(1.dp, if (isHot) ColorImp.copy(alpha = 0.35f) else ColorBorder)
                        .clickable(onClick = { onCastVote(index) })
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                )
                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (voteCount > 0) {
                            Box(
                                Modifier
                                    .height(3.dp)
                                    .width((42f * barWidth.value).dp)
                                    .background(if (isHot) ColorImp else ColorMuted),
                            )
                        }
                        Text(
                            text = player,
                            color = if (isHot) ColorImp else ColorText,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                    Text(
                        text = if (voteCount > 0) stringResource(
                            Res.string.nav_vote_count,
                            voteCount.toString()
                        ) else "-",
                        color = when {
                            isHot -> ColorImp
                            voteCount > 0 -> ColorMuted
                            else -> ColorDim
                        },
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(16.dp))
            DangerButton(
                text = stringResource(Res.string.nav_vote_reveal),
                onClick = onReveal,
                enabled = totalVotes > 0,
            )

            // Bottom spacer for centering when content fits
            Spacer(Modifier.weight(1f))
        }
    }
}
