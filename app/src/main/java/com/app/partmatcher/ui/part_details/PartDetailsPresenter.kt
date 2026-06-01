package com.app.partmatcher.ui.part_details

import com.app.partmatcher.data.api.ApiService
import com.app.partmatcher.data.model.ApiResponseDto
import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.util.ErrorUtils
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PartDetailsPresenter(
    private val apiService: ApiService,
    private val partId: Long,
    private val isAdmin: Boolean
) : MvpPresenter<PartDetailsView>() {

    private var isFavorite = false
    private var detailsCall: Call<PartDto>? = null
    private var favoriteCall: Call<ApiResponseDto>? = null
    private var favoritesListCall: Call<List<PartDto>>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showAdminActions(isAdmin)
        loadPartDetails()
    }

    private fun loadPartDetails() {
        viewState.showLoading()
        detailsCall = apiService.getPart(partId)
        detailsCall?.enqueue(object : Callback<PartDto> {
            override fun onResponse(call: Call<PartDto>, response: Response<PartDto>) {
                if (response.isSuccessful) {
                    val part = response.body()
                    if (part != null) {
                        viewState.showPartDetails(part)
                        checkFavoriteStatus()
                    } else {
                        viewState.showEmpty()
                    }
                } else if (response.code() == 401) {
                    viewState.onUnauthorized()
                } else {
                    viewState.showError(ErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<PartDto>, t: Throwable) {
                if (!call.isCanceled) {
                    viewState.showError(t.message ?: "Network error")
                }
            }
        })
    }

    private fun checkFavoriteStatus() {
        favoritesListCall = apiService.getFavorites()
        favoritesListCall?.enqueue(object : Callback<List<PartDto>> {
            override fun onResponse(call: Call<List<PartDto>>, response: Response<List<PartDto>>) {
                if (response.isSuccessful) {
                    val favorites = response.body()
                    isFavorite = favorites?.any { it.id == partId } == true
                    viewState.showFavoriteStatus(isFavorite)
                    viewState.showSuccess()
                }
                // We don't show error if favorites fail, just assume not favorite
            }

            override fun onFailure(call: Call<List<PartDto>>, t: Throwable) {
                // Ignore failure for background check
            }
        })
    }

    fun onFavoriteToggleClicked() {
        if (isFavorite) {
            removeFromFavorites()
        } else {
            addToFavorites()
        }
    }

    private fun addToFavorites() {
        favoriteCall = apiService.addToFavorites(partId)
        favoriteCall?.enqueue(object : Callback<ApiResponseDto> {
            override fun onResponse(call: Call<ApiResponseDto>, response: Response<ApiResponseDto>) {
                if (response.isSuccessful) {
                    isFavorite = true
                    viewState.showFavoriteStatus(true)
                } else if (response.code() == 401) {
                    viewState.onUnauthorized()
                } else {
                    viewState.showError(ErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<ApiResponseDto>, t: Throwable) {
                if (!call.isCanceled) {
                    viewState.showError(t.message ?: "Network error")
                }
            }
        })
    }

    private fun removeFromFavorites() {
        favoriteCall = apiService.removeFromFavorites(partId)
        favoriteCall?.enqueue(object : Callback<ApiResponseDto> {
            override fun onResponse(call: Call<ApiResponseDto>, response: Response<ApiResponseDto>) {
                if (response.isSuccessful) {
                    isFavorite = false
                    viewState.showFavoriteStatus(false)
                } else if (response.code() == 401) {
                    viewState.onUnauthorized()
                } else {
                    viewState.showError(ErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<ApiResponseDto>, t: Throwable) {
                if (!call.isCanceled) {
                    viewState.showError(t.message ?: "Network error")
                }
            }
        })
    }

    fun onAnalogsClicked() {
        viewState.navigateToAnalogs(partId)
    }

    fun onDeleteClicked() {
        if (!isAdmin) return
        
        apiService.deletePart(partId).enqueue(object : Callback<ApiResponseDto> {
            override fun onResponse(call: Call<ApiResponseDto>, response: Response<ApiResponseDto>) {
                if (response.isSuccessful) {
                    viewState.onPartDeleted()
                } else {
                    viewState.showError("Delete failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponseDto>, t: Throwable) {
                viewState.showError(t.message ?: "Unknown error")
            }
        })
    }

    fun onFavoritesListClicked() {
        viewState.navigateToFavorites()
    }

    override fun onDestroy() {
        detailsCall?.cancel()
        favoriteCall?.cancel()
        favoritesListCall?.cancel()
        super.onDestroy()
    }
}
