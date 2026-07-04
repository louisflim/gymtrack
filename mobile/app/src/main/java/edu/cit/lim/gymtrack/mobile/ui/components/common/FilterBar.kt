package edu.cit.lim.gymtrack.mobile.ui.components.common

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackAccent
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackBackground
import edu.cit.lim.gymtrack.mobile.ui.theme.GymTrackTextMuted

@Composable
fun FilterBar(
    search: String,
    onSearchChange: (String) -> Unit,
    searchPlaceholder: String = "Search...",
    date: String = "",
    onDateChange: ((String) -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = search,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(searchPlaceholder, color = GymTrackTextMuted) },
            singleLine = true
        )
        if (onDateChange != null) {
            OutlinedTextField(
                value = date,
                onValueChange = onDateChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Filter date (YYYY-MM-DD)", color = GymTrackTextMuted) },
                singleLine = true
            )
        }
    }
}

@Composable
fun StatusFilterChips(
    selected: String,
    options: List<Pair<String, String>>,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (value, label) ->
            FilterChip(
                selected = selected == value,
                onClick = { onSelected(value) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GymTrackAccent,
                    selectedLabelColor = GymTrackBackground,
                    labelColor = GymTrackTextMuted
                )
            )
        }
    }
}
