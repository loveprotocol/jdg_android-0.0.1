package com.imhc2.plumboard.mvvm.model.domain

import java.io.Serializable


/**
 * Created by user on 2018-09-10.
 */
data class PointState(
        var state:String?=null,
        var date:String?=null,
        var point:Long?=null,
        var what:String?=null
):Serializable
