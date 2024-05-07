package dk.itu.moapd.copenhagenbuzz.edwr.Model

/**
 * Represents a Location for events
 * @property latitude The latitude of the Location.
 * @property longitude The longitude of the Location.
 * @property address The address of the Location.
 * @constructor Creates an empty EventLocation
 */
data class EventLocation (
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var address: String = ""
)
