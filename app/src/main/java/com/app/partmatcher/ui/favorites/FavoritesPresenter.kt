package com.app.partmatcher.ui.favorites

import com.app.partmatcher.data.api.ApiService
import com.app.partmatcher.data.model.ApiResponseDto
import com.app.partmatcher.data.model.PartDto
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesPresenter(
    private val apiService: ApiService
) : MvpPresenter<FavoritesView>() {

    private var currentFavorites: MutableList<PartDto> = mutableListOf()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadFavorites()
    }

    fun loadFavorites() {
        viewState.showLoading()
        apiService.getFavorites().enqueue(object : Callback<List<PartDto>> {
            override fun onResponse(call: Call<List<PartDto>>, response: Response<List<PartDto>>) {
                if (response.isSuccessful) {
                    val favorites = response.body() ?: emptyList()
                    currentFavorites = favorites.toMutableList()
                    
                    if (currentFavorites.isEmpty()) {
                        viewState.showEmpty()
                    } else {
                        viewState.showFavorites(currentFavorites.toList())
                        viewState.showSuccess()
                    }
                } else if (response.code() == 401) {
                    viewState.onUnauthorized()
                } else {
                    viewState.showError("Ошибка загрузки: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<PartDto>>, t: Throwable) {
                viewState.showError("Ошибка сети: ${t.message}")
            }
        })
    }

    fun onRemoveFromFavorites(partId: Long) {
        apiService.removeFromFavorites(partId).enqueue(object : Callback<ApiResponseDto> {
            override fun onResponse(call: Call<ApiResponseDto>, response: Response<ApiResponseDto>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    // Update local list without full reload
                    currentFavorites.removeAll { it.id == partId }
                    if (currentFavorites.isEmpty()) {
                        viewState.showEmpty()
                    } else {
                        viewState.showFavorites(currentFavorites.toList())
                    }
                } else if (response.code() == 401) {
                    viewState.onUnauthorized()
                } else {
                    viewState.showError("Не удалось удалить из избранного")
                }
            }

            override fun onFailure(call: Call<ApiResponseDto>, t: Throwable) {
                viewState.showError("Ошибка сети: ${t.message}")
            }
        })
    }

    fun onPartClicked(partId: Long) {
        viewState.navigateToPartDetails(partId)
    }
}
