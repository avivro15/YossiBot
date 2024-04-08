package com.example.yossibot.data

data class SendData(val recipients: MutableList<String>,val title: String, val data: String,
                    val filesName: MutableList<String>? = null)
