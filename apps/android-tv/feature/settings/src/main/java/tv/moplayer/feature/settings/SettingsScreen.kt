package tv.moplayer.feature.settings

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
import androidx.compose.material3.Switch
import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.moplayer.domain.model.ServerProfile

@Composable
fun SettingsScreen(
    servers: List<ServerProfile> = emptyList(),
    onSetDefault: (ServerProfile) -> Unit = {},
    onRemoveServer: (ServerProfile) -> Unit = {}
) {
    val previewEnabled = remember { mutableStateOf(true) }
    val parental = remember { mutableStateOf(false) }

    Card(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Settings", style = MaterialTheme.typography.headlineSmall)
            Text("Servers", style = MaterialTheme.typography.titleMedium)
            if (servers.isEmpty()) {
                Text("No servers configured yet", style = MaterialTheme.typography.bodySmall)
            } else {
                servers.take(6).forEach { server ->
                    androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("- ${server.name}", style = MaterialTheme.typography.bodySmall)
                        Button(onClick = { onSetDefault(server) }) { Text("Default") }
                        Button(onClick = { onRemoveServer(server) }) { Text("Remove") }
                    }
                }
            }
            RowSetting("Live Preview", previewEnabled.value) { previewEnabled.value = it }
            RowSetting("Parental PIN", parental.value) { parental.value = it }
            Button(onClick = {}) { Text("Clear Cache") }
            Text("Language: Arabic / English", style = MaterialTheme.typography.bodySmall)
            Text("Remote shortcuts are configurable in next milestone", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun RowSetting(title: String, value: Boolean, onValue: (Boolean) -> Unit) {
    androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = value, onCheckedChange = onValue)
    }
}
