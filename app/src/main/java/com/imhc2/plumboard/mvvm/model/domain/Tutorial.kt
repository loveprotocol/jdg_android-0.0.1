package com.imhc2.plumboard.mvvm.model.domain

data class Tutorial(
        var pubNm: String? = null,//게시자이름
        var pubTImg: String? = null,//게시자 썸네일이미지
        var ttl: String? = null,//타이틀
        var bd: String? = null,//본문
        var img: String? = null,//이미지주소
        var pPC: Int? = null,//포인트
        var rtg: TutRtg? = null,//평점
        var cDataRf: String? = null//RTDB 경로
)

data class TutRtg(
        var cum: Float? = null,//평점합계
        var avg: Float? = null,//평균
        var ct: Int? = null//횟수
)