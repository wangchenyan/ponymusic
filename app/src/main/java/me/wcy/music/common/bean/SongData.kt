package me.wcy.music.common.bean

import com.google.gson.annotations.SerializedName

data class SongData(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("pst")
    val pst: Int = 0,
    @SerializedName("t")
    val t: Int = 0,
    @SerializedName("ar")
    val ar: List<ArtistData> = listOf(),
    @SerializedName("pop")
    val pop: Int = 0,
    @SerializedName("st")
    val st: Int = 0,
    @SerializedName("rt")
    val rt: String = "",
    @SerializedName("fee")
    val fee: Int = 0,
    @SerializedName("v")
    val v: Int = 0,
    @SerializedName("cf")
    val cf: String = "",
    @SerializedName("al")
    val al: AlbumData = AlbumData(),
    @SerializedName("dt")
    val dt: Long = 0,
    @SerializedName("h")
    val h: QualityData = QualityData(),
    @SerializedName("m")
    val m: QualityData = QualityData(),
    @SerializedName("l")
    val l: QualityData = QualityData(),
    @SerializedName("sq")
    val sq: QualityData = QualityData(),
    @SerializedName("hr")
    val hr: QualityData = QualityData(),
    @SerializedName("cd")
    val cd: String = "",
    @SerializedName("no")
    val no: Int = 0,
    @SerializedName("ftype")
    val ftype: Int = 0,
    @SerializedName("djId")
    val djId: Int = 0,
    @SerializedName("copyright")
    val copyright: Int = 0,
    @SerializedName("s_id")
    val sId: Int = 0,
    @SerializedName("mark")
    val mark: Int = 0,
    @SerializedName("originCoverType")
    val originCoverType: Int = 0,
    @SerializedName("originSongSimpleData")
    val originSongSimpleData: OriginSongSimpleData? = null,
    @SerializedName("resourceState")
    val resourceState: Boolean = false,
    @SerializedName("version")
    val version: Int = 0,
    @SerializedName("single")
    val single: Int = 0,
    @SerializedName("rtype")
    val rtype: Int = 0,
    @SerializedName("mst")
    val mst: Int = 0,
    @SerializedName("cp")
    val cp: Int = 0,
    @SerializedName("mv")
    val mv: Int = 0,
    @SerializedName("publishTime")
    val publishTime: Int = 0,
    @SerializedName("reason")
    val reason: String = "",
    @SerializedName("tns")
    val tns: List<String> = listOf(),
    @SerializedName("recommendReason")
    val recommendReason: String = "",
    @SerializedName("alg")
    val alg: String = ""
)