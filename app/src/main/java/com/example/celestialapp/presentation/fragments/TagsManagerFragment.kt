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
import com.example.celestialapp.databinding.FragmentKeywordsManagerBinding
import com.example.celestialapp.presentation.adapters.KeywordsManagerAdapter
import com.example.celestialapp.presentation.vm.KeywordsManagerViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Tags manager fragment: list of tags
 * add \ delete tag from local database
 */
@AndroidEntryPoint
class KeywordsManagerFragment : Fragment() {
    private var _binding: FragmentKeywordsManagerBinding? = null
    private val binding: FragmentKeywordsManagerBinding get() = _binding!!

    private val adapter: KeywordsManagerAdapter by lazy {
        KeywordsManagerAdapter()
    }

    @Inject lateinit var dialog: DialogFactory

    private val viewModel: KeywordsManagerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKeywordsManagerBinding.inflate(inflater, container, false)
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
            val itemTouchHelperCallback = ItemTouchHelperCallback(::deleteKeyword)
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
                    viewModel.addKeyword(name)
                }
                true
            }
        }
    }

    /**
     * удалить ключевое слово через диалог подтверждения
     */
    private fun deleteKeyword(position: Int) {
        viewModel.getKeywordByPosition(position)?.let {  keyword ->
            dialog.showDeleteTagDialog(requireContext(), keyword.name) { confirmDelete ->
                if (confirmDelete) {
                    // ответ диалога = подтверждение, вызываем удаление ключевого слова в БД
                    viewModel.deleteKeyword(keyword.tagId)
                } else {
                    /* ответ диалога = отмена, вызываем восстановление элемента
                    т.к. свайп уже был и элемент исчез визуально
                     */
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * настройка view model
     */
    private fun setupViewModel() {
        // запрашиваем данные из модели
        viewModel.getKeywords()

        // слушаем модель на получение списка ключевых слов
        viewModel.keywords.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        // слушаем модель на ошибки
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Snackbar.make(requireView(), message.toString(), Snackbar.LENGTH_SHORT).show()
        }
    }
}