package com.imhc2.plumboard.mvvm.view.fragment.dialog

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatDialog
import com.imhc2.plumboard.R
import timber.log.Timber

class ProgressDialogHelper(var context: Context) {
    var appCompatDialog: AppCompatDialog = AppCompatDialog(context)

    init {
        appCompatDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        //appCompatDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)//불투명색 없애기
        appCompatDialog.setContentView(R.layout.fragment_progress)
        appCompatDialog.setCancelable(false)
    }

    fun show() {
        if(!(context as Activity).isFinishing){
            Timber.e("ProgressDialogHelper show ${context.javaClass}")
            if (!appCompatDialog.isShowing) {
                appCompatDialog.show()
            }
        }
    }

    fun dismiss() {
        if(!(context as Activity).isFinishing){
            Timber.e("ProgressDialogHelper dismiss ${context.javaClass}")
            if (appCompatDialog.isShowing) {
                appCompatDialog.dismiss()
            }
        }

    }
}