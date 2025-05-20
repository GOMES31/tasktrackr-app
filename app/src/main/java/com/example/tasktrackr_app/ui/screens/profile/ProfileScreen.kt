package com.example.tasktrackr_app.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.CustomButton
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TaskTrackrTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_lightmode),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(40.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.menu_burger),
                contentDescription = "Menu",
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = stringResource(R.string.my_profile),
            color = TaskTrackrTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            style = TaskTrackrTheme.typography.header,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Image
        Image(
            painter = painterResource(id = R.drawable.default_profile),
            contentDescription = stringResource(R.string.my_profile),
            modifier = Modifier
                .size(90.dp)
                .border(2.dp, TaskTrackrTheme.colorScheme.text, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "John Doe",
            color = TaskTrackrTheme.colorScheme.secondary,
            style = TaskTrackrTheme.typography.bodyStrong
        )

        Text(
            text = "johndoe@gmail.com",
            color = TaskTrackrTheme.colorScheme.accent,
            style = TaskTrackrTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomButton(
            text = stringResource(R.string.edit_profile),
            modifier = Modifier.width(180.dp),
            onClick = { }
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(
            color = TaskTrackrTheme.colorScheme.primary,
            thickness = 2.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Activity Title (Left Aligned)
        Text(
            text = stringResource(R.string.recent_activity),
            color = TaskTrackrTheme.colorScheme.primary,
            style = TaskTrackrTheme.typography.subHeader,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        repeat(3) {
            ActivityCard()
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ActivityCard() {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, TaskTrackrTheme.colorScheme.secondary),
        colors = CardDefaults.cardColors(
            containerColor = TaskTrackrTheme.colorScheme.cardBackground
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = TaskTrackrTheme.colorScheme.text,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Top)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Task Name",
                    color = TaskTrackrTheme.colorScheme.secondary,
                    style = TaskTrackrTheme.typography.bodyStrong
                )
                Text(
                    text = "Description of what was done. Ex: change the finish date of the task, added comments to a task etc.",
                    color = TaskTrackrTheme.colorScheme.text,
                    style = TaskTrackrTheme.typography.bodySmall
                )
            }
        }
    }
}
