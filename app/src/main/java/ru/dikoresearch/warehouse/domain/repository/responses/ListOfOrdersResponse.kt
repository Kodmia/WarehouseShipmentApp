package ru.dikoresearch.warehouse.domain.repository.responses

import ru.dikoresearch.warehouse.domain.entities.Order

data class ListOfOrdersResponse(
    val orders: List<Order>
)
