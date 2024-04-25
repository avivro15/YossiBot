package com.example.yossibot.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipientItem(
    id: String,
    name: String,
    isChecked: Boolean,
    onCheckedChangeListener: (Boolean) -> Unit,
    onLongClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12))
            .padding(all = 8.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onCheckedChangeListener(!isChecked) },
                onLongClick = { onLongClick() }
            )) {
        Text(text = id,
            style = MaterialTheme.typography.bodyMedium,
            modifier =
            Modifier
                .padding(3.dp)
                .border(1.dp, MaterialTheme.colorScheme.inversePrimary, CircleShape)
                .background(MaterialTheme.colorScheme.inversePrimary, CircleShape)
                .padding(5.dp))
        Text(text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxHeight()
                .padding(3.dp)
                .weight(2f))
        Checkbox(modifier = Modifier.weight(1f),
                checked = isChecked,
                onCheckedChange = onCheckedChangeListener)

    }
}