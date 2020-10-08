package com.imhc2.plumboard.mvvm.view.fragment.cardtypes

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.imhc2.plumboard.R
import com.imhc2.plumboard.commons.eventbus.EventBus
import com.imhc2.plumboard.commons.eventbus.Events
import com.imhc2.plumboard.commons.querys.FirestoreQuerys
import com.imhc2.plumboard.mvvm.model.domain.Auth
import com.imhc2.plumboard.mvvm.view.activity.mypage.AuthActivity
import com.imhc2.plumboard.mvvm.view.activity.mypage.LocationActivity
import kotlinx.android.synthetic.main.fragment_type_auth.view.*
import timber.log.Timber

private const val AUTH = "param1"

class AuthFragment : Fragment() {
    private var auth: Auth? = null
    lateinit var firestore: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth
    private var isCheck: Boolean? = false
    lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            auth = it.getSerializable(AUTH) as? Auth?
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_type_auth, container, false)
        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        mView.fragment_type_auth_btn.textOff = auth?.optBd.toString()
        mView.fragment_type_auth_btn.textOn = auth?.optBd.toString()

        mView.fragment_type_auth_title.text = auth?.ttl
        mView.fragment_type_auth_content.text = auth?.desc

        return mView
    }

    override fun onResume() {
        super.onResume()
        getAuthState()
        Timber.e("authfragment onresume")
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser){
            if (isCheck!!) {
                EventBus.get().post(Events.CardResult(""))
            }
        }
    }

    private fun getAuthState() {
        FirestoreQuerys.getUserCrd(firestore, mAuth.currentUser?.uid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val isAuth = task.result?.get("jdg.ath.is") as? Boolean
                        val isLoc = task.result?.get("jdg.loc.is") as? Boolean

                        when (auth?.act) {
                            "AuthActivity" -> {
                                isCheck = isAuth!!
                                mView.fragment_type_auth_btn.isChecked = isCheck!!
                            }

                            "LocationActivity" -> {
                                isCheck = isLoc!!
                                mView.fragment_type_auth_btn.isChecked = isCheck!!
                            }
                        }

                        mView.fragment_type_auth_btn.setOnClickListener {
                            mView.fragment_type_auth_btn.isChecked = !mView.fragment_type_auth_btn.isChecked

                            if(auth?.act.equals("AuthActivity")){
                                if (!mView.fragment_type_auth_btn.isChecked) {
                                    var intent =Intent(activity, AuthActivity::class.java)
                                    intent.putExtra("kind","certification")
                                    startActivity(intent)
                                }
                            }else if(auth?.act.equals("LocationActivity")){
                                if (!mView.fragment_type_auth_btn.isChecked) {
                                    startActivity(Intent(activity, LocationActivity::class.java))
                                }
                            }

                        }
                        if (isCheck!!) {
                            EventBus.get().post(Events.CardResult(""))
                        }
                    } else {
                        Timber.e("AuthActivity error =${task.exception?.message}")
                    }
                }
    }

    companion object {
        @JvmStatic
        fun newInstance(auth: Auth) =
                AuthFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(AUTH, auth)
                    }
                }
    }
}
