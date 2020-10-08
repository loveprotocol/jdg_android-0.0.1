package com.imhc2.plumboard.mvvm.view.fragment.dialog


import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.imhc2.plumboard.R
import com.imhc2.plumboard.commons.functions.FunctionEvents
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_location_dialog.view.*
import timber.log.Timber


private const val ADDRESS = "locationAddress"
private const val ZIPCODE = "zipcode"

class LocationDialogFragment : AppCompatDialogFragment() {
    private var address: String? = null
    private var zipcode: String? = null
    private lateinit var firestore:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            address = it.getString(ADDRESS)
            zipcode = it.getString(ZIPCODE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_location_dialog, container, false)
        view.fragment_location_dialog_checkBox.text = address
        events(view)
        firestore= FirebaseFirestore.getInstance()

        firestore
                .collection("admPub").document("gen")
                .get()
                .addOnCompleteListener { task ->
                    var locDate = task.result!!.get("jdgLocLock") as Long //30일
                    view.fragment_location_dialog_content.text =addHtmlTest("<font color='#3F51B5'>지역 정보는 등록 후 ${locDate}일 동안 변경이 불가능하니 신중하게 선택해주세요</font>")
                }

        return view
    }


    private fun events(view:View){
        view.fragment_location_dialog_checkBox.setOnCheckedChangeListener { _, isChecked ->
            view.fragment_location_dialog_yesBtn.isEnabled = isChecked
        }
        view.fragment_location_dialog_cancelBtn.setOnClickListener {
            dialog.dismiss()
            activity?.finish()
        }

        view.fragment_location_dialog_yesBtn.setOnClickListener { _ ->
            var progressDialogHelper = ProgressDialogHelper(this.context!!)
            progressDialogHelper.show()
            FunctionEvents().registerUserLocationAuth(zipcode).addOnCompleteListener { task ->
                progressDialogHelper.dismiss()
                if(task.isSuccessful){
                    val isSuccess = task.result?.get("success") as Boolean
                    if (isSuccess){
                        activity?.finish()
                        dialog.dismiss()
                        Toasty.normal(this?.context!!,"지역 등록이 완료되었습니다",Toast.LENGTH_SHORT).show()
                    }else{
                        val message = task.result?.get("message") as String
                        if(activity?.isFinishing==false){
                            val builder = AlertDialog.Builder(context!!)
                            builder.setMessage(message)
                            builder.setPositiveButton(getString(R.string.dialog_positive_check), DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                            builder.create().show()
                            Timber.e("registerUserLocationAuth fail: " + task.exception?.message)
                        }
                    }

                    Timber.e("location auth function = ${task.result}")
                }else{

                    if(activity?.isFinishing==false){
                        val builder = AlertDialog.Builder(this?.context!!)
                        builder.setMessage(getString(R.string.dialog_function_error))
                        builder.setPositiveButton(getString(R.string.dialog_positive_check), DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                        builder.create().show()
                        Timber.e("registerUserLocationAuth fail: " + task.exception?.message)
                    }

                }
            }
        }
    }
    fun addHtmlTest(tv: String): Spanned? {
        val htmlAsSpanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(tv, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(tv)
        }
        return htmlAsSpanned
    }

    companion object {
        @JvmStatic
        fun newInstance(zipcode: String, address: String) =
                LocationDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(ADDRESS, address)
                        putString(ZIPCODE, zipcode)
                    }
                }
    }

}
