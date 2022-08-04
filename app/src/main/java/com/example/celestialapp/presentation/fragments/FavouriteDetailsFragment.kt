package com.example.celestialapp.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.celestialapp.databinding.FragmentFavouriteDetailsBinding
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.models.TagDataItem
import com.example.celestialapp.presentation.CelestialEvent
import com.example.celestialapp.presentation.Constants
import com.example.celestialapp.presentation.adapters.KeywordAdapter
import com.example.celestialapp.presentation.vm.DetailedViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// окно детализации небесного тела для избранных небесных тел
@AndroidEntryPoint
class FavouriteDetailsFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteDetailsBinding

    private lateinit var toolbarFragment: Toolbar

    private val adapter: KeywordAdapter by lazy {
        KeywordAdapter(requireContext(), ::keywordTapped)
    }

    @Inject lateinit var dialog: DialogFactory

    private var nasaId: String? = null

    private val viewModel: DetailedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let {
            nasaId = it.getString(Constants.nav_argument1)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // настройка интерфейса
        setupUI()

        // настройка viewModel
        setupViewModel()
    }

    private fun setupUI() {
        with(binding) {
            toolbarFragment = toolbar

            recycleView.adapter = adapter

            addKeywordButton.setOnClickListener {
                // покажем диалог ввода нового ключевого слова
                dialog.showAddKeywordDialog(requireContext()) { keywordName ->
                    // сохраним тег и картинку в БД
                    viewModel.addFavouriteCelestial(keywordName)
                }
            }

            // слушаем тап по картинке для перехода в zoom окно
            celestialImageView.setOnClickListener {
                viewModel.detailedData.value?.let { favouriteCelestialDataItem ->
                    val nasaId = favouriteCelestialDataItem.nasaId
                    val direction = FavouriteDetailsFragmentDirections.actionFavouriteDetailsFragmentToZoomFragment(nasaId)

                    findNavController().navigate(direction)
                }
            }
        }

        // слушаем тап по кнопке назад
        toolbarFragment.setNavigationOnClickListener {
            // закрыть окно
            findNavController().popBackStack()
        }

        // тап по меню кнопке расшарить фото
        toolbarFragment.setOnMenuItemClickListener {
            viewModel.detailedData.value?.let {
                sharePhoto(it.imagePath)
            }

            true
        }
    }

    /**
     * отправить ссылку на фото другу
     */
    private fun sharePhoto(imagePath: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, imagePath)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    /**
     * настройка view model
     */
    private fun setupViewModel() {
        // запрашиваем данные из модели
        viewModel.loadDataFromCacheAndAPI(nasaId)

        // слушаем модель на данные
        viewModel.detailedData.observe(viewLifecycleOwner) { celestial ->
            updateUIData(celestial)

            // запрашиваем список ключевых слов с пометкой, что они привязаны к телу
            viewModel.getTags()
        }

        // слушаем модель на событие добавления тега\ изменения привязки
        viewModel.eventCelestial.observe(viewLifecycleOwner) { celestialEvent ->
            when (celestialEvent) {
                is CelestialEvent.Add, is CelestialEvent.Save, is CelestialEvent.Delete -> {
                    viewModel.getTags()

                    celestialEvent.stringId?.let {
                        Snackbar.make(
                            requireView(),
                            getString(it),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                }
                else -> {}
            }
        }

        // слушаем модель на получение списка ключевых слов
        viewModel.tags.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }

        // слушаем модель на ошибки
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it.toString(), Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * обновить данные в окне
     */
    private fun updateUIData(data: FavouriteCelestialDataItem) {
        with(binding) {
            // настройка заголовка
            titleTextView.text = data.title

            // настройка изображения
            celestialImageView.load(data.image) {
                target { drawable ->
                    celestialImageView.setImageDrawable(drawable)
                }
            }

            // настройка даты
            timeAgoTextView.text = data.yearsAgo

            // настройка описания
            descriptionTextView.text = data.description
        }
    }

    /**
     * тап по тегу
     */
    private fun keywordTapped(item: TagDataItem) {
        viewModel.tappedKeyword(item)
    }
}