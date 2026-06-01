package com.app.partmatcher.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ArrayAdapter
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
import com.app.partmatcher.util.NoFilterAdapter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class AdminPartListFragment : MvpAppCompatFragment(), AdminPartListView {

    private var _binding: FragmentAdminPartListBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter {
        AdminPartListPresenter(NetworkModule.getApiService(requireContext()))
    }

    private lateinit var suggestionsAdapter: NoFilterAdapter

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

        suggestionsAdapter = NoFilterAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        binding.searchEditText.setAdapter(suggestionsAdapter)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.partsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.partsRecyclerView.adapter = adapter

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
            presenter.onSearchQueryChanged(query)
        }

        binding.fabAddPart.setOnClickListener {
            Toast.makeText(context, "Add part clicked", Toast.LENGTH_SHORT).show()
            // Here you would navigate to AddPartFragment
        }
    }

    override fun showParts(parts: List<PartDto>) {
        adapter.submitList(parts)
    }

    override fun showSearchSuggestions(suggestions: List<String>) {
        suggestionsAdapter.updateItems(suggestions)
        if (suggestions.isNotEmpty() && binding.searchEditText.isFocused) {
            binding.searchEditText.showDropDown()
        }
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
