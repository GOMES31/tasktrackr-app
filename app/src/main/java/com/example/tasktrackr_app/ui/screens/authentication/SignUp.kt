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
fun SignUp(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthViewModel = viewModel(),
    onLanguageSelected: (Locale) -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val isFormValid = (
            name.isNotBlank() &&
                    email.isNotBlank() &&
                    password.isNotBlank() &&
                    confirmPassword.isNotBlank()
    )


    val signUpSuccess by viewModel.signUpSuccess.collectAsState()
    val errorCode by viewModel.errorCode.collectAsState()

    LaunchedEffect(signUpSuccess) {
        if (signUpSuccess) {
            navController.navigate("profile") {
                popUpTo("signup") { inclusive = true }
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
            Spacer(Modifier.width(1.dp))
            LanguageMenu(onLanguageSelected = onLanguageSelected)
        }

        Spacer(Modifier.height(30.dp))

        TaskTrackrLogo(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .padding(bottom = 30.dp)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.sign_up),
            color = TaskTrackrTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            style = TaskTrackrTheme.typography.header
        )

        Spacer(Modifier.height(30.dp))

        TextInputField(
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp),
            label = stringResource(R.string.name),
            value = name,
            onValueChange = { name = it },
            placeholder = stringResource(R.string.name_input_placeholder)
        )

        TextInputField(
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp),
            label = stringResource(R.string.email),
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(R.string.email_input_placeholder)
        )
        if (errorCode == HttpURLConnection.HTTP_CONFLICT) {
            ErrorMessage(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(start = 16.dp, bottom = 8.dp),
                text = stringResource(R.string.error_user_exists)
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

        TextInputField(
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp),
            label = stringResource(R.string.confirm_password),
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = stringResource(R.string.password_input_placeholder),
            isPassword = true
        )
        if (confirmPassword.isNotEmpty() && confirmPassword != password) {
            ErrorMessage(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(start = 16.dp, bottom = 8.dp),
                text = stringResource(R.string.error_password_mismatch)
            )
        }

        Spacer(Modifier.height(20.dp))

        AuthLink(
            text = stringResource(R.string.sign_in_link_message),
            redirect = stringResource(R.string.sign_in),
            onClick = { navController.navigate("signin") }
        )

        Spacer(Modifier.height(30.dp))


        CustomButton(
            text = stringResource(R.string.sign_up),
            modifier = Modifier.width(200.dp),
            enabled = isFormValid,
            onClick = {
                viewModel.signUp(name, email, password)
            }
        )
    }
}
