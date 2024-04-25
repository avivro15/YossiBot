package com.example.yossibot.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yossibot.recipients.RecipientsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form(viewmodel: RecipientsViewModel) {
    Column(modifier = Modifier.fillMaxWidth()
        .padding(3.dp)) {
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            OutlinedTextField(value = viewmodel.title,
                onValueChange = {title -> viewmodel.updateTitle(title)},
                label = { Text(text = "Title") },
                placeholder = { Text(text = "Mail Title") }
            )
        }
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            OutlinedTextField(value = viewmodel.data,
                onValueChange = {data -> viewmodel.updateMsgData(data)},
                label = { Text(text = "Data") },
                placeholder = { Text(text = "Mail Data") })
        }
    }
}