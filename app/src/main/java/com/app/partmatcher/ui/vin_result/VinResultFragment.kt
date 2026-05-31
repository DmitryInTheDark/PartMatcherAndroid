package com.app.partmatcher.ui.vin_result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.partmatcher.data.api.NetworkModule
import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.data.model.VehicleDto
import com.app.partmatcher.databinding.FragmentVinResultBinding
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class VinResultFragment : MvpAppCompatFragment(), VinResultView {

    private var _binding: FragmentVinResultBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter {
        val vin = arguments?.getString("vin") ?: ""
        VinResultPresenter(NetworkModule.getApiService(requireContext()), vin)
    }

    private val adapter = PartsAdapter { part ->
        presenter.onPartClicked(part.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVinResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        binding.partsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.partsRecyclerView.adapter = adapter
    }

    override fun showVehicleInfo(vehicle: VehicleDto) {
        binding.tvVehicleTitle.text = "${vehicle.brand} ${vehicle.model}"
        binding.tvVehicleSubtitle.text = "${vehicle.year} • ${vehicle.engine} • ${vehicle.bodyType}"
    }

    override fun showCompatibleParts(parts: List<PartDto>) {
        adapter.submitList(parts)
    }

    override fun navigateToPartDetails(partId: Long) {
        Toast.makeText(context, "Part Details for ID: $partId", Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        // Show progress bar
    }

    override fun showSuccess() {
        // Hide progress bar
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun showEmpty() {
        // Show empty state view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
