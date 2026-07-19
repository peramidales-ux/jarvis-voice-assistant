package com.jarvis.assistant.commands

class CommandParser {

    fun parse(text: String): Command {
        val normalized = text.lowercase().trim()

        return when {
            // Phone calls
            normalized.matches(Regex("позвони\\s+(.+)|позвони\\s+в\\s+(.+)")) -> {
                val contact = normalized
                    .replace(Regex("^позвони\\s+(в\\s+)?"), "")
                    .trim()
                Command.Call(contact)
            }

            // SMS
            normalized.matches(Regex("отправь\\s+(смс|сообщение)\\s+(.+)\\s+(.+)")) -> {
                val parts = normalized
                    .replace(Regex("^отправь\\s+(смс|сообщение)\\s+"), "")
                    .split("\\s+(?:текст|содержание|сказать)\\s+".toRegex(), limit = 2)
                if (parts.size == 2) {
                    Command.SendSms(parts[0].trim(), parts[1].trim())
                } else {
                    Command.Unknown(text)
                }
            }

            // Alarms
            normalized.matches(Regex("поставь\\s+будильник\\s+на\\s+\\d{1,2}\\s*(час|:\\d{2})")) -> {
                val timePart = Regex("(\\d{1,2})(?::(\\d{2}))?").find(normalized)
                val hour = timePart?.groupValues?.get(1)?.toIntOrNull() ?: 0
                val minute = timePart?.groupValues?.get(2)?.toIntOrNull() ?: 0
                Command.SetAlarm(hour, minute, "Будильник")
            }

            // Open apps
            normalized.matches(Regex("открой\\s+(.+)")) -> {
                val app = normalized.replace(Regex("^открой\\s+"), "").trim()
                when {
                    app.contains("камер") -> Command.OpenCamera
                    app.contains("настройк") -> Command.OpenSettings
                    else -> Command.OpenApp(app)
                }
            }

            // Volume
            normalized.matches(Regex("(убавь|поставь|сделай)\\s+громкость\\s+(на\\s+)?(\\d+)")) -> {
                val level = Regex("(\\d+)").find(normalized)?.value?.toIntOrNull() ?: 50
                Command.SetVolume(level)
            }

            // Bluetooth
            normalized.contains("блютуз") || normalized.contains("bluetooth") -> Command.ToggleBluetooth

            // WiFi
            normalized.contains("вай-фай") || normalized.contains("wifi") -> Command.ToggleWifi

            // Flashlight
            normalized.contains("фонарик") || normalized.contains("свет") -> Command.ToggleFlashlight

            // Time
            normalized.contains("время") || normalized.contains("который час") -> Command.GetTime

            // Date
            normalized.contains("дата") || normalized.contains("какое сегодня число") -> Command.GetDate

            // Weather
            normalized.contains("погод") -> Command.GetWeather

            // Greetings
            normalized.contains("привет") || normalized.contains("здравствуй") ||
                normalized.contains("доброе утро") || normalized.contains("добрый день") -> Command.Greeting

            // Help
            normalized.contains("помощь") || normalized.contains("помоги") ||
                normalized.contains("что ты умеешь") -> Command.Help

            // Shutdown
            normalized.contains("выключись") || normalized.contains("стоп") -> Command.Shutdown

            // AI - default for unrecognized
            else -> Command.AskAI(text)
        }
    }
}
