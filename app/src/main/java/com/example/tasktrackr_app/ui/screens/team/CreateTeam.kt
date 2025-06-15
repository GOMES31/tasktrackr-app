package com.example.tasktrackr_app.ui.screens.team

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.tasktrackr_app.components.CustomButton
import com.example.tasktrackr_app.components.SideMenu
import com.example.tasktrackr_app.components.TextInputField
import com.example.tasktrackr_app.components.TopBar
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.TeamViewModel
import com.example.tasktrackr_app.utils.NotificationHelper
import java.util.Locale

@Composable
fun CreateTeam(
    modifier: Modifier = Modifier,
    teamViewModel: TeamViewModel,
    navController: NavController,
    authViewModel: AuthViewModel,
    onLanguageSelected: (Locale) -> Unit = {}
) {
    var teamName by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var isSideMenuVisible by remember { mutableStateOf(false) }
    var logoUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val createTeamSuccess by teamViewModel.createTeamSuccess.collectAsState()

    LaunchedEffect(createTeamSuccess) {
        if (createTeamSuccess) {
            NotificationHelper.showNotification(context, R.string.create_team_success, true)
            teamViewModel.resetCreateTeamSuccess()
            navController.popBackStack()
        }
    }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { logoUri = it }

    val formValid = teamName.isNotBlank() && department.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TaskTrackrTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(onMenuClick = { isSideMenuVisible = true })

        Text(
            text = stringResource(R.string.create_team),
            color = TaskTrackrTheme.colorScheme.primary,
            style = TaskTrackrTheme.typography.header,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        Text(
            text = stringResource(R.string.upload_team_logo),
            color = TaskTrackrTheme.colorScheme.secondary,
            style = TaskTrackrTheme.typography.subHeader,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(8.dp))

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

        Spacer(Modifier.height(8.dp))

        CustomButton(
            text = stringResource(R.string.upload_team_logo),
            modifier = Modifier.width(200.dp),
            enabled = true,
            onClick = { pickImage.launch("image/*") }
        )

        Spacer(Modifier.height(32.dp))

        TextInputField(
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp),
            label = stringResource(R.string.team_name),
            value = teamName,
            onValueChange = { teamName = it },
            placeholder = stringResource(R.string.team_name_placeholder)
        )

        TextInputField(
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp),
            label = stringResource(R.string.department),
            value = department,
            onValueChange = { department = it },
            placeholder = stringResource(R.string.department_placeholder)
        )

        Spacer(Modifier.height(32.dp))

        CustomButton(
            text = stringResource(R.string.create_team),
            enabled = formValid,
            modifier = Modifier.width(200.dp),
            onClick = {
                teamViewModel.createTeam(teamName, department, logoUri)
            }
        )
    }

    SideMenu(
        isVisible = isSideMenuVisible,
        navController = navController,
        onDismiss = { isSideMenuVisible = false },
        onLanguageSelected = onLanguageSelected,
        onSignOut = {
            authViewModel.signOut {
                isSideMenuVisible = false
                navController.navigate("signin") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    )
}
