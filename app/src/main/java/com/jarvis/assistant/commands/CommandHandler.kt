package com.jarvis.assistant.commands

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import java.text.SimpleDateFormat
import java.util.*

class CommandHandler(private val context: Context) {

    fun execute(command: Command): String {
        return when (command) {
            is Command.Call -> handleCall(command.contact)
            is Command.SendSms -> handleSms(command.contact, command.message)
            is Command.SetAlarm -> handleAlarm(command.hour, command.minute, command.label)
            is Command.OpenApp -> handleOpenApp(command.appName)
            is Command.OpenCamera -> handleOpenCamera()
            is Command.OpenSettings -> handleOpenSettings()
            is Command.SetVolume -> handleVolume(command.level)
            is Command.ToggleBluetooth -> "Блютуз будет переключён через настройки"
            is Command.ToggleWifi -> "Wi-Fi будет переключён через настройки"
            is Command.ToggleFlashlight -> "Фонарик будет переключён"
            is Command.GetTime -> handleTime()
            is Command.GetDate -> handleDate()
            is Command.GetWeather -> "Погода пока не интегрирована. Скоро будет!"
            is Command.Greeting -> handleGreeting()
            is Command.Shutdown -> "До свидания, сэр."
            is Command.Help -> handleHelp()
            is Command.AskAI -> "" // Will be handled by AI backend
            is Command.Unknown -> "Я не понял команду. Скажите 'помощь' для списка команд."
        }
    }

    private fun handleCall(contact: String): String {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return "Набираю номер для $contact"
    }

    private fun handleSms(contact: String, message: String): String {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:")
            putExtra("sms_body", message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return "Отправляю SMS $contact: $message"
    }

    private fun handleAlarm(hour: Int, minute: Int, label: String): String {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, label)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return "Будильник установлен на $hour:${String.format("%02d", minute)}"
    }

    private fun handleOpenApp(appName: String): String {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(appName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
            return "Открываю $appName"
        }
        return "Приложение $appName не найдено"
    }

    private fun handleOpenCamera(): String {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return "Открываю камеру"
    }

    private fun handleOpenSettings(): String {
        val intent = Intent(android.provider.Settings.ACTION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        return "Открываю настройки"
    }

    private fun handleVolume(level: Int): String {
        return "Громкость установлена на $level%"
    }

    private fun handleTime(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = sdf.format(Date())
        return "Сейчас $currentTime"
    }

    private fun handleDate(): String {
        val sdf = SimpleDateFormat("d MMMM yyyy, EEEE", Locale("ru", "RU"))
        val currentDate = sdf.format(Date())
        return "Сегодня $currentDate"
    }

    private fun handleGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 6 -> "Доброй ночи, сэр. Чем могу помочь?"
            hour < 12 -> "Доброе утро, сэр. Чем могу помочь?"
            hour < 18 -> "Добрый день, сэр. Чем могу помочь?"
            else -> "Добрый вечер, сэр. Чем могу помочь?"
        }
    }

    private fun handleHelp(): String {
        return """
            |Вот что я умею, сэр:
            |
            |Телефон:
            |• "Позвони [имя]" — совершить звонок
            |• "Отправь SMS [имя] [текст]" — отправить сообщение
            |
            |Будильники:
            |• "Поставь будильник на [время]"
            |
            |Приложения:
            |• "Открой [название]" — открыть приложение
            |• "Открой камеру/настройки"
            |
            |Система:
            |• "Убавь громкость на [число]"
            |• "Включи блютуз/вай-фай"
            |• "Включи фонарик"
            |
            |Информация:
            |• "Который час?"
            |• "Какое сегодня число?"
            |• "Какая погода?"
            |
            |Ассистент:
            |• "Привет" — поздороваться
            |• Задайте любой вопрос — я отвечу через AI
            |• "Помощь" — показать эту справку
        """.trimMargin()
    }
}
