package dk.itu.moapd.copenhagenbuzz.edwr.Model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Image(val url: String? = null, val path: String? = null, val createdAt: Long? = null)