package ru.dikoresearch.warehouse.domain.repository

import android.graphics.Bitmap
import ru.dikoresearch.warehouse.domain.entities.Order
import ru.dikoresearch.warehouse.domain.repository.requests.RequestResult
import ru.dikoresearch.warehouse.domain.repository.responses.ListOfOrdersResponse
import ru.dikoresearch.warehouse.domain.repository.responses.LoginResponse

interface WarehouseRepository {
    /**
     * Login request
     * @return RequestResult with LoginResponse or error
     */
    suspend fun login(username: String, password: String): RequestResult<LoginResponse>
    /**
     * Logout request
     */
    suspend fun logout()

    /**
     * Get all orders request
     * @return RequestResult with ListOfOrdersResponse or error
     */
    suspend fun getAllOrders(): RequestResult<ListOfOrdersResponse>

    /**
     * Get order details by given orderName
     * @param orderName name of order to get details
     * @return RequestResult with Order or error
     */
    suspend fun getOrderByName(orderName: String): RequestResult<Order>

    /**
     * Store new order in remote DB
     * @param orderFullInfo order to create
     * @return RequestResult with created order or error
     */
    suspend fun createNewOrder(orderFullInfo: Order): RequestResult<Order>

    /**
     * Get location of product
     * @param art product ID
     * @return RequestResult with created order or error
     */
    suspend fun showProductLocation(art: String): RequestResult<Unit>

    /**
     * Upload image to server
     * @param orderId ID of order to link image to
     * @param imageName Name of image
     * @param image Bitmap
     * @return empty RequestResult or error
     */
    suspend fun uploadImage(orderId: Int, imageName: String, image: Bitmap): RequestResult<Unit>
}