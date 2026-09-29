package com.example.service

import android.util.Log
import com.example.model.MqttSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import javax.net.ssl.SSLSocketFactory

sealed class MqttConnectionState {
    object Disconnected : MqttConnectionState()
    object Connecting : MqttConnectionState()
    object Connected : MqttConnectionState()
    data class Error(val message: String) : MqttConnectionState()
}

class MqttService(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    companion object {
        private const val TAG = "MqttService"
    }

    private var client: MqttAsyncClient? = null
    private var currentSettings: MqttSettings? = null

    private val _connectionState = MutableStateFlow<MqttConnectionState>(MqttConnectionState.Disconnected)
    val connectionState: StateFlow<MqttConnectionState> = _connectionState.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<Pair<String, String>>(extraBufferCapacity = 64)
    val incomingMessages: SharedFlow<Pair<String, String>> = _incomingMessages.asSharedFlow()

    fun isConnected(): Boolean {
        return client?.isConnected == true && _connectionState.value is MqttConnectionState.Connected
    }

    @Synchronized
    fun connect(settings: MqttSettings) {
        currentSettings = settings
        if (settings.brokerHost.isBlank()) {
            _connectionState.value = MqttConnectionState.Error("Broker host cannot be empty")
            return
        }

        try {
            if (client != null && client?.isConnected == true) {
                try {
                    client?.disconnect()
                } catch (_: Exception) {}
            }

            _connectionState.value = MqttConnectionState.Connecting

            val protocol = if (settings.useTls) "ssl" else "tcp"
            val brokerUri = "$protocol://${settings.brokerHost.trim()}:${settings.port}"
            val finalClientId = if (settings.clientId.isNotBlank()) settings.clientId else "AICompanion-${System.currentTimeMillis() % 10000}"

            client = MqttAsyncClient(brokerUri, finalClientId, MemoryPersistence())

            val options = MqttConnectOptions().apply {
                isAutomaticReconnect = true
                isCleanSession = true
                connectionTimeout = 10
                keepAliveInterval = settings.keepAlive
                if (settings.username.isNotBlank()) {
                    userName = settings.username.trim()
                }
                if (settings.password.isNotBlank()) {
                    password = settings.password.toCharArray()
                }
                if (settings.useTls) {
                    socketFactory = SSLSocketFactory.getDefault()
                }
            }

            client?.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    Log.i(TAG, "MQTT connected to $serverURI (reconnect=$reconnect)")
                    _connectionState.value = MqttConnectionState.Connected
                    subscribeToRobotTopics(settings.robotId)
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.w(TAG, "MQTT connection lost: ${cause?.message}")
                    _connectionState.value = MqttConnectionState.Error(cause?.message ?: "Connection lost")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    if (topic != null && message != null) {
                        val payload = String(message.payload)
                        Log.d(TAG, "Message arrived on $topic: $payload")
                        scope.launch {
                            _incomingMessages.emit(Pair(topic, payload))
                        }
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(TAG, "MQTT delivery complete")
                }
            })

            client?.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.i(TAG, "Initial connect success")
                    _connectionState.value = MqttConnectionState.Connected
                    subscribeToRobotTopics(settings.robotId)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    val errMsg = exception?.localizedMessage ?: "Failed to connect to broker"
                    Log.e(TAG, "Connect failure: $errMsg")
                    _connectionState.value = MqttConnectionState.Error(errMsg)
                }
            })
        } catch (e: Exception) {
            val errMsg = e.localizedMessage ?: "MQTT setup error"
            Log.e(TAG, "Exception during connect: $errMsg", e)
            _connectionState.value = MqttConnectionState.Error(errMsg)
        }
    }

    private fun subscribeToRobotTopics(robotId: String) {
        val c = client ?: return
        if (!c.isConnected) return

        val topics = arrayOf(
            "ai_companion/$robotId/ack",
            "ai_companion/$robotId/status",
            "ai_companion/$robotId/telemetry",
            "ai_companion/$robotId/event",
            "ai_companion/$robotId/camera/status"
        )
        val qos = intArrayOf(1, 1, 0, 1, 0)

        try {
            c.subscribe(topics, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.i(TAG, "Successfully subscribed to topics for $robotId")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.w(TAG, "Subscription failure: ${exception?.message}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error subscribing to topics: ${e.message}")
        }
    }

    fun disconnect() {
        try {
            if (client?.isConnected == true) {
                client?.disconnect(null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        _connectionState.value = MqttConnectionState.Disconnected
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        _connectionState.value = MqttConnectionState.Disconnected
                    }
                })
            } else {
                _connectionState.value = MqttConnectionState.Disconnected
            }
        } catch (e: Exception) {
            _connectionState.value = MqttConnectionState.Disconnected
        }
    }

    fun reconnect() {
        val s = currentSettings
        if (s != null) {
            connect(s)
        }
    }

    fun publish(
        topic: String,
        payload: String,
        qos: Int = 1,
        retained: Boolean = false,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Throwable?) -> Unit)? = null
    ) {
        val c = client
        if (c == null || !c.isConnected) {
            onFailure?.invoke(IllegalStateException("MQTT client not connected"))
            return
        }

        try {
            val message = MqttMessage(payload.toByteArray()).apply {
                this.qos = qos
                this.isRetained = retained
            }
            c.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    onSuccess?.invoke()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    onFailure?.invoke(exception)
                }
            })
        } catch (e: Exception) {
            onFailure?.invoke(e)
        }
    }
}
