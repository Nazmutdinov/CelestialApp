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

    private lateinit var toolbarFragment: Toolbar

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

        // настройка интерфейса
        setupUI()

        // настройка viewModel
        setupViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUI() {
        with(binding) {
            toolbarFragment = toolbar

            // слушаем тап по кнопке назад
            toolbarFragment.setNavigationOnClickListener {
                // закрыть окно
                findNavController().popBackStack()
            }
        }
    }


    /**
     * настройка view model
     */
    private fun setupViewModel() {
        // запрашиваем данные из модели
        viewModel.getImage(nasaId)

        // слушаем модель на получение данных небесных тел из апи
        viewModel.image.observe(viewLifecycleOwner) { image ->
            updateUIData(image)
        }

        // слушаем модель на ошибки
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it.toString(), Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * обновить данные в окне
     */
    private fun updateUIData(image: ByteArray?) {
        with(binding) {
            // берем картинку из кэша
            photoView.load(image) {
                target { drawable ->
                    photoView.setImageDrawable(drawable)
                }
            }
        }
    }

}