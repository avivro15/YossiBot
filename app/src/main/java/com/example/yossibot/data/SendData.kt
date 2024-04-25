package com.example.yossibot.data

import com.example.yossibot.recipients.Recipient

data class SendData(val recipients: MutableList<Recipient>, val title: String, val data: String)
