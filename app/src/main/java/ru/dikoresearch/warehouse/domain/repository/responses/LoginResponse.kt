package ru.dikoresearch.warehouse.domain.repository.responses

data class LoginResponse(
    val username: String,
    val token: String
)