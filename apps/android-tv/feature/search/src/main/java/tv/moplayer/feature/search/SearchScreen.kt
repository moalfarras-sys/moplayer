package tv.moplayer.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextField
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.moplayer.domain.model.ContentItem

@Composable
fun SearchScreen(catalog: List<ContentItem> = emptyList()) {
    val query = remember { mutableStateOf("") }
    val filtered = remember(query.value, catalog) {
        val q = query.value.trim().lowercase()
        if (q.isBlank()) emptyList()
        else catalog.filter { it.title.lowercase().contains(q) }.take(12)
    }

    Card(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Global Search", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = query.value,
                onValueChange = { query.value = it },
                label = { Text("Search channels, movies, series") },
                modifier = Modifier.fillMaxWidth()
            )
            if (query.value.isNotBlank()) {
                if (filtered.isEmpty()) {
                    Text("No results", style = MaterialTheme.typography.bodySmall)
                } else {
                    filtered.forEach { item ->
                        Text("• ${item.title}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Text("Voice search integration: pending Google TV Provider wiring", style = MaterialTheme.typography.bodySmall)
        }
    }
}
