package com.ayush.wallpapers.data

interface Downloader {
    fun downloadFile(url: String): Long
}