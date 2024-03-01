package dk.itu.moapd.copenhagenbuzz.edwr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import dk.itu.moapd.copenhagenbuzz.edwr.Event

class EventAdapter(private val context: Context, private val events: List<Event>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.event_row_item, parent, false)
            holder = ViewHolder()
            holder.titleTextView = view.findViewById(R.id.text_event_title)
            holder.locationTextView = view.findViewById(R.id.text_event_location)
            holder.typeTextView = view.findViewById(R.id.text_event_type)
            holder.descTextView = view.findViewById(R.id.text_event_description)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val event = events[position]
        holder.titleTextView.text = event.eventName
        holder.locationTextView.text = event.eventLocation
        holder.date = event.eventDate
        holder.typeTextView.text = event.eventType
        holder.descTextView.text = event.eventDescription
        return view!!
    }

    override fun getCount(): Int { return events.size }

    override fun getItem(position: Int): Any { return events[position] }

    override fun getItemId(position: Int): Long { return position.toLong() }

    private class ViewHolder {
        lateinit var titleTextView: TextView
        lateinit var locationTextView: TextView
        var date: Long = 0L
        lateinit var typeTextView: TextView
        lateinit var descTextView: TextView
    }
}

