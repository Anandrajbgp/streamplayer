package com.streamplayer.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.streamplayer.app.data.DashboardResponse
import com.streamplayer.app.data.LiveMatch
import com.streamplayer.app.data.Repository
import com.streamplayer.app.data.VideoItem
import com.streamplayer.app.update.UpdateManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onPlayVideo: (String, List<com.streamplayer.app.data.StreamSource>) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { Repository(context) }
    val updateManager = remember { UpdateManager(context) }

    var selectedTab by remember { mutableStateOf(0) }
    var dashboardData by remember { mutableStateOf<DashboardResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var updateDialogShow by remember { mutableStateOf(false) }
    var updateVersionNotes by remember { mutableStateOf("") }
    var updateApkUrl by remember { mutableStateOf("") }
    var updateProgress by remember { mutableStateOf(0) }
    var isDownloading by remember { mutableStateOf(false) }

    // Fetch dashboard data & check updates on launch
    LaunchedEffect(Unit) {
        scope.launch {
            dashboardData = repository.fetchDashboard()
            isLoading = false

            // Check for updates
            val updateInfo = repository.checkUpdates()
            if (updateInfo != null && updateInfo.versionCode > 1) { // Assuming current versionCode = 1
                updateVersionNotes = updateInfo.releaseNotes
                updateApkUrl = updateInfo.apkUrl
                updateDialogShow = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("StreamPlayer", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val updateInfo = repository.checkUpdates()
                            if (updateInfo != null) {
                                updateVersionNotes = updateInfo.releaseNotes
                                updateApkUrl = updateInfo.apkUrl
                                updateDialogShow = true
                            } else {
                                android.widget.Toast.makeText(context, "You are on the latest version!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Icon(Icons.Default.SystemUpdate, contentDescription = "Check Updates")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Movie, contentDescription = "Movies") },
                    label = { Text("Movies") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Tv, contentDescription = "Web Series") },
                    label = { Text("Series") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.SportsCricket, contentDescription = "Live Sports") },
                    label = { Text("Live Sports") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                dashboardData?.let { data ->
                    when (selectedTab) {
                        0 -> HomeScreen(data, onPlayVideo)
                        1 -> VideoGridScreen(data.movies, "Movies", onPlayVideo)
                        2 -> VideoGridScreen(data.webSeries, "Web Series", onPlayVideo)
                        3 -> LiveSportsScreen(data.liveMatches, onPlayVideo)
                    }
                }
            }
        }
    }

    // Update Dialog (Non-Play Store APK Updater)
    if (updateDialogShow) {
        AlertDialog(
            onDismissRequest = { if (!isDownloading) updateDialogShow = false },
            title = { Text("New Update Available") },
            text = {
                Column {
                    Text(updateVersionNotes)
                    if (isDownloading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { updateProgress / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Downloading APK... $updateProgress%")
                    }
                }
            },
            confirmButton = {
                if (!isDownloading) {
                    Button(onClick = {
                        isDownloading = true
                        scope.launch {
                            updateManager.downloadAndInstallApk(updateApkUrl) { progress ->
                                updateProgress = progress
                            }
                            isDownloading = false
                            updateDialogShow = false
                        }
                    }) {
                        Text("Download & Update")
                    }
                }
            },
            dismissButton = {
                if (!isDownloading) {
                    TextButton(onClick = { updateDialogShow = false }) {
                        Text("Later")
                    }
                }
            }
        )
    }
}

@Composable
fun HomeScreen(
    data: DashboardResponse,
    onPlayVideo: (String, List<com.streamplayer.app.data.StreamSource>) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (data.liveMatches.isNotEmpty()) {
            item {
                Text("🔴 Live Cricket & Sports", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(data.liveMatches) { match ->
                        LiveMatchCard(match = match, onPlay = onPlayVideo)
                    }
                }
            }
        }

        item {
            Text("Featured Content", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(data.featured) { item ->
                    VideoCard(item = item, onPlay = onPlayVideo)
                }
            }
        }

        item {
            Text("Trending Movies", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(data.movies) { item ->
                    VideoCard(item = item, onPlay = onPlayVideo)
                }
            }
        }
    }
}

@Composable
fun LiveMatchCard(
    match: LiveMatch,
    onPlay: (String, List<com.streamplayer.app.data.StreamSource>) -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(160.dp)
            .clickable { onPlay(match.title, match.streams) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.error)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("LIVE NOW", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(match.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun VideoCard(
    item: VideoItem,
    onPlay: (String, List<com.streamplayer.app.data.StreamSource>) -> Unit
) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable {
                onPlay(
                    item.title,
                    listOf(com.streamplayer.app.data.StreamSource("Primary", item.videoUrl, 1))
                )
            }
    ) {
        AsyncImage(
            model = item.posterUrl,
            contentDescription = item.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1
        )
    }
}

@Composable
fun VideoGridScreen(
    items: List<VideoItem>,
    title: String,
    onPlay: (String, List<com.streamplayer.app.data.StreamSource>) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))
        // Simplified list for brevity or lazy vertical grid
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onPlay(
                                item.title,
                                listOf(com.streamplayer.app.data.StreamSource("Primary", item.videoUrl, 1))
                            )
                        },
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = item.posterUrl,
                        contentDescription = item.title,
                        modifier = Modifier
                            .width(80.dp)
                            .height(110.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        Text(item.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(item.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                        Spacer(modifier = Modifier.height(4.dp))
                        item.duration?.let {
                            Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveSportsScreen(
    matches: List<LiveMatch>,
    onPlay: (String, List<com.streamplayer.app.data.StreamSource>) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Live Cricket Matches", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(matches) { match ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlay(match.title, match.streams) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.error)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("LIVE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(match.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Available Streams: ${match.streams.size} (Auto-fallback enabled)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
