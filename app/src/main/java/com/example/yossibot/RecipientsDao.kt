package com.example.yossibot

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yossibot.settings.model.Recipient
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipientsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createRecipient(recipient: Recipient)

    @Query("SELECT * FROM recipients_table")
    fun getAll(): Flow<List<Recipient>>

}