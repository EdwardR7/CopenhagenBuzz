package dk.itu.moapd.copenhagenbuzz.edwr.Adapter

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.navigation.NavController
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.squareup.picasso.Picasso
import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event
import dk.itu.moapd.copenhagenbuzz.edwr.R

interface OnItemClickListener {
    fun onItemClick(event: Event)
    fun onFavoriteClick(event: Event, isFavorite: Boolean)
    fun onDeleteClick(event: Event)
    fun onEditClick(event: Event)
}

class EventAdapter(
    context: Context,
    options: FirebaseListOptions<Event>,
    private val itemClickListener: OnItemClickListener
) : FirebaseListAdapter<Event>(options) {

    private lateinit var navController: NavController


    override fun populateView(v: View, model: Event, position: Int) {
        val titleTextView: TextView = v.findViewById(R.id.text_event_title)
        val locationTextView: TextView = v.findViewById(R.id.text_event_location)
        val typeTextView: TextView = v.findViewById(R.id.text_event_type)
        val eventDate: TextView = v.findViewById(R.id.text_field_event_date)
        val descTextView: TextView = v.findViewById(R.id.text_event_description)
        val favoriteButton: ToggleButton = v.findViewById(R.id.button_favorite)
        val editButton: Button = v.findViewById(R.id.button_edit)
        val deleteButton: Button = v.findViewById(R.id.button_delete)
        val imageView: ImageView = v.findViewById(R.id.image_event)


        titleTextView.text = model.eventName
        locationTextView.text = model.eventLocation.address
        eventDate.text = model.eventDate.toString()
        typeTextView.text = model.eventType
        descTextView.text = model.eventDescription

        favoriteButton.isChecked = model.isFavorite
        favoriteButton.setOnCheckedChangeListener(null)
        favoriteButton.setOnClickListener {
            val isFavorite = (it as ToggleButton).isChecked
            itemClickListener.onFavoriteClick(model, isFavorite)
        }

        deleteButton.setOnClickListener{
            itemClickListener.onDeleteClick(model)
        }

        editButton.setOnClickListener {
            itemClickListener.onEditClick(model) //UpdateEventFragment should be opened here
        }

        v.setOnClickListener {
            itemClickListener.onItemClick(model)
        }

        // Inside populateView method
        if (model.imageUrl.isNotEmpty()) {
            // If imageUrl is not empty, load the image using Picasso
            Picasso.get().load(model.imageUrl).into(imageView)
        } else {
            // If imageUrl is empty, set a placeholder image or hide the ImageView
            imageView.setImageResource(R.drawable.baseline_image_24)
        }
    }
}
