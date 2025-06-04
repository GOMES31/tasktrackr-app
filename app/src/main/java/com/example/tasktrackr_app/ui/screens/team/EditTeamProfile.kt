package com.example.tasktrackr_app.ui.screens.team

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.*
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.TeamViewModel
import com.example.tasktrackr_app.utils.LocalImageStorage
import com.example.tasktrackr_app.utils.NotificationHelper
import java.util.Locale

@Composable
fun EditTeamProfile(
    modifier: Modifier = Modifier,
    navController: NavController,
    teamViewModel: TeamViewModel,
    onLanguageSelected: (Locale) -> Unit = {},
    teamId: String
) {
    val teamData by teamViewModel.selectedTeam.collectAsState()
    val updateTeamSuccess by teamViewModel.updateTeamSuccess.collectAsState()
    var name by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var logoUri by remember { mutableStateOf<Uri?>(null) }
    var isSideMenuVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(teamId) {
        teamViewModel.loadTeam(teamId)
    }

    LaunchedEffect(teamData) {
        teamData?.let {
            name = it.name
            department = it.department
        }
    }

    LaunchedEffect(updateTeamSuccess) {
        if (updateTeamSuccess) {
            NotificationHelper.showNotification(context, R.string.team_edit_profile_success, true)
            teamViewModel.resetUpdateTeamSuccess()
            navController.popBackStack()
        }
    }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { logoUri = it }

    val formValid = name.isNotBlank() && department.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TaskTrackrTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(onMenuClick = { isSideMenuVisible = true })

        Text(
            text = stringResource(R.string.edit_team),
            color = TaskTrackrTheme.colorScheme.primary,
            style = TaskTrackrTheme.typography.header,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        Text(
            text = stringResource(R.string.upload_team_logo),
            color = TaskTrackrTheme.colorScheme.secondary,
            style = TaskTrackrTheme.typography.subHeader
        )

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, TaskTrackrTheme.colorScheme.text, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            when {
                logoUri != null -> {
                    AsyncImage(
                        model = logoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                !teamData?.imageUrl.isNullOrEmpty() -> {
                    val imageFile = LocalImageStorage.getImageFile(context, teamData!!.imageUrl)
                    if (imageFile != null) {
                        AsyncImage(
                            model = imageFile,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
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
                else -> {
                    Image(
                        painter = painterResource(R.drawable.default_profile),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        CustomButton(
            text = stringResource(R.string.upload_team_logo),
            modifier = Modifier.width(200.dp),
            enabled = true,
            onClick = { pickImage.launch("image/*") }
        )

        Spacer(Modifier.height(24.dp))

        TextInputField(
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp),
            label = stringResource(R.string.team_name),
            value = name,
            onValueChange = { name = it }
        )

        TextInputField(
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp),
            label = stringResource(R.string.department),
            value = department,
            onValueChange = { department = it }
        )

        Spacer(Modifier.height(32.dp))

        CustomButton(
            text = stringResource(R.string.confirm_changes),
            enabled = formValid,
            modifier = Modifier
                .width(110.dp)
                .height(60.dp),
            onClick = {
                teamViewModel.updateTeam(
                    teamId = teamId,
                    name = name,
                    department = department,
                    logoUri = logoUri
                )
                navController.popBackStack()
            }
        )
    }

    SideMenu(
        isVisible = isSideMenuVisible,
        navController = navController,
        onDismiss = { isSideMenuVisible = false },
        onLanguageSelected = onLanguageSelected
    )
}
