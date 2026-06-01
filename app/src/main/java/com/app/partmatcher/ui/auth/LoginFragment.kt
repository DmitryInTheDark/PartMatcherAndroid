package com.app.partmatcher.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.app.partmatcher.R
import com.app.partmatcher.data.api.NetworkModule
import com.app.partmatcher.databinding.FragmentLoginBinding
import com.app.partmatcher.util.TokenManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class LoginFragment : MvpAppCompatFragment(), LoginView {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter {
        LoginPresenter(
            NetworkModule.getApiService(requireContext()),
            TokenManager(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            presenter.onLoginClicked(email, password)
        }

        binding.btnRegister.setOnClickListener {
            presenter.onRegisterClicked()
        }
    }

    override fun navigateToHome() {
        // We use popBackStack or navigate with clearing to ensure user can't go back to login
        val options = navOptions {
            popUpTo(R.id.nav_graph) { inclusive = true }
        }
        findNavController().navigate(R.id.homeFragment, null, options)
    }

    override fun navigateToRegister() {
        findNavController().navigate(R.id.registerFragment)
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
    }

    override fun showSuccess() {
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true
    }

    override fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun showEmpty() {
        // Not used
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
