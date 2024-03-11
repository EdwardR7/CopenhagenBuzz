package dk.itu.moapd.copenhagenbuzz.edwr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentTimelineBinding
class TimelineFragment : Fragment(), EventAdapter.EventClickListener {

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

        dataViewModel = ViewModelProvider(requireActivity()).get(DataViewModel::class.java)
        dataViewModel.fetchEvents()

        eventAdapter = EventAdapter(requireContext(), emptyList(), this)
        binding.listViewEvents.adapter = eventAdapter

        dataViewModel.events.observe(viewLifecycleOwner) { events ->
            eventAdapter.updateEvents(events)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onFavoriteClicked(event: Event) {
        if (event.isFavorite) {
            dataViewModel.addToFavorites(event)
        } else {
            dataViewModel.removeFromFavorites(event)
        }
    }
}
