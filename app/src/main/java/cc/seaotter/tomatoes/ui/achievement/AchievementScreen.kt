package cc.seaotter.tomatoes.ui.achievement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cc.seaotter.tomatoes.ext.formatTimeWithText
import cc.seaotter.tomatoes.ui.common.AutoResizedText
import cc.seaotter.tomatoes.ui.common.snackbar.SnackbarManager
import cc.seaotter.tomatoes.ui.common.snackbar.SnackbarMessage


@Composable
fun AchievementScreen(
    clearAndNavigate: (String) -> Unit,
    viewModel: AchievementViewModel = hiltViewModel()
) {

    AchievementScreenContent(
        uiState = viewModel.uiState,
        signOut = { viewModel.signOut(clearAndNavigate) },
        loadAchievement = { viewModel.loadAchievement(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AchievementScreenContent(
    uiState: MutableState<AchievementUiState>,
    signOut: () -> Unit,
    loadAchievement: (TimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { true }
    )

    Scaffold(
        topBar = {
            AchievementTopAppBar(
                scrollBehavior = scrollBehavior,
                signOut = signOut
            )
        },
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(
                alignment = Alignment.Top,
                space = 16.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            var selectedIndex by remember { mutableIntStateOf(0) }
            val options = TimeRange.entries.map { it.value }
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onClick = {
                            selectedIndex = index
                            uiState.value = uiState.value.copy(
                                timeRange = TimeRange.entries[index]
                            )
                            loadAchievement(TimeRange.entries[index])
                        },
                        selected = index == selectedIndex
                    ) {
                        Text(stringResource(id = label))
                    }
                }
            }
            Text(
                text = "此為功能預覽，非真實數據。敬請期待。",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            TotalTomatoesCountCard(
                totalTomatoes = uiState.value.totalTomatoes,
                totalFocusDays = uiState.value.totalFocusDays
            )
            TimeRangeCategoryCard(
                timeRange = uiState.value.timeRange,
                totalFocusTime = uiState.value.totalFocusTime,
                focusTimeInRange = uiState.value.focusTimeInRange
            )
            TimeRangeTomatoesCard(
                timeRange = uiState.value.timeRange,
                totalTomatoes = uiState.value.totalTomatoes,
                tomatoesInRange = uiState.value.tomatoesInRange
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

    }
}

@Composable
private fun TotalTomatoesCountCard(
    totalTomatoes: Int,
    totalFocusDays: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AchievementInfoRow(
            leftTitleText = "累計專注番茄數",
            leftValueText = totalTomatoes.toString(),
            rightTitleText = "累計專注天數",
            rightValueText = totalFocusDays.toString()
        )
    }
}

@Composable
private fun TimeRangeCategoryCard(
    timeRange: TimeRange,
    totalFocusTime: Long,
    focusTimeInRange: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            AchievementInfoRow(
                leftTitleText = "本" + stringResource(id = timeRange.value) + "專注",
                leftValueText = focusTimeInRange.formatTimeWithText(),
                rightTitleText = "累計專注時長",
                rightValueText = totalFocusTime.formatTimeWithText()
            )

            // TODO: 加入類別時間的長條圖
        }
    }
}

@Composable
private fun TimeRangeTomatoesCard(
    timeRange: TimeRange,
    totalTomatoes: Int,
    tomatoesInRange: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AchievementInfoRow(
            leftTitleText = "本" + stringResource(id = timeRange.value) + "專注番茄數",
            leftValueText = tomatoesInRange.toString(),
            rightTitleText = "累計專注番茄數",
            rightValueText = totalTomatoes.toString()
        )
    }
}

@Composable
private fun AchievementInfoRow(
    leftTitleText: String,
    leftValueText: String,
    rightTitleText: String,
    rightValueText: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(leftTitleText)
            AutoResizedText(
                text = leftValueText,
                style = MaterialTheme.typography.headlineLarge
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(rightTitleText)
            AutoResizedText(
                text = rightValueText,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AchievementTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    signOut: () -> Unit
) {

    MediumTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                "我的成就",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            AppBarMenu(
                signOut = signOut
            )
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun AppBarMenu(
    signOut: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    IconButton(onClick = { menuExpanded = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
    }
    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = { menuExpanded = false },
        modifier = Modifier.width(170.dp)
    ) {
        DropdownMenuItem(
            text = { Text("設定") },
            onClick = {
                menuExpanded = false
                SnackbarManager.showMessage(
                    SnackbarMessage.StringSnackbar("這項功能還在開發中，敬請期待！")
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = null
                )
            }
        )
        DropdownMenuItem(
            text = { Text("登出") },
            onClick = {
                menuExpanded = false
                signOut()
            },
            leadingIcon = {
                Icon(
                    Icons.AutoMirrored.Outlined.ExitToApp,
                    contentDescription = null
                )
            }
        )
        HorizontalDivider()
        DropdownMenuItem(
            text = { Text("意見回饋") },
            onClick = { /* Handle send feedback! */ },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Email,
                    contentDescription = null
                )
            }
        )
    }
}


@Preview
@Composable
fun PreviewAchievementScreen() {
    AchievementScreenContent(
        uiState = remember {
            mutableStateOf(
                AchievementUiState()
            )
        },
        signOut = {},
        loadAchievement = {}
    )
}

@Preview
@Composable
fun PreviewAppBarMenu() {
    AppBarMenu(
        signOut = {}
    )
}