package edu.cit.lim.gymtrack.mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBackground
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBorder
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBrandPanel
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackDisabledButton
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackError
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackErrorBackground
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackErrorBorder
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackPlaceholder
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackSummaryBackground
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
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            color = GymTrackTextMuted
        )
        Spacer(modifier = Modifier.height(24.dp))

        lines.forEach { line ->
            Text(
                text = line.uppercase(),
                style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
                color = GymTrackTextPrimary
            )
        }
        Text(
            text = highlight.uppercase(),
            style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
            color = GymTrackAccent
        )

        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = tagline,
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            color = GymTrackTextSecondary
        )

        if (stats.isNotEmpty()) {
            Spacer(modifier = Modifier.height(40.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                stats.forEach { stat ->
                    Column {
                        Text(
                            text = stat.value,
                            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
                            color = GymTrackTextPrimary
                        )
                        Text(
                            text = stat.label.uppercase(),
                            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
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
                    colors = listOf(
                        Color(0x05FFFFFF),
                        Color.Transparent
                    )
                )
            )
            .padding(horizontal = 28.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = eyebrow.uppercase(),
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                color = GymTrackAccent
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = heading.uppercase(),
                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
                color = GymTrackTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subheading,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = GymTrackTextMuted
            )
            Spacer(modifier = Modifier.height(32.dp))
            content()
        }
    }
}

@Composable
fun AuthErrorBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
            .background(GymTrackErrorBackground)
            .border(1.dp, GymTrackErrorBorder)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = message,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = GymTrackError
        )
    }
}

@Composable
fun AuthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Column(modifier = modifier.padding(bottom = 20.dp)) {
        Text(
            text = label.uppercase(),
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = GymTrackTextMuted,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(text = placeholder, color = GymTrackPlaceholder)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (isPassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GymTrackAccent,
                unfocusedBorderColor = GymTrackBorder,
                focusedTextColor = GymTrackTextPrimary,
                unfocusedTextColor = GymTrackTextPrimary,
                cursorColor = GymTrackAccent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthRoleDropdown(
    selectedRole: String,
    onRoleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("member" to "Member", "owner" to "Gym Owner")

    Column(modifier = modifier.padding(bottom = 20.dp)) {
        Text(
            text = "REGISTER AS",
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = GymTrackTextMuted,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = options.first { it.first == selectedRole }.second,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GymTrackAccent,
                    unfocusedBorderColor = GymTrackBorder,
                    focusedTextColor = GymTrackTextPrimary,
                    unfocusedTextColor = GymTrackTextPrimary,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { (value, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onRoleSelected(value)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AuthSubmitButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GymTrackAccent,
            contentColor = GymTrackBackground,
            disabledContainerColor = GymTrackDisabledButton,
            disabledContentColor = GymTrackTextMuted
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = androidx.compose.material3.MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun AuthFooterText(
    message: String,
    actionLabel: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$message ",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = GymTrackTextMuted
        )
        Text(
            text = actionLabel,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            ),
            color = GymTrackAccent,
            modifier = Modifier.clickable(onClick = onActionClick)
        )
    }
}

@Composable
fun DashboardSummaryItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GymTrackBorder)
            .background(GymTrackSummaryBackground)
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Text(
            text = label.uppercase(),
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = GymTrackTextMuted
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            color = GymTrackTextPrimary
        )
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
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            color = GymTrackTextMuted
        )
        Spacer(modifier = Modifier.height(24.dp))
        title.lines().forEach { line ->
            Text(
                text = line.uppercase(),
                style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
                color = GymTrackTextPrimary
            )
        }
        Text(
            text = highlight.uppercase(),
            style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
            color = GymTrackAccent
        )
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = tagline,
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            color = GymTrackTextSecondary
        )
        Spacer(modifier = Modifier.height(40.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            stats.forEach { stat ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stat.value,
                        style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
                        color = GymTrackTextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stat.label.uppercase(),
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = GymTrackTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
