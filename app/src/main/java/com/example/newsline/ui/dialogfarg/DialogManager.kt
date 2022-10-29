package com.example.newsline.ui.dialogfarg

import android.content.Context
import androidx.appcompat.app.AlertDialog

object DialogManager {

    fun showDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        var dialog: AlertDialog? = null
        builder.setTitle("Delete ALL?")
        builder.setPositiveButton("OK") { _, _ ->
            listener.onClick()
            dialog?.dismiss()
        }
        builder.setNegativeButton("CANCEL") { _, _ ->
            dialog?.dismiss()
        }
        dialog = builder.create()
        dialog.show()

    }

    interface Listener {
        fun onClick()
    }
}