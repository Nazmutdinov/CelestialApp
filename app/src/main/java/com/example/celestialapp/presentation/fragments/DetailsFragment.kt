package com.example.celestialapp.presentation.fragments

import android.view.View
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * Details of celestial: image, description, tages
 * search same celestials by NASA API keywords
 */
@AndroidEntryPoint
class DetailsFragment: FavouriteDetailsFragment() {

    override fun setupUI() {
        super.setupUI()
        with(binding.searchButton) {
            visibility = View.VISIBLE
            setOnClickListener {
                showFragmentWithSameCelestials()
            }
        }
    }

    private fun showFragmentWithSameCelestials() {
        with(viewModel) {
            loadKeywords()

            keywords.observe(viewLifecycleOwner) { keywords ->
                keywords?.let {
                    val action =
                        DetailsFragmentDirections.actionDetailsFragmentToSearchFragment(keywords.toTypedArray())
                    findNavController().navigate(action)
                }
            }
        }
    }
}
