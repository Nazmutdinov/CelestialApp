package com.example.celestialapp.presentation.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.celestialapp.R
import com.example.celestialapp.databinding.FragmentHomeBinding
import com.example.celestialapp.domain.models.CelestialDataItem
import com.example.celestialapp.presentation.CelestialName
import com.example.celestialapp.presentation.adapters.CelestialAdapter
import com.example.celestialapp.presentation.vm.MainViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Main fragment, list of celestials
 * get list from NASA API
 * kind of celestials setup in app bar menu
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    private lateinit var toolbar: Toolbar

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView

    private val adapter: CelestialAdapter by lazy { CelestialAdapter(::celestialItemTapped) }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // настройка интерфейса
        setupUI()

        // настройка viewModel
        setupViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * настройка UI элементов окна
     */
    private fun setupUI() {
        with(binding) {
            // настройка адаптера
            homeFragmentRecycleView.adapter = adapter

            // настройка toolbar
            toolbar = homeFragmentToolbar

            // настройка DrawerLayout
            drawerLayout = homeFragmentDrawLayout

            // настройка бокового меню NavigationView
            navigationView = homeFragmentNavigationView
        }

        // настройка меню в toolbar
        setupToolbar()

        // настройка бокового меню
        setupNavigationView()
    }

    /**
     * настройка toolbar
     */
    private fun setupToolbar() {
        // слушаем тап по меню
        toolbar.setOnMenuItemClickListener {
            // открыть navigation view справа-налево
            drawerLayout.openDrawer(GravityCompat.END, false)
            true
        }
    }

    /**
     * настройка бокового меню Navigation view
     */
    private fun setupNavigationView() {
        navigationView.setNavigationItemSelectedListener {
            // закрыть navigation view справа-налево
            drawerLayout.closeDrawer(GravityCompat.END)

            // обработаем выбранный пункт
            navigationViewItemTapped(it.itemId)

            true
        }
    }

    /**
     * обработка бокового меню
     */
    private fun navigationViewItemTapped(menuItemId: Int) {
        val celestialName =
            when (menuItemId) {
                R.id.item_1 -> CelestialName.MOON
                R.id.item_2_1 -> CelestialName.MERCURY
                R.id.item_2_2 -> CelestialName.VENUS
                R.id.item_2_3 -> CelestialName.MARS
                R.id.item_2_4 -> CelestialName.JUPITER
                R.id.item_2_5 -> CelestialName.SATURN
                R.id.item_2_6 -> CelestialName.URANUS
                R.id.item_2_7 -> CelestialName.NEPTUNE
                R.id.item_2_8 -> CelestialName.PLUTO
                else -> CelestialName.GALAXY
            }

        // обновим данные в окне
        refreshCelestialData(celestialName)
    }

    /**
     * обновить данные о небесных телах
     */
    private fun refreshCelestialData(celestialName: CelestialName) {
        // обновим заголовок
        toolbar.title = celestialName.name

        // перезапустим процесс получения данных в модели
        val keywords = listOf(celestialName.name)

        // сохраним выбранный пункт navigation view в модель
        viewModel.saveKeywords(keywords)
    }

    /**
     * тап по картинке небесного тела
     */
    private fun celestialItemTapped(celestialDataItem: CelestialDataItem) {
        // сохраним выбранное тел в модели
        val nasaId = celestialDataItem.nasaId
        val imagePath = celestialDataItem.imagePath

        // перейдем в детализацию
        val direction =
            HomeFragmentDirections.actionHomeFragmentToDetailsFragment(nasaId, imagePath)
        findNavController().navigate(direction)
    }

    /**
     * настройка view model
     */
    private fun setupViewModel() {
        // слушаем модель на получение списка небесных тел
        lifecycleScope.launch {
            viewModel.celestialsFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
   }
}