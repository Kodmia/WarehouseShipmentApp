package ru.dikoresearch.warehouse.presentation.orderdetails

import ru.dikoresearch.warehouse.domain.entities.RemoteGoods


data class OrderGoodsAdapterModel(
    val goods: RemoteGoods,
    val isChecked: Boolean,
    val isLoaded: Boolean
)

