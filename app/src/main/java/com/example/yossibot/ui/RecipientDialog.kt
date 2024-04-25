package com.example.yossibot.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.yossibot.recipients.UiRecipient

const val INVALID_RECIPIENT_ID = 0

@Composable
fun RecipientDialog(recipient: UiRecipient,
                    onConfirm: (UiRecipient) -> Unit,
                    onDelete: (UiRecipient) -> Unit,
                    onDismiss: () -> Unit) {

    val recipientIdState = rememberSaveable {
        mutableIntStateOf(recipient.recipientId.value)
    }

    val recipientNameState = rememberSaveable {
        mutableStateOf(recipient.name.value)
    }

    ShowRecipientDialog(
        recipientName = recipientNameState.value,
        recipientId = if (recipientIdState.intValue != INVALID_RECIPIENT_ID) recipientIdState.value.toString() else "",
        title = if (recipient.id == 0) "Create Recipient" else "Edit Recipient",
        isEditing = recipient.id !=0,
        onConfirm = { onConfirm(recipient.copy(name = recipientNameState, recipientId = recipientIdState)) },
        onDismiss = onDismiss,
        onIdChange = { recipientIdState.intValue = try {
            it.toInt()
        } catch (exception : NumberFormatException) {
            INVALID_RECIPIENT_ID
        }},
        onNameChange = { recipientNameState.value = it },
        onDelete = { onDelete(recipient)}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowRecipientDialog(recipientName: String,
                        recipientId: String,
                        title: String,
                        isEditing: Boolean,
                        onConfirm: () -> Unit,
                        onDismiss: () -> Unit,
                        onIdChange: (String) -> Unit,
                        onNameChange: (String) -> Unit,
                        onDelete: () -> Unit) {


    Dialog(onDismissRequest = { onDismiss() }) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .height(375.dp)
            .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium,
                    text = title
                )

                OutlinedTextField(value = recipientId,
                    onValueChange = {id -> onIdChange(id)},
                    label = { Text(text = "Recipient ID") },
                    placeholder = { Text(text = "Insert Recipient ID") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

                OutlinedTextField(value = recipientName,
                    onValueChange = {name -> onNameChange(name)},
                    label = { Text(text = "Recipient Name") },
                    placeholder = { Text(text = "Insert Recipient Name") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))

                ButtonsRow(
                    onDismiss = { onDismiss() },
                    onConfirm = { onConfirm() },
                    onDelete = { onDelete() },
                    isEditing
                )

            }
        }
    }
}

@Composable
fun ButtonsRow(onDismiss: () -> Unit, onConfirm: () -> Unit, onDelete: () -> Unit, isEditing: Boolean) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            TextButton(
                onClick = { onDismiss() },
                modifier = Modifier.padding(8.dp),
            ) {
                Text("Dismiss")
            }
            TextButton(
                onClick = { onConfirm() },
                modifier = Modifier.padding(8.dp),
            ) {
                Text("Confirm")
            }
        }
        if (isEditing) {
            IconButton(onClick = { onDelete() }, modifier = Modifier.align(Alignment.Start)) {
                Icon(
                    Icons.Filled.Delete, contentDescription = "Add Recipient"
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun PreviewDialog() {
    RecipientDialog(
        recipient = UiRecipient(
            12,
            mutableIntStateOf(1),
            mutableStateOf("asdf")
        ),
        onConfirm = {},
        onDismiss = {},
        onDelete = {}
    )
}