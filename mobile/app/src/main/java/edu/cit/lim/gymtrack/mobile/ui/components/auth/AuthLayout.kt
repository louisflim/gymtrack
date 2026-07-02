package edu.cit.lim.gymtrack.mobile.ui.components.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBackground
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBrandPanel
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextMuted
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextPrimary
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextSecondary

data class BrandStat(
    val value: String,
    val label: String
)

@Composable
fun AuthShell(
    brandEyebrow: String,
    brandTitle: String,
    brandHighlight: String,
    brandTagline: String,
    brandStats: List<BrandStat>,
    formEyebrow: String,
    formHeading: String,
    formSubheading: String,
    brandModifier: Modifier = Modifier,
    formModifier: Modifier = Modifier,
    formContent: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymTrackBackground)
            .verticalScroll(rememberScrollState())
    ) {
        BrandPanel(
            eyebrow = brandEyebrow,
            title = brandTitle,
            highlight = brandHighlight,
            tagline = brandTagline,
            stats = brandStats,
            modifier = brandModifier
        )

        FormPanel(
            eyebrow = formEyebrow,
            heading = formHeading,
            subheading = formSubheading,
            modifier = formModifier,
            content = formContent
        )
    }
}

@Composable
private fun BrandPanel(
    eyebrow: String,
    title: String,
    highlight: String,
    tagline: String,
    stats: List<BrandStat>,
    modifier: Modifier = Modifier
) {
    val lines = title.lines()
    val accentLine = Color(0x0DC8FF3D)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GymTrackBrandPanel)
            .drawBehind {
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = accentLine,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += 28f
                }
            }
            .padding(horizontal = 32.dp, vertical = 40.dp)
    ) {
        Text(
            text = eyebrow.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = GymTrackTextMuted
        )
        Spacer(modifier = Modifier.height(24.dp))

        lines.forEach { line ->
            Text(
                text = line.uppercase(),
                style = MaterialTheme.typography.displayLarge,
                color = GymTrackTextPrimary
            )
        }
        Text(
            text = highlight.uppercase(),
            style = MaterialTheme.typography.displayLarge,
            color = GymTrackAccent
        )

        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = tagline,
            style = MaterialTheme.typography.bodyLarge,
            color = GymTrackTextSecondary
        )

        if (stats.isNotEmpty()) {
            Spacer(modifier = Modifier.height(40.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                stats.forEach { stat ->
                    Column {
                        Text(
                            text = stat.value,
                            style = MaterialTheme.typography.headlineLarge,
                            color = GymTrackTextPrimary
                        )
                        Text(
                            text = stat.label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = GymTrackTextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormPanel(
    eyebrow: String,
    heading: String,
    subheading: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0x05FFFFFF), Color.Transparent)
                )
            )
            .padding(horizontal = 28.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = eyebrow.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = GymTrackAccent
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = heading.uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                color = GymTrackTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subheading,
                style = MaterialTheme.typography.bodyMedium,
                color = GymTrackTextMuted
            )
            Spacer(modifier = Modifier.height(32.dp))
            content()
        }
    }
}

@Composable
fun DashboardBrandPanel(
    eyebrow: String,
    title: String,
    highlight: String,
    tagline: String,
    stats: List<BrandStat>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0x24C8FF3D), Color.Transparent),
                    radius = 500f
                )
            )
            .background(GymTrackBrandPanel)
            .padding(horizontal = 32.dp, vertical = 40.dp)
    ) {
        Text(
            text = eyebrow.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = GymTrackTextMuted
        )
        Spacer(modifier = Modifier.height(24.dp))
        title.lines().forEach { line ->
            Text(
                text = line.uppercase(),
                style = MaterialTheme.typography.displayLarge,
                color = GymTrackTextPrimary
            )
        }
        Text(
            text = highlight.uppercase(),
            style = MaterialTheme.typography.displayLarge,
            color = GymTrackAccent
        )
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = tagline,
            style = MaterialTheme.typography.bodyLarge,
            color = GymTrackTextSecondary
        )
        Spacer(modifier = Modifier.height(40.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            stats.forEach { stat ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stat.value,
                        style = MaterialTheme.typography.headlineLarge,
                        color = GymTrackTextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stat.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = GymTrackTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
