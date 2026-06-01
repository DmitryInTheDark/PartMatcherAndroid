package com.app.partmatcher.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.partmatcher.R
import com.app.partmatcher.data.api.NetworkModule
import com.app.partmatcher.data.model.UserDto
import com.app.partmatcher.databinding.FragmentSupportInboxBinding
import com.app.partmatcher.util.TokenManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class SupportInboxFragment : MvpAppCompatFragment(), SupportInboxView {

    private var _binding: FragmentSupportInboxBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter {
        SupportInboxPresenter(NetworkModule.getApiService(requireContext()))
    }

    private lateinit var adapter: UserInboxAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupportInboxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserInboxAdapter { user ->
            val bundle = bundleOf("recipientId" to user.id)
            findNavController().navigate(R.id.action_supportInboxFragment_to_chatFragment, bundle)
        }

        binding.rvInbox.layoutManager = LinearLayoutManager(context)
        binding.rvInbox.adapter = adapter
    }

    override fun showActiveChats(users: List<UserDto>) {
        adapter.setUsers(users)
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun showSuccess() {
        binding.progressBar.visibility = View.GONE
    }

    override fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun showEmpty() {
        binding.progressBar.visibility = View.GONE
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
