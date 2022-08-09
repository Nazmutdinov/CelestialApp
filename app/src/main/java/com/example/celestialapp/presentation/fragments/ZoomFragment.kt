package com.example.celestialapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.celestialapp.databinding.FragmentZoomBinding
import com.example.celestialapp.presentation.Constants
import com.example.celestialapp.presentation.vm.ZoomViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

/**
 * Zoom celestial image from cache
 */
@AndroidEntryPoint
class ZoomFragment : Fragment() {
    private var _binding: FragmentZoomBinding? = null
    private val binding: FragmentZoomBinding get() = _binding!!

    private var toolbarFragment: Toolbar? = null
    private var nasaId: String? = null

    private val viewModel: ZoomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let {
            nasaId = it.getString(Constants.nav_argument1).toString()
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentZoomBinding.inflate(inflater, container, false)
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
            toolbarFragment = toolbar

            toolbarFragment?.setNavigationOnClickListener {
                // закрыть окно
                findNavController().popBackStack()
            }
        }
    }

    private fun setupViewModel() {
        viewModel.loadImage(nasaId)

        viewModel.image.observe(viewLifecycleOwner) { image ->
            updateUIData(image)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it.toString(), Snackbar.LENGTH_SHORT).show()
        }
    }

    // MAIN UI LOGICS

    private fun updateUIData(image: ByteArray?) {
        with(binding) {
            photoView.load(image) {
                target { drawable ->
                    photoView.setImageDrawable(drawable)
                }
            }
        }
    }

}