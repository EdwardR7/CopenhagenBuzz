package dk.itu.moapd.copenhagenbuzz.edwr

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentAddeventBinding
import java.util.Calendar
import java.util.TimeZone

class AddEventFragment : Fragment() {
    private val event: Event = Event("", "", 0, "", "")
    private var _binding: FragmentAddeventBinding? = null
    private val binding

        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAddeventBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // Listener for user interaction in the 'Event date' textfield.
            editTextEventDate.apply {
                keyListener = null
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus)
                        showDatePicker()
                }
            }

            addEventButton.setOnClickListener {
                Log.d("AddEventFragment", "Add event button clicked")
                // Update the object attributes.
                event.eventName = editTextEventName.text.toString().trim()
                event.eventLocation = editTextEventLocation.text.toString().trim()
                event.eventType = editEventType.text.toString().trim()
                event.eventDescription = editTextEventDesc.text.toString().trim()

                // Check if all required fields are filled
                if (event.eventName.isNotEmpty() &&
                    event.eventLocation.isNotEmpty() &&
                    event.eventType.isNotEmpty() &&
                    event.eventDescription.isNotEmpty()) {

                    // Show a Snackbar
                    Snackbar.make(binding.root, "Event added successfully!", Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.addEventButton)
                        .show()
                } else {
                    Snackbar.make(binding.root, "Please fill all the fields", Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.addEventButton)
                        .show()
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
        datePicker.addOnPositiveButtonClickListener { selection ->
            // Handle date selection
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            val selectedDate = calendar.time

            // Update the event object with the selected date
            event.eventDate = selectedDate.time

            // Update the EditText with the selected date using the binding object
            binding.editTextEventDate.setText(selectedDate.toString())
        }
        datePicker.show(requireActivity().supportFragmentManager, "DATE_PICKER")
    }
}