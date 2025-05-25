package com.example.tasktrackr_app.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.*
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.UserViewModel
import java.util.Locale

@Composable
fun EditUserProfile(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: UserViewModel,
    onLanguageSelected: (Locale) -> Unit = {}
) {
    val userData by viewModel.userData.collectAsState()
    var name by remember { mutableStateOf(userData?.name.orEmpty()) }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var isSideMenuVisible by remember { mutableStateOf(false) }

    LaunchedEffect(userData) {
        name = userData?.name.orEmpty()
    }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { avatarUri = it }

    val formValid = name.isNotBlank() &&
            (newPassword.isEmpty() || newPassword == confirmNewPassword)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TaskTrackrTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(onMenuClick = { isSideMenuVisible = true })

        Text(
            text = stringResource(R.string.edit_profile),
            color = TaskTrackrTheme.colorScheme.primary,
            style = TaskTrackrTheme.typography.header,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        Text(
            text = stringResource(R.string.upload_avatar),
            color = TaskTrackrTheme.colorScheme.secondary,
            style = TaskTrackrTheme.typography.subHeader
        )

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val painter = if (!userData?.avatarUrl.isNullOrEmpty()) {
                rememberAsyncImagePainter(userData!!.avatarUrl)
            } else {
                painterResource(R.drawable.default_profile)
            }
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(8.dp))

        CustomButton(
            text = stringResource(R.string.upload_avatar),
            modifier = Modifier.width(200.dp),
            enabled = true,
            onClick = { pickImage.launch("image/*") }
        )

        Spacer(Modifier.height(24.dp))

        TextInputField(
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp),
            label = stringResource(R.string.name),
            value = name,
            onValueChange = { name = it }
        )

        TextInputField(
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp),
            label = stringResource(R.string.new_password),
            placeholder = stringResource(R.string.password_input_placeholder),
            value = newPassword,
            onValueChange = { newPassword = it },
            isPassword = true
        )

        TextInputField(
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp),
            label = stringResource(R.string.confirm_new_password),
            placeholder = stringResource(R.string.password_input_placeholder),
            value = confirmNewPassword,
            onValueChange = { confirmNewPassword = it },
            isPassword = true
        )

        if (newPassword.isNotEmpty() && newPassword != confirmNewPassword) {
            ErrorMessage(
                modifier = Modifier
                    .width(320.dp)
                    .padding(start = 16.dp, top = 4.dp),
                text = stringResource(R.string.error_password_mismatch)
            )
        }

        Spacer(Modifier.height(32.dp))

        CustomButton(
            text = stringResource(R.string.confirm_changes),
            enabled = formValid,
            modifier = Modifier.width(200.dp),
            onClick = {
                viewModel.updateProfile(
                    name = name,
                    password = newPassword.takeIf { it.isNotBlank() },
                    avatarUrl = avatarUri?.lastPathSegment
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
