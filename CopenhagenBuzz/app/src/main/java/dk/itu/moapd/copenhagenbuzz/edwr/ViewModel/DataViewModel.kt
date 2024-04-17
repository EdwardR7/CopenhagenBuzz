package dk.itu.moapd.copenhagenbuzz.edwr.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dk.itu.moapd.copenhagenbuzz.edwr.DATABASE_URL
import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event
import kotlinx.coroutines.launch

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
}
