package com.imhc2.plumboard.mvvm.model.domain

import java.io.Serializable

data class Faq(
        var bd:String?=null,
        var cat:String?=null,
        var ord:Int?=null,
        var inf:FaqInf?=null,
        var sts:Int?=null,
        var tgt:FaqTgt?=null,
        var ttl:String?=null
):Serializable

data class FaqTgt(
        var jdg:Boolean?=null,
        var ptr:Boolean?=null
):Serializable

data class FaqInf(
        var cA:Long?=null,
        var cB:String?=null,
        var lA:Long?=null,
        var lB:String?=null
):Serializable