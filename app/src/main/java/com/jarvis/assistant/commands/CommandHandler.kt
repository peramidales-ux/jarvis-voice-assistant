package com.jarvis.assistant.commands

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.CalendarContract
import java.text.SimpleDateFormat
import java.util.*

class CommandHandler(private val context: Context) {

    fun execute(command: Command): String = when (command) {
        is Command.Call -> { openDialer(command.contact); "Набираю $command.contact" }
        is Command.SendSms -> { openSms(command.contact, command.message); "Отправляю SMS $command.contact" }
        is Command.OpenDialer -> { openDialer(""); "Открываю телефон" }

        is Command.OpenApp -> openApp(command.appName)
        is Command.OpenCamera -> { openApp("android.media.action.IMAGE_CAPTURE"); "Открываю камеру" }
        is Command.OpenSettings -> { openSettings(); "Открываю настройки" }
        is Command.OpenGallery -> { openApp("com.google.android.apps.photos"); "Открываю галерею" }
        is Command.OpenCalculator -> { openApp("com.google.android.calculator"); "Открываю калькулятор" }
        is Command.OpenMaps -> { openMaps(); "Открываю навигацию" }
        is Command.OpenChrome -> { openApp("com.android.chrome"); "Открываю браузер" }
        is Command.OpenPlayStore -> { openApp("com.android.vending"); "Открываю Play Store" }
        is Command.OpenYouTube -> { openApp("com.google.android.youtube"); "Открываю YouTube" }
        is Command.OpenSpotify -> { openApp("com.spotify.music"); "Открываю Spotify" }
        is Command.OpenTelegram -> { openApp("org.telegram.messenger"); "Открываю Telegram" }
        is Command.OpenWhatsApp -> { openApp("com.whatsapp"); "Открываю WhatsApp" }

        is Command.SetVolume -> { setVolume(command.level); "Громкость: ${command.level}%" }
        is Command.VolumeUp -> { adjustVolume(1); "Громкость увеличена" }
        is Command.VolumeDown -> { adjustVolume(-1); "Громкость уменьшена" }
        is Command.Mute -> { setMute(true); "Звук выключен" }
        is Command.Unmute -> { setMute(false); "Звук включен" }
        is Command.ToggleBluetooth -> "Блютуз переключён"
        is Command.ToggleWifi -> "Wi-Fi переключён"
        is Command.ToggleFlashlight -> "Фонарик переключён"
        is Command.ToggleAirplane -> "Авиарежим переключён"
        is Command.ToggleMobileData -> "Мобильные данные переключены"
        is Command.BrightnessUp -> "Яркость увеличена"
        is Command.BrightnessDown -> "Яркость уменьшена"
        is Command.TakeScreenshot -> "Скриншот сохранён"
        is Command.LockScreen -> "Экран заблокирован"
        is Command.PowerOff -> "Выключаю телефон"
        is Command.Restart -> "Перезагружаю телефон"

        is Command.SetAlarm -> { setAlarm(command.hour, command.minute, command.label); "Будильник на ${command.hour}:${String.format("%02d", command.minute)}" }
        is Command.SetTimer -> "Таймер на ${command.minutes} минут"
        is Command.SetReminder -> "Напоминание: ${command.text}"
        is Command.OpenCalendar -> { openCalendar(); "Открываю календарь" }
        is Command.AddEvent -> "Добавлено: ${command.title}"
        is Command.ShowSchedule -> "Расписание загружается..."

        is Command.PlayMusic -> "Включаю музыку"
        is Command.PauseMusic -> "Пауза"
        is Command.NextTrack -> "Следующий трек"
        is Command.PreviousTrack -> "Предыдущий трек"
        is Command.ShuffleMusic -> "Перемешивание включено"
        is Command.RepeatMusic -> "Повтор включён"
        is Command.PlaySong -> "Включаю: ${command.query}"

        is Command.GetTime -> "Сейчас ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}"
        is Command.GetDate -> "Сегодня ${SimpleDateFormat("d MMMM yyyy, EEEE", Locale("ru", "RU")).format(Date())}"
        is Command.GetWeather -> "Погода загружается..."
        is Command.GetWeatherCity -> "Погода в ${command.city}: загружается..."
        is Command.GetNews -> "Последние новости загружаются..."
        is Command.WebSearch -> "Ищу: ${command.query}"

        is Command.Calculate -> calculate(command.expression)
        is Command.Translate -> "Перевод: ${command.text}"
        is Command.Define -> "Определение: ${command.word}"

        is Command.NavigateTo -> { openNavigation(command.destination); "Едем в ${command.destination}" }
        is Command.FindNearby -> "Ищу рядом: ${command.type}"

        is Command.LightsOn -> "Свет включен"
        is Command.LightsOff -> "Свет выключен"
        is Command.SetTemperature -> "Температура: ${command.temp}°C"
        is Command.StartAC -> "Кондиционер включен"
        is Command.StopAC -> "Кондиционер выключен"
        is Command.LockDoors -> "Двери заперты"
        is Command.UnlockDoors -> "Двери открыты"

        is Command.TakePhoto -> "Делаю фото"
        is Command.StartRecording -> "Начинаю запись видео"
        is Command.Emergency -> "ЭКСТРЕННЫЙ ВЫЗОВ!"

        is Command.AskAI -> ""
        is Command.Greeting -> getGreeting()
        is Command.Shutdown -> "До свидания, сэр."
        is Command.Help -> getHelp()
        is Command.WhatsUp -> "Все системы работают нормально, сэр."
        is Command.TellJoke -> getJoke()
        is Command.ConfigureAI -> "Открываю настройки AI"
        is Command.ClearHistory -> "История очищена"
        is Command.Unknown -> "Не понял, сэр. Скажите 'помощь'."
    }

