package com.jarvis.assistant.commands

sealed class Command {
    // Phone
    data class Call(val contact: String) : Command()
    data class SendSms(val contact: String, val message: String) : Command()

    // Alarms
    data class SetAlarm(val hour: Int, val minute: Int, val label: String) : Command()

    // Apps
    data class OpenApp(val appName: String) : Command()
    object OpenCamera : Command()
    object OpenSettings : Command()

    // System
    data class SetVolume(val level: Int) : Command()
    object ToggleBluetooth : Command()
    object ToggleWifi : Command()
    object ToggleFlashlight : Command()

    // AI
    data class AskAI(val question: String) : Command()

    // Info
    object GetTime : Command()
    object GetDate : Command()
    object GetWeather : Command()

    // Meta
    object Greeting : Command()
    object Shutdown : Command()
    object Help : Command()

    // Unknown
    data class Unknown(val rawText: String) : Command()
}
