package dk.itu.moapd.copenhagenbuzz.edwr

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentTimelineBinding
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.EventRowItemBinding
import dk.itu.moapd.copenhagenbuzz.edwr.Event
import dk.itu.moapd.copenhagenbuzz.edwr.DataViewModel
class EventAdapter(
    private val context: Context,
    private var events: List<Event>,
    private var ressource: Int,
    private var dataViewModel: DataViewModel
) : ArrayAdapter<Event>(context,R.layout.event_row_item,events
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(ressource, parent, false)
            holder = ViewHolder()
            holder.titleTextView = view.findViewById(R.id.text_event_title)
            holder.locationTextView = view.findViewById(R.id.text_event_location)
            holder.typeTextView = view.findViewById(R.id.text_event_type)
            holder.descTextView = view.findViewById(R.id.text_event_description)
            holder.favoriteButton = view.findViewById(R.id.button_favorite)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val event = events[position]
        holder.titleTextView.text = event.eventName
        holder.locationTextView.text = event.eventLocation
        holder.typeTextView.text = event.eventType
        holder.descTextView.text = event.eventDescription

        holder.favoriteButton.setOnCheckedChangeListener { _, isChecked ->
            event.isFavorite = isChecked
            dataViewModel.onFavoriteClicked(event)
            dataViewModel.fetchFavorites()
        }

        return view!!
    }

    override fun getCount(): Int = events.size

    override fun getItemId(position: Int): Long = position.toLong()

    inner class ViewHolder {
        lateinit var titleTextView: TextView
        lateinit var locationTextView: TextView
        lateinit var typeTextView: TextView
        lateinit var descTextView: TextView
        lateinit var favoriteButton: ToggleButton
    }
}