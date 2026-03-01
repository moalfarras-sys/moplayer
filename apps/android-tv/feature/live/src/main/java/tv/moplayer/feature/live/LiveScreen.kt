package tv.moplayer.feature.live

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.moplayer.domain.model.LiveChannel

@Composable
fun LiveScreen(
    channels: List<LiveChannel> = emptyList(),
    selectedIndex: Int = 0,
    onChannelOpen: (LiveChannel) -> Unit = {},
    onToggleFavorite: (LiveChannel) -> Unit = {}
) {
    val grouped = remember(channels) { channels.groupBy { it.group } }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(onClick = {}, modifier = Modifier.weight(0.34f)) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Groups", style = MaterialTheme.typography.titleMedium)
                grouped.keys.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
            }
        }

        Card(onClick = {}, modifier = Modifier.weight(0.66f)) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Channels", style = MaterialTheme.typography.titleMedium)
                channels.take(12).forEachIndexed { index, channel ->
                    val prefix = if (index == selectedIndex) "?" else "•"
                    Text("$prefix ${channel.name}", style = MaterialTheme.typography.bodySmall)
                }
                channels.getOrNull(selectedIndex)?.let { current ->
                    Text("Current preview: ${current.name}", style = MaterialTheme.typography.bodySmall)
                    Text("OK: Play | Triple OK: Favorite | Long OK: Options", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}