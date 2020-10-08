package com.imhc2.plumboard.mvvm.model.domain

import java.io.Serializable

data class Banner(
        var act:String?=null,
        var img:String?=null,
        var isDel:Boolean?=null,
        var tgt:BannerTgt?=null,
        var type:Int?=null
):Serializable

data class BannerTgt(var jdg:Boolean?=null,var ptr:Boolean?=null):Serializable
