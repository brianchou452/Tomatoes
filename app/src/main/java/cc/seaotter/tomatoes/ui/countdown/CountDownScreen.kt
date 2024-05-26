package cc.seaotter.tomatoes.ui.countdown


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cc.seaotter.tomatoes.R
import cc.seaotter.tomatoes.ext.formatTime
import cc.seaotter.tomatoes.ui.common.PrimaryButton
import cc.seaotter.tomatoes.ui.common.SecondaryButton


@Composable
fun CountDownScreen(
    modifier: Modifier = Modifier,
    popUpCountDown: () -> Unit, viewModel: CountDownViewModel = hiltViewModel()
) {
    val timerValue by viewModel.timer.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            IconButton(
                onClick = {
                    popUpCountDown()
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Close, contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (viewModel.uiState.value.screenState) {
                CountDownScreenState.BEGIN_TOMATO, CountDownScreenState.IN_TOMATO -> {
                    CountDownSubTitle(viewModel.uiState.value.todo.title)
                    CountDownTitle(title = timerValue.formatTime())
                }

                CountDownScreenState.IN_SHORT_BREAK -> {
                    CountDownSubTitle(stringResource(id = R.string.label_short_break))
                    CountDownTitle(title = timerValue.formatTime())
                }

                CountDownScreenState.IN_LONG_BREAK -> {
                    CountDownSubTitle(stringResource(id = R.string.label_long_break))
                    CountDownTitle(title = timerValue.formatTime())
                }

                CountDownScreenState.END_TOMATO_BEGIN_SHORT_BREAK, CountDownScreenState.END_TOMATO_BEGIN_LONG_BREAK -> {
                    CountDownTitle(title = stringResource(id = R.string.label_tomato_finished))
                }

                CountDownScreenState.END_BREAK -> {
                    CountDownTitle(title = stringResource(id = R.string.label_break_finished))
                }
            }
        }

        CountDownActionButton(viewModel.uiState.value.screenState,
            startTimer = { viewModel.startTimer() },
            cancelTimer = { viewModel.cancelTimer() })
    }
}

@Composable
fun CountDownSubTitle(
    subTitle: String
) {
    Text(
        text = subTitle, style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun CountDownTitle(
    title: String
) {
    Text(
        text = title, style = MaterialTheme.typography.displayLarge
    )
}


@Composable
fun CountDownActionButton(
    state: CountDownScreenState, startTimer: () -> Unit, cancelTimer: () -> Unit
) {
    val modifier = Modifier
        .padding(32.dp)
        .width(200.dp)
    when (state) {
        CountDownScreenState.BEGIN_TOMATO -> {
            PrimaryButton(
                onClick = {
                    startTimer()
                }, modifier = modifier
            ) {
                Text(text = stringResource(id = R.string.btn_start_timer))
            }
        }

        CountDownScreenState.IN_TOMATO -> {
            SecondaryButton(
                onClick = {
                    cancelTimer()
                }, modifier = modifier
            ) {
                Text(text = stringResource(id = R.string.btn_cancel))
            }
        }

        CountDownScreenState.END_TOMATO_BEGIN_SHORT_BREAK -> {
            PrimaryButton(
                onClick = {
                    startTimer()
                }, modifier = modifier
            ) {
                Text(text = stringResource(id = R.string.btn_begin_short_break))
            }
        }

        CountDownScreenState.END_TOMATO_BEGIN_LONG_BREAK -> {
            PrimaryButton(
                onClick = {
                    startTimer()
                }, modifier = modifier
            ) {
                Text(text = stringResource(id = R.string.btn_begin_long_break))
            }
        }

        CountDownScreenState.IN_SHORT_BREAK -> {
            SecondaryButton(
                onClick = {
                    cancelTimer()
                }, modifier = modifier
            ) {
                Text(text = stringResource(id = R.string.btn_end_short_break))
            }
        }

        CountDownScreenState.IN_LONG_BREAK -> {
            SecondaryButton(
                onClick = {
                    cancelTimer()
                }, modifier = modifier
            ) {
                Text(text = stringResource(id = R.string.btn_end_long_break))
            }
        }

        CountDownScreenState.END_BREAK -> {
            PrimaryButton(
                onClick = {
                    startTimer()
                }, modifier = modifier
            ) {
                Text(text = stringResource(id = R.string.btn_continue_fighting))
            }
        }

    }
}
