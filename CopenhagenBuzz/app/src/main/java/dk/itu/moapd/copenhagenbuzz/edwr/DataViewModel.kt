package dk.itu.moapd.copenhagenbuzz.edwr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dk.itu.moapd.copenhagenbuzz.edwr.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataViewModel : ViewModel() {

    // LiveData object to hold a list of events
    private val _events = MutableLiveData<List<Event>>()

    // Expose the list of events as LiveData
    val events: LiveData<List<Event>>
        get() = _events

    // Method to fetch the list of events asynchronously
    fun fetchEvents() {
        CoroutineScope(Dispatchers.IO).launch {
            // Generate or fetch the list of events asynchronously
            val eventsList = generateOrFetchEvents()
            // Update LiveData with the fetched events list
            _events.postValue(eventsList)
        }
    }

    // Method to generate or fetch the list of events asynchronously
    private fun generateOrFetchEvents(): List<Event> {
        // Implement real logic to generate or fetch the list of events
        // mock data
        return listOf(
            Event("Event 1",  "Location 1", 1,"Type 1", "Description 1"),
            Event("Event 2",  "Location 2", 2,"Type 2", "Description 2"),
            Event("Event 3",  "Location 3", 3, "Type 3","Description 3")
            // Add more events as needed
        )
    }
}
