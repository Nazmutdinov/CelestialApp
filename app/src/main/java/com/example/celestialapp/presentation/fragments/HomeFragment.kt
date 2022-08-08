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

    private var toolbar: Toolbar? = null
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private val adapter: CelestialAdapter by lazy { CelestialAdapter(::showDetailedCelestialData) }

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

        setupUI()
        setupViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUI() {
        with(binding) {
            homeFragmentRecycleView.adapter = adapter
            toolbar = homeFragmentToolbar
            drawerLayout = homeFragmentDrawLayout
            navigationView = homeFragmentNavigationView
        }

        setupToolbar()
        setupNavigationView()
    }

    private fun setupToolbar() {
        toolbar?.setOnMenuItemClickListener {
            drawerLayout?.openDrawer(GravityCompat.END, false)
            true
        }
    }

    private fun setupNavigationView() {
        navigationView?.setNavigationItemSelectedListener {
            // close navigation view from right-to-left
            drawerLayout?.closeDrawer(GravityCompat.END)
            navigationViewItemTapped(it.itemId)

            true
        }
    }

    private fun setupViewModel() {
        lifecycleScope.launch {
            viewModel.celestialsFlowFromAPI?.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
   }

    // MAIN LOGICS

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

        updateUIData(celestialName)
    }

    private fun updateUIData(celestialMenuItemName: CelestialName) {
        toolbar?.title = celestialMenuItemName.name
        viewModel.saveSelectedMenuItemName(celestialMenuItemName.name)
    }

    private fun showDetailedCelestialData(celestialDataItem: CelestialDataItem) {
        val nasaId = celestialDataItem.nasaId
        val imagePath = celestialDataItem.imagePath

        val action =
            HomeFragmentDirections.actionHomeFragmentToDetailsFragment(nasaId, imagePath)
        findNavController().navigate(action)
    }
}