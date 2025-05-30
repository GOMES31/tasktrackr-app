package com.example.tasktrackr_app.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.*
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.UserViewModel
import com.example.tasktrackr_app.utils.LocalImageStorage
import java.util.Locale

@Composable
fun UserProfile(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: UserViewModel,
    onLanguageSelected: (Locale) -> Unit = {}
) {
    var isSideMenuVisible by remember { mutableStateOf(false) }
    val userData by viewModel.userData.collectAsState()
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TaskTrackrTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(
                onMenuClick = { isSideMenuVisible = true },
            )

            Column(
                modifier = Modifier.padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.my_profile),
                    color = TaskTrackrTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    style = TaskTrackrTheme.typography.header,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Log.d("UserProfile", "Avatar URL from userData: ${userData?.avatarUrl}")

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(2.dp, TaskTrackrTheme.colorScheme.text, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val imageFile = userData?.avatarUrl?.let { url ->
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userData?.name.orEmpty(),
                    color = TaskTrackrTheme.colorScheme.secondary,
                    style = TaskTrackrTheme.typography.header
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = userData?.email.orEmpty(),
                    color = TaskTrackrTheme.colorScheme.accent,
                    style = TaskTrackrTheme.typography.body
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomButton(
                    text = stringResource(R.string.edit_profile),
                    modifier = Modifier.width(180.dp),
                    enabled = true,
                    onClick = { navController.navigate("edit-user-profile") }
                )

                Spacer(modifier = Modifier.height(32.dp))

                HorizontalDivider(
                    thickness = 2.dp,
                    color = TaskTrackrTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.recent_activity),
                    color = TaskTrackrTheme.colorScheme.primary,
                    style = TaskTrackrTheme.typography.subHeader,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                repeat(3) {
                    ActivityCard()
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        SideMenu(
            isVisible = isSideMenuVisible,
            navController = navController,
            onDismiss = { isSideMenuVisible = false },
            onLanguageSelected = onLanguageSelected,
        )
    }
}

