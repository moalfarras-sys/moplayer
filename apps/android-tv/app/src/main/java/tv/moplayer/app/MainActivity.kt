package tv.moplayer.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.room.Room
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tv.moplayer.core.designsystem.MoPlayerTheme
import tv.moplayer.core.player.AndroidPlaybackGateway
import tv.moplayer.core.player.ExternalPlayerLauncher
import tv.moplayer.core.player.ui.TvPlayerSurface
import tv.moplayer.data.repository.BasicEpgProvider
import tv.moplayer.data.repository.InMemoryPlaylistIngestor
import tv.moplayer.data.repository.InMemorySupabaseSyncGateway
import tv.moplayer.data.repository.RuleBasedRecommendationEngine
import tv.moplayer.data.local.LocalStorageRepository
import tv.moplayer.data.supabase.RestSupabaseSyncGateway
import tv.moplayer.data.widgets.WidgetDataRepository
import tv.moplayer.core.database.MoPlayerDatabase
import tv.moplayer.domain.model.ContentItem
import tv.moplayer.domain.model.LiveChannel
import tv.moplayer.domain.model.ServerProfile
import tv.moplayer.feature.home.HomeScreen
import tv.moplayer.feature.library.LibraryScreen
import tv.moplayer.feature.live.LiveScreen
import tv.moplayer.feature.login.LoginScreen
import tv.moplayer.feature.search.SearchScreen
import tv.moplayer.feature.settings.SettingsScreen
import tv.moplayer.feature.supabasesync.SupabaseSyncScreen
import tv.moplayer.feature.vod.VodScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoPlayerTheme {
                MoPlayerRoot(onExit = { finish() })
            }
        }
    }
}

enum class DockDestination(val label: String) {
    HOME("Home"), LIVE("Live"), MOVIES_SERIES("Movies/Series"), SEARCH("Search"), LIBRARY("Library"), SETTINGS("Settings"), SYNC("Sync")
}

