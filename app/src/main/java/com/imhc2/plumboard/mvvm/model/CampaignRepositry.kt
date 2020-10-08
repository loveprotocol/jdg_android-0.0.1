package com.imhc2.plumboard.mvvm.model

import android.arch.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.imhc2.plumboard.commons.querys.FirestoreQuerys
import com.imhc2.plumboard.mvvm.model.domain.CampExe
import timber.log.Timber
import java.util.*

/**
 * Created by user on 2018-08-02.
 */

class CampaignRepositry {

    private val data: MutableLiveData<List<CampExe>> = MutableLiveData()

    private val firestore: FirebaseFirestore
    private val mAuth: FirebaseAuth

    //private Query query;

    private var gender: String? = null
    private var loca: String? = null
    private var realAge: String? = null
    private val myCampaignDoneList: MutableList<String> = mutableListOf()
    private var campIdNSts: MutableMap<String, Int> = mutableMapOf()

    private val campExes: MutableList<CampExe>

    init {
        campExes = ArrayList()
        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
    }

    //    public MutableLiveData<List<CampExe>> notUserGetCampaignData() {
    //        FirestoreQuerys.INSTANCE.notUserGetCampaignData(firestore)
    //                .get()
    //                .addOnCompleteListener(task -> {
    //                    if (task.isSuccessful()) {
    //                        for (DocumentSnapshot snapshot : task.getResult()) {
    //                            CampExe campExe = snapshot.toObject(CampExe.class);
    //                            campExe.setId(snapshot.getId());
    //                            list.add(campExe);
    //                        }
    //                        data.setValue(list);
    //                    } else {
    //                        Timber.e(task.getException().getMessage());
    //                    }
    //                });
    //
    //        return data;
    //    }
    fun userNotAuthNotLocGetCampaignExe(): MutableLiveData<List<CampExe>> {
        FirestoreQuerys.userNotAuthGetCampaignPartData(firestore, mAuth.currentUser?.uid)
                //.whereGreaterThanOrEqualTo("scdED", System.currentTimeMillis())
                .get()
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        for (snapshot in task1.result!!) {
                            if (snapshot.exists()) {
                                myCampaignDoneList.add(snapshot.get("inf.campRf") as String)
                            }
                        }
                        //Timber.e("myCampaignDoneList = "+myCampaignDoneList);
                        FirestoreQuerys.userNotAuthNotLocGetCampaign(firestore)
                                .get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        for (snapshot in task.result!!) {
                                            val campExe = snapshot.toObject(CampExe::class.java)
                                            campExe.id = snapshot.id
                                            if (!myCampaignDoneList.contains(campExe.id!!)) {
                                                campExes.add(campExe)
                                            }
                                        }
                                        data.setValue(campExes)
                                    } else {
                                        Timber.e(task.exception?.message)
                                    }
                                }


                    } else {
                        Timber.e(task1.exception?.message)
                    }
                }
        return data
    }

    fun userAuthNotLocGetCampaignExe(): MutableLiveData<List<CampExe>> {
        FirestoreQuerys.userNotAuthGetCampaignPartData(firestore, mAuth.currentUser?.uid)
                //.whereGreaterThanOrEqualTo("scdED", System.currentTimeMillis())
                .get()
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        for (snapshot in task1.result!!) {
                            if (snapshot.exists()) {
                                myCampaignDoneList.add(snapshot.get("inf.campRf") as String)
                            }
                        }

                        FirestoreQuerys.getUserCrd(firestore, mAuth.currentUser!!.uid)
                                .get()//내정보에서 나의 나이,성별 가져오기
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        if (task.result?.exists()!!) {
                                            var age = task.result?.get("jdg.ath.DOB").toString()//DOB: string [ "19901010" ]
                                            val year = Integer.parseInt(age.substring(0, 4))
                                            val month = Integer.parseInt(age.substring(4, 6))
                                            val day = Integer.parseInt(age.substring(6, 8))
                                            realAge = getAge(year, month, day).toString()
                                            gender = task.result?.get("jdg.ath.gndr").toString()//gndr: number [ 0, 1, 2 ]
                                        }
                                        FirestoreQuerys.userAuthNotLocGetCampaign(firestore, realAge, gender)
                                                .get()
                                                .addOnCompleteListener { result ->
                                                    if (result.isSuccessful) {
                                                        for (snapshot in result.result!!) {
                                                            var campExe = snapshot.toObject(CampExe::class.java)
                                                            campExe.id = snapshot.id

                                                            if (!myCampaignDoneList.contains(campExe.id!!)) {
                                                                campExes.add(campExe)
                                                            }
                                                        }
                                                        data.setValue(campExes)
                                                    } else {
                                                        Timber.e(result.exception?.message)
                                                    }
                                                }


                                    } else {
                                        Timber.e(task.exception?.message)
                                    }
                                }


                    } else {
                        Timber.e(task1.exception?.message)
                    }
                }


        return data
    }

    fun userNotAuthLocGetCampaignExe(): MutableLiveData<List<CampExe>> {
        FirestoreQuerys.userNotAuthGetCampaignPartData(firestore, mAuth.currentUser?.uid)
                //.whereGreaterThanOrEqualTo("scdED", System.currentTimeMillis())
                .get()
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        for (snapshot in task1.result!!) {
                            if (snapshot.exists()) {
                                myCampaignDoneList.add(snapshot.get("inf.campRf") as String)
                            }
                        }

                        FirestoreQuerys.getUserCrd(firestore, mAuth.currentUser?.uid)
                                .get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        if (task.result?.exists()!!) {
                                            loca = task.result?.get("jdg.loc.idx").toString()//loc: string [ "012" ]
                                        }
                                        FirestoreQuerys.userNotAuthLocGetCampaign(firestore, loca)
                                                .get()
                                                .addOnCompleteListener { result ->
                                                    if (result.isSuccessful) {
                                                        for (snapshot in result.result!!) {
                                                            var campExe = snapshot.toObject(CampExe::class.java)
                                                            campExe.id = snapshot.id

                                                            if (!myCampaignDoneList.contains(campExe.id!!)) {
                                                                campExes.add(campExe)
                                                            }
                                                        }
                                                        data.setValue(campExes)
                                                    } else {
                                                        Timber.e(result.exception?.message)
                                                    }
                                                }


                                    } else {
                                        Timber.e(task.exception?.message)
                                    }
                                }


                    } else {
                        Timber.e(task1.exception?.message)
                    }
                }


        return data
    }

    fun userAuthLocGetCampaignExe(): MutableLiveData<List<CampExe>> {
        FirestoreQuerys.userNotAuthGetCampaignPartData(firestore, mAuth.currentUser?.uid)
                //.whereGreaterThanOrEqualTo("scdED", System.currentTimeMillis())
                .get()
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        for (snapshot in task1.result!!) {
                            if (snapshot.exists()) {
                                myCampaignDoneList.add(snapshot.get("inf.campRf") as String)
                            }
                        }

                        FirestoreQuerys.getUserCrd(firestore, mAuth.currentUser?.uid)
                                .get()//내정보에서 나의 나이,지역,성별 가져오기
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        if (task.result?.exists()!!) {
                                            var age = task.result?.get("jdg.ath.DOB").toString()//DOB: string [ "19901010" ]
                                            val year = Integer.parseInt(age.substring(0, 4))
                                            val month = Integer.parseInt(age.substring(4, 6))
                                            val day = Integer.parseInt(age.substring(6, 8))
                                            realAge = getAge(year, month, day).toString()
                                            gender = task.result?.get("jdg.ath.gndr").toString()//gndr: number [ 0, 1, 2 ]
                                            loca = task.result?.get("jdg.loc.idx").toString()//loc: string [ "012" ]
                                            Timber.e("나이=$realAge,성별=$gender,지역=$loca")
                                        }
                                        FirestoreQuerys.userAuthLocGetCampaign(firestore, realAge, gender, loca)
                                                .get()
                                                .addOnCompleteListener { result ->
                                                    if (result.isSuccessful) {
                                                        for (snapshot in result.result!!) {
                                                            var campExe = snapshot.toObject(CampExe::class.java)
                                                            campExe.id = snapshot.id

                                                            if (!myCampaignDoneList.contains(campExe.id!!)) {
                                                                campExes.add(campExe)
                                                            }
                                                        }
                                                        data.setValue(campExes)
                                                    } else {
                                                        Timber.e("인증회원 가져오기 실패 ${result.exception?.message}")
                                                    }
                                                }


                                    } else {
                                        Timber.e(task.exception?.message)
                                    }
                                }


                    } else {
                        Timber.e(task1.exception?.message)
                    }
                }


        return data
    }

    fun userNotAuthGetCampaignData(): MutableLiveData<List<CampExe>> {

        FirestoreQuerys.userNotAuthGetCampaignPartData(firestore, mAuth.currentUser?.uid)
                //.whereGreaterThanOrEqualTo("scdED", System.currentTimeMillis())
                .get()
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        for (snapshot in task1.result!!) {
                            if (snapshot.exists()) {
                                myCampaignDoneList.add(snapshot.get("inf.campRf") as String)
                            }
                        }
                        //Timber.e("myCampaignDoneList = "+myCampaignDoneList);
                        FirestoreQuerys.userGetCampaignDataAll(firestore)
                                .get()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        for (snapshot in task.result!!) {
                                            val campExe = snapshot.toObject(CampExe::class.java)
                                            campExe.id = snapshot.id

                                            if (!myCampaignDoneList.contains(campExe.id!!)) {
                                                campExes.add(campExe)
                                            }
                                        }
                                        data.setValue(campExes)
                                    } else {
                                        Timber.e(task.exception!!.message)
                                    }
                                }


                    } else {
                        Timber.e(task1.exception!!.message)
                    }
                }

        return data
    }

    fun userAuthGetCampaignData(): MutableLiveData<List<CampExe>> {
        FirestoreQuerys.userNotAuthGetCampaignPartData(firestore, mAuth.currentUser!!.uid)
                //.whereGreaterThanOrEqualTo("scdED", System.currentTimeMillis())
                .get()
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        for (snapshot in task1.result!!) {
                            if (snapshot.exists()) {
                                myCampaignDoneList.add(snapshot.get("inf.campRf") as String)
                            }
                        }

                        FirestoreQuerys.getUserCrd(firestore, mAuth.currentUser?.uid)
                                .get()//내정보에서 나의 나이,지역,성별 가져오기
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        if (task.result?.exists()!!) {
                                            var age = task.result?.get("jdg.ath.DOB").toString()//DOB: string [ "19901010" ]
                                            val year = Integer.parseInt(age.substring(0, 4))
                                            val month = Integer.parseInt(age.substring(4, 6))
                                            val day = Integer.parseInt(age.substring(6, 8))
                                            realAge = getAge(year, month, day).toString()
                                            gender = task.result?.get("jdg.ath.gndr").toString()//gndr: number [ 0, 1, 2 ]
                                            loca = task.result?.get("jdg.ath.loc").toString()//loc: string [ "012" ]

                                        }
                                        FirestoreQuerys.userAuthGetTargetCamp(firestore, realAge, gender, loca)
                                                .get()
                                                .addOnCompleteListener { result ->
                                                    if (result.isSuccessful) {
                                                        for (snapshot in result.result!!) {
                                                            var campExe = snapshot.toObject(CampExe::class.java)
                                                            campExe.id = snapshot.id

                                                            if (!myCampaignDoneList.contains(campExe.id!!)) {
                                                                campExes.add(campExe)
                                                            }
                                                        }
                                                        data.setValue(campExes)
                                                    } else {
                                                        Timber.e(result.exception?.message)
                                                    }
                                                }


                                    } else {
                                        Timber.e(task.exception?.message)
                                    }
                                }


                    } else {
                        Timber.e(task1.exception?.message)
                    }
                }


        return data
    }

    private fun getAge(birthYear: Int, birthMonth: Int, birthDay: Int): Int {
        val current = Calendar.getInstance()
        val currentYear = current.get(Calendar.YEAR)
        val currentMonth = current.get(Calendar.MONTH) + 1
        val currentDay = current.get(Calendar.DAY_OF_MONTH)

        var age = currentYear - birthYear
        // 생일 안 지난 경우 -1
        val curent = currentMonth * 100 + currentDay
        val my = birthMonth * 100 + birthDay
        if (my > curent)
            age--

        return age
    }

}
