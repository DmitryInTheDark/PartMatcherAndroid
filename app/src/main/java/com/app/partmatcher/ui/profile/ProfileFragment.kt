package com.app.partmatcher.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.app.partmatcher.R
import com.app.partmatcher.data.api.NetworkModule
import com.app.partmatcher.data.model.UserDto
import com.app.partmatcher.databinding.FragmentProfileBinding
import com.app.partmatcher.util.TokenManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class ProfileFragment : MvpAppCompatFragment(), ProfileView {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter {
        ProfilePresenter(
            NetworkModule.getApiService(requireContext()),
            TokenManager(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSupport.setOnClickListener {
            val bundle = Bundle().apply {
                putLong("recipientId", 1L) // Assuming 1L is Support ID
            }
            findNavController().navigate(R.id.chatFragment, bundle)
        }
        binding.btnLogout.setOnClickListener {
            presenter.onLogoutClicked()
        }
    }

    override fun showUserInfo(user: UserDto) {
        binding.tvName.text = user.name
        binding.tvEmail.text = user.email
        binding.tvRole.text = "Role: ${user.roles.joinToString()}"
    }

    override fun navigateToLogin() {
        val options = navOptions {
            popUpTo(R.id.nav_graph) { inclusive = true }
        }
        findNavController().navigate(R.id.loginFragment, null, options)
    }

    override fun showLoading() {
        // Implement loading state if needed
    }

    override fun showSuccess() {
        // Implement success state if needed
    }

    override fun showError(message: String) {
        // Show error message
    }

    override fun showEmpty() {
        // Show empty state
    }

    override fun onUnauthorized() {
        TokenManager(requireContext()).clearToken()
        navigateToLogin()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
