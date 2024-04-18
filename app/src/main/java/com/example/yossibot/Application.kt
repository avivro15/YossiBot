package com.example.yossibot

import android.app.Application
import com.example.yossibot.settings.model.RecipientsRepo

class Application : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { RecipientsRepo(database.userDao()) }
}