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
import com.imposter.play.ui.components.GhostButton
import com.imposter.play.ui.components.GridBackground
import com.imposter.play.ui.components.MonoBadge
import com.imposter.play.ui.components.PrimaryButton
import imposter.sharedui.generated.resources.Res
import imposter.sharedui.generated.resources.nav_home_badge
import imposter.sharedui.generated.resources.nav_home_customize
import imposter.sharedui.generated.resources.nav_home_play_now
import imposter.sharedui.generated.resources.nav_home_subtitle
import imposter.sharedui.generated.resources.nav_home_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    onPlayNow: () -> Unit,
    onCustomize: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier.fillMaxSize(),
    ) {
        GridBackground()
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            MonoBadge(
                text = stringResource(Res.string.nav_home_badge),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(Res.string.nav_home_title),
                style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.nav_home_subtitle),
                style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(36.dp))
            PrimaryButton(
                text = stringResource(Res.string.nav_home_play_now),
                onClick = onPlayNow,
            )
            Spacer(Modifier.height(12.dp))
            GhostButton(
                text = stringResource(Res.string.nav_home_customize),
                onClick = onCustomize,
            )
        }
    }
}

