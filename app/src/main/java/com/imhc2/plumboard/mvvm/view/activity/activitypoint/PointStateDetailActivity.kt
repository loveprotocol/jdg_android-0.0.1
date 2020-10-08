package com.imhc2.plumboard.mvvm.view.activity.activitypoint

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.imhc2.plumboard.R
import com.imhc2.plumboard.commons.functions.FunctionEvents
import com.imhc2.plumboard.mvvm.model.domain.HistoryMoney
import com.imhc2.plumboard.mvvm.view.fragment.dialog.ProgressDialogHelper
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_point_state_detail.*
import timber.log.Timber
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class PointStateDetailActivity : AppCompatActivity() {

    lateinit var histroyMoney: HistoryMoney
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_state_detail)

        mAuth = FirebaseAuth.getInstance()

        if (intent.hasExtra("historyMonies")) {
            histroyMoney = intent.getSerializableExtra("historyMonies") as HistoryMoney
            activity_point_state_detail_moneyTv.text = NumberFormat.getInstance().format(histroyMoney.wdl?.amt?.tot)
            activity_point_state_detail_bankNumTv.text = histroyMoney.wdl?.bank?.acct.toString()
            activity_point_state_detail_nameTv.text = histroyMoney.wdl?.bank?.dep
            activity_point_state_detail_bankTv.text = histroyMoney.wdl?.bank?.nm
            activity_point_state_detail_dateTv.text = convertDate(histroyMoney.wdl?.date?.req, "yyyy-MM-dd kk:mm")


            when (histroyMoney.sts?.toInt()) {
                1 -> {
                    activity_point_state_detail_stateTv.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPointStateAccept))
                    activity_point_state_detail_stateTv.text = getString(R.string.PointStateActivity_accept)
                    //activity_point_state_detail_cancelBtn.visibility = View.VISIBLE mytodo:일시 중시
                }
                2 -> {
                    activity_point_state_detail_stateTv.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPointStateSuccess))
                    activity_point_state_detail_stateTv.text = getString(R.string.PointStateActivity_done)
                    if (histroyMoney.wdl?.date?.proc != null) {
                        activity_point_state_detail_dateDoneRl.visibility=View.VISIBLE
                        activity_point_state_detail_dateDoneTv.text = convertDate(histroyMoney.wdl?.date?.proc, "yyyy-MM-dd kk:mm")
                    }
                }
                3 -> {
                    activity_point_state_detail_stateTv.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPointStateError))
                    activity_point_state_detail_stateTv.text = getString(R.string.PointStateActivity_error)
                    if (histroyMoney.wdl?.date?.proc != null) {
                        activity_point_state_detail_dateDoneRl.visibility=View.VISIBLE
                        activity_point_state_detail_dateDoneTv.text = convertDate(histroyMoney.wdl?.date?.proc, "yyyy-MM-dd kk:mm")
                    }

                    activity_point_state_detail_errorRl.visibility = View.VISIBLE
                    if (histroyMoney.errDtl != null) {
                        activity_point_state_detail_errorTv.text = histroyMoney.errDtl.toString()
                    }
                }
                4 -> {
                    activity_point_state_detail_stateTv.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPointStateCancel))
                    activity_point_state_detail_stateTv.text = getString(R.string.PointStateActivity_cancel)
                    if (histroyMoney.wdl?.date?.proc != null) {
                        activity_point_state_detail_dateDoneRl.visibility=View.VISIBLE
                        activity_point_state_detail_dateDoneTv.text = convertDate(histroyMoney.wdl?.date?.proc, "yyyy-MM-dd kk:mm")
                    }
                }
            }


        }

        activity_point_state_detail_cancelBtn.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setMessage(getString(R.string.dialog_title_cancel))
                setPositiveButton(getString(R.string.dialog_positive_check)) { dialog, _ ->
                    var dialogHelper = ProgressDialogHelper(context)
                    dialogHelper.show()
                    FunctionEvents().cancelWithdraw(histroyMoney.docId).addOnCompleteListener { task ->
                        dialogHelper.dismiss()
                        if (task.isSuccessful) {
                            val isSuccess = task.result?.get("success") as Boolean
                            if (isSuccess) {
                                Timber.e("cancelWithdraw 성공")
                                finish()
                                Toasty.normal(context, getString(R.string.toast_PointStateDetailActivity_success), Toast.LENGTH_SHORT).show()
                            } else {
                                val message = task.result?.get("message") as String
                                if (!this@PointStateDetailActivity.isFinishing) {
                                    val builder = AlertDialog.Builder(context)
                                    builder.setMessage(message)
                                    builder.setPositiveButton(getString(R.string.dialog_positive_check)) { dialog, which -> dialog.dismiss() }
                                    builder.create().show()
                                    Timber.e("cancelWithdraw fail: " + task.exception?.message)
                                }
                            }

                        } else {
                            if (!this@PointStateDetailActivity.isFinishing) {
                                val builder = AlertDialog.Builder(context)
                                builder.setMessage(getString(R.string.dialog_function_error))
                                builder.setPositiveButton(getString(R.string.dialog_positive_check)) { dialog, which -> dialog.dismiss() }
                                builder.create().show()
                                Timber.e("cancelWithdraw fail: " + task.exception?.message)
                            }
                        }
                    }
                    dialog.dismiss()
                }
                setNegativeButton(getString(R.string.dialog_negative_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                create().show()
            }

        }

        initView()

    }


    fun initView() {
        activity_point_state_detail_toolbar.title = getString(R.string.PointStateDetailActivity_toolbarTitle)
        activity_point_state_detail_toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPlumBoardSub))

        setSupportActionBar(activity_point_state_detail_toolbar)
        supportActionBar.apply {
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    fun convertDate(dateInMilliseconds: Long?, dateFormat: String): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.KOREA)
        formatter.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return formatter.format(Date(dateInMilliseconds!!))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}
