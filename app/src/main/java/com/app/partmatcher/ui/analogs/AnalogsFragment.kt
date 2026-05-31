package com.app.partmatcher.ui.analogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.partmatcher.data.api.NetworkModule
import com.app.partmatcher.data.model.AnalogPartDto
import com.app.partmatcher.databinding.FragmentAnalogsBinding
import com.app.partmatcher.ui.vin_result.PartsAdapter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class AnalogsFragment : MvpAppCompatFragment(), AnalogsView {

    private var _binding: FragmentAnalogsBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter {
        val partId = arguments?.getLong("partId") ?: 0L
        AnalogsPresenter(NetworkModule.getApiService(requireContext()), partId)
    }

    private val adapter = PartsAdapter { part ->
        // Navigate to details of the analog part if needed
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        binding.analogsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.analogsRecyclerView.adapter = adapter
    }

    override fun showAnalogs(analogs: List<AnalogPartDto>) {
        adapter.submitList(analogs.map { it.analogPart })
    }

    override fun showLoading() {}

    override fun showSuccess() {}

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun showEmpty() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
