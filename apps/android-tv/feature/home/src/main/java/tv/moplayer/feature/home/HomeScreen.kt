package tv.moplayer.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.moplayer.domain.model.ContentItem

@Composable
fun HomeScreen(
    weatherSummary: String = "Berlin 16°C | Clear",
    matchesSummary: String = "EPL: MCI 2-1 ARS",
    latestMovies: List<ContentItem> = emptyList(),
    latestSeries: List<ContentItem> = emptyList(),
    recommendations: List<ContentItem> = emptyList(),
    onSurpriseMe: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryCard("Weather", weatherSummary, Modifier.weight(1f))
            SummaryCard("Matches", matchesSummary, Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ContentRail("Latest Movies", latestMovies.take(5), Modifier.weight(1f))
            ContentRail("Latest Series", latestSeries.take(5), Modifier.weight(1f))
        }

        ContentRail("Recommended For You", recommendations.take(8), Modifier.fillMaxWidth())

        Button(onClick = onSurpriseMe) { Text("Surprise Me") }
    }
}

@Composable
private fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(onClick = {}, modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ContentRail(title: String, items: List<ContentItem>, modifier: Modifier = Modifier) {
    Card(onClick = {}, modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (items.isEmpty()) {
                Text("No content yet", style = MaterialTheme.typography.bodySmall)
            } else {
                items.forEach { Text("• ${it.title}", style = MaterialTheme.typography.bodySmall) }
            }
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}