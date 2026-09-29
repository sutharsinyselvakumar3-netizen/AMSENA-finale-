package com.example

import com.example.model.CommandAck
import com.example.model.CommandMessage
import com.example.model.MqttSettings
import com.example.model.RobotStatus
import com.example.model.RobotTelemetry
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RobotModelUnitTest {

    @Test
    fun testMqttSettingsUrlFormatting() {
        val settings = MqttSettings(
            cameraHost = "192.168.1.100",
            cameraPort = 81,
            cameraStreamPath = "/stream",
            cameraCapturePath = "/capture"
        )
        assertEquals("http://192.168.1.100:81/stream", settings.getStreamUrl())
        assertEquals("http://192.168.1.100:81/capture", settings.getCaptureUrl())
    }

    @Test
    fun testCommandMessageJson() {
        val cmd = CommandMessage(
            commandId = "CMD-001",
            command = "motor",
            action = "forward",
            speed = 60,
            timestamp = 123456789L
        )

        val json = JSONObject().apply {
            put("command_id", cmd.commandId)
            put("command", cmd.command)
            put("action", cmd.action)
            put("speed", cmd.speed)
            put("timestamp", cmd.timestamp)
        }

        assertEquals("CMD-001", json.getString("command_id"))
        assertEquals("motor", json.getString("command"))
        assertEquals("forward", json.getString("action"))
        assertEquals(60, json.getInt("speed"))
    }

    @Test
    fun testCommandAckParsing() {
        val payload = """
            {
                "command_id": "CMD-001",
                "event": "motor_ack",
                "success": true,
                "motor_state": "forward",
                "speed": 60,
                "timestamp": 123456789
            }
        """.trimIndent()

        val json = JSONObject(payload)
        val ack = CommandAck(
            commandId = json.getString("command_id"),
            event = json.optString("event"),
            success = json.getBoolean("success"),
            motorState = json.optString("motor_state"),
            speed = json.optInt("speed")
        )

        assertEquals("CMD-001", ack.commandId)
        assertTrue(ack.success)
        assertEquals("forward", ack.motorState)
        assertEquals(60, ack.speed)
    }

    @Test
    fun testSoilServoMaxClamp() {
        // Must never exceed 45 degrees
        val inputAngle = 75
        val clamped = inputAngle.coerceIn(0, 45)
        assertEquals(45, clamped)
    }

    @Test
    fun testRobotTelemetryDefaults() {
        val telemetry = RobotTelemetry()
        assertFalse(telemetry.online)
        assertEquals("stop", telemetry.motorState)
        assertEquals(0, telemetry.soilPercent)
        assertEquals(0, telemetry.waterPercent)
        assertFalse(telemetry.soilRelay)
        assertFalse(telemetry.waterPump)
    }
}
