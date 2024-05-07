package dk.itu.moapd.copenhagenbuzz.edwr.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.squareup.picasso.Picasso
import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event
import dk.itu.moapd.copenhagenbuzz.edwr.R

class FavoriteAdapter(
    private val context: Context,
    options: FirebaseRecyclerOptions<Event>
) : FirebaseRecyclerAdapter<Event, FavoriteAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.favorite_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Event) {
        // Bind event data to views in each list item
        holder.bind(model)
    }

    inner class ViewHolder(recyclerView: View) : RecyclerView.ViewHolder(recyclerView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.text_event_title)
        private val typeTextView: TextView = itemView.findViewById(R.id.text_event_type)
        private val imageView: ImageView = itemView.findViewById(R.id.image_event)


        fun bind(event: Event) {
            titleTextView.text = event.eventName
            typeTextView.text = event.eventType
            // Bind other event data here

            // Inside populateView method
            if (event.imageUrl.isNotEmpty()) {
                // If imageUrl is not empty, load the image using Picasso
                Picasso.get().load(event.imageUrl).into(imageView)
            } else {
                // If imageUrl is empty, set a placeholder image or hide the ImageView
                imageView.setImageResource(R.drawable.baseline_image_24)
            }
        }

    }
}
