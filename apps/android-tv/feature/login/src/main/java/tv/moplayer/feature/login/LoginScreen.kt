package tv.moplayer.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextField
import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import tv.moplayer.domain.model.ServerProfile
import tv.moplayer.domain.model.ServerType

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LoginScreen(
    compact: Boolean = false,
    isLoading: Boolean = false,
    statusMessage: String = "",
    onSubmit: (ServerProfile) -> Unit = {},
    onLongPressOk: () -> Unit = {},
    onTripleOk: () -> Unit = {}
) {
    var methodIndex by remember { mutableIntStateOf(0) }
    val methods = ServerType.entries

    var name by remember { mutableStateOf("My Server") }
    var url by remember { mutableStateOf("https://") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var okTapCount by remember { mutableIntStateOf(0) }
    var okDownAt by remember { mutableLongStateOf(0L) }
    Card(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth(if (compact) 0.36f else 0.62f)
            .scale(1.06f)
            .onPreviewKeyEvent {
                when {
                    it.type == KeyEventType.KeyDown && (it.key == Key.Enter || it.key == Key.NumPadEnter || it.key == Key.DirectionCenter) -> {
                        okDownAt = System.currentTimeMillis()
                        okTapCount += 1
                        if (okTapCount == 3) {
                            okTapCount = 0
                            onTripleOk()
                        }
                        false
                    }
                    it.type == KeyEventType.KeyUp && (it.key == Key.Enter || it.key == Key.NumPadEnter || it.key == Key.DirectionCenter) -> {
                        val pressDuration = System.currentTimeMillis() - okDownAt
                        if (pressDuration >= 3000L) onLongPressOk()
                        false
                    }
                    it.type != KeyEventType.KeyDown -> false
                    else -> when (it.key) {
                    Key.DirectionLeft -> {
                        methodIndex = (methodIndex - 1).coerceAtLeast(0)
                        true
                    }

                    Key.DirectionRight -> {
                        methodIndex = (methodIndex + 1).coerceAtMost(methods.lastIndex)
                        true
                    }

                    else -> false
                }
                }
            },
        colors = androidx.tv.material3.CardDefaults.colors(
            containerColor = Color.White.copy(alpha = 0.11f)
        ),
        shape = androidx.tv.material3.CardDefaults.shape(RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(if (compact) 16.dp else 24.dp)
                .background(Color.Transparent),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("MoPlayer", style = MaterialTheme.typography.headlineLarge)
            Text("Smart IPTV OS Login", style = MaterialTheme.typography.bodyLarge)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                methods.forEachIndexed { idx, type ->
                    OutlinedButton(onClick = { methodIndex = idx }) {
                        Text(type.name)
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Server Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL / File Path") },
                modifier = Modifier.fillMaxWidth()
            )

            if (methods[methodIndex] == ServerType.XTREAM) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = {
                    val profile = ServerProfile(
                        id = java.util.UUID.randomUUID().toString(),
                        name = name,
                        type = methods[methodIndex],
                        baseUrl = url,
                        username = username.ifBlank { null },
                        encryptedPassword = password.ifBlank { null }
                    )
                    onSubmit(profile)
                }) {
                    Text("Continue")
                }
                OutlinedButton(onClick = onLongPressOk) { Text("Advanced") }
            }

            if (isLoading) {
                Text("Loading categories...", style = MaterialTheme.typography.bodySmall)
            }
            if (statusMessage.isNotBlank()) {
                Text(statusMessage, style = MaterialTheme.typography.bodyMedium)
            }

            if (!compact) {
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    Text("MoPlayer by Moalfarras", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Focus scale 1.06", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
