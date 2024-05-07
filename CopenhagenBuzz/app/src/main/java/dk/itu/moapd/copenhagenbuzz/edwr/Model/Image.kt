package dk.itu.moapd.copenhagenbuzz.edwr.Model

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Represents a Location for events
 * @property url The url of an image.
 * @property path The path of an image.
 * @property createdAt The date created of the image.
 * @constructor Creates an empty Image
 */
@IgnoreExtraProperties
data class Image(val url: String? = null, val path: String? = null, val createdAt: Long? = null)