package com.example.tasktrackr_app.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.*
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import com.example.tasktrackr_app.ui.viewmodel.UserViewModel
import java.net.HttpURLConnection
import java.util.Locale

@Composable
fun SignIn(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    onLanguageSelected: (Locale) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isFormValid = email.isNotBlank() && password.isNotBlank()
    val signInSuccess by authViewModel.signInSuccess.collectAsState()
    val errorCode by authViewModel.errorCode.collectAsState()
    val userData by authViewModel.userData.collectAsState()

    LaunchedEffect(signInSuccess) {
        if (signInSuccess && userData != null) {
            userViewModel.loadProfile(userData!!)
            navController.navigate("user-profile") {
                popUpTo("signin") { inclusive = true }
            }
        }
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TaskTrackrTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToggleTheme()
            LanguageMenu(onLanguageSelected = onLanguageSelected)
        }

        Spacer(modifier = Modifier.height(30.dp))

        TaskTrackrLogo(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.sign_in),
            color = TaskTrackrTheme.colorScheme.primary,
            style = TaskTrackrTheme.typography.header,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            TextInputField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(R.string.email),
                placeholder = stringResource(R.string.email_input_placeholder)
            )
            if (errorCode == HttpURLConnection.HTTP_NOT_FOUND) {
                ErrorMessage(
                    text = stringResource(R.string.error_user_not_found)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            TextInputField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(R.string.password),
                placeholder = stringResource(R.string.password_input_placeholder),
                isPassword = true
            )
            if (errorCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                ErrorMessage(
                    text = stringResource(R.string.error_invalid_credentials)
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        AuthLink(
            text = stringResource(R.string.sign_up_link_message),
            redirect = stringResource(R.string.sign_up),
            onClick = { navController.navigate("signup") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        CustomButton(
            text = stringResource(R.string.sign_in),
            enabled = isFormValid,
            onClick = { authViewModel.signIn(email, password) },
            modifier = Modifier.fillMaxWidth(0.5f)
        )
    }
}
