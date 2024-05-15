package com.example.yossibot.recipients

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipients_table")
data class Recipient(
    @PrimaryKey(autoGenerate = true) internal val id: Int,
    val recipientsId: Int,
    val name: String
)
