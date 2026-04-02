package com.imposter.play.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.imposter.play.engine.GameSession
import com.imposter.play.engine.GameState
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.MonoBadge
import com.imposter.play.ui.components.PrimaryButton
import com.imposter.play.ui.components.RoleRevealCard
import com.imposter.play.theme.ColorBorder
import com.imposter.play.theme.ColorCrew
import com.imposter.play.theme.ColorImp
import com.imposter.play.theme.ColorImpDim
import com.imposter.play.theme.ColorMuted
import com.imposter.play.theme.ColorText
import com.imposter.play.theme.ColorWin
import com.imposter.play.theme.ColorWinDim
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_result_caught
import imposter.sharedui.generated.resources.nav_result_crew_word
import imposter.sharedui.generated.resources.nav_result_crew_wins
import imposter.sharedui.generated.resources.nav_result_escaped
import imposter.sharedui.generated.resources.nav_result_imposter_was
import imposter.sharedui.generated.resources.nav_result_imposter_wins
import imposter.sharedui.generated.resources.nav_result_play_again
import imposter.sharedui.generated.resources.nav_result_their_hint
import org.jetbrains.compose.resources.stringResource

@Composable
fun ResultScreen(
    session: GameSession,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val result = session.state as? GameState.Result ?: return
    val crewWon = result.imposterCaught
    val headline = if (crewWon) {
        stringResource(Res.string.nav_result_crew_wins)
    } else {
        stringResource(Res.string.nav_result_imposter_wins)
    }
    val accent = if (crewWon) ColorWin else ColorImp
    val accentDim = if (crewWon) ColorWinDim else ColorImpDim
    val imposterName = session.normalizedPlayerNames.getOrNull(session.imposterIndex) ?: "Player ${session.imposterIndex + 1}"
    val hint = session.currentWord.hint.ifBlank { "???" }

    Box(modifier = modifier.fillMaxSize()) {
        GridBackground(tint = accent, opacity = 0.08f)
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 18.dp),
            ) {
                MonoBadge(
                    text = if (crewWon) stringResource(Res.string.nav_result_caught) else stringResource(Res.string.nav_result_escaped),
                    color = accent,
                    border = accent.copy(alpha = 0.35f),
                )
                Text(
                    text = headline,
                    style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
                    color = accent,
                    textAlign = TextAlign.Center,
                )
                RoleRevealCard(
                    accent = accent,
                    accentDim = accentDim,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
                    topInset = 8.dp,
                    bottomInset = 8.dp,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = stringResource(Res.string.nav_result_imposter_was), color = ColorMuted, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                        Text(text = imposterName, color = ColorText, style = androidx.compose.material3.MaterialTheme.typography.displayMedium)
                        Spacer(Modifier.height(12.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(ColorBorder))
                        Spacer(Modifier.height(14.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = stringResource(Res.string.nav_result_crew_word), color = ColorMuted, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                                Text(text = session.currentWord.real, color = ColorCrew, style = androidx.compose.material3.MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            }
                            Box(Modifier.height(42.dp).width(1.dp).background(ColorBorder))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = stringResource(Res.string.nav_result_their_hint), color = ColorMuted, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                                Text(text = hint, color = ColorImp, style = androidx.compose.material3.MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
            PrimaryButton(
                text = stringResource(Res.string.nav_result_play_again),
                onClick = onPlayAgain,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}
