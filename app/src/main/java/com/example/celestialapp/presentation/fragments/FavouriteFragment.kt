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
 * окно избранных небесных тел
 */
@AndroidEntryPoint
class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteBinding

    private val keywordsAdapter: KeywordAdapter by lazy {
        KeywordAdapter(requireContext(), ::keywordTapped)
    }

    private val celestialsAdapter: FavouriteCelestialAdapter by lazy {
        FavouriteCelestialAdapter(::celestialItemTapped)
    }

    private val viewModel: FavouriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // настройка интерфейса
        setupUI()

        // настройка viewModel
        setupViewModel()
    }

    /**
     * настройка UI элементов окна
     */
    private fun setupUI() {
        with(binding) {
            // адаптер ключевых слов
            favouriteFragmentKeywordsRecycleView.adapter = keywordsAdapter

            // адаптер небесных тел
            favouriteFragmentCelestialsRecycleView.adapter = celestialsAdapter
        }

        // настройка меню в toolbar
        setupToolbar()
    }

    /**
     * настройка toolbar
     */
    private fun setupToolbar() {
        // слушаем нажатие пунктов меню
        binding.favouriteFragmentToolbar.setOnMenuItemClickListener {
            // переход в управление тегами
            val direction =
                FavouriteFragmentDirections.actionFavouriteFragmentToKeywordsManagerFragment()
            findNavController().navigate(direction)

            true
        }
    }


    /**
     * тап по тегу
     */
    private fun keywordTapped(item: TagDataItem) {
        viewModel.tappedKeyword(item)
        keywordsAdapter.notifyDataSetChanged()
    }

    /**
     * переход в детальную инфу небесного тела
     */
    private fun celestialItemTapped(favouriteCelestialDataItem: FavouriteCelestialDataItem) {
        val nasaId = favouriteCelestialDataItem.nasaId

        val direction =
            FavouriteFragmentDirections.actionFavouriteFragmentToFavouriteDetailsFragment(nasaId)
        findNavController().navigate(direction)
    }

    /**
     * настройка view model
     */
    private fun setupViewModel() {
        // запускаем получение ключевых слов
        viewModel.getKeywords()

        // слушаем модель на получение ключевых слов
        viewModel.keywords.observe(viewLifecycleOwner) {
            keywordsAdapter.submitList(it)
        }

        // слушаем модель на получение тегов
        viewModel.celestials.observe(viewLifecycleOwner) {
            celestialsAdapter.submitList(it)
            celestialsAdapter.notifyDataSetChanged()
        }

        // слушаем модель на ошибки
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it.toString(), Snackbar.LENGTH_SHORT).show()
        }
    }
}