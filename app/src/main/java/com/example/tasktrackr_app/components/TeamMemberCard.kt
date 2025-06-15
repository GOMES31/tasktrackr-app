package com.example.tasktrackr_app.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.data.remote.response.data.TeamMemberData
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.utils.LocalImageStorage

@Composable
fun TeamMemberCard(
    modifier: Modifier = Modifier,
    member: TeamMemberData,
    isAdmin: Boolean = false,
    showActions: Boolean = isAdmin,
    showRemove: Boolean = showActions,
    onEditClick: () -> Unit = {},
    onRemoveClick: () -> Unit = {},
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(TaskTrackrTheme.colorScheme.background)
            .border(1.dp, TaskTrackrTheme.colorScheme.primary)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val imageFile = member.avatarUrl?.let { url ->
                    LocalImageStorage.getImageFile(context, url)
                }

                if (imageFile != null) {
                    AsyncImage(
                        model = imageFile,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.default_profile),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column {
                Text(
                    text = member.role,
                    color = TaskTrackrTheme.colorScheme.accent,
                    style = TaskTrackrTheme.typography.caption
                )

                Text(
                    text = member.name,
                    color = TaskTrackrTheme.colorScheme.secondary,
                    style = TaskTrackrTheme.typography.smallTitles
                )

                Text(
                    text = member.email,
                    color = TaskTrackrTheme.colorScheme.text,
                    style = TaskTrackrTheme.typography.body
                )
            }
        }

        if (showActions) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.edit_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onEditClick() }
                )

                if (showRemove) {
                    Image(
                        painter = painterResource(id = R.drawable.remove_user),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onRemoveClick() }
                    )
                }
            }
        }
    }
}
