package com.app.partmatcher.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.partmatcher.R
import com.app.partmatcher.data.api.NetworkModule
import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.databinding.FragmentFavoritesBinding
import com.app.partmatcher.util.TokenManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FavoritesFragment : MvpAppCompatFragment(), FavoritesView {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter {
        FavoritesPresenter(NetworkModule.getApiService(requireContext()))
    }

    private val adapter = FavoritesAdapter(
        onPartClick = { part -> presenter.onPartClicked(part.id) },
        onRemoveClick = { part -> presenter.onRemoveFromFavorites(part.id) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.favoritesRecyclerView.adapter = adapter

        binding.btnRetry.setOnClickListener {
            presenter.loadFavorites()
        }
    }

    override fun showFavorites(parts: List<PartDto>) {
        adapter.submitList(parts)
        binding.favoritesRecyclerView.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE
        binding.errorState.visibility = View.GONE
    }

    override fun navigateToPartDetails(partId: Long) {
        findNavController().navigate(
            R.id.action_favoritesFragment_to_partDetailsFragment,
            bundleOf("partId" to partId)
        )
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.favoritesRecyclerView.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
        binding.errorState.visibility = View.GONE
    }

    override fun showSuccess() {
        binding.progressBar.visibility = View.GONE
        binding.errorState.visibility = View.GONE
    }

    override fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.errorState.visibility = View.VISIBLE
        binding.tvErrorMessage.text = message
        binding.favoritesRecyclerView.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
    }

    override fun showEmpty() {
        binding.progressBar.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
        binding.favoritesRecyclerView.visibility = View.GONE
        binding.errorState.visibility = View.GONE
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
