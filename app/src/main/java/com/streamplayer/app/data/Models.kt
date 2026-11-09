package com.streamplayer.app.data

import com.google.gson.annotations.SerializedName

data class StreamSource(
    @SerializedName("source") val source: String,
    @SerializedName("url") val url: String,
    @SerializedName("priority") val priority: Int
)

data class LiveMatch(
    @SerializedName("match_id") val matchId: String,
    @SerializedName("title") val title: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("streams") val streams: List<StreamSource>
)

data class VideoItem(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("poster_url") val posterUrl: String,
    @SerializedName("video_url") val videoUrl: String,
    @SerializedName("category") val category: String, // "movie" or "series"
    @SerializedName("description") val description: String,
    @SerializedName("duration") val duration: String? = null
)

data class AppUpdateInfo(
    @SerializedName("version_code") val versionCode: Int,
    @SerializedName("version_name") val versionName: String,
    @SerializedName("apk_url") val apkUrl: String,
    @SerializedName("release_notes") val releaseNotes: String,
    @SerializedName("is_forced") val isForced: Boolean
)

data class DashboardResponse(
    @SerializedName("featured") val featured: List<VideoItem>,
    @SerializedName("movies") val movies: List<VideoItem>,
    @SerializedName("web_series") val webSeries: List<VideoItem>,
    @SerializedName("live_matches") val liveMatches: List<LiveMatch>
)
