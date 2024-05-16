package cc.seaotter.tomatoes.ui.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeAnimationSource
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.seaotter.tomatoes.R
import cc.seaotter.tomatoes.data.Todo
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TodoScreen(
    navigateToCountDown: (String) -> Unit, viewModel: TodoViewModel = hiltViewModel()
) {

    val todos = viewModel.todos.collectAsStateWithLifecycle(emptyList())
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    fun hideSheet() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showBottomSheet = false
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    showBottomSheet = true
                    scope.launch { sheetState.expand() }
                },
                icon = { Icon(Icons.Filled.Edit, "Extended floating action button.") },
                text = { Text(text = stringResource(id = R.string.new_todo_button)) },
                modifier = Modifier.padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    ),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        },
    ) { innerPadding ->
        TodoBody(
            todos = todos.value,
            onTodoChange = viewModel::onTodoChange,
            navigateToCountDown = navigateToCountDown,
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                windowInsets = WindowInsets.imeAnimationSource,
                modifier = Modifier.imePadding()
            ) {
                AddTodoForm(
                    hideSheet = ::hideSheet, viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun TodoBody(
    todos: List<Todo>,
    onTodoChange: (Todo) -> Unit,
    navigateToCountDown: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    if (todos.isEmpty()) {
        ListEmpty(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = contentPadding,
        ) {
            items(todos, key = { it.id }) { todoItem ->
                TodoItem(
                    todo = todoItem,
                    navigateToCountDown = navigateToCountDown,
                    onCheckChange = {
                        val todo = todoItem.copy(completed = !todoItem.completed)
                        onTodoChange(todoItem.copy(completed = todo.completed))
                    },
                )
            }
        }
    }
}

@Composable
fun ListEmpty(
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "No items",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp),
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun AddTodoForm(
    hideSheet: () -> Unit, viewModel: TodoViewModel
) {
    val todo = remember { mutableStateOf(Todo()) }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        OutlinedTextField(singleLine = true,
            value = todo.value.title,
            onValueChange = { todo.value = todo.value.copy(title = it) },
            placeholder = { Text("title") })

        Row {
            Spacer(modifier = Modifier.weight(1f))
            Text(text = todo.value.numOfTomatoes.toString() + " tomatoes")
        }
        Slider(
            value = todo.value.numOfTomatoes.toFloat(),
            onValueChange = { todo.value = todo.value.copy(numOfTomatoes = it.toInt()) },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 8,
            valueRange = 1f..10f
        )

        // TODO: 新增類別選擇 Chips

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    hideSheet()
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ), modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    viewModel.addTodo(todo.value)
                    hideSheet()
                }, modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text("Done")
            }
        }
        Spacer(
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
@Preview
fun TodoScreenPreview() {
    TodoScreen(navigateToCountDown = {})
}