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
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.MonoBadge
import com.imposter.play.ui.components.PrimaryButton
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_result_crew_wins
import imposter.sharedui.generated.resources.nav_result_imposter_wins
import imposter.sharedui.generated.resources.nav_result_play_again
import imposter.sharedui.generated.resources.nav_result_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ResultScreen(
    session: GameSession,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val result = session.state as? GameState.Result
    val headline = if (result?.imposterCaught == true) {
        stringResource(Res.string.nav_result_crew_wins)
    } else {
        stringResource(Res.string.nav_result_imposter_wins)
    }

    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize()) {
        GridBackground()
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            MonoBadge(text = stringResource(Res.string.nav_result_title))
            Spacer(Modifier.padding(8.dp))
            Text(
                text = headline,
                style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.padding(12.dp))
            PrimaryButton(
                text = stringResource(Res.string.nav_result_play_again),
                onClick = onPlayAgain,
            )
        }
    }
}
