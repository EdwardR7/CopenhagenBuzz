package dk.itu.moapd.copenhagenbuzz.edwr.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event
import kotlinx.coroutines.launch

class DataViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>()
    private val _favorites = MutableLiveData<List<Event>>()

    val events: LiveData<List<Event>>
        get() = _events

    val favorites: LiveData<List<Event>> //To reference
        get() = _favorites

    init {
        fetchEvents()
    }
    private fun fetchEvents() {
        viewModelScope.launch {
            val eventsList = generateOrFetchEvents()
            _events.value = eventsList
            updateFavorites(eventsList.filter { it.isFavorite })
        }
    }

     fun fetchFavorites(){
         viewModelScope.launch {
             val eventsList = _events.value?.toList()
             if (eventsList != null) {
                 updateFavorites(eventsList.filter { it.isFavorite })
             }
         }
     }

    private fun addToFavorites(event: Event) {
        val updatedFavoritesList = _favorites.value?.toMutableList() ?: mutableListOf()
        updatedFavoritesList.add(event)
        updateFavorites(updatedFavoritesList)
    }

    private fun removeFromFavorites(event: Event) {
        val updatedFavoritesList = _favorites.value?.toMutableList() ?: mutableListOf()
        updatedFavoritesList.remove(event)
        updateFavorites(updatedFavoritesList)
    }

    private fun updateFavorites(newFavorites: List<Event>) {
        _favorites.value = newFavorites
    }

     fun onFavoriteClicked(event: Event) {
        if (event.isFavorite) {
            addToFavorites(event)
        } else {
            removeFromFavorites(event)
        }
    }

    private fun generateOrFetchEvents(): List<Event> { //Needs to be deleted
        // Generate or fetch the list of events
        // For demonstration purposes, mock data is used here
        return listOf(
            Event("Event 1", "Location 1", 1, "Type 1", "Description 1", false,""),
            Event("Event 2", "Location 2", 2, "Type 2", "Description 2", false,""),
            Event("Event 3", "Location 3", 3, "Type 3", "Description 3", false,"")
            // Add more events as needed
        )
    }
}
