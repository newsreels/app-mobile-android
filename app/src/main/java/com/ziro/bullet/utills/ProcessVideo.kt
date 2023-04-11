package com.ziro.bullet.utills

import android.app.Activity
import android.os.Build
import android.os.StrictMode
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ProcessVideo(private val context: Activity) {
    /*inline*/ fun download(
        link: String,
        path: String,
        downloadProgress: DownloadProgress
    ) {
        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val executor: ExecutorService = Executors.newSingleThreadExecutor()

        executor.execute {
            try {
                val url = URL(link)
                val connection = url.openConnection()
                connection.connect()
                val length =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) connection.contentLengthLong else
                        connection.contentLength.toLong()
                url.openStream().use { input ->
                    FileOutputStream(File(path)).use { output ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var bytesRead = input.read(buffer)
                        var bytesCopied = 0L
                        while (bytesRead >= 0) {
                            output.write(buffer, 0, bytesRead)
                            bytesCopied += bytesRead
                            downloadProgress.onProgress(bytesCopied, length)
                            if (bytesCopied == length) {
                                downloadProgress.onComplete()
                            }
//                    progress?.invoke(bytesCopied, length)
                            bytesRead = input.read(buffer)
                        }

                    }
                }
            } catch (exception: Exception) {
                downloadProgress.onError()
                exception.printStackTrace()
            }
        }

    }

    interface DownloadProgress {
        fun onProgress(progress: Long, length: Long)
        fun onComplete()
        fun onError()
    }

}