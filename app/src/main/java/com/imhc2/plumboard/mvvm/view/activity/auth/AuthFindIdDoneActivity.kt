package com.imhc2.plumboard.mvvm.view.activity.auth

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.imhc2.plumboard.R
import com.imhc2.plumboard.mvvm.view.activity.MainActivity
import kotlinx.android.synthetic.main.activity_auth_find_id_done.*
import timber.log.Timber

class AuthFindIdDoneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_find_id_done)
        initToolbar()

        val intent = intent
        Timber.e(intent.getStringExtra("myEmail")?.toString())

        if(intent.hasExtra("myEmail")){
            val result = intent.getStringExtra("myEmail")
            activity_auth_find_id_done_btn.text = result
            activity_auth_find_id_done_btn.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("로그인 화면으로 이동하시겠습니까?")
                builder.setPositiveButton(getString(R.string.dialog_positive_check)) { _, _ ->
                    //startActivity(Intent(this, AuthMainActivity::class.java))

                    val intents = mutableListOf<Intent>(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK),Intent(this,AuthMainActivity::class.java))
                    startActivities(intents.toTypedArray())
                }.setNegativeButton(getString(R.string.dialog_negative_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.create().show()

            }
        }else{
            activity_auth_find_id_done_title.text = "일증 정보와 일치하는 계정이\n존재하지 않습니다"
            activity_auth_find_id_done_btn.text = "로그인 화면으로"
            activity_auth_find_id_done_btn.setOnClickListener {
                var intents = mutableListOf<Intent>(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK),Intent(this,AuthMainActivity::class.java))
                startActivities(intents.toTypedArray())
            }
        }

    }

    private fun initToolbar() {
        activity_auth_find_id_done_toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPlumBoardSub))
        activity_auth_find_id_done_toolbar.title = ""
        setSupportActionBar(activity_auth_find_id_done_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
