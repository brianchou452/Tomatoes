package cc.seaotter.tomatoes.ui.common.permission

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import cc.seaotter.tomatoes.R

@Composable
fun PermissionDialog(onRequestPermission: () -> Unit) {
    var showWarningDialog by remember { mutableStateOf(true) }

    if (showWarningDialog) {
        AlertDialog(
            title = { Text(stringResource(id = R.string.notification_permission_title)) },
            text = { Text(stringResource(id = R.string.notification_permission_description)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRequestPermission()
                        showWarningDialog = false
                    },
                ) { Text(text = stringResource(R.string.request_notification_permission)) }
            },
            onDismissRequest = { }
        )
    }
}

@Composable
fun RationaleDialog() {
    var showWarningDialog by remember { mutableStateOf(true) }

    if (showWarningDialog) {
        AlertDialog(
            title = { Text(stringResource(id = R.string.notification_permission_title)) },
            text = { Text(stringResource(id = R.string.notification_permission_settings)) },
            confirmButton = {
                TextButton(
                    onClick = { showWarningDialog = false },
                ) { Text(text = stringResource(R.string.ok)) }
            },
            onDismissRequest = { showWarningDialog = false }
        )
    }
}