package com.example.celestialapp.presentation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.celestialapp.R
import com.example.celestialapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    // LOGICS
    /**
     * setup base UI, events
     */
    private fun setupUI() {
        setupNavigationController()
    }

    private fun setupNavigationController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // setup bottom menu
        val bottomNavigationView = binding?.bottomNavigationView
        bottomNavigationView?.setupWithNavController(navController)

        hideBottomMenuForSomeFragments(bottomNavigationView)
    }

    private fun hideBottomMenuForSomeFragments(bottomNavigationView: BottomNavigationView?) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // if we navigate to details, then we don't need navigation
                R.id.detailsFragment,
                R.id.searchFragment,
                R.id.favouriteDetailsFragment,
                R.id.keywordsManagerFragment,
                R.id.zoomFragment -> bottomNavigationView?.visibility = View.GONE
                else -> bottomNavigationView?.visibility = View.VISIBLE
            }
        }
    }
}