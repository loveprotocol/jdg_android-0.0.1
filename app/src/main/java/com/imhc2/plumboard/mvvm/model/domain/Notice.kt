package com.imhc2.plumboard.mvvm.model.domain

import java.io.Serializable

data class Notice(
        var inf:NoticeInf?=null,
        var date:Long?=null,
        var tgt:NoticeTgt?=null,
        var sts:Int?=null,
        var ttl:String?=null,
        var bd:String?=null,
        var btnIs:Boolean?=null,
        var btnNm:String?=null,
        var btnFunc:String?=null):Serializable


data class NoticeTgt(
        var jdg:Boolean?=null,
        var ptr:Boolean?=null
):Serializable

data class NoticeInf(
        var cB:String?=null,
        var cA:Long?=null,
        var lB:String?=null,
        var lA:Long?=null):Serializable