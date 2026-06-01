package com.app.partmatcher.data.api

import com.app.partmatcher.data.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // AUTH
    @POST("/api/auth/register")
    fun register(@Body request: UserRegistrationDto): Call<AuthResponseDto>

    @POST("/api/auth/login")
    fun login(@Body request: AuthRequestDto): Call<AuthResponseDto>

    // USER
    @GET("/api/user/me")
    fun getMe(): Call<UserDto>

    @GET("/api/user/favorites")
    fun getFavorites(): Call<List<PartDto>>

    @POST("/api/user/favorites/{partId}")
    fun addToFavorites(@Path("partId") partId: Long): Call<ApiResponseDto>

    @DELETE("/api/user/favorites/{partId}")
    fun removeFromFavorites(@Path("partId") partId: Long): Call<ApiResponseDto>

    @GET("/api/user/history/search")
    fun getSearchHistory(): Call<List<SearchHistoryDto>>

    @GET("/api/user/history/vin")
    fun getVinHistory(): Call<List<VinHistoryDto>>

    // VEHICLE
    @GET("/api/vehicles/vin/{vin}")
    fun searchByVin(@Path("vin") vin: String): Call<VehicleSearchResultDto>

    @GET("/api/vehicles/vin/{vin}/parts")
    fun getVehicleParts(@Path("vin") vin: String): Call<List<PartDto>>

    @POST("/api/vehicles")
    fun createVehicle(@Body request: VehicleCreateDto): Call<VehicleDto>

    @PUT("/api/vehicles/{id}")
    fun updateVehicle(@Path("id") id: Long, @Body request: VehicleUpdateDto): Call<VehicleDto>

    @DELETE("/api/vehicles/{id}")
    fun deleteVehicle(@Path("id") id: Long): Call<ApiResponseDto>

    @POST("/api/vehicles/{vehicleId}/parts/{partId}")
    fun linkPartToVehicle(@Path("vehicleId") vehicleId: Long, @Path("partId") partId: Long): Call<ApiResponseDto>

    @DELETE("/api/vehicles/{vehicleId}/parts/{partId}")
    fun unlinkPartFromVehicle(@Path("vehicleId") vehicleId: Long, @Path("partId") partId: Long): Call<ApiResponseDto>

    // PART
    @GET("/api/parts/{id}")
    fun getPart(@Path("id") id: Long): Call<PartDto>

    @GET("/api/parts/search")
    fun searchParts(@Query("query") query: String): Call<List<PartDto>>

    @GET("/api/parts/{id}/analogs")
    fun getAnalogs(@Path("id") id: Long): Call<List<AnalogPartDto>>

    @GET("/api/parts/vehicle/{vin}")
    fun getPartsByVehicle(@Path("vin") vin: String): Call<List<PartDto>>

    @POST("/api/parts")
    fun createPart(@Body request: PartCreateDto): Call<PartDto>

    @PUT("/api/parts/{id}")
    fun updatePart(@Path("id") id: Long, @Body request: PartUpdateDto): Call<PartDto>

    @DELETE("/api/parts/{id}")
    fun deletePart(@Path("id") id: Long): Call<ApiResponseDto>

    // SUPPORT
    @GET("/api/support/chat/history")
    fun getChatHistory(@Query("counterpartyId") counterpartyId: Long): Call<List<ChatMessageDto>>

    @GET("/api/support/chat/contacts")
    fun getChatContacts(): Call<List<UserDto>>

    // ADMIN
    @GET("/api/admin/statistics")
    fun getStatistics(): Call<AdminStatisticsDto>
}
