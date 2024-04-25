package com.example.yossibot.recipients

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipientsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createRecipient(recipient: Recipient)

    @Delete
    suspend fun deleteRecipient(recipient: Recipient)

    @Query("SELECT * FROM recipients_table")
    fun getAll(): Flow<List<Recipient>>

}