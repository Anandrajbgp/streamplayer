package com.streamplayer.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val context: Context) {
    private val apiService = ApiService.create()

    suspend fun fetchDashboard(): DashboardResponse {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getDashboardData()
            } catch (e: Exception) {
                // Fallback to rich mock data if network fails or backend is offline
                getMockDashboard()
            }
        }
    }

    suspend fun checkUpdates(): AppUpdateInfo? {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getUpdateInfo()
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun getMockDashboard(): DashboardResponse {
        val sampleHls = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        val liveStreams = listOf(
            StreamSource("Primary Link", sampleHls, 1),
            StreamSource("Backup Link 1", sampleHls, 2),
            StreamSource("Backup Link 2", sampleHls, 3)
        )

        val liveMatches = listOf(
            LiveMatch("ind_vs_aus_2026", "India vs Australia - Live T20", true, liveStreams),
            LiveMatch("eng_vs_ind_2026", "England vs India - ODI Clash", true, liveStreams)
        )

        val movies = listOf(
            VideoItem("m1", "Action Movie 2026", "https://picsum.photos/300/450?random=1", sampleHls, "movie", "High octane action thriller.", "2h 15m"),
            VideoItem("m2", "Sci-Fi Odyssey", "https://picsum.photos/300/450?random=2", sampleHls, "movie", "Journey beyond the stars.", "1h 55m"),
            VideoItem("m3", "Mystery Island", "https://picsum.photos/300/450?random=3", sampleHls, "movie", "Uncover the hidden secrets.", "2h 05m")
        )

        val webSeries = listOf(
            VideoItem("s1", "Cyber Chronicles S1", "https://picsum.photos/300/450?random=4", sampleHls, "series", "Hacker wars in neon city.", "8 Episodes"),
            VideoItem("s2", "The Detective Chronicles", "https://picsum.photos/300/450?random=5", sampleHls, "series", "Solving cold cases in London.", "6 Episodes")
        )

        return DashboardResponse(
            featured = movies + webSeries,
            movies = movies,
            webSeries = webSeries,
            liveMatches = liveMatches
        )
    }
}
