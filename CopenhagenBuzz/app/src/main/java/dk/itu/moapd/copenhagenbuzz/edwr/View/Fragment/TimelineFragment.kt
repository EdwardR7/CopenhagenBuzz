package dk.itu.moapd.copenhagenbuzz.edwr.View.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.firebase.ui.database.FirebaseListOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dk.itu.moapd.copenhagenbuzz.edwr.Adapter.EventAdapter
import dk.itu.moapd.copenhagenbuzz.edwr.Adapter.OnItemClickListener
import dk.itu.moapd.copenhagenbuzz.edwr.DATABASE_URL
import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event
import dk.itu.moapd.copenhagenbuzz.edwr.R
import dk.itu.moapd.copenhagenbuzz.edwr.ViewModel.DataViewModel
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentTimelineBinding

class TimelineFragment : Fragment() {

    private lateinit var navController: NavController
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
                override fun onEditClick(event: Event) {
                    val bundle = Bundle().apply {
                        // Put event details into the bundle
                        putString("eventId", event.eventId)
                        // Put other event details as needed
                    }
                    val user = FirebaseAuth.getInstance().currentUser
                    if(event.userId == user?.uid){
                    findNavController().navigate(R.id.action_timelineFragment_to_UpdateEventFragment, bundle)
                        } else {
                        Snackbar.make(binding.root, "You cannot edit another users event",Snackbar.LENGTH_SHORT)
                            .setAnchorView(R.id.button_edit).show()
                    }
                    }

                override fun onFavoriteClick(event: Event, isFavorite: Boolean) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if(user?.isAnonymous != true) {
                        dataViewModel.onFavoriteClicked(event)
                        dataViewModel.fetchFavorites()

                    } else {
                        Snackbar.make(binding.root, "You must login to favorite an event",Snackbar.LENGTH_SHORT)
                            .setAnchorView(R.id.button_delete).show()
                    }
                }

                override fun onDeleteClick(event: Event) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (event.userId == user?.uid) {
                        dataViewModel.deleteEvent(event)
                    } else {
                        Snackbar.make(binding.root, "You cannot delete another users event",Snackbar.LENGTH_SHORT)
                            .setAnchorView(R.id.button_delete).show()
                    }
                }

                override fun onItemClick(event: Event) {
                    val bundle = Bundle().apply {
                        // Put event details into the bundle
                        putString("eventId", event.eventId)
                        // Put other event details as needed
                    }
                }
            })
            // Set the adapter to the ListView
            binding.listViewEvents.adapter = eventAdapter

          binding.filterButton.setOnClickListener {
              // Call filterEvent method from DataViewModel
              filterEvent(null)
          }
        }
    }

    private fun filterEvent(eventType: String?) {
        // Get a reference to the Firebase database
        val databaseReference = FirebaseDatabase.getInstance().reference.child("events")

        // Create a base query without any filters
        var query = databaseReference.orderByChild("eventType")

        // Apply the filter if eventType is not null
        eventType?.let { type ->
            query = query.equalTo(type)
        }

        // Build the FirebaseListOptions
        val options = FirebaseListOptions.Builder<Event>()
            .setLayout(R.layout.event_row_item)
            .setQuery(query, Event::class.java)
            .setLifecycleOwner(this@TimelineFragment)
            .build()

        // Create a new EventAdapter with the filtered options
        eventAdapter = EventAdapter(requireContext(), options, object : OnItemClickListener {
            override fun onEditClick(event: Event) {
                // Handle edit click
            }

            override fun onFavoriteClick(event: Event, isFavorite: Boolean) {
                // Handle favorite click
            }

            override fun onDeleteClick(event: Event) {
                // Handle delete click
            }

            override fun onItemClick(event: Event) {
                // Handle item click
            }
        })

        // Set the adapter to the ListView
        binding.listViewEvents.adapter = eventAdapter
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
