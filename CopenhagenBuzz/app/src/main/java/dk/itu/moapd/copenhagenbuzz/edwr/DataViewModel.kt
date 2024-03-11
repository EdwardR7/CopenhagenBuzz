package dk.itu.moapd.copenhagenbuzz.edwr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>()
    private val _favorites = MutableLiveData<List<Event>>()

    val events: LiveData<List<Event>>
        get() = _events

    val favorites: LiveData<List<Event>>
        get() = _favorites

    fun fetchEvents() {
        CoroutineScope(Dispatchers.IO).launch {
            val eventsList = generateOrFetchEvents()
            _events.postValue(eventsList)
            updateFavorites(eventsList.filter { it.isFavorite })
        }
    }

    fun addToFavorites(event: Event) {
        val updatedFavoritesList = _favorites.value?.toMutableList() ?: mutableListOf()
        updatedFavoritesList.add(event)
        updateFavorites(updatedFavoritesList)
    }

    fun removeFromFavorites(event: Event) {
        val updatedFavoritesList = _favorites.value?.toMutableList() ?: mutableListOf()
        updatedFavoritesList.remove(event)
        updateFavorites(updatedFavoritesList)
    }

    private fun updateFavorites(newFavorites: List<Event>) {
        _favorites.postValue(newFavorites)
    }

    private fun generateOrFetchEvents(): List<Event> {
        // Generate or fetch the list of events
        // For demonstration purposes, mock data is used here
        val events = listOf(
            Event("Event 1", "Location 1", 1, "Type 1", "Description 1", false),
            Event("Event 2", "Location 2", 2, "Type 2", "Description 2", false),
            Event("Event 3", "Location 3", 3, "Type 3", "Description 3", false)
            // Add more events as needed
        )
        return events
    }
}
