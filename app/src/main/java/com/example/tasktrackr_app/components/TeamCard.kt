package com.example.tasktrackr_app.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.data.remote.response.data.TeamMemberData
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.utils.LocalImageStorage

@Composable
fun TeamCard(
    modifier: Modifier = Modifier,
    name: String,
    members: List<TeamMemberData>,
    imageUrl: String? = null,
    teamId: String,
    onTeamClick: (String) -> Unit = {}
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = TaskTrackrTheme.colorScheme.cardBackground
        ),
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, TaskTrackrTheme.colorScheme.primary)
            .clickable { onTeamClick(teamId) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Team Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, TaskTrackrTheme.colorScheme.text, CircleShape)
            ) {
                if (imageUrl.isNullOrEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.default_profile),
                        contentDescription = "Team Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val imageFile = LocalImageStorage.getImageFile(context, imageUrl)
                    if (imageFile != null) {
                        AsyncImage(
                            model = imageFile,
                            contentDescription = "Team Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.default_profile),
                            fallback = painterResource(id = R.drawable.default_profile)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.default_profile),
                            contentDescription = "Team Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Team Information
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = TaskTrackrTheme.colorScheme.secondary,
                    style = TaskTrackrTheme.typography.subHeader
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Team Admin
                val admin = members.find { it.role == "ADMIN" }
                Text(
                    text = "Team Admin:",
                    color = TaskTrackrTheme.colorScheme.text,
                    style = TaskTrackrTheme.typography.bodyStrong
                )
                if (admin != null) {
                    Text(
                        text = admin.name,
                        color = TaskTrackrTheme.colorScheme.text,
                        style = TaskTrackrTheme.typography.body
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Project Managers
                val projectManagers = members.filter { it.role == "PROJECT_MANAGER" }
                if (projectManagers.isNotEmpty()) {
                    Text(
                        text = "Project Managers:",
                        color = TaskTrackrTheme.colorScheme.text,
                        style = TaskTrackrTheme.typography.bodyStrong
                    )
                    projectManagers.forEach { manager ->
                        Text(
                            text = manager.name,
                            color = TaskTrackrTheme.colorScheme.text,
                            style = TaskTrackrTheme.typography.body
                        )
                    }
                }
            }
        }
    }
}
