package com.example.celestialapp.presentation.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.celestialapp.databinding.FragmentSearchResultsBinding
import com.example.celestialapp.domain.models.CelestialDataItem
import com.example.celestialapp.presentation.Constants
import com.example.celestialapp.presentation.adapters.CelestialAdapter
import com.example.celestialapp.presentation.vm.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * окно результата поиска небесных тел по заданным тэгам
 */
@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchResultsBinding

    // список ключевых слов для поиска небесных тел передают из вызывающего фрагмента
    private val keywords = mutableListOf<String>()

    private val adapter: CelestialAdapter by lazy { CelestialAdapter( ::celestialItemTapped) }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // распакуем ключевые слова, переданные из вызывающего фрагмента
        arguments?.let {
            val array = it.getStringArray(Constants.nav_argument3)

            array?.let { items ->
                keywords.clear()
                keywords.addAll(items)
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchResultsBinding.inflate(inflater, container, false)
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
        // настройка адаптера
        with(binding) {
            searchFragmentRecycleView.adapter = adapter

            searchFragmentToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    /**
     * тап по картинке небесного тела
     */
    private fun celestialItemTapped(celestialDataItem: CelestialDataItem) {
        // сохраним выбранное тел в модели
        val nasaId = celestialDataItem.nasaId
        val imagePath = celestialDataItem.imagePath

        // перейдем в детализацию
        val direction = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(nasaId, imagePath)
        findNavController().navigate(direction)
    }

    /**
     * настройка view model
     */
    private fun setupViewModel() {
        // передадим в модель список ключевых слов для поиска
        viewModel.saveKeywords(keywords)

        // слушаем модель на получение списка небесных тел
        lifecycleScope.launch {
            viewModel.celestialsFlow.collectLatest {
                adapter.submitData(it)
            }
        }
    }
}