package com.example.yossibot.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yossibot.recipients.UiRecipient

@Composable
    fun RecipientsList(recipients: List<UiRecipient>, 
                       onCheckChangeListener: (Boolean, UiRecipient) -> Unit, 
                       openDialogListener: (UiRecipient?) -> Unit) {
        Column {
            LazyColumn(modifier = Modifier
                .padding(10.dp)
                .height(350.dp)) {
                items(count = recipients.size) { index ->
                    val recipient = recipients[index]

                    Log.d("aaa", recipient.recipientId.value.toString())

                    RecipientItem(
                        id = recipient.recipientId.value.toString(),
                        name = recipient.name.value,
                        isChecked = recipient.isChecked.value,
                        onCheckedChangeListener = { onCheckChangeListener(it, recipient) },
                        onLongClick = { openDialogListener(recipient) }
                        )
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
            IconButton(onClick = { openDialogListener(null) }, modifier = Modifier.align(Alignment.End) ) {
                Icon(
                    Icons.Filled.Add, contentDescription = "Add Recipient"
                )
            }
        }
    }
