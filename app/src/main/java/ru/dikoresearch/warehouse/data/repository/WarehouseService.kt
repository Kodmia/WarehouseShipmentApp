package ru.dikoresearch.warehouse.data.repository

import okhttp3.MultipartBody
import retrofit2.http.*
import ru.dikoresearch.warehouse.domain.entities.Order
import ru.dikoresearch.warehouse.domain.repository.requests.LoginRequest
import ru.dikoresearch.warehouse.domain.repository.responses.ListOfOrdersResponse
import ru.dikoresearch.warehouse.domain.repository.responses.LoginResponse

interface WarehouseService {

    @POST("user/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @GET("orders/all")
    suspend fun getAllOrders(): ListOfOrdersResponse

    @GET("orders/name/{name}")
    suspend fun getOrderByName(@Path("name") orderName: String): Order

    @GET("orders/product/{art}")
    suspend fun showProductLocation(@Path("art") art: String)

    @POST("orders/new")
    suspend fun createNewOrder(@Body orderFullInfo: Order): Order

    @POST("orders/uploadImage")
    @Multipart
    suspend fun uploadImage(
        @Part("orderId") orderId: String,
        @Part("imageName") imageName: String,
        @Part body: MultipartBody.Part
    )
}