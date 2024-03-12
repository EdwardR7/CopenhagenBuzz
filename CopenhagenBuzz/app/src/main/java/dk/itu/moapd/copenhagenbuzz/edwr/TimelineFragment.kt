package dk.itu.moapd.copenhagenbuzz.edwr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentTimelineBinding
class TimelineFragment : Fragment(){

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

        dataViewModel.events.observe(viewLifecycleOwner) { events ->
            binding.listViewEvents.adapter = EventAdapter(requireContext(),events,R.layout.event_row_item,dataViewModel)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
