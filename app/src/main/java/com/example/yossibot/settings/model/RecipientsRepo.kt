package com.example.yossibot.settings.model

import androidx.annotation.WorkerThread
import com.example.yossibot.RecipientsDao
import kotlinx.coroutines.flow.Flow

class RecipientsRepo(private val recipientsDao: RecipientsDao) {

    val allRecipients: Flow<List<Recipient>> = recipientsDao.getAll()

    @WorkerThread
    suspend fun insertRecipient(recipient: Recipient) {
        recipientsDao.createRecipient(recipient)
    }
}