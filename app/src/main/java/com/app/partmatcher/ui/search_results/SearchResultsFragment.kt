package com.app.partmatcher.ui.search_results

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
import com.app.partmatcher.databinding.FragmentSearchResultsBinding
import com.app.partmatcher.ui.vin_result.PartsAdapter
import com.app.partmatcher.util.TokenManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class SearchResultsFragment : MvpAppCompatFragment(), SearchResultsView {

    private var _binding: FragmentSearchResultsBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter {
        val query = arguments?.getString("query") ?: ""
        SearchResultsPresenter(NetworkModule.getApiService(requireContext()), query)
    }

    private val adapter = PartsAdapter { part ->
        presenter.onPartClicked(part.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.resultsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.resultsRecyclerView.adapter = adapter
    }

    override fun showResults(parts: List<PartDto>) {
        adapter.submitList(parts)
    }

    override fun navigateToPartDetails(partId: Long) {
        val bundle = bundleOf("partId" to partId)
        findNavController().navigate(R.id.partDetailsFragment, bundle)
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.resultsRecyclerView.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
    }

    override fun showSuccess() {
        binding.progressBar.visibility = View.GONE
        binding.resultsRecyclerView.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE
    }

    override fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        // Show error message via Toast or Snackbar
    }

    override fun showEmpty() {
        binding.progressBar.visibility = View.GONE
        binding.resultsRecyclerView.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
    }

    override fun onUnauthorized() {
        TokenManager(requireContext()).clearToken()
        findNavController().navigate(R.id.loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
