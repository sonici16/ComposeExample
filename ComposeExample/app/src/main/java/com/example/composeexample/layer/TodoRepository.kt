package com.example.composeexample.layer

class TodoRepository {

    // 메모리 내에서 할 일 목록을 저장하는 리스트
    private val todoList = mutableListOf<Todo>()
    private val todos = mutableListOf<Todo>()

    // 추가: 삭제된 아이템 목록
    private val deletedTodoList = mutableListOf<Todo>()

    // Todo 아이템 추가
    fun addTodo(todo: Todo) {
        todoList.add(todo)
    }

    // Todo 아이템 목록 가져오기
    fun getAllTodos(): List<Todo> {
        return todoList
    }

    // 추가: 삭제된 Todo 아이템 목록 가져오기
    fun getDeletedTodos(): List<Todo> {
        return deletedTodoList
    }

    // Todo 아이템 업데이트
    fun updateTodo(updatedTodo: Todo) {
        val index = todoList.indexOfFirst { it.id == updatedTodo.id }
        if (index != -1) {
            todoList[index] = updatedTodo
        }
    }

    // Todo 아이템 삭제
    fun deleteTodo(todo: Todo) {
        val index = todoList.indexOfFirst { it.id == todo.id }
        if (index != -1) {
            // 삭제된 Todo를 삭제된 목록에 추가하고, Todo 목록에서 삭제
            val deletedItem = todoList.removeAt(index)
            deletedTodoList.add(deletedItem)
        }
    }
}
