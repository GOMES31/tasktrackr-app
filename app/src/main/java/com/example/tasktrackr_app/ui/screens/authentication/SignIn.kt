
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
import com.example.tasktrackr_app.components.AuthLink
import com.example.tasktrackr_app.components.CustomButton
import com.example.tasktrackr_app.components.ErrorMessage
import com.example.tasktrackr_app.components.LanguageMenu
import com.example.tasktrackr_app.components.TaskTrackrLogo
import com.example.tasktrackr_app.components.TextInputField
import com.example.tasktrackr_app.components.ToggleTheme
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.ui.viewmodel.AuthViewModel
import java.net.HttpURLConnection
import java.util.Locale


@Composable
fun SignIn(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthViewModel = viewModel(),
    onLanguageSelected: (Locale) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isFormValid = email.isNotBlank() && password.isNotBlank()
    val signInSuccess by viewModel.signInSuccess.collectAsState()
    val errorCode by viewModel.errorCode.collectAsState()

    LaunchedEffect(signInSuccess) {
        if (signInSuccess) {
            navController.navigate("profile") {
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
      ){
            ToggleTheme()
            Spacer(Modifier.width(1.dp))
            LanguageMenu(onLanguageSelected = onLanguageSelected)
        }

        Spacer(Modifier.height(30.dp))

            Spacer(modifier = Modifier.height(30.dp))

            TaskTrackrLogo(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .padding(bottom = 30.dp)
            )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.sign_in),
            color = TaskTrackrTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            style = TaskTrackrTheme.typography.header
        )

        Spacer(Modifier.height(30.dp))

        TextInputField(
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp),
            label = stringResource(R.string.email),
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(R.string.email_input_placeholder)
        )
        if (errorCode == HttpURLConnection.HTTP_NOT_FOUND) {
            ErrorMessage(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(start = 16.dp, bottom = 8.dp),
                text = stringResource(R.string.error_user_not_found)
            )
        }

        TextInputField(
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp),
            label = stringResource(R.string.password),
            value = password,
            onValueChange = { password = it },
            placeholder = stringResource(R.string.password_input_placeholder),
            isPassword = true
        )
        if (errorCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            ErrorMessage(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(start = 16.dp, bottom = 8.dp),
                text = stringResource(R.string.error_invalid_credentials)
            )
        }

        Spacer(Modifier.height(20.dp))

            AuthLink(
                text = stringResource(R.string.sign_up_link_message),
                redirect = stringResource(R.string.sign_up),
                onClick = { navController.navigate("signup") }
            )

        Spacer(Modifier.height(30.dp))

        CustomButton(
            modifier = Modifier.width(200.dp),
            text = stringResource(R.string.sign_in),
            enabled = isFormValid,
            onClick = {
                viewModel.signIn(email, password)
                 }
        )
    }
}
