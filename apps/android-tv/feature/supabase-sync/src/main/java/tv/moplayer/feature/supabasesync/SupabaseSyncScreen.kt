package tv.moplayer.feature.supabasesync

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

@Composable
fun SupabaseSyncScreen(
    realtimeConnected: Boolean = true,
    activeChannels: List<String> = listOf("themes", "widgets", "feature_flags", "servers")
) {
    Card(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Supabase Realtime", style = MaterialTheme.typography.headlineSmall)
            Text("Connected: $realtimeConnected", style = MaterialTheme.typography.bodyMedium)
            Text("Active channels:", style = MaterialTheme.typography.titleSmall)
            activeChannels.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
        }
    }
}