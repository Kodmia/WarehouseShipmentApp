package ru.dikoresearch.warehouse.domain.entities

data class OrdersListItem(
    val orderId: Int,
    val status: String,
    val orderName: String,
    val username: String,
    val createdAt: String
)
