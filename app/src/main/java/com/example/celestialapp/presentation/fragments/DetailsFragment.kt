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
import com.example.celestialapp.databinding.FragmentDetailsBinding
import com.example.celestialapp.domain.models.FavouriteCelestialDataItem
import com.example.celestialapp.domain.models.TagDataItem
import com.example.celestialapp.presentation.CelestialEvent
import com.example.celestialapp.presentation.Constants
import com.example.celestialapp.presentation.adapters.KeywordAdapter
import com.example.celestialapp.presentation.vm.DetailedViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Details of celestial: image, description, tages
 * search same celestials by NASA API keywords
 */
@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding get() = _binding!!

    private var toolbarFragment: Toolbar? = null

    private val adapter: KeywordAdapter by lazy {
        KeywordAdapter(requireContext(), ::keywordTapped)
    }

    @Inject
    lateinit var dialog: DialogFactory
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
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
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

            recycleView.adapter = adapter

            searchButton.setOnClickListener {
                showFragmentWithSameCelestials()
            }

            addKeywordButton.setOnClickListener {
                dialog.showAddTagDialog(requireContext()) { keywordName ->
                    viewModel.addFavouriteCelestial(keywordName)
                }
            }

            celestialImageView.setOnClickListener {
                viewModel.detailedData.value?.let { favouriteCelestialDataItem ->
                    val nasaId = favouriteCelestialDataItem.nasaId
                    val direction =
                        DetailsFragmentDirections.actionDetailsFragmentToZoomFragment(nasaId)

                    findNavController().navigate(direction)
                }
            }
        }

        toolbarFragment?.let { toolbFrg ->
            toolbFrg.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            toolbFrg.setOnMenuItemClickListener {
                viewModel.detailedData.value?.let { favouriteCelestialDataItem ->
                    sharePhotoToViberMessengers(favouriteCelestialDataItem.imagePath)
                }

                true
            }
        }
    }

    private fun setupViewModel() {
        with(viewModel) {
            nasaId?.let { loadDataFromCacheOrAPI(it) }

            detailedData.observe(viewLifecycleOwner) { celestial ->
                updateUIData(celestial)

                loadTags()
            }

            // event when we add new tag
            eventCelestial.observe(viewLifecycleOwner) { celestialEvent ->
                when (celestialEvent) {
                    is CelestialEvent.Add, is CelestialEvent.Save, is CelestialEvent.Delete -> {
                        loadTags()

                        celestialEvent.stringId?.let {
                            showSnackBar(it)
                        }
                    }
                    else -> Unit
                }
            }

            tags.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

            errorMessage.observe(viewLifecycleOwner) {
                showSnackBar(it.toString())
            }
        }
    }

    // MAIN UI LOGICS

    private fun updateUIData(data: FavouriteCelestialDataItem) {
        with(binding) {
            titleTextView.text = data.title
            timeAgoTextView.text = data.photoOwnerCreatedYearsAgo
            descriptionTextView.text = data.description

            celestialImageView.load(data.image) {
                target { celestialImageView.setImageDrawable(it) }
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

    private fun keywordTapped(item: TagDataItem) {
        viewModel.tappedTag(item)
        adapter.notifyDataSetChanged()
    }


    private fun sharePhotoToViberMessengers(imagePath: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, imagePath)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showSnackBar(resId: Int) {
        Snackbar.make(requireView(), getString(resId), Snackbar.LENGTH_SHORT).show()
    }
}