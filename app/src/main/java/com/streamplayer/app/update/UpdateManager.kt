package com.streamplayer.app.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class UpdateManager(private val context: Context) {

    suspend fun downloadAndInstallApk(apkUrl: String, onProgress: (Int) -> Unit): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(apkUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext false
                }

                val file = File(context.cacheDir, "update.apk")
                if (file.exists()) file.delete()

                val inputStream = connection.inputStream
                val outputStream = file.outputStream()
                val fileSize = connection.contentLength
                var downloadedSize = 0L

                val buffer = ByteArray(8192)
                var bytesRead = inputStream.read(buffer)
                while (bytesRead != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    downloadedSize += bytesRead
                    if (fileSize > 0) {
                        val progress = ((downloadedSize * 100) / fileSize).toInt()
                        onProgress(progress)
                    }
                    bytesRead = inputStream.read(buffer)
                }

                outputStream.close()
                inputStream.close()

                // Trigger APK installation intent
                installApk(file)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun installApk(file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
