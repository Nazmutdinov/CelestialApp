package com.example.celestialapp.presentation.fragments

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.celestialapp.R
import com.google.android.material.textfield.TextInputEditText


class DialogFactory {
    /**
     * диалог для ввода названия нового ключевого слова
     */
    fun showAddKeywordDialog(context: Context, callbackAddClick: (String) -> Unit) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.creation_keyword_dialog, null)
        val editText = dialogView.findViewById<TextInputEditText>(R.id.name_edit_text)

        builder.setTitle(context.getString(R.string.add_keyword_dialog_title))
            .setMessage(context.getString(R.string.add_keyword_dialog_message))
            .setView(dialogView)
            .setNegativeButton(R.string.dialog_button_cancel) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .setPositiveButton(R.string.dialog_button_add) { dialogInterface, _ ->
                val ts = editText.text.toString()
                callbackAddClick(ts)

                dialogInterface.dismiss()
            }
            .create()
            .show()
    }

    /**
     * диалог для удаления ключевого слова
     */
    fun showDeleteKeywordDialog(
        context: Context,
        keywordName: String,
        callbackClick: (Boolean) -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.showDeleteConfirmationDialogTitle, keywordName))
            .setMessage(context.getString(R.string.showDeleteConfirmationDialogMessage))
            .setNegativeButton(R.string.dialog_button_no) { dialogInterface, _ ->
                callbackClick(false)
                dialogInterface.cancel()
            }
            .setPositiveButton(R.string.dialog_button_yes) { dialogInterface, _ ->
                callbackClick(true)
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }
}