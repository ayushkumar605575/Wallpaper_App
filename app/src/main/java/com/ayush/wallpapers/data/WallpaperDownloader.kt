package com.ayush.wallpapers.data

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

class WallpaperDownloader(
    context: Context
) : Downloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(url: String): Long {
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("image/jpeg")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("Wallpaper Image")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES,
                "/Wallpaper/wallpaper${Category}.jpg"
            )

        return downloadManager.enqueue(request)
    }

}