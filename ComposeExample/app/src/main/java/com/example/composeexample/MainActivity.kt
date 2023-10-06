package com.example.composeexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.composeexample.layer.Todo
import com.example.composeexample.layer.TodoRepository
import com.example.composeexample.layer.presentation.TodoListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private val todoRepository = TodoRepository()
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyToDoListTheme {
                // Initialize the ViewModel
                val todoViewModel = TodoListViewModel(todoRepository)
                // Composable for the main screen
                TodoListScreen(todoViewModel)
            }
        }
    }
}


@Composable
fun TodoListScreen(
    viewModel: TodoListViewModel,
    modifier: Modifier = Modifier
) {
    val todos by viewModel.todos.collectAsState(emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "To-Do List",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
        )

        TodoList(
            todos = todos,
            onTodoClicked = { todo ->
                viewModel.toggleTodoCompleted(todo)
            },
            onTodoDeleted = {
                    todo ->
                viewModel.deleteTodo(todo)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TodoInput(
            onAddTodoClicked = { text ->
                viewModel.addTodo(text)
            }
        )
    }
}

@Composable
fun TodoList(
    todos: List<Todo>,
    onTodoClicked: (Todo) -> Unit,
    onTodoDeleted: (Todo) -> Unit) {
    LazyColumn {
        item {
            // 첫 번째 아이템 위에 10dp 마진 추가
            Spacer(modifier = Modifier.height(20.dp))
        }
        items(todos) { todo ->
            TodoItem(todo = todo, onTodoClicked,onTodoDeleted)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoItem(todo: Todo, onTodoClicked: (Todo) -> Unit, onTodoDeleted: (Todo) -> Unit
) {
    val dismissState = rememberDismissState(confirmValueChange = { dismissValue ->
        when (dismissValue) {
            DismissValue.Default -> { // dismissThresholds 만족 안한 상태
                true
            }
            DismissValue.DismissedToEnd -> { // -> 방향 스와이프 (수정)
                // "수정" 동작을 수행하는 코드를 여기에 추가
                // 예: 수정을 위한 다이얼로그 또는 화면 전환 등을 수행
                false
            }
            DismissValue.DismissedToStart -> { // <- 방향 스와이프 (삭제)
                // "삭제" 동작을 수행하는 코드를 여기에 추가
                // 예: 아이템 삭제 로직을 호출
                CoroutineScope(Dispatchers.Main).launch {
                    delay(400)
                    onTodoDeleted(todo)
                }
                true
            }

            else -> true // 기본적으로 true 반환하여 스와이프 동작 진행
        }
    })

    val shape = RoundedCornerShape(4.dp)

    SwipeToDismiss(
        state = dismissState,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(shape),
        dismissContent = {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onTodoClicked(todo) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (todo.completed) "✅ ${todo.text}" else "❌ ${todo.text}",
                        style = TextStyle(fontSize = 18.sp)
                    )

                    Text(
                        text = "Added on: ${todo.timestamp}",
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                    )
                }
            }
        },
        background = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> Color.White
                    else -> Color.Gray
                }
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = Dp(20f)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Icon",
                    modifier = Modifier.scale(scale)
                )
            }
        }
    )
}


@Composable
fun TodoInput(onAddTodoClicked: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
            },
            textStyle = TextStyle(fontSize = 18.sp),
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
                .background(Color.White),
        )

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onAddTodoClicked("$text")
                    text = ""
                }
            }
        ) {
            Text(text = "Add")
        }
    }
}

@Composable
fun MyToDoListTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = myThemeColors,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}

private val myThemeColors = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC6)
)


@Preview
@Composable
fun TodoListScreenPreview() {
    MyToDoListTheme {
        val viewModel = TodoListViewModel(todoRepository)
        TodoListScreen(viewModel)
    }
}