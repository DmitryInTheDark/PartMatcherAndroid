package com.app.partmatcher.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.app.partmatcher.R
import com.app.partmatcher.data.api.NetworkModule
import com.app.partmatcher.databinding.FragmentHomeBinding
import com.app.partmatcher.util.TokenManager
import com.google.android.material.chip.Chip
import com.app.partmatcher.util.NoFilterAdapter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class HomeFragment : MvpAppCompatFragment(), HomeView {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter {
        HomePresenter(
            NetworkModule.getApiService(requireContext()),
            TokenManager(requireContext())
        )
    }

    private lateinit var suggestionsAdapter: NoFilterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        suggestionsAdapter = NoFilterAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        binding.searchEditText.setAdapter(suggestionsAdapter)

        binding.btnGoToInbox.setOnClickListener {
            findNavController().navigate(R.id.supportInboxFragment)
        }

        binding.toolbar.inflateMenu(R.menu.home_menu)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_favorites -> {
                    presenter.onFavoritesClicked()
                    true
                }
                else -> false
            }
        }

        binding.btnFindVehicle.setOnClickListener {
            val vin = binding.vinEditText.text.toString()
            presenter.onVinSearchClicked(vin)
        }

        binding.searchEditText.addTextChangedListener { text ->
            presenter.onSearchQueryChanged(text?.toString() ?: "")
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val text = binding.searchEditText.text.toString()
                if (text.length >= 2) {
                    presenter.onSearchQueryChanged(text)
                }
            }
        }

        binding.searchEditText.setOnItemClickListener { parent, _, position, _ ->
            val selectedSuggestion = parent.getItemAtPosition(position) as String
            val query = selectedSuggestion.substringBefore(" (")
            binding.searchEditText.setText(query)
            presenter.onTextSearchClicked(query)
        }

        binding.searchEditText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchEditText.text.toString()
            presenter.onTextSearchClicked(query)
            true
        }
    }

    override fun showSearchSuggestions(suggestions: List<String>) {
        suggestionsAdapter.updateItems(suggestions)
        if (suggestions.isNotEmpty() && binding.searchEditText.isFocused) {
            binding.searchEditText.showDropDown()
        }
    }

    override fun showSupportGreeting(name: String, chatCount: Int) = with(binding){
        supportCard.visibility = View.VISIBLE
        tvSupportGreeting.text = "Добрый день, $name!"
        tvActiveChatsCount.text = if (chatCount > 0) chatCount.toString() else "..."
        cvVin.isVisible = false
        tvSearchTitle.isVisible = false
        searchInputLayout.isVisible = false
        recents.isVisible = false
    }

    override fun showRecentRequests(requests: List<String>) {
        binding.recentRequestsChipGroup.removeAllViews()
        requests.forEach { request ->
            val chip = Chip(requireContext()).apply {
                text = request
                setChipIconResource(R.drawable.ic_history)
                setOnClickListener { presenter.onTextSearchClicked(request) }
            }
            binding.recentRequestsChipGroup.addView(chip)
        }
    }

    override fun navigateToVinResult(vin: String) {
        findNavController().navigate(
            R.id.action_homeFragment_to_vinResultFragment,
            bundleOf("vin" to vin)
        )
    }

    override fun navigateToSearchResults(query: String) {
        findNavController().navigate(
            R.id.action_homeFragment_to_searchResultsFragment,
            bundleOf("query" to query)
        )
    }

    override fun navigateToFavorites() {
        findNavController().navigate(R.id.action_homeFragment_to_favoritesFragment)
    }

    override fun showLoading() {
        // Show loading state
    }

    override fun showSuccess() {
        // Show success state
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun showEmpty() {
        // Show empty state
    }

    override fun onUnauthorized() {
        TokenManager(requireContext()).clearToken()
        findNavController().navigate(resId = R.id.loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
