package dk.itu.moapd.copenhagenbuzz.edwr.View.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event
import dk.itu.moapd.copenhagenbuzz.edwr.Model.EventLocation
import dk.itu.moapd.copenhagenbuzz.edwr.R
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentAddeventBinding
import java.util.Calendar

class UpdateEventFragment : Fragment() {
    private var _binding: FragmentAddeventBinding? = null
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val binding get() = _binding!!

    private var selectedDate: Long = 0
    private var eventId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddeventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        eventId = bundle?.getString("eventId")

        _binding?.apply {
            editTextEventDate.setOnClickListener {
                showDatePicker()
            }

            addEventButton.setOnClickListener {
                val eventName = editTextEventName.text.toString().trim()
                val eventLocation = editTextEventLocation.text.toString().trim()
                val eventType = editEventType.text.toString().trim()
                val eventDescription = editTextEventDesc.text.toString().trim()

                if (eventName.isNotEmpty() && eventLocation.isNotEmpty() && eventType.isNotEmpty() && eventDescription.isNotEmpty() && currentUser?.isAnonymous != true && eventId != null) {
                    val userId = currentUser?.uid
                    val eventsRef = FirebaseDatabase.getInstance().reference.child("events")

                    val updatedEvent = userId?.let { it1 ->
                        Event(
                            eventId = eventId!!,
                            eventName = eventName,
                            eventDescription = eventDescription,
                            eventDate = selectedDate,
                            eventLocation = EventLocation(),
                            eventType = eventType,
                            isFavorite = false,
                            userId = it1
                        )
                    }

                    eventsRef.child(eventId!!).setValue(updatedEvent).addOnSuccessListener {
                        findNavController().navigate(R.id.action_calendarFragment_to_timelineFragment, bundle)
                        Snackbar.make(
                            requireView(),
                            "Event updated successfully!",
                            Snackbar.LENGTH_SHORT
                        )
                            .setAnchorView(addEventButton).show()
                    }.addOnFailureListener { exception ->
                        Snackbar.make(
                            requireView(),
                            "Failed to update event: ${exception.message}",
                            Snackbar.LENGTH_SHORT
                        )
                            .setAnchorView(addEventButton).show()
                    }
                } else if (currentUser?.isAnonymous == true) {
                    Snackbar.make(
                        requireView(),
                        "You cannot update as an anonymous user, please login or signup",
                        Snackbar.LENGTH_SHORT
                    )
                        .setAnchorView(addEventButton).show()
                } else {
                    Snackbar.make(
                        requireView(),
                        "Please fill all the fields",
                        Snackbar.LENGTH_SHORT
                    )
                        .setAnchorView(addEventButton).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()
        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener {
            selectedDate = it
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            val dateString = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
            binding.editTextEventDate.setText(dateString)
        }
        datePicker.show(requireActivity().supportFragmentManager, "DATE_PICKER")
    }
}
