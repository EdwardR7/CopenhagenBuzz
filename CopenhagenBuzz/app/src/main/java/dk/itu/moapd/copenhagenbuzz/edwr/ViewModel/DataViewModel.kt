package dk.itu.moapd.copenhagenbuzz.edwr.ViewModel

import androidx.camera.core.CameraSelector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dk.itu.moapd.copenhagenbuzz.edwr.DATABASE_URL
import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event

class DataViewModel : ViewModel() {
    private val _favorites = MutableLiveData<List<Event>>()
    private val _eventLocation = MutableLiveData<String>()

    val favorites: LiveData<List<Event>>
        get() = _favorites

    val eventLocation: LiveData<String>
        get() = _eventLocation

    init {
        fetchFavorites()
    }

    fun fetchFavorites() {
        val userId = getUserId() ?: return
        val query = FirebaseDatabase.getInstance(DATABASE_URL!!).reference
            .child("events")
            .child(userId)
            .orderByChild("isFavorite")
            .equalTo(true)
    }
    fun onFavoriteClicked(event: Event) {
        event.isFavorite = !event.isFavorite
        updateEvent(event)
    }
    fun deleteEvent(event: Event) {
        val userId = getUserId() ?: return
        val eventRef = FirebaseDatabase.getInstance().reference
            .child("events")
            //.child(userId)
            .child(event.eventId ?: "")
        eventRef.removeValue()
    }

    private fun updateEvent(event: Event) {
        val userId = getUserId() ?: return
        val eventRef = FirebaseDatabase.getInstance().reference
            .child("events")
            //.child(userId)
            .child(event.eventId ?: "") // Use the eventId to reference the specific event
        eventRef.setValue(event)
    }

    private fun getUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun setEventLocation(location: String) {
        _eventLocation.value = location
    }
    /**
     * The current selected camera.
     */
    private var _selector = MutableLiveData<CameraSelector>()

    /**
     * A `LiveData` which publicly exposes any update in the camera selector.
     */
    val selector: LiveData<CameraSelector>
        get() = _selector

    /**
     * This method will be executed when the user interacts with the camera selector component. It
     * sets the selector into the LiveData instance.
     *
     * @param selector A set of requirements and priorities used to select a camera.
     */
    fun onCameraSelectorChanged(selector: CameraSelector) {
        this._selector.value = selector
    }

}
