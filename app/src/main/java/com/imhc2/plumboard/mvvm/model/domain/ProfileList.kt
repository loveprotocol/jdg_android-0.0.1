package com.imhc2.plumboard.mvvm.model.domain

/**
 * Created by user on 2018-09-19.
 */
data class ProfileList(
        var title:String?=null,
        var activity:Class<*>? =null,
        var imageResource:Int?=null)