package dk.itu.moapd.copenhagenbuzz.edwr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentTimelineBinding

class TimelineFragment : Fragment() {

    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataViewModel: DataViewModel
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

        // Initialize DataViewModel
        dataViewModel = ViewModelProvider(requireActivity())[DataViewModel::class.java]

        // Fetch events when the fragment is created
        dataViewModel.fetchEvents()

        // Initialize EventAdapter
        eventAdapter = EventAdapter(requireContext(), emptyList()) // Initialize with empty list

        // Bind the adapter to the ListView
        binding.listViewEvents.adapter = eventAdapter

        // Observe the list of events from the DataViewModel
        dataViewModel.events.observe(viewLifecycleOwner) { events ->
            // Update the adapter with the new list of events
            eventAdapter.updateEvents(events)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
