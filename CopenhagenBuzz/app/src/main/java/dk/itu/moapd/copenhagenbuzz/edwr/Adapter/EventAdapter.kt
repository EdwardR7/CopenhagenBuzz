package dk.itu.moapd.copenhagenbuzz.edwr.Adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.ToggleButton
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.database.Query
import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event
import dk.itu.moapd.copenhagenbuzz.edwr.R
import dk.itu.moapd.copenhagenbuzz.edwr.ViewModel.DataViewModel

class EventAdapter(
    context: Context,
    private val dataViewModel: DataViewModel,
    options: FirebaseListOptions<Event>
) : FirebaseListAdapter<Event>(options) {

    override fun populateView(v: View, model: Event, position: Int) {
        val titleTextView: TextView = v.findViewById(R.id.text_event_title)
        val locationTextView: TextView = v.findViewById(R.id.text_event_location)
        val typeTextView: TextView = v.findViewById(R.id.text_event_type)
        val descTextView: TextView = v.findViewById(R.id.text_event_description)
        val favoriteButton: ToggleButton = v.findViewById(R.id.button_favorite)

        titleTextView.text = model.eventName
        locationTextView.text = model.eventLocation
        typeTextView.text = model.eventType
        descTextView.text = model.eventDescription

        favoriteButton.isChecked = model.isFavorite
        favoriteButton.setOnCheckedChangeListener { _, isChecked ->
            model.isFavorite = isChecked
            dataViewModel.onFavoriteClicked(model)
            dataViewModel.fetchFavorites()
        }
    }
}