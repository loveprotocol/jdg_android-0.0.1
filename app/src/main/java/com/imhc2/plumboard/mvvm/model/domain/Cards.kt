package com.imhc2.plumboard.mvvm.model.domain

import java.io.Serializable

/**
 * Created by user on 2018-08-08.
 */

open class Cards() : Serializable

data class Dp(//설명용 텍스트
        var ord: Int? = null,//순서
        var ttl: String? = null,//제목
        var type: String? = null,//타입
        var bd: String? = null,//본문
        var imgA: Boolean? = null,//이미지 첨부여부
        var img: String? = null//이미지 경로
) : Cards()

data class La(//장문형
        var ord: Int? = null,//순서
        var ttl: String? = null,//제목
        var type: String? = null,//타입
        var req: Boolean? = null,//필수여부
        var descA: Boolean? = null,//설명여부
        var imgA: Boolean? = null,//이미지첨부여부
        var desc: String? = null,//설명데이터
        var img: String? = null,//이미지경로
        var tLA: Boolean? = null,//답변 길이 제한 활성화 여부
        var tLM: Int? = null//답변 길이 제한 활성시 최대 길이
) : Cards()

data class Ls(//직선형척도
        var ord: Int? = null,//순서
        var ttl: String? = null,//제목
        var type: String? = null,//타입
        var req: Boolean? = null,//필수 여부
        var descA: Boolean? = null,//설명 여부
        var imgA: Boolean? = null,//이미지 첨부 여부
        var desc: String? = null,//설명 데이터
        var img: String? = null,//이미지 경로
        var sL: Int? = null,//직선형 척도 최소값
        var sH: Int? = null,//직선형 척도 최대값
        var lL: String? = null,//직선형 척도 최소 라벨
        var lH: String? = null//직선형 척도 최대 라벨
) : Cards()

data class Mc(//객관식
        var ord: Int? = null,//순서
        var ttl: String? = null,//제목
        var type: String? = null,//타입
        var res: List<String>? = null,//옵션들
        var req: Boolean? = null,//필수 여부
        var descA: Boolean? = null,//설명 여부
        var imgA: Boolean? = null,//이미지 첨부 여부
        var desc: String? = null,//설명 데이터
        var img: String? = null,//이미지 경로
        var sfl: Boolean? = null,//랜덤보기 순서 활성화 여부
        var mA: Boolean? = null,//다수 선택 활성화 여부
        var mL: Int? = null,//다수 선택 활성화 시 최소 답변
        var mH: Int? = null//다수 선택 활성화 시 최대 답변
) : Cards()

data class Sa(//단답형 글
        var ord: Int? = null,//순서
        var ttl: String? = null,//제목
        var type: String? = null,//타입
        var req: Boolean? = null,//필수 여부
        var descA: Boolean? = null,//설명 여부
        var imgA: Boolean? = null,//이미지 첨부 여부
        var desc: String? = null,//설명 데이터
        var img: String? = null,//이미지 경로
        var tLA: Boolean? = null,//답변 길이 제한 활성화 여부
        var tLM: Int? = null//답변 길이 제한 활성화 시 최대 길이
) : Cards()

data class Vd(//동영상
        var ord: Int? = null,//순서
        var ttl: String? = null,//제목
        var type: String? = null,//타입
        var req: Boolean? = null,//필수 여부
        var descA: Boolean? = null,//설명 여부
        var desc: String? = null,//설명 데이터
        var nS: Boolean? = null,//최초 재생시 스킵불가 여부
        var vP: String? = null//비디오주소경로
) : Cards()

data class Auth(//인증
        var ord: Int? = null,//순서
        var ttl: String? = null,//제목
        var type: String? = null,//종류
        var descA: Boolean? = null,//설명여부
        var desc: String? = null,//설명데이터
        var req: Boolean? = null,//필수여부
        var optBd: String? = null,//옵션설명
        var act: String? = null//옵션 클릭시 행동
) : Cards()

data class McSubList(var item: String? = null)



