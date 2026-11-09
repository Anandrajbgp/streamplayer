package com.streamplayer.app.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("dashboard.json")
    suspend fun getDashboardData(): DashboardResponse

    @GET("update.json")
    suspend fun getUpdateInfo(): AppUpdateInfo

    companion object {
        // Default base URL (points to local FastAPI control panel on Android emulator)
        private const val DEFAULT_BASE_URL = "http://10.0.2.2:8000/"

        fun create(baseUrl: String = DEFAULT_BASE_URL): ApiService {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
