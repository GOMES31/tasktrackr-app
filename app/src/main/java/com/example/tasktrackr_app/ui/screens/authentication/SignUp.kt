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
import androidx.navigation.NavController
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.components.AuthLink
import com.example.tasktrackr_app.components.CustomButton
import com.example.tasktrackr_app.components.LanguageMenu
import com.example.tasktrackr_app.components.TaskTrackrLogo
import com.example.tasktrackr_app.components.TextInputField
import com.example.tasktrackr_app.components.ToggleTheme
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import java.util.Locale

@Composable
fun SignUp(
    navController: NavController,
    modifier: Modifier = Modifier,
    onLanguageSelected: (Locale) -> Unit = {}
) {
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
            Spacer(modifier = Modifier.width(1.dp))
            LanguageMenu(onLanguageSelected = onLanguageSelected)
        }

        Spacer(modifier = Modifier.height(30.dp))

        TaskTrackrLogo(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .padding(bottom = 30.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = stringResource(R.string.sign_up),
            color = TaskTrackrTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            style = TaskTrackrTheme.typography.header,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        TextInputField(
            label = stringResource(R.string.name),
            placeholder = stringResource(R.string.name_input_placeholder),
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextInputField(
            label = stringResource(R.string.email),
            placeholder = stringResource(R.string.email_input_placeholder),
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextInputField(
            label = stringResource(R.string.password),
            placeholder = stringResource(R.string.password_input_placeholder),
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextInputField(
            label = stringResource(R.string.confirm_password),
            placeholder = stringResource(R.string.password_input_placeholder),
            modifier = Modifier
                .width(320.dp)
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        AuthLink(
            text = stringResource(R.string.sign_in_link_message),
            redirect = stringResource(R.string.sign_in),
            onClick = { navController.navigate("signin") }
        )

        Spacer(modifier = Modifier.height(30.dp))

        CustomButton(
            text = stringResource(R.string.sign_up),
            modifier = Modifier.width(200.dp)
        )
    }
}
