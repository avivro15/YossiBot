package com.example.yossibot

import android.app.Application
import com.example.yossibot.recipients.RecipientsRepo

class Application : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { RecipientsRepo(database.userDao()) }
}