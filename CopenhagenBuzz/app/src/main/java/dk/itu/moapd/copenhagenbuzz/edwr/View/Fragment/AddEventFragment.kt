package dk.itu.moapd.copenhagenbuzz.edwr.View.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentAddeventBinding
import java.util.*

class AddEventFragment : Fragment() {
    private var _binding: FragmentAddeventBinding? = null
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val binding get() = _binding!!

    private var selectedDate: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddeventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextEventDate.setOnClickListener {
            showDatePicker()
        }

        binding.addEventButton.setOnClickListener {
            val eventName = binding.editTextEventName.text.toString().trim()
            val eventLocation = binding.editTextEventLocation.text.toString().trim()
            val eventType = binding.editEventType.text.toString().trim()
            val eventDescription = binding.editTextEventDesc.text.toString().trim()

            if (eventName.isNotEmpty() && eventLocation.isNotEmpty() && eventType.isNotEmpty() && eventDescription.isNotEmpty()) {
                currentUser?.let { user ->
                    val userId = user.uid
                    val eventsRef = FirebaseDatabase.getInstance().reference.child("events")
                    val newEventKey = eventsRef.push().key

                    val event = newEventKey?.let { it1 ->
                        Event(
                            eventId = it1,
                            eventName = eventName,
                            eventDescription = eventDescription,
                            eventDate = selectedDate,
                            eventLocation = eventLocation,
                            eventType = eventType,
                            isFavorite = false,
                            userId = userId
                        )
                    }

                    newEventKey?.let { key ->
                        eventsRef.child(key).setValue(event).addOnSuccessListener {
                            Snackbar.make(binding.root, "Event added successfully!", Snackbar.LENGTH_SHORT)
                                .setAnchorView(binding.addEventButton).show()
                        }
                    }
                }
            } else {
                Snackbar.make(binding.root, "Please fill all the fields", Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.addEventButton).show()
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
