package com.imhc2.plumboard.mvvm.model.domain

import java.io.Serializable

data class HistoryMoney(
        var inf:Inf2?=null,
        var sts: Long? = null,
        var errDtl: String? = null,
        var wdl:Wdl?=null,
        var docId:String?=null
):Serializable

data class Inf2(
        var cA: Long? = null,
        var cB: String? = null,
        var lA: Long? = null,
        var lB: String? = null
):Serializable


data class Wdl(
        var date:Date?=null,
        var amt:Amt?=null,
        var bank:Bank?=null):Serializable


data class Amt(
        var tot: Long? = null,
        var aPt: Long? = null,
        var pPt: Long? = null,
        var iPtP: Long? = null,
        var iPtA: Long? = null
):Serializable
data class Bank(
        var nm:String?=null,
        var dep:String?=null,
        var acct:String?=null
):Serializable

data class Date(
        var req:Long?=null,
        var proc:Long?=null
):Serializable

