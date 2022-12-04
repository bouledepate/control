package com.amir.ctrl.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val uuid: String? = "",
    val email: String = "",
    val firstName: String? = "",
    val secondName: String? = "",
    val lastName: String? = "",
    val iin: String? = "",
    val phone: String? = "",
    val birthday: String = "",
    val address: String? = "",
    val supervisor: Boolean = false,
    var reports: Int = 0
) : Parcelable
