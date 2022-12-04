package com.amir.ctrl.data

import com.google.gson.annotations.SerializedName

data class TimeZone(

    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("countryCode") var countryCode: String? = null,
    @SerializedName("countryName") var countryName: String? = null,
    @SerializedName("regionName") var regionName: String? = null,
    @SerializedName("cityName") var cityName: String? = null,
    @SerializedName("zoneName") var zoneName: String? = null,
    @SerializedName("abbreviation") var abbreviation: String? = null,
    @SerializedName("gmtOffset") var gmtOffset: Int? = null,
    @SerializedName("dst") var dst: String? = null,
    @SerializedName("zoneStart") var zoneStart: Int? = null,
    @SerializedName("zoneEnd") var zoneEnd: Int? = null,
    @SerializedName("nextAbbreviation") var nextAbbreviation: String? = null,
    @SerializedName("timestamp") var timestamp: Int? = null,
    @SerializedName("formatted") var formatted: String? = null

)
