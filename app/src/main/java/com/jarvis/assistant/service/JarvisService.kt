package com.jarvis.assistant.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.jarvis.assistant.MainActivity
import com.jarvis.assistant.commands.CommandHandler
import com.jarvis.assistant.commands.CommandParser
import com.jarvis.assistant.voice.VoiceEngine
import kotlinx.coroutines.*

class JarvisService : Service() {
    private lateinit var voiceEngine: VoiceEngine
    private lateinit var commandParser: CommandParser
    private lateinit var commandHandler: CommandHandler
    private lateinit var hotWordDetector: HotWordDetector
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    companion object {
        private const val CHANNEL_ID = "jarvis_service"
        private const val NOTIFICATION_ID = 1001
        var isRunning = false
            private set
    }

    override fun onCreate() {
        super.onCreate()
        voiceEngine = VoiceEngine(this)
        voiceEngine.initialize()
        commandParser = CommandParser()
        commandHandler = CommandHandler(this)
        hotWordDetector = HotWordDetector(this) { onHotWordDetected() }
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("JARVIS готов"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTION_START_LISTENING" -> startListening()
            "ACTION_STOP_LISTENING" -> stopListening()
        }
        hotWordDetector.start()
        isRunning = true
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun onHotWordDetected() {
        hotWordDetector.stop()
        startListening()
    }

    private fun startListening() {
        updateNotification("Слушаю...")
        voiceEngine.startListening()
        serviceScope.launch {
            voiceEngine.lastResult.collect { text ->
                if (text.isNotBlank()) {
                    processCommand(text)
                }
            }
        }
    }

    private fun stopListening() {
        voiceEngine.stopListening()
        hotWordDetector.start()
        updateNotification("JARVIS готов")
    }

    private fun processCommand(text: String) {
        updateNotification("Обрабатываю: ${text.take(30)}...")
        val command = commandParser.parse(text)
        val response = commandHandler.execute(command)
        if (response.isNotBlank()) {
            voiceEngine.speak(response)
            updateNotification(response.take(50))
        }
        hotWordDetector.start()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "JARVIS", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun createNotification(text: String): Notification {
        val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val listenPi = PendingIntent.getService(this, 1, Intent(this, JarvisService::class.java).apply { action = "ACTION_STOP_LISTENING" }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("JARVIS").setContentText(text)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentIntent(pi)
            .addAction(android.R.drawable.ic_btn_speak_now, "Стоп", listenPi)
            .setOngoing(true).build()
    }

    private fun updateNotification(text: String) {
        getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, createNotification(text))
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        hotWordDetector.stop()
        voiceEngine.shutdown()
        isRunning = false
    }
}
