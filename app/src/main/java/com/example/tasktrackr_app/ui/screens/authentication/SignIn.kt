package com.example.tasktrackr_app.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.composables.AuthLink
import com.example.tasktrackr_app.composables.CustomButton
import com.example.tasktrackr_app.composables.LanguageMenu
import com.example.tasktrackr_app.composables.TaskTrackrLogo
import com.example.tasktrackr_app.composables.TextInputField
import com.example.tasktrackr_app.composables.ToggleTheme
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import java.util.Locale

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onLanguageSelected: (Locale) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TaskTrackrTheme.colorScheme.background)
            .padding(16.dp)
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
            text = stringResource(R.string.sign_in),
            color = TaskTrackrTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            style = TaskTrackrTheme.typography.header,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        TextInputField(
            label = stringResource(R.string.email)
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextInputField(
            label = stringResource(R.string.password)
        )

        Spacer(modifier = Modifier.height(20.dp))

        AuthLink(
            text = stringResource(R.string.sign_up_link_message),
            redirect = stringResource(R.string.sign_up)
        )

        Spacer(modifier = Modifier.height(30.dp))

        CustomButton(text = stringResource(R.string.sign_in))
    }
}
