package com.example.tasktrackr_app.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import androidx.compose.ui.res.stringResource // Import stringResource
import com.example.tasktrackr_app.R // Import your R class

data class TaskSummaryData(
    val tasksToday: Int = 1,
    val completed: Int = 1,
    val inProgress: Int = 3,
    val notStarted: Int = 4,
    val timeSpent: String = "1h 45m",
    val averageProgress: Int = 50,
    val averageTimeSpent: String = "1h 30m"
)

@Composable
fun TaskSummary(
    modifier: Modifier = Modifier,
    summaryData: TaskSummaryData = TaskSummaryData()
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = TaskTrackrTheme.colorScheme.cardBackground
            ),
            border = BorderStroke(
                width = 1.dp,
                color = TaskTrackrTheme.colorScheme.secondary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.task_summary_title), // Using string resource
                    style = TaskTrackrTheme.typography.subHeader,
                    color = TaskTrackrTheme.colorScheme.text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 4.dp),
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = TaskTrackrTheme.colorScheme.secondary
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    SummaryRow(stringResource(R.string.summary_tasks_today_label), summaryData.tasksToday.toString())
                    SummaryRow(stringResource(R.string.summary_completed_label), summaryData.completed.toString())
                    SummaryRow(stringResource(R.string.summary_in_progress_label), summaryData.inProgress.toString())
                    SummaryRow(stringResource(R.string.summary_not_started_label), summaryData.notStarted.toString())
                    SummaryRow(stringResource(R.string.summary_time_spent_label), summaryData.timeSpent)
                    SummaryRow(stringResource(R.string.summary_average_progress_label), "${summaryData.averageProgress}%")
                    SummaryRow(stringResource(R.string.summary_average_time_spent_label), summaryData.averageTimeSpent)
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = TaskTrackrTheme.typography.bodySmall,
            color = TaskTrackrTheme.colorScheme.text,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = TaskTrackrTheme.typography.bodySmall,
            color = TaskTrackrTheme.colorScheme.text,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
    }
}