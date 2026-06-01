package com.app.partmatcher.ui.part_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.app.partmatcher.R
import com.app.partmatcher.data.api.NetworkModule
import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.databinding.FragmentPartDetailsBinding
import com.app.partmatcher.util.TokenManager
import com.bumptech.glide.Glide
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class PartDetailsFragment : MvpAppCompatFragment(), PartDetailsView {

    private var _binding: FragmentPartDetailsBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter {
        val partId = arguments?.getLong("partId") ?: 0L
        val tokenManager = TokenManager(requireContext())
        val roles = tokenManager.getRoles()
        val isAdmin = roles.contains("ADMIN") || roles.contains("ROLE_ADMIN")
        PartDetailsPresenter(NetworkModule.getApiService(requireContext()), partId, isAdmin)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPartDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddToFavorites.setOnClickListener {
            presenter.onFavoriteToggleClicked()
        }

        binding.btnAnalogs.setOnClickListener {
            presenter.onAnalogsClicked()
        }

        binding.btnDeletePart.setOnClickListener {
            presenter.onDeleteClicked()
        }
        
        binding.btnEditPart.setOnClickListener {
            Toast.makeText(context, "Edit part clicked", Toast.LENGTH_SHORT).show()
        }
    }

    override fun showPartDetails(part: PartDto) {
        binding.tvPartTitle.text = part.name
        binding.tvPriceDetails.text = part.price?.let { "$it ₽" } ?: "Цена по запросу"
        binding.chipBrandDetails.text = part.manufacturer
        binding.tvArticleDetails.text = "Art: ${part.article}"
        binding.tvDescription.text = part.description ?: "Нет описания"

        Glide.with(this)
            .load(part.imageUrl)
            .placeholder(R.drawable.ic_part_placeholder)
            .error(R.drawable.ic_part_placeholder)
            .into(binding.ivPartLarge)
    }

    override fun showFavoriteStatus(isFavorite: Boolean) {
        if (isFavorite) {
            binding.btnAddToFavorites.text = "В избранном"
            binding.btnAddToFavorites.setIconResource(R.drawable.ic_favorite_filled)
        } else {
            binding.btnAddToFavorites.text = "В избранное"
            binding.btnAddToFavorites.setIconResource(R.drawable.ic_favorite_border)
        }
    }

    override fun navigateToAnalogs(partId: Long) {
        val bundle = Bundle().apply { putLong("partId", partId) }
        findNavController().navigate(resId = R.id.analogsFragment, args = bundle)
    }

    override fun navigateToFavorites() {
        findNavController().navigate(resId = R.id.favoritesFragment)
    }

    override fun showAdminActions(visible: Boolean) {
        binding.adminActionBar.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onPartDeleted() {
        Toast.makeText(context, "Деталь удалена", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentContainer.visibility = View.GONE
    }

    override fun showSuccess() {
        binding.progressBar.visibility = View.GONE
        binding.contentContainer.visibility = View.VISIBLE
    }

    override fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun showEmpty() {
        binding.progressBar.visibility = View.GONE
        // Можно добавить отдельный View для пустого состояния, если нужно
        Toast.makeText(requireContext(), "Деталь не найдена", Toast.LENGTH_SHORT).show()
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
