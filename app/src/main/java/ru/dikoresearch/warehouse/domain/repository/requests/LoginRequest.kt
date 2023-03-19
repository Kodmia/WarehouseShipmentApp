package ru.dikoresearch.warehouse.domain.repository.requests

data class LoginRequest(
    val username: String,
    val password: String
)
