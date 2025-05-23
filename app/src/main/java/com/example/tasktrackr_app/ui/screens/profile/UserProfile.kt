package com.example.tasktrackr_app.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.CustomButton
import com.example.tasktrackr_app.components.ActivityCard
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import java.util.Locale

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthViewModel = viewModel(),
    onLanguageSelected: (Locale) -> Unit = {}
) {
    val userEmail by viewModel.userEmail.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TaskTrackrTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
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

        Text(
            text = stringResource(R.string.my_profile),
            color = TaskTrackrTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            style = TaskTrackrTheme.typography.header,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            text = userEmail ?: "",
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
            thickness = 2.dp,
            color = TaskTrackrTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

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