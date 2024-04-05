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
    private val _events = MutableLiveData<List<Event>>()
    private val _favorites = MutableLiveData<List<Event>>()

    val events: LiveData<List<Event>>
        get() = _events

    val favorites: LiveData<List<Event>>
        get() = _favorites

    init {
        fetchEvents()
    }

    private fun fetchEvents() { //Not directly needed in TimeLine for now
        val userId = getUserId() ?: return
        val query = FirebaseDatabase.getInstance(DATABASE_URL!!).reference
            .child("events")
            .child(userId)
            .orderByChild("eventDate")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eventsList = mutableListOf<Event>()
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    event?.let { eventsList.add(it) }
                }
                _events.value = eventsList
                updateFavorites(eventsList.filter { it.isFavorite })
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    fun fetchFavorites() {
        viewModelScope.launch {
            val eventsList = _events.value.orEmpty()
            updateFavorites(eventsList.filter { it.isFavorite })
        }
    }

    private fun updateFavorites(newFavorites: List<Event>) {
        _favorites.value = newFavorites
    }

    fun onFavoriteClicked(event: Event) {
        val isFavorite = event.isFavorite
        event.isFavorite = !isFavorite
        updateEvent(event)
    }

    private fun updateEvent(event: Event) {
        val userId = getUserId() ?: return
        val eventRef = FirebaseDatabase.getInstance().reference
            .child("events")
            .child(userId)
           // .child(event.id)
        eventRef.setValue(event)
    }

    private fun getUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}
