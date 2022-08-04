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


// Окно управления тегами добавить\удалить тег
@AndroidEntryPoint
class KeywordsManagerFragment : Fragment() {
    lateinit var binding: FragmentKeywordsManagerBinding

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
        binding = FragmentKeywordsManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // настройка интерфейса
        setupUI()

        // настройка viewModel
        setupViewModel()
    }

    /**
     * настройка UI элементов окна
     */
    private fun setupUI() {
        with(binding) {
            // настройка адаптера
            recycleView.adapter = adapter

            // разделитеь м/у строками recycle view
            val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            recycleView.addItemDecoration(decoration)

            // обработчик свайпа влево
            val itemTouchHelperCallback = ItemTouchHelperCallback(::deleteKeyword)
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

            itemTouchHelper.attachToRecyclerView(recycleView)
        }

        // настройка меню в toolbar
        setupToolbar()
    }

    /**
     * настройка toolbar
     */
    private fun setupToolbar() {
        with(binding.toolbar) {
            // слушаем кнопку назад
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            // слушаем тап по меню
            setOnMenuItemClickListener {
                // показать окно добавления тега
                dialog.showAddKeywordDialog(requireContext()) { name ->
                    // добавим тег в БД
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
            dialog.showDeleteKeywordDialog(requireContext(), keyword.name) { confirmDelete ->
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