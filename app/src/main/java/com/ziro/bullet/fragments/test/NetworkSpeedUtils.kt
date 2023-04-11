package com.ziro.bullet.fragments.test

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import android.util.Log

class NetworkSpeedUtils {

    companion object {

        fun availableRam(context: Context): Double {
            try {
                val actManager =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager   // Declaring and Initializing the ActivityManager

                val memInfo = ActivityManager.MemoryInfo()    // Declaring MemoryInfo object

                actManager.getMemoryInfo(memInfo) // Fetching the data from the ActivityManager
                // Fetching the available and total memory and converting into Giga Bytes
                val availMemory = memInfo.availMem.toDouble() / (1024 * 1024 * 1024)
//            val totalMemory = memInfo.totalMem.toDouble() / (1024 * 1024 * 1024)

                val availRam: Double = String.format("%.2f", (availMemory)).toDouble()
//            val totalRam: Double = String.format("%.2f", totalMemory).toDouble()/
                return availRam
            } catch (e: Exception) {
                return 0.0
            }
        }

        fun totaleRam(context: Context): Double {
            val actManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager   // Declaring and Initializing the ActivityManager

            val memInfo = ActivityManager.MemoryInfo()    // Declaring MemoryInfo object

            actManager.getMemoryInfo(memInfo) // Fetching the data from the ActivityManager
            // Fetching the available and total memory and converting into Giga Bytes
            val totalMemory = memInfo.totalMem.toDouble() / (1024 * 1024 * 1024)

            Log.e("totalMemory", "totalMemory:$totalMemory ")
            val totalRam: Double = String.format("%.2f", totalMemory).toDouble()

            return totalRam
        }

        fun deviceRam(streamUrl: String, context: Context): String {
            var availRam = availableRam(context)
            Log.e("availRam", "availRam:$availRam ")

            var isWifiConnected =
                NetworkSpeedUtils.getNetworkConnected(context)    //WIFi or MOBILE DATA

            var isNetworkSpeed =
                NetworkSpeedUtils.checkNetworkType(context)  //network speed kbps,mbps,gbps
            var url = streamUrl


            if (isWifiConnected) {
                url = if (availRam > 6) {
//                    getFilesFromURLNew(URL(streamUrl).openStream(), "1080p")
                    streamUrl.replace("playlist_", "1080p_", true)
                } else if (availRam > 3) {
//                    getFilesFromURLNew(URL(streamUrl).openStream(), "720p")
                    streamUrl.replace("playlist_", "720p_", true)
                } else {
//                    M3UParser.getFilesFromURLNew(URL(streamUrl).openStream(),"480p")
//                    getFilesFromURLNew(URL(streamUrl).openStream(), "480")
                    streamUrl.replace("playlist_", "480p_", true)
                }
            } else if (!isWifiConnected && isNetworkSpeed) {
                url = if (availRam > 3) {
//                    getFilesFromURLNew(URL(streamUrl).openStream(), "720p")
                    streamUrl.replace("playlist_", "720p_", true)
                } else {
//                    getFilesFromURLNew(URL(streamUrl).openStream(), "480")
                    streamUrl.replace("playlist_", "480p_", true)
                }
            } else if (!isWifiConnected && !isNetworkSpeed) {
//                getFilesFromURLNew(URL(streamUrl).openStream(), "480")
                url = streamUrl.replace("playlist_", "480p_", true)
            }

            return url
        }

        private fun checkNetworkType(context: Context): Boolean {
            return isConnectedFast(context)
        }


        private fun getNetworkConnected(context: Context): Boolean {
            var connectivityManager: ConnectivityManager =
                context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            var isiwifi: Boolean = false

            val wifiConnected: NetworkInfo? =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

            val mobileDataConnected: NetworkInfo? =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

            if (wifiConnected != null && wifiConnected.isConnected) {
                isiwifi = true
            } else if (mobileDataConnected != null && mobileDataConnected.isConnected) {
                isiwifi = false
            }

            return isiwifi
        }

        private fun isConnectedFast(context: Context): Boolean {
            val info: NetworkInfo? = getNetworkInfo(context)
            if (info != null) {
                Log.e("testing", "isConnectedFast: ${info.type} ${info.subtype}")
            }

            return (info != null) && info.isConnected && isConnectionFast(
                info.type,
                info.subtype,
                context
            )
        }

        private fun getNetworkInfo(context: Context): NetworkInfo? {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo
        }

        private fun isConnectionFast(type: Int, subType: Int, context: Context): Boolean {
            return when (type) {
                ConnectivityManager.TYPE_WIFI -> {
                    true
                }
                ConnectivityManager.TYPE_MOBILE -> {
                    when (subType) {
                        TelephonyManager.NETWORK_TYPE_1xRTT -> false // ~ 50-100 kbps
                        TelephonyManager.NETWORK_TYPE_CDMA -> false // ~ 14-64 kbps
                        TelephonyManager.NETWORK_TYPE_EDGE -> false // ~ 50-100 kbps
                        TelephonyManager.NETWORK_TYPE_EVDO_0 -> true // ~ 400-1000 kbps
                        TelephonyManager.NETWORK_TYPE_EVDO_A -> true // ~ 600-1400 kbps
                        TelephonyManager.NETWORK_TYPE_GPRS -> false // ~ 100 kbps
                        TelephonyManager.NETWORK_TYPE_HSDPA -> true // ~ 2-14 Mbps
                        TelephonyManager.NETWORK_TYPE_HSPA -> true // ~ 700-1700 kbps
                        TelephonyManager.NETWORK_TYPE_HSUPA -> true // ~ 1-23 Mbps
                        TelephonyManager.NETWORK_TYPE_UMTS -> true // ~ 400-7000 kbps
                        TelephonyManager.NETWORK_TYPE_EHRPD -> true // ~ 1-2 Mbps
                        TelephonyManager.NETWORK_TYPE_EVDO_B -> true // ~ 5 Mbps
                        TelephonyManager.NETWORK_TYPE_HSPAP -> true // ~ 10-20 Mbps
                        TelephonyManager.NETWORK_TYPE_IDEN -> false // ~25 kbps
                        TelephonyManager.NETWORK_TYPE_LTE -> true // ~ 10+ Mbps
                        TelephonyManager.NETWORK_TYPE_UNKNOWN -> false
                        else -> false
                    }
                }
                else -> {
                    false
                }
            }
        }


    }

}