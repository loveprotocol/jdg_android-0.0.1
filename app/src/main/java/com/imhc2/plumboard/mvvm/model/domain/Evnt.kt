package com.imhc2.plumboard.mvvm.model.domain

import java.io.Serializable


data class Rcmd(
        var mEvntCt: MEvntCt? = null,
        var ratePerCt: RatePerCt? = null,
        var iS: Boolean? = null,
        var mRate: Double? = null
) : Serializable

data class RatePerCt(
        var ent: Double? = null,
        var entBy: Double? = null
) : Serializable

data class MEvntCt(
        var mEnt: Long? = null,
        var mEntBy: Long? = null
) : Serializable


data class RcmdUser(
        var code: String? = null,
        var cumEntBy: Long? = null,
        var evntCt: EvntCt? = null,
        var evntInf: EvntInf? = null
) : Serializable

data class EvntCt(
        var ent: Long? = null,
        var entBy: Long? = null
) : Serializable

data class EvntInf(
        var cA: Long? = null,
        var cB: String? = null,
        var lA: Long? = null,
        var lB: String? = null
) : Serializable


data class Cnstv(
        var cIs: Boolean? = null,
        var mEvntCt: Long? = null,
        var mRate: Double? = null,
        var ratePerCt: Double? = null
) : Serializable

data class CnstvUser(
        var evntCt: Long? = null,
        var inf: EvntInf? = null,
        var prgs: List<String>? = null
) : Serializable

data class Evnt(
        var rcmd: Rcmd? = null,
        var rcmduser: RcmdUser? = null,
        var cnstv: Cnstv? = null,
        var cnstvUser: CnstvUser? = null
) : Serializable


