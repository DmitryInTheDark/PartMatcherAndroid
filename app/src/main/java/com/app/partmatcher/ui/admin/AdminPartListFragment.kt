package com.app.partmatcher.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.partmatcher.R
import com.app.partmatcher.data.api.NetworkModule
import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.databinding.FragmentAdminPartListBinding
import com.app.partmatcher.ui.vin_result.PartsAdapter
import com.app.partmatcher.util.TokenManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class AdminPartListFragment : MvpAppCompatFragment(), AdminPartListView {

    private var _binding: FragmentAdminPartListBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter {
        AdminPartListPresenter(NetworkModule.getApiService(requireContext()))
    }

    private val adapter = PartsAdapter { part ->
        findNavController().navigate(
            R.id.action_adminPartListFragment_to_partDetailsFragment,
            bundleOf("partId" to part.id)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminPartListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.partsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.partsRecyclerView.adapter = adapter

        binding.searchEditText.addTextChangedListener { text ->
            presenter.onSearchQueryChanged(text?.toString() ?: "")
        }

        binding.fabAddPart.setOnClickListener {
            Toast.makeText(context, "Add part clicked", Toast.LENGTH_SHORT).show()
            // Here you would navigate to AddPartFragment
        }
    }

    override fun showParts(parts: List<PartDto>) {
        adapter.submitList(parts)
    }

    override fun showLoading() {
        // Show loading state
    }

    override fun showSuccess() {
        // Hide loading state
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun showEmpty() {
        adapter.submitList(emptyList())
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
