package com.example.yossibot.settings.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipients_table")
data class Recipient(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val recipientsId: Int,
    val firstName: String
)
