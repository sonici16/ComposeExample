package com.example.composeexample.layer

data class Todo(
    val id: Int,
    val text: String,
    var completed: Boolean,
    val timestamp: String // 추가한 시간을 저장할 timestamp 추가
)
