package dk.itu.moapd.copenhagenbuzz.edwr

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataViewModel : ViewModel() {

    private val _events = MutableLiveData<List<Event>>()
    private val _favorites = MutableLiveData<List<Event>>() // LiveData to hold the list of favorite events

    val events: LiveData<List<Event>>
        get() = _events

    fun fetchEvents() {
        CoroutineScope(Dispatchers.IO).launch {
            val eventsList = generateOrFetchEvents()
            _events.postValue(eventsList)

            // Filter events marked as favorites and update the favorites list
            val favoriteEvents = eventsList.filter { it.isFavorite }
            _favorites.postValue(favoriteEvents)
        }
    }

    // Add event to favorites list
    fun addToFavorites(event: Event) {
        val updatedFavoritesList = _favorites.value?.toMutableList() ?: mutableListOf()
        updatedFavoritesList.add(event)
        _favorites.postValue(updatedFavoritesList)
    }

    // Remove event from favorites list
    fun removeFromFavorites(event: Event) {
        val updatedFavoritesList = _favorites.value?.toMutableList() ?: mutableListOf()
        updatedFavoritesList.remove(event)
        _favorites.postValue(updatedFavoritesList)
    }

    // Method to generate or fetch the list of events asynchronously
    private fun generateOrFetchEvents(): List<Event> {
        // Generate or fetch the list of events
        // For demonstration purposes, mock data is used here
        val events = listOf(
            Event("Event 1",  "Location 1", 1,"Type 1", "Description 1",false),
            Event("Event 2",  "Location 2", 2,"Type 2", "Description 2",false),
            Event("Event 3",  "Location 3", 3, "Type 3","Description 3",false)
            // Add more events as needed
        )
        return events
    }
}
