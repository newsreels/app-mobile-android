package com.ziro.bullet.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.ziro.bullet.CacheData.DbHandler
import com.ziro.bullet.background.extensions.acquirePartialWakeLock
import com.ziro.bullet.background.extensions.safeRelease
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.*

class VideoProcessorService : Service() {
    companion object {
        private const val TAG = "VideoProcessorService"
        private const val UPLOAD_NOTIFICATION_BASE_ID = 12214
        private const val UPLOAD_NOTIFICATION_CHANNEL_ID = "Background"
        private const val UPLOAD_NOTIFICATION_CHANNEL_NAME = "Background Process"

        private const val NOTIFICATION_TEXT_PROCESS_STARTED = "Processing.."
        private const val NOTIFICATION_TEXT_UPLOAD_POST_STARTED = "Uploading.."
        private const val NOTIFICATION_TEXT_UPLOAD_COMPLETED = "Upload Completed"
        private const val NOTIFICATION_TEXT_PUBLISHING_POST = "Publishing.."
        private const val NOTIFICATION_TEXT_PUBLISHED = "Published"
        private const val NOTIFICATION_TEXT_PUBLISHING_FAILED = "Publishing Failed"

        private val tasksMap = ConcurrentHashMap<String, BgTask>()

        /**
         * Gets the list of the currently active upload tasks.
         * @return list of uploadIDs or an empty list if no tasks are currently running
         */
        val taskList: List<String>
            @Synchronized get() = if (tasksMap.isEmpty()) {
                emptyList()
            } else {
                tasksMap.keys().toList()
            }


        /**
         * Stops the upload task with the given uploadId.
         * @param uploadId The unique upload id
         */
        @Synchronized
        @JvmStatic
        fun stopUpload(uploadId: String) {
            tasksMap[uploadId]?.cancel()
        }

        /**
         * Stop all the active uploads.
         */
        @Synchronized
        @JvmStatic
        fun stopAllUploads() {
            val iterator = tasksMap.keys.iterator()

            while (iterator.hasNext()) {
                tasksMap[iterator.next()]?.cancel()
            }
        }

        /**
         * Stops the service.
         * @param context application context
         * @param forceStop if true stops the service no matter if some tasks are running, else
         * stops only if there aren't any active tasks
         * @return true if the service is getting stopped, false otherwise
         */
        @Synchronized
        @JvmOverloads
        @JvmStatic
        fun stop(context: Context, forceStop: Boolean = false) = if (forceStop) {
            stopAllUploads()
            context.stopService(Intent(context, VideoProcessorService::class.java))
        } else {
            tasksMap.isEmpty() && context.stopService(
                Intent(
                    context,
                    VideoProcessorService::class.java
                )
            )
        }
    }

    private var wakeLock: PowerManager.WakeLock? = null

