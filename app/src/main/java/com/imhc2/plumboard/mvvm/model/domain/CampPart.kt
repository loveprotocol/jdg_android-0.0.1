package com.imhc2.plumboard.mvvm.model.domain

import java.io.Serializable

data class CampPart(
        var inf: CampPartInf? = null,
        var sts: Long? = null,
        var endAt: Long? = null,
        var scdED: Long? = null
)

data class CampPartInf(
        var cA: Long? = null,
        var cB: String? = null,
        var lA: Long? = null,
        var lB: String? = null,
        var prjRf: String? = null
) : Serializable