package com.imhc2.plumboard.mvvm.view.fragment.dialog


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.imhc2.plumboard.R
import com.imhc2.plumboard.commons.functions.FunctionEvents
import com.imhc2.plumboard.mvvm.view.activity.MainActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_account_freeze.view.*
import timber.log.Timber

class AccountFreezeFragment : DialogFragment() {

    lateinit var dialogHelper: ProgressDialogHelper
    lateinit var mAuth: FirebaseAuth
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account_freeze, container, false)
        mAuth = FirebaseAuth.getInstance()

        view.fragment_account_freeze_content.text = filterHtml(getString(R.string.AccountFreezeFragment_contentTv))
        view.fragment_account_freeze_checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            view.fragment_account_freeze_yesBtn.isEnabled = isChecked
        }

        view.fragment_account_freeze_cancelBtn.setOnClickListener {
            dismiss()
        }

        dialogHelper = ProgressDialogHelper((activity as Context?)!!)
        view.fragment_account_freeze_yesBtn.setOnClickListener {
            dialogHelper.show()
            FunctionEvents().disabledAccount().addOnCompleteListener { task ->
                dialogHelper.dismiss()
                if (task.isSuccessful) {
                    val isSuccess=task.result?.get("success") as? Boolean
                    if (isSuccess!=null &&isSuccess) {
                        mAuth.signOut()
                        Timber.e("disabledAccount : 성공!")
                        val intent = Intent(activity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        activity?.finish()
                    }else{
                        val message=task.result?.get("message") as? String
                        if (!(activity?.isFinishing)!!) {
                            Toasty.normal(context!!,message!!,Toast.LENGTH_SHORT).show()
//                            val builder = AlertDialog.Builder(activity!!)
//                            builder.setMessage(message)
//                            builder.setPositiveButton(getString(R.string.dialog_positive_check)) { dialog, which -> dialog.dismiss() }
//                            builder.create().show()
                            Timber.e("disabledAccount fail: " + task.exception?.message)
                        }
                    }
                } else {
                    if (!(activity?.isFinishing)!!) {
                        Toasty.normal(context!!,getString(R.string.dialog_function_error),Toast.LENGTH_SHORT).show()
//                        val builder = AlertDialog.Builder(activity!!)
//                        builder.setMessage(getString(R.string.dialog_function_error))
//                        builder.setPositiveButton(getString(R.string.dialog_positive_check)) { dialog, which -> dialog.dismiss() }
//                        builder.create().show()
                        Timber.e("disabledAccount fail: " + task.exception?.message)
                    }

                }

            }
        }

        return view
    }

    override fun onStop() {
        super.onStop()
        dialogHelper.dismiss()
    }

    fun filterHtml(tv: String): Spanned? {
        val htmlAsSpanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(tv, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(tv)
        }
        return htmlAsSpanned
    }
}
