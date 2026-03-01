package tv.moplayer.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
fun LibraryScreen(
    favorites: List<ContentItem> = emptyList(),
    history: List<ContentItem> = emptyList()
) {
    Card(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Favorites", style = MaterialTheme.typography.titleMedium)
            favorites.take(6).forEach { Text("• ${it.title}", style = MaterialTheme.typography.bodySmall) }
            Text("History", style = MaterialTheme.typography.titleMedium)
            history.take(6).forEach { Text("• ${it.title}", style = MaterialTheme.typography.bodySmall) }
        }
    }
}