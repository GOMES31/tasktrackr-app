package com.example.tasktrackr_app.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import java.util.*

@Composable
fun SideMenu(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
    navController: NavController,
    onDismiss: () -> Unit = {},
    onLanguageSelected: (Locale) -> Unit = {},
    onSignOut: () -> Unit,
) {

    if (isVisible) {
        Box(modifier = modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { onDismiss() }
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp)
                    .align(Alignment.CenterEnd)
                    .background(
                        TaskTrackrTheme.colorScheme.cardBackground,
                        RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
                    .padding(32.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.close),
                            contentDescription = "Close",
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { onDismiss() }
                                .padding(4.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.default_avatar_menu),
                            contentDescription = "Profile Icon",
                            modifier = Modifier.size(40.dp)
                                .clickable { 
                                    navController.navigate("user-profile")
                                    onDismiss()
                                }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    DividerWithSpecificPadding(alpha = 0.1f)

                    MenuItemRow(
                        text = stringResource(R.string.my_tasks),
                        iconRes = R.drawable.clipboard,
                        onClick = {
                            navController.navigate("my-tasks")
                            onDismiss()
                        }
                    )
                    DividerWithSpecificPadding()

                    MenuItemRow(
                        text = stringResource(R.string.my_projects),
                        iconRes = R.drawable.calendar,
                        onClick = {
                            navController.navigate("projects")
                            onDismiss()
                        }
                    )
                    DividerWithSpecificPadding()

                    MenuItemRow(
                        text = stringResource(R.string.teams),
                        iconRes = R.drawable.calendar,
                        onClick = {
                            onDismiss()
                            navController.navigate("user-teams")
                        }
                    )
                    DividerWithSpecificPadding()

                    MenuItemRow(
                        text = stringResource(R.string.reports),
                        iconRes = R.drawable.file_text,
                        onClick = { onDismiss() }
                    )
                    DividerWithSpecificPadding()

                    Spacer(modifier = Modifier.weight(1f))

                    DividerWithSpecificPadding()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.sign_out),
                            color = TaskTrackrTheme.colorScheme.text,
                            style = TaskTrackrTheme.typography.subHeader
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            painter = painterResource(id = R.drawable.sign_out),
                            contentDescription = "Sign Out",
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    onSignOut()
                                }
                        )
                    }

                    DividerWithSpecificPadding()

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.language),
                                color = TaskTrackrTheme.colorScheme.text,
                                style = TaskTrackrTheme.typography.label
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row {
                                Image(
                                    painter = painterResource(id = R.drawable.pt_flag),
                                    contentDescription = "Portuguese",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable {
                                            onLanguageSelected(Locale("pt", "PT"))
                                            onDismiss()
                                        }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.uk_flag),
                                    contentDescription = "English",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable {
                                            onLanguageSelected(Locale("en", "US"))
                                            onDismiss()
                                        }
                                )
                            }
                        }

                        Column {
                            Text(
                                text = stringResource(R.string.theme),
                                color = TaskTrackrTheme.colorScheme.text,
                                style = TaskTrackrTheme.typography.label
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ToggleTheme(
                                modifier = Modifier
                                    .clickable {
                                        onDismiss()
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DividerWithSpecificPadding(alpha: Float = 0.1f) {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        thickness = 1.dp,
        color = TaskTrackrTheme.colorScheme.text.copy(alpha = alpha)
    )
}

@Composable
private fun MenuItemRow(
    text: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = TaskTrackrTheme.colorScheme.text,
            style = TaskTrackrTheme.typography.subHeader
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
    }
}

