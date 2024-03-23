package dk.itu.moapd.copenhagenbuzz.edwr.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.ToggleButton
import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event
import dk.itu.moapd.copenhagenbuzz.edwr.R
import dk.itu.moapd.copenhagenbuzz.edwr.ViewModel.DataViewModel

class EventAdapter(
    private val context: Context,
    private var events: List<Event>,
    private var ressource: Int,
    private var dataViewModel: DataViewModel
) : ArrayAdapter<Event>(context, R.layout.event_row_item,events
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(ressource, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val event = events[position]
        holder.bind(event)

        return view!!
    }

    private inner class ViewHolder(view: View) {
        private val titleTextView: TextView = view.findViewById(R.id.text_event_title)
        private val locationTextView: TextView = view.findViewById(R.id.text_event_location)
        private val typeTextView: TextView = view.findViewById(R.id.text_event_type)
        private val descTextView: TextView = view.findViewById(R.id.text_event_description)
        private val favoriteButton: ToggleButton = view.findViewById(R.id.button_favorite)

        fun bind(event: Event) {
            titleTextView.text = event.eventName
            locationTextView.text = event.eventLocation
            typeTextView.text = event.eventType
            descTextView.text = event.eventDescription

            favoriteButton.setOnCheckedChangeListener { _, isChecked ->
                event.isFavorite = isChecked
                dataViewModel.onFavoriteClicked(event)
                dataViewModel.fetchFavorites()
            }
        }
    }
}
