package com.example.yossibot.recipients

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "recipients_table")
data class Recipient(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @Expose val recipientsId: Int,
    @Expose val name: String
)