@Composable
private fun MoPlayerRoot(onExit: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val ingest = remember { InMemoryPlaylistIngestor() }
    val recommendation = remember { RuleBasedRecommendationEngine() }
    val epg = remember { BasicEpgProvider() }
    val sync = remember {
        if (BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) {
            RestSupabaseSyncGateway(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY)
        } else {
            InMemorySupabaseSyncGateway()
        }
    }
    val widgets = remember { WidgetDataRepository() }
    val playback = remember { AndroidPlaybackGateway(context) }
    val database = remember {
        Room.databaseBuilder(context, MoPlayerDatabase::class.java, "moplayer.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    val local = remember { LocalStorageRepository(database) }
    DisposableEffect(Unit) {
        onDispose {
            playback.release()
            database.close()
        }
    }

    var selected by remember { mutableStateOf(DockDestination.HOME) }
    var loginDone by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Add server to start") }
    var lastBack by remember { mutableLongStateOf(0L) }
    var accentColor by remember { mutableStateOf(Color(0xFF6BC7FF)) }
    var enableSyncTab by remember { mutableStateOf(true) }
    var weatherText by remember { mutableStateOf("Berlin 16C | Clear") }
    var matchesText by remember { mutableStateOf("EPL: MCI 2-1 ARS") }
    var nowPlaying by remember { mutableStateOf<String?>(null) }

    val channels = remember { mutableStateListOf<LiveChannel>() }
    val movies = remember { mutableStateListOf<ContentItem>() }
    val series = remember { mutableStateListOf<ContentItem>() }
    val favorites = remember { mutableStateListOf<ContentItem>() }
    val servers = remember { mutableStateListOf<ServerProfile>() }

    LaunchedEffect(Unit) {
        if (sync is RestSupabaseSyncGateway) {
            sync.refreshTheme()
            sync.refreshFlags()
        }
    }
    LaunchedEffect(Unit) {
        local.observeServers().collectLatest { stored ->
            servers.clear()
            servers.addAll(stored)
            if (stored.isNotEmpty() && !loginDone) {
                status = "Stored server available. Press Continue to connect."
            }
        }
    }
    LaunchedEffect(movies.size, series.size) {
        if (movies.isNotEmpty() || series.isNotEmpty()) {
            local.replaceCatalog(movies + series)
        }
    }
    LaunchedEffect(Unit) {
        sync.themeUpdates().collectLatest { theme ->
            accentColor = parseHexColor(theme.accentHex, Color(0xFF6BC7FF))
        }
    }
    LaunchedEffect(Unit) {
        sync.featureFlagUpdates().collectLatest { flags ->
            enableSyncTab = flags["sync_tab_enabled"] ?: true
            if (!enableSyncTab && selected == DockDestination.SYNC) {
                selected = DockDestination.HOME
            }
        }
    }
    LaunchedEffect(Unit) {
        widgets.weather().collectLatest { weather ->
            weatherText = "${weather.city} ${weather.tempC}C | ${weather.condition}"
        }
    }
    LaunchedEffect(Unit) {
        widgets.matches().collectLatest { rows ->
            matchesText = rows.take(2).joinToString(" | ") { it.title }
        }
    }

    BackHandler {
        if (!loginDone) {
            onExit()
            return@BackHandler
        }
        if (selected != DockDestination.HOME) {
            selected = DockDestination.HOME
            return@BackHandler
        }
        val now = System.currentTimeMillis()
        if (now - lastBack < 1500L) {
            onExit()
        } else {
            lastBack = now
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                    listOf(Color(0xFF0C1325), accentColor.copy(alpha = 0.22f), Color(0xFF0B111C))
                )
            )
            .padding(TvUiContract.OverscanSafePaddingDp.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(targetState = selected, label = "dock-content") { destination ->
                when (destination) {
                    DockDestination.HOME -> HomeScreen(
                        weatherSummary = weatherText,
                        matchesSummary = matchesText,
                        latestMovies = movies,
                        latestSeries = series,
                        recommendations = movies.take(4) + series.take(4),
                        onSurpriseMe = {
                            val catalog = (movies + series)
                            if (catalog.isNotEmpty()) {
                                Toast.makeText(context, "Surprise: ${catalog.random().title}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    DockDestination.LIVE -> LiveScreen(
                        channels = channels,
                        selectedIndex = 0,
                        onChannelOpen = {
                            scope.launch { playback.play(it.streamUrl, it.name) }
                            nowPlaying = it.name
                            scope.launch {
                                local.addHistory(
                                    ContentItem(
                                        id = it.id,
                                        title = it.name,
                                        type = tv.moplayer.domain.model.ContentType.LIVE,
                                        group = it.group,
                                        streamUrl = it.streamUrl
                                    )
                                )
                            }
                            Toast.makeText(context, "Playing ${it.name}", Toast.LENGTH_SHORT).show()
                        },
                        onToggleFavorite = { channel ->
                            val item = ContentItem(channel.id, channel.name, tv.moplayer.domain.model.ContentType.LIVE, group = channel.group, streamUrl = channel.streamUrl)
                            favorites.add(item)
                            scope.launch { local.addFavorite(item) }
                        }
                    )

                    DockDestination.MOVIES_SERIES -> VodScreen(
                        movies = movies,
                        series = series,
                        continueWatching = favorites,
                        onOpen = {
                            val url = it.streamUrl
                            if (url != null) {
                                scope.launch { playback.play(url, it.title) }
                                nowPlaying = it.title
                                scope.launch { local.addHistory(it) }
                                Toast.makeText(context, "Playing ${it.title}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    DockDestination.SEARCH -> SearchScreen(
                        catalog = buildList {
                            addAll(movies)
                            addAll(series)
                            addAll(channels.map {
                                ContentItem(
                                    id = it.id,
                                    title = it.name,
                                    type = tv.moplayer.domain.model.ContentType.LIVE,
                                    group = it.group,
                                    streamUrl = it.streamUrl
                                )
                            })
                        }
                    )
                    DockDestination.LIBRARY -> LibraryScreen(favorites = favorites, history = favorites)
                    DockDestination.SETTINGS -> SettingsScreen(
                        servers = servers,
                        onSetDefault = { target ->
                            val updated = servers.map { it.copy(isDefault = it.id == target.id) }
                            servers.clear()
                            servers.addAll(updated)
                            scope.launch { local.setDefaultServer(target.id) }
                            Toast.makeText(context, "${target.name} set as default", Toast.LENGTH_SHORT).show()
                        },
                        onRemoveServer = { target ->
                            servers.removeAll { it.id == target.id }
                            scope.launch { local.deleteServer(target.id) }
                            Toast.makeText(context, "${target.name} removed", Toast.LENGTH_SHORT).show()
                        }
                    )
                    DockDestination.SYNC -> SupabaseSyncScreen()
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            DockBar(
                selected = selected,
                onSelected = { selected = it },
                includeSync = enableSyncTab
            )
        }

        if (!loginDone) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                LoginScreen(
                    compact = false,
                    isLoading = loading,
                    statusMessage = status,
                    onSubmit = { profile: ServerProfile ->
                        scope.launch {
                            loading = true
                            status = "Validating server..."
                            val result = withContext(Dispatchers.Default) { ingest.ingest(profile) }
                            if (result.isSuccess) {
                                status = "Loading categories..."
                                val liveNow = withContext(Dispatchers.Default) { ingest.liveChannels().first() }
                                val moviesNow = withContext(Dispatchers.Default) { ingest.movies().first() }
                                val seriesNow = withContext(Dispatchers.Default) { ingest.series().first() }
                                channels.clear(); channels.addAll(liveNow)
                                movies.clear(); movies.addAll(moviesNow)
                                series.clear(); series.addAll(seriesNow)
                                recommendation.seedByHistory(favorites, moviesNow + seriesNow)
                            } else {
                                status = result.exceptionOrNull()?.message ?: "Unknown error"
                            }
                            loading = false
                            if (channels.isNotEmpty() || movies.isNotEmpty() || series.isNotEmpty()) {
                                servers.removeAll { it.id == profile.id }
                                servers.add(profile)
                                local.saveServer(profile)
                                loginDone = true
                                status = "Connected"
                                sync.syncServer(profile)
                                epg.sync(profile)
                                withContext(Dispatchers.Default) { widgets.refresh() }
                            }
                        }
                    },
                    onLongPressOk = { Toast.makeText(context, "Advanced options: external EPG / external player", Toast.LENGTH_SHORT).show() },
                    onTripleOk = {
                        channels.firstOrNull()?.streamUrl?.let { preview ->
                            val launched = ExternalPlayerLauncher.launchVlc(context, preview)
                            Toast.makeText(
                                context,
                                if (launched) "Opened in VLC" else "VLC not installed",
                                Toast.LENGTH_SHORT
                            ).show()
                        } ?: Toast.makeText(context, "Server saved to favorites", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        nowPlaying?.let { title ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.76f)
                    .height(360.dp)
                    .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(18.dp))
                    .padding(10.dp)
            ) {
                TvPlayerSurface(player = playback.rawPlayer())
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(10.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Now Playing: $title", style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = {
                        scope.launch { playback.stop() }
                        nowPlaying = null
                    }) { Text("Close") }
                }
            }
        }
    }
}

@Composable
private fun DockBar(selected: DockDestination, onSelected: (DockDestination) -> Unit, includeSync: Boolean) {
    val items = if (includeSync) DockDestination.entries else DockDestination.entries.filter { it != DockDestination.SYNC }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items.forEach { destination ->
            Card(
                onClick = { onSelected(destination) },
                modifier = Modifier.weight(1f),
                shape = androidx.tv.material3.CardDefaults.shape(RoundedCornerShape(18.dp)),
                colors = androidx.tv.material3.CardDefaults.colors(
                    containerColor = if (selected == destination) Color(0x663FA9F5) else Color(0x26FFFFFF)
                )
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    Text(destination.label, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

private fun parseHexColor(hex: String, fallback: Color): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Throwable) {
        fallback
    }
}
