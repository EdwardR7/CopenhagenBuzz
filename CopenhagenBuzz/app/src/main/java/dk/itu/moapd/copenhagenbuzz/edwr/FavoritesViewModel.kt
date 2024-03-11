package dk.itu.moapd.copenhagenbuzz.edwr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {

    private val _favorites = MutableLiveData<List<Event>>()

    val favorites: LiveData<List<Event>>
        get() = _favorites

    fun addToFavorites(event: Event) {
        val updatedFavorites = _favorites.value.orEmpty().toMutableList()
        updatedFavorites.add(event)
        _favorites.value = updatedFavorites
    }

    fun removeFromFavorites(event: Event) {
        val updatedFavorites = _favorites.value.orEmpty().toMutableList()
        updatedFavorites.remove(event)
        _favorites.value = updatedFavorites
    }
}
