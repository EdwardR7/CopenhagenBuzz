/*
 * MIT License
 *
 * Copyright (c) [2024] [Edward Rostomian]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dk.itu.moapd.copenhagenbuzz.edwr.Model

/**
 * Represents an event
 * @property eventName The name of the event.
 * @property eventLocation The location where the event takes place.
 * @property eventDate The date of the event.
 * @property eventType The type or category of the event.
 * @property eventDescription A description providing additional details about the event.
 * @constructor Creates an empty event
  */
data class Event(
    var eventName: String = "",
    var eventLocation: EventLocation = EventLocation(),
    var eventDate: Long = 0,
    var eventType: String = "",
    var eventDescription: String = "",
    var isFavorite: Boolean = false,
    var userId: String = "",
    val eventId: String = "",
    var imageUrl: String = ""
)
