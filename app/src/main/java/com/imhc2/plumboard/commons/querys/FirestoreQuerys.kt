package com.imhc2.plumboard.commons.querys

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object FirestoreQuerys {
    //=============== 비회원 캠페인 불러오기 ========================

    //회원가입이 안된 유저의 home 캠페인 불러오기
    fun notUserGetCampaignData(firestore: FirebaseFirestore): Query {
        return firestore
                .collection("campExe")
                .whereEqualTo("sts", 1)
                .orderBy("camp.pop.tot", Query.Direction.DESCENDING)
                .limit(10)
    }

    //비회원 모든타입 필터값 최대값이 500 이상일때
    fun notUserFilterMaxGetCampaign(firestore: FirebaseFirestore, minValue: Int): Query {
        return firestore.collection("campExe")
                .whereEqualTo("sts", 1)
                .whereGreaterThanOrEqualTo("camp.bgt.jdg", minValue)
                .orderBy("camp.bgt.jdg", Query.Direction.DESCENDING)
    }

    //비회원 모든타입 필터 최소 최대값을 알때
    fun notUserFilterMinMaxGetCampaign(firestore: FirebaseFirestore, minValue: Int, maxValue: Int): Query {
        return firestore.collection("campExe")
                .whereEqualTo("sts", 1)
                .whereGreaterThanOrEqualTo("camp.bgt.jdg", minValue)
                .whereLessThanOrEqualTo("camp.bgt.jdg", maxValue)
                .orderBy("camp.bgt.jdg", Query.Direction.DESCENDING)
    }

    //비회원 비디오타입 필터 최대값이 500이상일떄
    fun notUserFilterVideoMaxGetCampaign(firestore: FirebaseFirestore, minValue: Int): Query {
        return firestore.collection("campExe")
                .whereEqualTo("sts", 1)
                .whereEqualTo("camp.type", 1)
                .whereGreaterThanOrEqualTo("camp.bgt.jdg", minValue)
                //.orderBy(FieldPath.of("camp", "pop", "tot"), Query.Direction.DESCENDING);
                .orderBy("camp.bgt.jdg", Query.Direction.DESCENDING)
    }

    //비회원 모든타입 필터 최소 최대값을 알때
    fun notUserFilterVideoMinMaxGetCampaign(firestore: FirebaseFirestore, minValue: Int, maxValue: Int): Query {
        return firestore.collection("campExe")
                .whereEqualTo("sts", 1)
                .whereEqualTo(FieldPath.of("camp", "type"), 1)
                .whereGreaterThanOrEqualTo(FieldPath.of("camp", "bgt", "jdg"), minValue)
                .whereLessThanOrEqualTo(FieldPath.of("camp", "bgt", "jdg"), maxValue)
                //.orderBy(FieldPath.of("camp", "pop", "tot"), Query.Direction.DESCENDING);
                .orderBy(FieldPath.of("camp", "bgt", "jdg"), Query.Direction.DESCENDING)
    }

    //비회원 비디오타입 필터 최대값이 500이상일떄
    fun notUserFilterSurveyMaxGetCampaign(firestore: FirebaseFirestore, minValue: Int): Query {
        return firestore.collection("campExe")
                .whereEqualTo("sts", 1)
                .whereEqualTo(FieldPath.of("camp", "type"), 2)
                .whereGreaterThanOrEqualTo(FieldPath.of("camp", "bgt", "jdg"), minValue)
                //.orderBy(FieldPath.of("camp", "pop", "tot"), Query.Direction.DESCENDING);
                .orderBy(FieldPath.of("camp", "bgt", "jdg"), Query.Direction.DESCENDING)
    }

    //비회원 모든타입 필터 최소 최대값을 알때
    fun notUserFilterSurveyMinMaxGetCampaign(firestore: FirebaseFirestore, minValue: Int, maxValue: Int): Query {
        return firestore.collection("campExe")
                .whereEqualTo("sts", 1)
                .whereEqualTo(FieldPath.of("camp", "type"), 2)
                .whereGreaterThanOrEqualTo(FieldPath.of("camp", "bgt", "jdg"), minValue)
                .whereLessThanOrEqualTo(FieldPath.of("camp", "bgt", "jdg"), maxValue)
                //.orderBy(FieldPath.of("camp", "pop", "tot"), Query.Direction.DESCENDING);
                .orderBy(FieldPath.of("camp", "bgt", "jdg"), Query.Direction.DESCENDING)
    }


    //======================================================


    //====================회원유저 캠페인 불러오기===============

    // 집행중인 캠페인을 불러오는 쿼리
    fun userGetCampaignDataAll(firestore: FirebaseFirestore): Query {
        return firestore
                .collection("campExe")
                .whereEqualTo("sts", 1)
    }

    //회원 - 본인인증 x 위치 x
    fun userNotAuthNotLocGetCampaign(firestore: FirebaseFirestore): Query {
        return firestore
                .collection("campExe")
                .whereEqualTo("camp.tgt.athReq", false)
                .whereEqualTo("camp.tgt.locReq", false)
                .whereEqualTo("sts", 1)
    }

    //회원 - 본인인증 o 위치 x
    fun userAuthNotLocGetCampaign(firestore: FirebaseFirestore, age: String?, gender: String?): Query {
        return firestore
                .collection("campExe")
                .whereEqualTo("camp.tgt.age.$age", true)
                .whereEqualTo("camp.tgt.gndr.$gender", true)
                .whereEqualTo("camp.tgt.locReq", false)
                .whereEqualTo("sts", 1)
    }

    //회원 - 본인인증 x 위치 o
    fun userNotAuthLocGetCampaign(firestore: FirebaseFirestore, loc: String?): Query {
        return firestore
                .collection("campExe")
                .whereEqualTo("camp.tgt.athReq", false)
                .whereEqualTo("camp.tgt.loc.$loc", true)
                .whereEqualTo("sts", 1)
    }

    //회원 - 본인인증 o 위치 o
    fun userAuthLocGetCampaign(firestore: FirebaseFirestore, age: String?, gender: String?, loc: String?): Query {
        return firestore
                .collection("campExe")
                .whereEqualTo("camp.tgt.age.$age", true)
                .whereEqualTo("camp.tgt.gndr.$gender", true)
                .whereEqualTo("camp.tgt.loc.$loc", true)
                .whereEqualTo("sts", 1)
    }


    //인증이 안되거나,된 회원유저의 내가 한 캠페인들 불러오는 쿼리
    fun userNotAuthGetCampaignPartData(firestore: FirebaseFirestore, myUid: String?): Query {
        return firestore
                .collection("campPart")
                .whereEqualTo(FieldPath.of("inf", "cB"), myUid)
                .whereEqualTo("sts", 2)
    }

    //집행중인 캠페인중  나이 성별 지역에 맞는걸 가져오는 쿼리
    fun userAuthGetTargetCamp(firestore: FirebaseFirestore, age: String?, gender: String?, loc: String?): Query {
        return firestore.collection("campExe")
                .whereEqualTo("sts", 1)
                .whereEqualTo(FieldPath.of("camp", "tgt", "age", age), true)
                .whereEqualTo(FieldPath.of("camp", "tgt", "gndr", gender), true)
                .whereEqualTo(FieldPath.of("camp", "tgt", "loc", loc), true)
    }
    //=======================================================

    //dynamicLink로 들어온 캠페인가져오기
    fun getCampExeSingle(firestore: FirebaseFirestore, campaignId: String): DocumentReference {
        return firestore
                .collection("campExe")
                .document(campaignId)
    }


    //연속참가보상의 유저정보
    fun getEventUserCnstv(firestore: FirebaseFirestore, myUid: String): DocumentReference {
        return firestore
                .collection("evnt").document("cnstv")
                .collection("usr").document(myUid)
    }

    //연속참가보상
    fun getEventCnstv(firestore: FirebaseFirestore): DocumentReference {
        return firestore
                .collection("evnt").document("cnstv")
    }


    //추천인의 유저정보
    fun getEventUserRcmd(firestore: FirebaseFirestore, myUid: String): DocumentReference {
        return firestore
                .collection("evnt").document("rcmd")
                .collection("usr").document(myUid)
    }


    //추천인
    fun getEventRcmd(firestore: FirebaseFirestore): DocumentReference {
        return firestore
                .collection("evnt").document("rcmd")
    }

    //개인정보 가져오기2
    fun getUserUsr(firestore: FirebaseFirestore, myUid: String): DocumentReference {
        return firestore
                .collection("usr").document(myUid)
    }


    //인증여부,포인트, 나이 지역 성별 를 위한 개인정보 가져오기
    fun getUserCrd(firestore: FirebaseFirestore, myUid: String?): DocumentReference {
        return firestore
                .collection("usrCrd").document(myUid.toString())
    }

    //매일매일의 개인데이터 가져오기
    fun getUserCrdDaily(firestore: FirebaseFirestore, myUid: String, day: String): DocumentReference {
        return firestore
                .collection("usrCrd").document(myUid)
                .collection("daily").document(day)
    }

    //나의 정보에서 이번주 월요일 0시0분 부터의 정보를 불러옴
    fun getUserDailyFirstTime(firestore: FirebaseFirestore, myUid: String, startTime: Long): Query {
        return firestore
                .collection("usrCrd").document(myUid)
                .collection("daily")
                .whereGreaterThanOrEqualTo("inf.cA", startTime)
    }

    //은행리스트 불러오기
    fun getBankList(firestore: FirebaseFirestore): DocumentReference {
        return firestore
                .collection("admPub").document("bank")
    }

    //faq 불러오기
    fun getFaq(firestore: FirebaseFirestore): Query {
        return firestore
                .collection("admPub").document("FAQ")
                .collection("FAQ")
                .whereEqualTo("tgt.jdg", true)
                .whereEqualTo("sts", 1)
    }

    //공지사항 데이터 불러오기
    fun getNotice(firestore: FirebaseFirestore): Query {
        return firestore.collection("admPub").document("ntc")
                .collection("ntc")
                .whereEqualTo("tgt.jdg", true)
                .whereEqualTo("sts", 1)
    }

    //알림 데이터 불러오기
    fun getAlarm(firestore: FirebaseFirestore, myUid: String): Query {
        return firestore.collection("usrCrd").document(myUid)
                .collection("ntf")
                .whereEqualTo("tgt.jdg", true)
    }

    //  내가 캠페인 집행한것들중 진행중인것 체크하기 (캠페인참여한적있나 체크)
    fun checkMyCampPart(firestore: FirebaseFirestore, myUid: String/*, campaignId: String*/): Query {
        return firestore.collection("campPart")
                .whereEqualTo("sts", 1)//진행중
                .whereEqualTo(FieldPath.of("inf", "cB"), myUid)//내꺼
        //.whereEqualTo(FieldPath.of("inf", "campRf"), campaignId)
    }

    //나의 포인트출금현황 불러오기
    fun getPointState(firestore: FirebaseFirestore, myUid: String): Query {
        return firestore
                .collection("hist").document("jdg")
                .collection("wdl")
                .whereEqualTo(FieldPath.of("inf", "cB"), myUid)
                .orderBy("inf.cA", Query.Direction.DESCENDING)
    }

    //배너 데이터 가져오기
    fun getBanners(firestore: FirebaseFirestore): Query {
        return firestore
                .collection("banner")
                .whereEqualTo("tgt.jdg", true)
    }

    //튜토리얼 데이터 가져오기
    fun getJdgTut(firestore: FirebaseFirestore): Query {
        return firestore.collection("jdgTut")
    }

    // progress 상태 가져오기
    fun getRate(firestore: FirebaseFirestore): Query {
        return firestore.collection("evnt")
                .whereEqualTo("is", true)
    }
    //캠페인1 가져오기
    fun getTut1(firestore: FirebaseFirestore): Query {
        return firestore.collection("jdgTut")
                .whereEqualTo("cDataRf", "tut1")
    }
    //캠페인2 가져오기
    fun getTut2(firestore: FirebaseFirestore): Query {
        return firestore.collection("jdgTut")
                .whereEqualTo("cDataRf", "tut2")
    }

    //=============== update 들  ========================

    //로그인시 나의정보에 시간 업데이트
    fun updateMyInfo(myUid: String, documentReference: DocumentReference): Task<Void> {
        return documentReference.update("inf.lA", System.currentTimeMillis(), "inf.lB", myUid);
    }

    //닉네임 업데이트
    fun updateNickName(documentReference: DocumentReference, nickName: String): Task<Void> {
        return documentReference.update("jdg.nm", nickName)
    }

    //닉네임,이미지 업데이트
    fun updateNickNameImage(documentReference: DocumentReference, nickName: String, imageUrl: String): Task<Void> {
        return documentReference.update("jdg.nm", nickName, "jdg.tImg", imageUrl)
    }

    //기본이미지로 업데이트
    fun updateMyImage(documentReference: DocumentReference, imageUrl: String): Task<Void> {
        return documentReference.update("jdg.tImg", imageUrl)
    }


}
