package com.example.tasktrackr_app.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tasktrackr_app.R
import com.example.tasktrackr_app.data.remote.response.data.TaskData
import com.example.tasktrackr_app.ui.theme.TaskTrackrTheme
import com.example.tasktrackr_app.utils.DateUtils

data class TaskSummaryData(
    val inProgress: Int,
    val pending: Int,
    val finished: Int,
    val totalTimeSpent: String,
    val averageTimeSpent: String
)

@Composable
fun TaskSummary(
    modifier: Modifier = Modifier,
    tasks: List<TaskData>
) {
    val summaryData = remember(tasks) {
        calculateTaskSummary(tasks)
    }

    Column(modifier = modifier.fillMaxWidth()) {
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
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.task_summary_title),
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
                    SummaryRow(stringResource(R.string.summary_in_progress_label), summaryData.inProgress.toString())
                    SummaryRow(stringResource(R.string.summary_pending_label), summaryData.pending.toString())
                    SummaryRow(stringResource(R.string.summary_finished_label), summaryData.finished.toString())
                    SummaryRow(stringResource(R.string.summary_time_spent_label), summaryData.totalTimeSpent)
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

fun calculateTaskSummary(tasks: List<TaskData>): TaskSummaryData {
    val inProgressCount = tasks.count { it.status == "IN_PROGRESS" }
    val pendingCount = tasks.count { it.status == "PENDING" }
    val finishedCount = tasks.count { it.status == "FINISHED" }

    val totalTimeInMinutes = tasks.sumOf {
        DateUtils.calculateTimeSpentInMinutes(it.startDate, it.endDate)
    }
    val totalTimeFormatted = DateUtils.formatMinutesToHm(totalTimeInMinutes)

    val taskCountWithTime = tasks.count {
        it.startDate != null && it.endDate != null
    }
    val averageTimeInMinutes = if (taskCountWithTime > 0) {
        totalTimeInMinutes / taskCountWithTime
    } else {
        0L
    }
    val averageTimeFormatted = DateUtils.formatMinutesToHm(averageTimeInMinutes)

    return TaskSummaryData(
        inProgress = inProgressCount,
        pending = pendingCount,
        finished = finishedCount,
        totalTimeSpent = totalTimeFormatted,
        averageTimeSpent = averageTimeFormatted
    )
}
