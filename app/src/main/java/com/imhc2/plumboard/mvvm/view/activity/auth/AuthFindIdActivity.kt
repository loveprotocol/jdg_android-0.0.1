package com.imhc2.plumboard.mvvm.view.activity.auth

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.imhc2.plumboard.R
import com.imhc2.plumboard.mvvm.view.activity.mypage.AuthActivity
import kotlinx.android.synthetic.main.activity_auth_find_id.*

const val REQUEST_ACTIVITY: Int = 110

class AuthFindIdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_find_id)

        setSupportActionBar(activity_auth_find_id_toolbar)
        supportActionBar.apply {
            this?.setDisplayHomeAsUpEnabled(true)
            this?.title = ""
        }

        activity_auth_find_id_btn.setOnClickListener {
            var intent = Intent(this, AuthActivity::class.java)
            intent.putExtra("kind", "findId")
            startActivity(intent)
            //startActivityForResult(intent, REQUEST_ACTIVITY)
        }

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_ACTIVITY && resultCode == Activity.RESULT_OK) {
//            val result = data?.getStringExtra("email")
//            activity_auth_find_id_btn.text = result
//            activity_auth_find_id_toolbar_title.text = "인증 정보와 일치하는 계정은\n다음과 같습니다"
//            activity_auth_find_id_btn.setOnClickListener {
//                var builder = AlertDialog.Builder(this)
//                builder.setMessage("로그인 화면으로 이동하시겠습니까?")
//                builder.setPositiveButton(getString(R.string.dialog_positive_check)) { _, _ ->
//                    startActivity(Intent(this, AuthMainActivity::class.java))
//
//                    var intents = mutableListOf<Intent>(Intent(this,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK),Intent(this,AuthMainActivity::class.java))
//                    startActivities(intents.toTypedArray())
//
//                }.setNegativeButton(getString(R.string.dialog_negative_cancel)) { dialog, _ ->
//                            dialog.dismiss()
//                        }.create().show()
//            }
//        }
//
//    }

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
