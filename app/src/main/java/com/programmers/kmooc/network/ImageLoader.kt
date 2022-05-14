package com.programmers.kmooc.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.*
import java.lang.Exception
import java.net.URL

object ImageLoader {
    private val imageCache = mutableMapOf<String, Bitmap>()
    fun loadImage(url: String, completed: (Bitmap?) -> Unit) {
        //TODO: String -> Bitmap 을 구현하세요
        if (url.isNullOrEmpty()) {
            completed(null)
            return
        }

        if (imageCache.containsKey(url)) {
            completed(imageCache[url])
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bitmap = BitmapFactory.decodeStream(URL(url).openStream())
                // 이미지 캐싱
                imageCache[url] = bitmap
                // 완료되면 비트맵을 반환
                withContext(Dispatchers.Main) {
                    completed(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    completed(null)
                }
            }
        }
    }
}