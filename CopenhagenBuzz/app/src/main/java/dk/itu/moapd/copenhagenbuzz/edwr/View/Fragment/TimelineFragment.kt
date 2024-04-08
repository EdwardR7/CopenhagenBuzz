package dk.itu.moapd.copenhagenbuzz.edwr.View.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dk.itu.moapd.copenhagenbuzz.edwr.Adapter.EventAdapter
import dk.itu.moapd.copenhagenbuzz.edwr.Adapter.OnItemClickListener
import dk.itu.moapd.copenhagenbuzz.edwr.DATABASE_URL
import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event
import dk.itu.moapd.copenhagenbuzz.edwr.R
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentTimelineBinding
import dk.itu.moapd.copenhagenbuzz.edwr.ViewModel.DataViewModel

class TimelineFragment : Fragment() {

    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = _binding!!

    private val dataViewModel: DataViewModel by viewModels()
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

      FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            val query = FirebaseDatabase.getInstance(DATABASE_URL!!).reference
                .child("events")
                //.child(userId)
                .orderByChild("eventDate")

            val options = FirebaseListOptions.Builder<Event>()
                .setLayout(R.layout.event_row_item) //Layout
                .setQuery(query, Event::class.java)
                .setLifecycleOwner(this)
                .build()

            eventAdapter = EventAdapter(requireContext(), options, object : OnItemClickListener {
                override fun onItemClick(event: Event) {
                    // Handle item click here if needed
                }

                override fun onFavoriteClick(event: Event, isFavorite: Boolean) {
                    dataViewModel.onFavoriteClicked(event)
                    dataViewModel.fetchFavorites()
                }
            })

            // Set the adapter to the ListView
            binding.listViewEvents.adapter = eventAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}