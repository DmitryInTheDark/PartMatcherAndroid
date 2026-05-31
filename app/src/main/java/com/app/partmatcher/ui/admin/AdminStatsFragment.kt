package com.app.partmatcher.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.partmatcher.data.api.NetworkModule
import com.app.partmatcher.data.model.AdminStatisticsDto
import com.app.partmatcher.databinding.FragmentAdminStatsBinding
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class AdminStatsFragment : MvpAppCompatFragment(), AdminStatsView {

    private var _binding: FragmentAdminStatsBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter {
        AdminStatsPresenter(NetworkModule.getApiService(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun showStatistics(stats: AdminStatisticsDto) {
        binding.tvTotalUsers.text = stats.totalUsers.toString()
        binding.tvTotalVehicles.text = stats.totalVehicles.toString()
        binding.tvTotalParts.text = stats.totalParts.toString()
        binding.tvTotalVinSearches.text = stats.totalVinSearches.toString()
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
