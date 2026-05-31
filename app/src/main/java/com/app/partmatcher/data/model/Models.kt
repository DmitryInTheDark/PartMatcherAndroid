package com.app.partmatcher.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class AuthRequestDto(
    val email: String,
    val password: String
)

data class UserRegistrationDto(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponseDto(
    val accessToken: String?,
    val user: UserDto?
)

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val roles: Set<String>
)

data class VehicleDto(
    val id: Long,
    val vin: String,
    val brand: String,
    val model: String,
    val year: Int,
    val engine: String,
    val bodyType: String
)

data class VehicleCreateDto(
    val vin: String,
    val brand: String,
    val model: String,
    val year: Int,
    val engine: String,
    val bodyType: String
)

data class VehicleUpdateDto(
    val brand: String,
    val model: String,
    val year: Int,
    val engine: String,
    val bodyType: String
)

data class PartDto(
    val id: Long,
    val article: String,
    val name: String,
    val manufacturer: String,
    val description: String?,
    val price: BigDecimal?,
    val imageUrl: String?,
    val category: String?
)

data class PartCreateDto(
    val article: String,
    val name: String,
    val manufacturer: String,
    val description: String?,
    val price: BigDecimal,
    val imageUrl: String?,
    val category: String
)

data class PartUpdateDto(
    val name: String,
    val manufacturer: String,
    val description: String?,
    val price: BigDecimal,
    val imageUrl: String?,
    val category: String
)

data class VehicleSearchResultDto(
    val vehicle: VehicleDto,
    val compatibleParts: List<PartDto>
)

data class AnalogPartDto(
    val id: Long,
    val originalPart: PartDto,
    val analogPart: PartDto
)

data class SearchHistoryDto(
    val id: Long,
    val query: String,
    val searchedAt: String
)

data class VinHistoryDto(
    val id: Long,
    val vin: String,
    val searchedAt: String
)

data class ChatMessageDto(
    val id: Long? = null,
    val senderId: Long? = null,
    val recipientId: Long,
    val content: String,
    val sentAt: String? = null,
    val read: Boolean = false
)

data class AdminStatisticsDto(
    val totalUsers: Long,
    val totalVehicles: Long,
    val totalParts: Long,
    val totalVinSearches: Long,
    val totalPartSearches: Long,
    val totalChatMessages: Long
)

data class ApiResponseDto(
    val message: String,
    val success: Boolean
)
