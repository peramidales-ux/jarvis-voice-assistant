package com.jarvis.assistant.commands

class CommandParser {
    fun parse(text: String): Command {
        val t = text.lowercase().trim()
        return when {
            // Phone
            t.contains(Regex("позвони|позвонить|набери")) -> {
                val contact = t.replace(Regex("^(позвони|позвонить|набери)\\s*(в\\s+)?"), "").trim()
                Command.Call(contact)
            }
            t.contains(Regex("отправь\\s+(смс|сообщение)")) -> {
                val parts = t.replace(Regex("^отправь\\s+(смс|сообщение)\\s+"), "").split(Regex("\\s+(текст|сказать)\\s+"), limit = 2)
                if (parts.size == 2) Command.SendSms(parts[0].trim(), parts[1].trim()) else Command.Unknown(text)
            }
            t.contains(Regex("открыть\\s+телефон|набрать номер")) -> Command.OpenDialer

            // Apps
            t.contains(Regex("открой\\s+камер")) -> Command.OpenCamera
            t.contains(Regex("открой\\s+настройк")) -> Command.OpenSettings
            t.contains(Regex("открой\\s+галере")) -> Command.OpenGallery
            t.contains(Regex("открой\\s+калькулятор|калькулятор")) -> Command.OpenCalculator
            t.contains(Regex("открой\\s+карт|навигац")) -> Command.OpenMaps
            t.contains(Regex("открой\\s+хром|браузер")) -> Command.OpenChrome
            t.contains(Regex("открой\\s+плей\\s+маркет")) -> Command.OpenPlayStore
            t.contains(Regex("открой\\s+ютуб|youtube")) -> Command.OpenYouTube
            t.contains(Regex("открой\\s+спотифай|spotify")) -> Command.OpenSpotify
            t.contains(Regex("открой\\s+телеграм|telegram")) -> Command.OpenTelegram
            t.contains(Regex("открой\\s+ватсап|whatsapp")) -> Command.OpenWhatsApp
            t.matches(Regex("открой\\s+.+")) -> Command.OpenApp(t.replace(Regex("^открой\\s+"), "").trim())

            // Volume
            t.contains(Regex("убавь\\s+громкость|тише|громкость\\s+минус")) -> Command.VolumeDown
            t.contains(Regex("прибавь\\s+громкость|громче|громкость\\s+плюс")) -> Command.VolumeUp
            t.contains(Regex("без\\s+звука|отключи\\s+звук|mute")) -> Command.Mute
            t.contains(Regex("включи\\s+звук|unmute")) -> Command.Unmute
            t.contains(Regex("(убавь|поставь)\\s+громкость\\s+(на\\s+)?(\\d+)")) -> {
                Command.SetVolume(Regex("(\\d+)").find(t)?.value?.toIntOrNull() ?: 50)
            }

            // System toggles
            t.contains(Regex("блютуз|bluetooth")) -> Command.ToggleBluetooth
            t.contains(Regex("вай-фай|wifi|wi-fi")) -> Command.ToggleWifi
            t.contains(Regex("фонарик|свет|фар")) -> Command.ToggleFlashlight
            t.contains(Regex("авиарежим|самолет|airplane")) -> Command.ToggleAirplane
            t.contains(Regex("мобильный\\s+интернет|дата")) -> Command.ToggleMobileData
            t.contains(Regex("яркость\\s+больше|ярче")) -> Command.BrightnessUp
            t.contains(Regex("яркость\\s+меньше|тусклее")) -> Command.BrightnessDown
            t.contains(Regex("скриншот|снимок\\s+экрана")) -> Command.TakeScreenshot
            t.contains(Regex("заблокируй|блокировка")) -> Command.LockScreen
            t.contains(Regex("выключи\\s+телефон")) -> Command.PowerOff
            t.contains(Regex("перезагрузи|рестарт")) -> Command.Restart

            // Alarms
            t.contains(Regex("поставь\\s+будильник\\s+на\\s+(\\d{1,2})")) -> {
                val time = Regex("(\\d{1,2})(?::(\\d{2}))?").find(t)
                Command.SetAlarm(time?.groupValues?.get(1)?.toIntOrNull() ?: 7, time?.groupValues?.get(2)?.toIntOrNull() ?: 0, "Будильник")
            }
            t.contains(Regex("таймер\\s+на\\s+(\\d+)\\s+минут")) -> {
                Command.SetTimer(Regex("(\\d+)").find(t.replace("таймер", ""))?.value?.toIntOrNull() ?: 5)
            }
            t.contains(Regex("напомни|напоминание")) -> Command.SetReminder(t.replace(Regex("^(напомни|напоминание)\\s+"), ""), "сейчас")
            t.contains(Regex("открой\\s+календарь|расписание")) -> Command.OpenCalendar
            t.contains(Regex("что\\s+в\\s+расписании")) -> Command.ShowSchedule

            // Media
            t.contains(Regex("включи\\s+музыку|играй\\s+музыку")) -> Command.PlayMusic
            t.contains(Regex("останови\\s+музыку|пауза|pause")) -> Command.PauseMusic
            t.contains(Regex("следующий\\s+трек|next|дальше")) -> Command.NextTrack
            t.contains(Regex("предыдущий\\s+трек|previous|назад")) -> Command.PreviousTrack
            t.contains(Regex("перемешать|shuffle")) -> Command.ShuffleMusic
            t.contains(Regex("повторять|repeat")) -> Command.RepeatMusic
            t.contains(Regex("включи\\s+(песню|трек)\\s+(.+)")) -> {
                Command.PlaySong(t.replace(Regex("^включи\\s+(песню|трек)\\s+"), "").trim())
            }

            // Information
            t.contains(Regex("время|который\\s+час|сколько\\s+времени")) -> Command.GetTime
            t.contains(Regex("дата|какое\\s+сегодня\\s+число")) -> Command.GetDate
            t.contains(Regex("погода\\s+в\\s+(.+)")) -> Command.GetWeatherCity(t.replace(Regex("^погода\\s+в\\s+"), "").trim())
            t.contains(Regex("погод|прогноз")) -> Command.GetWeather
            t.contains(Regex("новост|что\\s+происходит")) -> Command.GetNews
            t.contains(Regex("найди\\s+в\\s+интернете|поищи|search")) -> {
                Command.WebSearch(t.replace(Regex("^(найди\\s+в\\s+интернете|поищи|search)\\s+"), "").trim())
            }

            // Utilities
            t.contains(Regex("сколько\\s+будет|посчитай|\\d+\\s*[+\\-*/]\\s*\\d+")) -> {
                Command.Calculate(t.replace(Regex("^(сколько\\s+будет|посчитай)\\s+"), "").trim())
            }
            t.contains(Regex("переведи\\s+(на\\s+)?(.+)\\s+на\\s+(.+)")) -> {
                val parts = t.split(Regex("\\s+на\\s+"), limit = 2)
                Command.Translate(parts.getOrElse(1) { "" }, parts.getOrElse(2) { "english" })
            }
            t.contains(Regex("что\\s+значит|определение")) -> {
                Command.Define(t.replace(Regex("^(что\\s+значит|определение)\\s+"), "").trim())
            }

            // Navigation
            t.contains(Regex("отвези\\s+в|направляйся\\s+в|navigate")) -> {
                Command.NavigateTo(t.replace(Regex("^(отвези\\s+в|направляйся\\s+в|navigate)\\s+"), "").trim())
            }
            t.contains(Regex("найди\\s+рядом|где\\s+ближайшее")) -> {
                Command.FindNearby(t.replace(Regex("^(найди\\s+рядом|где\\s+ближайшее)\\s+"), "").trim())
            }

            // Smart Home
            t.contains(Regex("включи\\s+свет|свет\\s+включи")) -> Command.LightsOn
            t.contains(Regex("выключи\\s+свет|свет\\s+выключи")) -> Command.LightsOff
            t.contains(Regex("установи\\s+температуру\\s+на\\s+(\\d+)")) -> {
                Command.SetTemperature(Regex("(\\d+)").find(t)?.value?.toIntOrNull() ?: 22)
            }
            t.contains(Regex("включи\\s+кондиционер")) -> Command.StartAC
            t.contains(Regex("выключи\\s+кондиционер")) -> Command.StopAC
            t.contains(Regex("запри\\s+дверь")) -> Command.LockDoors
            t.contains(Regex("отопри\\s+дверь")) -> Command.UnlockDoors

            // Security
            t.contains(Regex("фото|сними\\s+фото")) -> Command.TakePhoto
            t.contains(Regex("запись\\s+видео|запиши\\s+видео")) -> Command.StartRecording
            t.contains(Regex("помоги|экстренно|sos")) -> Command.Emergency

            // Greetings
            t.contains(Regex("привет|здравствуй|доброе\\s+утро|добрый\\s+день|добрый\\s+вечер")) -> Command.Greeting

            // Help
            t.contains(Regex("помощь|помоги|что\\s+ты\\+умеешь")) -> Command.Help

            // Meta
            t.contains(Regex("настрой\\s+ai|задай\\s+ключ")) -> Command.ConfigureAI
            t.contains(Regex("очисти\\s+историю|забудь\\s+все")) -> Command.ClearHistory
            t.contains(Regex("расскажи\\s+анекдот|шутка")) -> Command.TellJoke
            t.contains(Regex("что\\s+нового|как\\s+дела")) -> Command.WhatsUp

            // AI - default
            else -> Command.AskAI(text)
        }
    }
}
