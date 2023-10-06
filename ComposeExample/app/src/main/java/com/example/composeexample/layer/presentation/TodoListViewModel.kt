package com.example.composeexample.layer.presentation

import androidx.lifecycle.ViewModel
import com.example.composeexample.layer.Todo
import com.example.composeexample.layer.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class TodoListViewModel(private val todoRepository: TodoRepository): ViewModel() {
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())

    val todos: StateFlow<List<Todo>> = _todos

    // 추가: 삭제된 아이템 목록을 저장하는 StateFlow
    private val _deletedTodos = MutableStateFlow<List<Todo>>(emptyList())
    val deletedTodos: StateFlow<List<Todo>> = _deletedTodos


    fun addTodo(text: String) {
        val newTodo = Todo(id = todos.value.size , text = text, completed = false, timestamp = getCurrentTimestamp()) // getCurrentTimestamp는 현재 시간을 가져오는 함수
        val updatedList = _todos.value.toMutableList().apply { add(newTodo) }
        _todos.value = updatedList
    }

    // 다른 ViewModel 메서드들

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    fun toggleTodoCompleted(todo: Todo) {
        val updatedTodo = todo.copy(completed = !todo.completed)
        val updatedList = _todos.value.map { if (it.id == updatedTodo.id) updatedTodo else it }
        _todos.value = updatedList
        // 여기에서 업데이트된 항목을 Repository로 전달하여 실제 데이터를 변경할 수 있습니다.
        todoRepository.updateTodo(updatedTodo)
    }



    // 추가: 아이템 삭제 동작
    fun deleteTodo(todo: Todo) {
        // 아이템 삭제 로직을 호출하고, 삭제된 아이템 목록을 업데이트
        todoRepository.deleteTodo(todo)
        val updatedDeletedTodos = _deletedTodos.value.toMutableList()
        updatedDeletedTodos.add(todo)
        _deletedTodos.value = updatedDeletedTodos
        // Todo 목록을 다시 로드
        _todos.value = todoRepository.getAllTodos()
    }


}