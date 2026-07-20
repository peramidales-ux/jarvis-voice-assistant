package com.jarvis.assistant.commands

sealed class Command {
    // Phone
    data class Call(val contact: String) : Command()
    data class SendSms(val contact: String, val message: String) : Command()
    object OpenDialer : Command()

    // Apps
    data class OpenApp(val appName: String) : Command()
    object OpenCamera : Command()
    object OpenSettings : Command()
    object OpenGallery : Command()
    object OpenCalculator : Command()
    object OpenMaps : Command()
    object OpenChrome : Command()
    object OpenPlayStore : Command()
    object OpenYouTube : Command()
    object OpenSpotify : Command()
    object OpenTelegram : Command()
    object OpenWhatsApp : Command()

    // System
    data class SetVolume(val level: Int) : Command()
    object VolumeUp : Command()
    object VolumeDown : Command()
    object Mute : Command()
    object Unmute : Command()
    object ToggleBluetooth : Command()
    object ToggleWifi : Command()
    object ToggleFlashlight : Command()
    object ToggleAirplane : Command()
    object ToggleMobileData : Command()
    object BrightnessUp : Command()
    object BrightnessDown : Command()
    object TakeScreenshot : Command()
    object LockScreen : Command()
    object PowerOff : Command()
    object Restart : Command()

    // Alarms & Calendar
    data class SetAlarm(val hour: Int, val minute: Int, val label: String) : Command()
    data class SetTimer(val minutes: Int) : Command()
    data class SetReminder(val text: String, val time: String) : Command()
    object OpenCalendar : Command()
    data class AddEvent(val title: String, val time: String) : Command()
    object ShowSchedule : Command()

    // Media
    object PlayMusic : Command()
    object PauseMusic : Command()
    object NextTrack : Command()
    object PreviousTrack : Command()
    object ShuffleMusic : Command()
    object RepeatMusic : Command()
    data class PlaySong(val query: String) : Command()

    // Information
    object GetTime : Command()
    object GetDate : Command()
    object GetWeather : Command()
    data class GetWeatherCity(val city: String) : Command()
    object GetNews : Command()
    data class WebSearch(val query: String) : Command()

    // Utilities
    data class Calculate(val expression: String) : Command()
    data class Translate(val text: String, val language: String) : Command()
    data class Define(val word: String) : Command()

    // Navigation
    data class NavigateTo(val destination: String) : Command()
    data class FindNearby(val type: String) : Command()

    // Smart Home
    object LightsOn : Command()
    object LightsOff : Command()
    data class SetTemperature(val temp: Int) : Command()
    object StartAC : Command()
    object StopAC : Command()
    object LockDoors : Command()
    object UnlockDoors : Command()

    // Security
    object TakePhoto : Command()
    object StartRecording : Command()
    object Emergency : Command()

    // AI
    data class AskAI(val question: String) : Command()

    // Meta
    object Greeting : Command()
    object Shutdown : Command()
    object Help : Command()
    object WhatsUp : Command()
    object TellJoke : Command()
    object ConfigureAI : Command()
    object ClearHistory : Command()

    // Unknown
    data class Unknown(val rawText: String) : Command()
}
