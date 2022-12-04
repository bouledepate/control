package com.amir.ctrl.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Report(
    val uuid: String? = "",
    val date: String? = "",
    val geolocation: String? = "",
    val image: String? = ""
) : Parcelable