    private lateinit var dbHandler: DbHandler


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)

        wakeLock = acquirePartialWakeLock(wakeLock, TAG)
        dbHandler = DbHandler(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(UPLOAD_NOTIFICATION_BASE_ID, createServiceNotification())

        Log.d(TAG, "onStartCommand: ")

        if (intent != null && intent.hasExtra("uploadInfo")) {
            val upload_info = intent.getParcelableExtra<UploadInfo>("uploadInfo")
//            val upload_info = dbHandler.getTaskWithId(id)
            if (upload_info != null) {
                val currentTask = BgTask().init(
                    this,
                    info = upload_info!!,
                    broadcastEmitter = BroadcastEmitter(),
                    dbHandler
                )
                tasksMap[upload_info.id] = currentTask

                if (taskList.size == 1)
                    threadPool.execute(currentTask)
            } else {
                stop(this, true)
            }
        } else {
            val allTasks = dbHandler.allTasks
            Log.d(TAG, "onStartCommand: allTasks = " + allTasks.size)
            if (allTasks.size > 0) {
                for (item in allTasks) {
                    val upload_info = item
                    val currentTask = BgTask().init(
                        this,
                        info = upload_info!!,
                        broadcastEmitter = BroadcastEmitter(),
                        dbHandler
                    )
                    tasksMap[upload_info.id] = currentTask
                }

                if (taskList.isNotEmpty())
                    threadPool.execute(tasksMap[taskList[0]])
            } else {
                stop(this, true)
            }
        }

        ///////
//
//        if (tasksMap.isEmpty()) {
//            val upload_info = dbHandler.task
//            Log.d(TAG, "onStartCommand: " + upload_info)
//
//            if (upload_info != null) {
//                val currentTask = BgTask().init(
//                    this,
//                    info = upload_info!!,
//                    broadcastEmitter = BroadcastEmitter(),
//                    dbHandler
//                )
//                tasksMap[upload_info.id] = currentTask
//
//                if (taskList.size == 1)
//                    threadPool.execute(currentTask)
//            } else {
//                stop(this, true)
//            }
//        } else {
//            if (dbHandler.allTasks.size > 0) {
//                val upload_info = dbHandler.task
//                if (upload_info.videoStatus == VideoStatus.ERROR) {
//                    threadPool.execute(tasksMap[upload_info.id])
//                }
//            } else {
//                stop(this, true)
//            }
//        }

        return START_STICKY
    }

    var threadPool: AbstractExecutorService = ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors(),
        5.toLong(), // Keep Alive Time
        TimeUnit.SECONDS,
        LinkedBlockingQueue<Runnable>()
    )


    @Subscribe
    fun onMessageEvent(event: BackgroundEvent) {
        when (event.data) {
            BroadcastEmitter.BG_PROCESS_START -> {
//                Log.d(TAG, "onMessageEvent: BG_PROCESS_START")
            }
            BroadcastEmitter.BG_PROCESSING_COMPLETED -> {
                Log.d(TAG, "onMessageEvent: BG_PROCESSING_COMPLETED")
                updateNotification(NOTIFICATION_TEXT_UPLOAD_POST_STARTED, true)
                dbHandler.setTaskStatus(event.id, VideoStatus.UPLOADING)
            }
            BroadcastEmitter.BG_UPLOAD_PROGRESS -> {

            }
            BroadcastEmitter.BG_UPLOAD_COMPLETED -> {
                Log.d(TAG, "onMessageEvent: BG_UPLOAD_COMPLETED")
                updateNotification(NOTIFICATION_TEXT_UPLOAD_COMPLETED, true)
            }
            BroadcastEmitter.BG_PUBLISHING -> {
                Log.d(TAG, "onMessageEvent: BG_PUBLISHING")
                updateNotification(NOTIFICATION_TEXT_PUBLISHING_POST, true)
            }
            BroadcastEmitter.BG_PUBLISHED -> {
                Log.d(TAG, "onMessageEvent: BG_PUBLISHED")
                dbHandler.deleteTask(event.id)
                tasksMap.remove(event.id)

                if (taskList.isNotEmpty()) {
                    threadPool.execute(tasksMap[taskList.get(0)])
                } else if (dbHandler.allTasks.size > 0) {

                    var isNewTask = false

                    for (item in dbHandler.allTasks) {
                        if (item.error.isEmpty()) {
                            val upload_info = item
                            val currentTask = BgTask().init(
                                this,
                                info = upload_info!!,
                                broadcastEmitter = BroadcastEmitter(),
                                dbHandler
                            )
                            isNewTask = true
                            tasksMap[upload_info.id] = currentTask
                        }
                    }

                    if (!isNewTask)
                        stop(this, true)
                    else
                        if (taskList.size > 0)
                            threadPool.execute(tasksMap[taskList.get(0)])
                } else if (taskList.isEmpty()) {
                    updateNotification(NOTIFICATION_TEXT_PUBLISHING_FAILED, false)
                    stop(this, true)
                }
            }
            BroadcastEmitter.BG_ERROR -> {
                Log.d(TAG, "onMessageEvent: BG_ERROR")
                if (event.error.equals(VideoStatus.INTERNET_ERROR)) {
                    dbHandler.setTaskError(event.id, event.error)
                } else {
                    dbHandler.deleteTask(event.id)
                }
                tasksMap.remove(event.id)

                if (taskList.isNotEmpty()) {
                    threadPool.execute(tasksMap[taskList.get(0)])
                } else if (dbHandler.allTasks.size > 0) {

                    var isNewTask = false

                    for (item in dbHandler.allTasks) {
                        if (item.error.isEmpty()) {
                            val upload_info = item
                            val currentTask = BgTask().init(
                                this,
                                info = upload_info!!,
                                broadcastEmitter = BroadcastEmitter(),
                                dbHandler
                            )
                            isNewTask = true
                            tasksMap[upload_info.id] = currentTask
                        }
                    }

                    if (!isNewTask)
                        stop(this, true)
                    else
                        if (taskList.size > 0)
                            threadPool.execute(tasksMap[taskList.get(0)])
                } else if (taskList.isEmpty()) {
                    updateNotification(NOTIFICATION_TEXT_PUBLISHING_FAILED, false)
                    stop(this, true)
                }
            }

            BroadcastEmitter.BG_STOP -> {
                Log.d(TAG, "onMessageEvent: BG_STOP")
                tasksMap.remove(event.id)

                Log.d(TAG, "onMessageEvent: BG_STOP taskList = " + taskList.size)

                if (taskList.isNotEmpty()) {
                    threadPool.execute(tasksMap[taskList.get(0)])
                } else if (dbHandler.allTasks.size > 0) {
                    val allTasks = dbHandler.allTasks
                    Log.d(TAG, "onMessageEvent: BG_STOP allTasks = " + allTasks.size)
                    var isNewTask = false

                    for (item in allTasks) {
                        Log.d(TAG, "onMessageEvent: BG_STOP item.error.isEmpty() = " + item.error.isEmpty())
                        if (item.error.isEmpty()) {
                            val upload_info = item
                            val currentTask = BgTask().init(
                                this,
                                info = upload_info!!,
                                broadcastEmitter = BroadcastEmitter(),
                                dbHandler
                            )

                            if (item.videoStatus == VideoStatus.UPLOAD_DONE) {
                                val taskReadyToPublish = dbHandler.isTaskReadyToPublish(item.id)
                                if (taskReadyToPublish) {
                                    tasksMap[upload_info.id] = currentTask
                                    isNewTask = true
                                }
                            } else {
                                tasksMap[upload_info.id] = currentTask
                                isNewTask = true
                            }
                        }
                    }

                    Log.d(TAG, "onMessageEvent: BG_STOP isNewTask = " + isNewTask)
                    Log.d(TAG, "onMessageEvent: BG_STOP taskList = " + taskList.size)

                    if (!isNewTask)
                        stop(this, true)
                    else
                        if (taskList.size > 0)
                            threadPool.execute(tasksMap[taskList.get(0)])
                } else
                    stop(this, true)
            }
        }
    }

    private fun createServiceNotification(): Notification {
        if (Build.VERSION.SDK_INT >= 26) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                UPLOAD_NOTIFICATION_CHANNEL_ID,
                UPLOAD_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, UPLOAD_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setOngoing(true)
            .setContentTitle(NOTIFICATION_TEXT_PROCESS_STARTED)
            .build()

        return notification
    }

    private fun updateNotification(title: String, isOnGoing: Boolean) {
        Log.d(TAG, "updateNotification: title =  ${title}")
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, UPLOAD_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setOngoing(isOnGoing)
            .setContentTitle(title)
            .build()
        notificationManager.notify(UPLOAD_NOTIFICATION_BASE_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)

        stopForeground(true)
        wakeLock.safeRelease()
    }

}