package com.example.itquiz.data

data class Question(
    val id: Int,
    val text: String,
    val image: String,
    val options: List<String>,
    val correctAnswer: Int
)

