package com.imhc2.plumboard.mvvm.model.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.io.Serializable

/**
 * Created by user on 2018-08-02.
 */
@Parcelize
data class CampExe(
        var inf: @RawValue Inf? = null,
        var sts:@RawValue Int? = null,
        var stsDtl:@RawValue Int? = null,
        var scdED:@RawValue Double? = null,
        var cmpAt: @RawValue Long? = null,
        var prj: @RawValue Prj? = Prj("",""),
        var camp: @RawValue Camp? = null,
        var id: @RawValue String? = null
) : Parcelable

data class Camp(
        var ttl: String? = null,//캠페인제목
        var bd: String? = null,//캠페인본문
        var img: String? = null,//캠페인 이미지
        var type: Int? = null, //캠페인 종류
        var bgt :Bgt?=null,
        var tgt: Tgt? = null, //타겟
        var cD: @RawValue Cd? = null,//수집정보
        var cont: @RawValue Cont? = null,//
        var pop: @RawValue Pop? = null,//
        var rtg: @RawValue Rtg? = Rtg(0f,0,0f)//
):Serializable

data class Bgt(
        var pmtType:Int?=null,
        var tot:Int?=null,
        var jdg:Int?=null,
        var adm:Int?=null,
        var vat:Int?=null
):Serializable

data class Cd(
        var age: Boolean? = null,
        var gndr: Boolean? = null,
        var loc: Boolean? = null
):Serializable

data class Cont(
        var cCt: Int? = null,
        var vCt: Int? = null,
        var vLth: MutableList<Int>? = null
):Serializable

data class Inf(
        var cA: Long? = null,
        var cB: String? = null,
        var lA: Long? = null,
        var lB: String? = null,
        var prjRf: String? = null
):Serializable

data class Pop(
        var pt: Double? = null,
        var rtg: Double? = null,
        var tot: Double? = null
):Serializable

data class Prj(
        var tImg: String? = null,
        var ttl: String? = null
):Serializable

data class Rtg(
        var avg: Float? = null,
        var ct: Int? = null,
        var cum: Float? = null
):Serializable

data class Tgt(
        var athReq: Boolean? = null,
        var locReq: Boolean? = null,
        var gndr: Map<String, Boolean>? = null,
        var age: Map<String, Boolean>? = null,
        var loc: Map<String, Boolean>? = null
):Serializable
