package dk.itu.moapd.copenhagenbuzz.edwr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentTimelineBinding

class TimelineFragment : Fragment() {

    private var _binding: FragmentTimelineBinding? = null
    private lateinit var dataViewModel: DataViewModel
    private lateinit var eventAdapter: EventAdapter
    private var isFavorite: Boolean = false
    private var buttonFavorite: ToggleButton? = null

    // Binding instance, created when the view is created
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize DataViewModel
        dataViewModel = ViewModelProvider(requireActivity()).get(DataViewModel::class.java)

        // Fetch events when the fragment is created
        dataViewModel.fetchEvents()

        // Initialize EventAdapter
        eventAdapter = EventAdapter(requireContext(), emptyList()) // Initialize with empty list

        // Bind the adapter to the RecyclerView
        binding.listViewEvents.adapter = eventAdapter

        // Observe the list of events from the DataViewModel
        dataViewModel.events.observe(viewLifecycleOwner) { events ->
            // Update the adapter with the new list of events
            eventAdapter.updateEvents(events)
        }

        // Find Button by ID
        buttonFavorite = view.findViewById(R.id.button_favorite)

        // Set click listener for the favorites button
        buttonFavorite?.setOnClickListener {
            // Toggle the state of the favorites button
            isFavorite = !isFavorite
            // Update the UI based on the new state
            updateFavoritesState(isFavorite)
        }

        // Restore the state of the favorites button if it was saved
        savedInstanceState?.let {
            isFavorite = it.getBoolean("isFavorite", false)
            updateFavoritesState(isFavorite)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the state of the favorites button
        outState.putBoolean("isFavorite", isFavorite)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        buttonFavorite = null
    }

    // Function to update the state of the favorites button
     fun updateFavoritesState(isFavoriteClicked: Boolean) {
        // Update the UI of the favorites button based on the state
        buttonFavorite?.isSelected = isFavoriteClicked
    }
}
