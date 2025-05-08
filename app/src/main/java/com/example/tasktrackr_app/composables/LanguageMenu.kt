package com.example.tasktrackr_app.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.R
import java.util.*

@Composable
fun LanguageMenu(
    modifier: Modifier = Modifier,
    onLanguageSelected: (Locale) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {

        // Globe Icon
        Image(
            painter = painterResource(R.drawable.globe),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentSize()
        ) {
            // Portuguese
            DropdownMenuItem(
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.pt_flag),
                            contentDescription = null,
                            modifier  = Modifier
                                .width(35.dp)
                                .aspectRatio(1f)
                        )
                        Text(text = "PT")
                    }
                },
                onClick = {
                    onLanguageSelected(Locale("pt", "PT"))
                    expanded = false
                }
            )
            // English
            DropdownMenuItem(
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.uk_flag),
                            contentDescription = null,
                            modifier = Modifier
                                .width(35.dp)
                                .aspectRatio(1f)
                        )
                        Text(text = "EN")
                    }
                },
                onClick = {
                    onLanguageSelected(Locale("en", "UK"))
                    expanded = false
                }
            )
        }
    }
}
