package dk.itu.moapd.copenhagenbuzz.edwr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.copenhagenbuzz.edwr.Event
import dk.itu.moapd.copenhagenbuzz.edwr.R

class FavoriteAdapter(
    private val context: Context,
    private var favoriteEvents: List<Event>
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.favorite_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = favoriteEvents[position]

        // Bind event data to views in each list item
        holder.titleTextView.text = event.eventName
        holder.typeTextView.text = event.eventType
        // profile image and event image here (how do we do this??)
    }

    override fun getItemCount(): Int {
        return favoriteEvents.size
    }

    // ViewHolder class to hold references to views in each list item
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.text_event_title)
        val typeTextView: TextView = itemView.findViewById(R.id.text_event_type)
        //Profile and ImageView here also
    }
}
