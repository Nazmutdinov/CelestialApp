package com.example.celestialapp.presentation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.celestialapp.R
import com.example.celestialapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // настройка интерфейса
        setupUI()
    }

    /**
     * настройка UI элементов окна
     */
    private fun setupUI() {
        // настройка nav controller
        setupNavigationController()
    }

    /**
     * настройка navigation controller
     */
    private fun setupNavigationController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // настройка bottom menu
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)


        // скроеем bottom menu в случае переходов внутри фрагмента Home
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // если переход в детали, то скрывать навигацию
                R.id.detailsFragment,
                R.id.searchFragment,
                R.id.favouriteDetailsFragment,
                R.id.keywordsManagerFragment,
                R.id.zoomFragment
                -> {
                    // скрыть bottom меню
                    bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
    }
}