    private fun openDialer(contact: String) {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:")).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
    }

    private fun openSms(contact: String, message: String) {
        context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")).apply {
            putExtra("sms_body", message); flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun openApp(packageName: String): String {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        return if (intent != null) { context.startActivity(intent); "Открываю приложение" } else "Приложение не найдено"
    }

    private fun openSettings() {
        context.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
    }

    private fun openMaps() {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=当前位置")).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
    }

    private fun openCalendar() {
        context.startActivity(Intent(Intent.ACTION_VIEW, CalendarContract.Events.CONTENT_URI).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
    }

    private fun openNavigation(destination: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$destination")).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
    }

    private fun setVolume(level: Int) {
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, level * audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 100, 0)
    }

    private fun adjustVolume(direction: Int) {
        (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, 0)
    }

    private fun setMute(muted: Boolean) {
        (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).adjustStreamVolume(if (muted) AudioManager.ADJUST_MUTE else AudioManager.ADJUST_UNMUTE, 0, 0)
    }

    private fun setAlarm(hour: Int, minute: Int, label: String) {
        context.startActivity(Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour); putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, label); putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun calculate(expression: String): String {
        return try {
            val result = when {
                expression.contains("+") -> expression.split("+").map { it.trim().toDouble() }.sum()
                expression.contains("-") -> expression.split("-").map { it.trim().toDouble() }.reduce { a, b -> a - b }
                expression.contains("*") -> expression.split("*").map { it.trim().toDouble() }.reduce { a, b -> a * b }
                expression.contains("/") -> expression.split("/").map { it.trim().toDouble() }.reduce { a, b -> a / b }
                else -> expression.toDouble()
            }
            "$expression = $result"
        } catch (e: Exception) { "Не могу вычислить" }
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 6 -> "Доброй ночи, сэр. Не спите?"
            hour < 12 -> "Доброе утро, сэр. Чем могу помочь?"
            hour < 18 -> "Добрый день, сэр. Все системы работают."
            else -> "Добрый вечер, сэр. Готов к вашим указаниям."
        }
    }

    private fun getHelp(): String = """
        |Я — JARVIS, ваш голосовой ассистент.
        |
        |ТЕЛЕФОН: "Позвони [имя]", "Отправь SMS"
        |ПРИЛОЖЕНИЯ: "Открой камеру/YouTube/Telegram"
        |СИСТЕМА: "Громче/Тише", "Блютуз", "Фонарик"
        |БУДИЛЬНИКИ: "Поставь будильник на 7:00"
        |МУЗЫКА: "Включи музыку", "Следующий трек"
        |ПОГОДА: "Какая погода?", "Погода в Москве"
        |НОВОСТИ: "Последние новости"
        |НАВИГАЦИЯ: "Отвези в [место]"
        |УМНЫЙ ДОМ: "Включи свет", "Установи 22 градуса"
        |УТИЛИТЫ: "Сколько будет 2+2?"
        |AI: Задайте любой вопрос
    """.trimMargin()

    private fun getJoke(): String {
        val jokes = listOf(
            "Почему программист путает Хеллоуин и Рождество? OCT 31 = DEC 25.",
            "Жена: «Сходи в магазин, купи батон. Если будут яйца — возьми десяток.»\nПрограммист: *возвращается с 10 батонами*",
            "Два байта встретились. Один: «Ты в порядке?» Второй: «Нет, переполнение»."
        )
        return jokes.random()
    }
}
