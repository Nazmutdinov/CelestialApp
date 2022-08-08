package com.example.celestialapp.presentation.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.celestialapp.databinding.FragmentFavouriteBinding
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.models.TagDataItem
import com.example.celestialapp.presentation.adapters.FavouriteCelestialAdapter
import com.example.celestialapp.presentation.adapters.KeywordAdapter
import com.example.celestialapp.presentation.vm.FavouriteViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

/**
 * list of celestials with tags from local db
 */
@AndroidEntryPoint
class FavouriteFragment : Fragment() {
    private var _binding: FragmentFavouriteBinding? = null
    private val binding: FragmentFavouriteBinding get() = _binding!!

    private val keywordsAdapter: KeywordAdapter by lazy {
        KeywordAdapter(requireContext(), ::tagTapped)
    }

    private val celestialsAdapter: FavouriteCelestialAdapter by lazy {
        FavouriteCelestialAdapter(::showDetailedCelestialData)
    }

    private val viewModel: FavouriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUI() {
        with(binding) {
            keywordsRecycleView.adapter = keywordsAdapter
            celestialsRecycleView.adapter = celestialsAdapter
        }

        setupToolbar()
    }

    private fun setupToolbar() {
        binding.favouriteFragmentToolbar.setOnMenuItemClickListener {
            val action =
                FavouriteFragmentDirections.actionFavouriteFragmentToKeywordsManagerFragment()
            findNavController().navigate(action)

            true
        }
    }

    private fun setupViewModel() {
        viewModel.loadTags()

        viewModel.tags.observe(viewLifecycleOwner) {
            keywordsAdapter.submitList(it)
        }

        viewModel.celestials.observe(viewLifecycleOwner) {
            celestialsAdapter.submitList(it)
            celestialsAdapter.notifyDataSetChanged()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it.toString(), Snackbar.LENGTH_SHORT).show()
        }
    }

    // MAIN LOGICS
    private fun tagTapped(item: TagDataItem) {
        viewModel.tappedTag(item)
        keywordsAdapter.notifyDataSetChanged()
    }

    private fun showDetailedCelestialData(favouriteCelestialDataItem: FavouriteCelestialDataItem) {
        val nasaId = favouriteCelestialDataItem.nasaId
        val action =
            FavouriteFragmentDirections.actionFavouriteFragmentToFavouriteDetailsFragment(nasaId)
        findNavController().navigate(action)
    }

}