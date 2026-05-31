package com.app.partmatcher.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.app.partmatcher.R
import com.app.partmatcher.databinding.FragmentHomeBinding
import com.google.android.material.chip.Chip
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class HomeFragment : MvpAppCompatFragment(), HomeView {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter { HomePresenter() }

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

        binding.searchEditText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchEditText.text.toString()
            presenter.onTextSearchClicked(query)
            true
        }
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
        Toast.makeText(requireContext(), "Searching for: $query", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
