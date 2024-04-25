package com.example.yossibot.recipients

import androidx.annotation.WorkerThread

class RecipientsRepo(private val recipientsDao: RecipientsDao) {

    fun getRecipients() = recipientsDao.getAll()

    @WorkerThread
    suspend fun insertRecipient(recipient: Recipient) {
        recipientsDao.createRecipient(recipient)
    }

    @WorkerThread
    suspend fun deleteRecipient(recipient: Recipient) {
        recipientsDao.deleteRecipient(recipient)
    }
}