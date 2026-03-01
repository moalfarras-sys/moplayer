package tv.moplayer.feature.vod

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.moplayer.domain.model.ContentItem

@Composable
fun VodScreen(
    movies: List<ContentItem> = emptyList(),
    series: List<ContentItem> = emptyList(),
    continueWatching: List<ContentItem> = emptyList(),
    onOpen: (ContentItem) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        VodColumn("Movies", movies, Modifier.weight(1f))
        VodColumn("Series", series, Modifier.weight(1f))
        VodColumn("Continue Watching", continueWatching, Modifier.weight(1f))
    }
}

@Composable
private fun VodColumn(title: String, items: List<ContentItem>, modifier: Modifier = Modifier) {
    Card(onClick = {}, modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (items.isEmpty()) {
                Text("No items", style = MaterialTheme.typography.bodySmall)
            } else {
                items.take(12).forEach { item ->
                    Text("• ${item.title}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}