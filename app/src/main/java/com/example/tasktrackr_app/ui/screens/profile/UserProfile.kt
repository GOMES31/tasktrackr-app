package com.example.tasktrackr_app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import java.util.Locale

@Composable
fun UserProfile(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthViewModel = viewModel(),
    onLanguageSelected: (Locale) -> Unit = {}
) {
    val email = viewModel.userEmail.collectAsState().value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (email != null) {
            Text(text = "Email: $email")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "It worked!")
        } else {
            Text(text = "No user info available.")
        }
    }
}
