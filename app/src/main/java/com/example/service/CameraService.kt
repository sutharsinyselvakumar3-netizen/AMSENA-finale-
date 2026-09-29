package com.example.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.model.CameraStatus
import com.example.model.MqttSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.util.concurrent.TimeUnit

class CameraService(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    companion object {
        private const val TAG = "CameraService"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    private val _cameraStatus = MutableStateFlow(CameraStatus(isOnline = false))
    val cameraStatus: StateFlow<CameraStatus> = _cameraStatus.asStateFlow()

    private val _currentFrame = MutableStateFlow<Bitmap?>(null)
    val currentFrame: StateFlow<Bitmap?> = _currentFrame.asStateFlow()

    private var streamingJob: Job? = null

    fun getStreamUrl(settings: MqttSettings): String = settings.getStreamUrl()

    fun connectCamera(settings: MqttSettings) {
        disconnectCamera()
        val streamUrl = settings.getStreamUrl()
        val captureUrl = settings.getCaptureUrl()

        if (settings.cameraHost.isBlank()) {
            _cameraStatus.value = CameraStatus(
                isOnline = false,
                streamUrl = "",
                error = "Camera host not configured"
            )
            _currentFrame.value = null
            return
        }

        streamingJob = scope.launch {
            try {
                // Try connecting and streaming
                _cameraStatus.value = CameraStatus(isOnline = false, streamUrl = streamUrl)

                // Try continuous stream or polling capture
                startStreamLoop(streamUrl, captureUrl)
            } catch (e: CancellationException) {
                // Normal job cancellation
            } catch (e: Exception) {
                Log.w(TAG, "Camera stream error: ${e.message}")
                _cameraStatus.value = CameraStatus(
                    isOnline = false,
                    streamUrl = streamUrl,
                    error = e.localizedMessage ?: "Camera offline"
                )
                _currentFrame.value = null
            }
        }
    }

    private suspend fun startStreamLoop(streamUrl: String, captureUrl: String) {
        val targetUrl = if (captureUrl.isNotBlank()) captureUrl else streamUrl

        while (scope.isActive) {
            var fetched = false
            try {
                val request = Request.Builder()
                    .url(targetUrl)
                    .header("User-Agent", "AICompanion-Robot-App")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body
                    if (body != null) {
                        val bytes = body.bytes()
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        if (bitmap != null) {
                            _currentFrame.value = bitmap
                            _cameraStatus.value = CameraStatus(
                                isOnline = true,
                                streamUrl = targetUrl,
                                lastUpdated = System.currentTimeMillis()
                            )
                            fetched = true
                        }
                    }
                } else {
                    _cameraStatus.value = CameraStatus(
                        isOnline = false,
                        streamUrl = targetUrl,
                        error = "HTTP ${response.code}"
                    )
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _cameraStatus.value = CameraStatus(
                    isOnline = false,
                    streamUrl = targetUrl,
                    error = e.localizedMessage ?: "Connection failed"
                )
            }

            // Interval between frame fetches (e.g. 100ms ~10fps or longer if failed)
            val waitMs = if (fetched) 100L else 3000L
            delay(waitMs)
        }
    }

    suspend fun testCamera(settings: MqttSettings): Boolean = withContext(Dispatchers.IO) {
        val url = if (settings.cameraCapturePath.isNotBlank()) settings.getCaptureUrl() else settings.getStreamUrl()
        if (url.isBlank()) return@withContext false
        return@withContext try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    fun disconnectCamera() {
        streamingJob?.cancel()
        streamingJob = null
        _cameraStatus.value = _cameraStatus.value.copy(isOnline = false)
        _currentFrame.value = null
    }
}
