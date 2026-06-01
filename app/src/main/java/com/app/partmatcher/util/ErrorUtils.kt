package com.app.partmatcher.util

import com.app.partmatcher.data.model.ApiResponseDto
import com.google.gson.Gson
import retrofit2.Response

object ErrorUtils {
    fun parseError(response: Response<*>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            val apiResponse = Gson().fromJson(errorBody, ApiResponseDto::class.java)
            apiResponse.message ?: "Unknown error (HTTP ${response.code()})"
        } catch (e: Exception) {
            "Error: ${response.code()}"
        }
    }
}
