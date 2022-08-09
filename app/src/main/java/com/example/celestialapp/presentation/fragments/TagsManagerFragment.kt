package com.example.celestialapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.celestialapp.databinding.FragmentTagsManagerBinding
import com.example.celestialapp.presentation.adapters.KeywordsManagerAdapter
import com.example.celestialapp.presentation.vm.TagsManagerViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Tags manager fragment: list of tags
 * add \ delete tag from local database
 */
@AndroidEntryPoint
class TagsManagerFragment : Fragment() {
    private var _binding: FragmentTagsManagerBinding? = null
    private val binding: FragmentTagsManagerBinding get() = _binding!!

    private val adapter: KeywordsManagerAdapter by lazy {
        KeywordsManagerAdapter()
    }

    @Inject lateinit var dialog: DialogFactory

    private val viewModel: TagsManagerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagsManagerBinding.inflate(inflater, container, false)
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
            recycleView.adapter = adapter

            val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            recycleView.addItemDecoration(decoration)

            // swipe on left for deleting
            val itemTouchHelperCallback = ItemTouchHelperCallback(::deleteTag)
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

            itemTouchHelper.attachToRecyclerView(recycleView)
        }

        setupToolbar()
    }

    private fun setupToolbar() {
        with(binding.toolbar) {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            setOnMenuItemClickListener {
                dialog.showAddTagDialog(requireContext()) { name ->
                    viewModel.addTag(name)
                }
                true
            }
        }
    }

    private fun setupViewModel() {
        viewModel.loadTags()

        viewModel.tags.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Snackbar.make(requireView(), message.toString(), Snackbar.LENGTH_SHORT).show()
        }
    }

    // MAIN LOGICS

    private fun deleteTag(position: Int) {
        viewModel.getTagByPosition(position)?.let { keyword ->
            dialog.showDeleteTagDialog(requireContext(), keyword.name) { confirmDelete ->
                if (confirmDelete) {
                    viewModel.deleteTag(keyword.tagId)
                } else {
                    // cause delete was canceled by swipe, we need call restore "deleted" in recycle view element
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}