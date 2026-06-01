package com.app.partmatcher.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.partmatcher.data.api.NetworkModule
import com.app.partmatcher.data.model.AdminStatisticsDto
import com.app.partmatcher.databinding.FragmentAdminStatsBinding
import com.app.partmatcher.util.TokenManager
import androidx.navigation.fragment.findNavController
import com.app.partmatcher.R
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardParts.setOnClickListener {
            findNavController().navigate(R.id.action_adminStatsFragment_to_adminPartListFragment)
        }
        binding.cardVehicles.setOnClickListener {
            Toast.makeText(context, "Управление автомобилями (скоро)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun showStatistics(stats: AdminStatisticsDto) {
        binding.tvTotalUsers.text = String.format("%,d", stats.totalUsers)
        binding.tvTotalVehicles.text = String.format("%,d", stats.totalVehicles)
        binding.tvTotalParts.text = String.format("%,d", stats.totalParts)
        binding.tvTotalVinSearches.text = String.format("%,d", stats.totalVinSearches)
    }

    override fun showLoading() {
        // Could add a progress bar if needed
    }

    override fun showSuccess() {
        // Handle success if needed
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun showEmpty() {}

    override fun onUnauthorized() {
        TokenManager(requireContext()).clearToken()
        findNavController().navigate(resId = R.id.loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
