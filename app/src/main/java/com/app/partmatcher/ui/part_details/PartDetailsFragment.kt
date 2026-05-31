package com.app.partmatcher.ui.part_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.partmatcher.R
import com.app.partmatcher.data.api.NetworkModule
import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.databinding.FragmentPartDetailsBinding
import com.bumptech.glide.Glide
import androidx.navigation.fragment.findNavController
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class PartDetailsFragment : MvpAppCompatFragment(), PartDetailsView {

    private var _binding: FragmentPartDetailsBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter {
        val partId = arguments?.getLong("partId") ?: 0L
        PartDetailsPresenter(NetworkModule.getApiService(requireContext()), partId)
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
        
        binding.toolbar.inflateMenu(R.menu.home_menu) // Reusing the same menu as it has the favorites action
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_favorites -> {
                    presenter.onFavoritesListClicked()
                    true
                }
                else -> false
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        binding.btnAddToFavorites.setOnClickListener {
            presenter.onAddToFavoritesClicked()
        }
        binding.btnAnalogs.setOnClickListener {
            presenter.onAnalogsClicked()
        }
    }

    override fun showPartDetails(part: PartDto) {
        binding.tvPartTitle.text = part.name
        binding.tvPriceDetails.text = "${part.price} ₽"
        binding.chipBrandDetails.text = part.manufacturer
        binding.tvArticleDetails.text = "Art: ${part.article}"
        binding.tvDescription.text = part.description

        Glide.with(this)
            .load(part.imageUrl)
            .placeholder(com.app.partmatcher.R.drawable.ic_part_placeholder)
            .into(binding.ivPartLarge)
    }

    override fun showFavoriteStatus(isFavorite: Boolean) {
        if (isFavorite) {
            binding.btnAddToFavorites.text = "В избранном"
            binding.btnAddToFavorites.setIconResource(com.app.partmatcher.R.drawable.ic_check)
        }
    }

    override fun navigateToAnalogs(partId: Long) {
        // Navigate to analogs fragment
        Toast.makeText(context, "Navigating to Analogs for $partId", Toast.LENGTH_SHORT).show()
    }

    override fun navigateToFavorites() {
        findNavController().navigate(R.id.action_partDetailsFragment_to_favoritesFragment)
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
