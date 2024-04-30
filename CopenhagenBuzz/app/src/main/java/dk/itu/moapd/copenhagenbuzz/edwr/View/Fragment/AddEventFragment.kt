package dk.itu.moapd.copenhagenbuzz.edwr.View.Fragment
import android.location.Geocoder
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
import java.util.Locale

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
            val imageUrl = ""

            if (eventName.isNotEmpty() && eventType.isNotEmpty() && eventDescription.isNotEmpty() && eventLocation.isNotEmpty() && currentUser?.isAnonymous != true) {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocationName(eventLocation, 1)
                if (!addresses.isNullOrEmpty()) {
                    val latitude = addresses[0].latitude
                    val longitude = addresses[0].longitude
                    val address = addresses[0].getAddressLine(0)

                    // Proceed with adding the event
                    currentUser!!.let { user ->
                        val userId = user.uid
                        val eventsRef = FirebaseDatabase.getInstance().reference.child("events")
                        val newEventKey = eventsRef.push().key

                        val event = newEventKey?.let { it1 ->
                            Event(
                                eventId = it1,
                                eventName = eventName,
                                eventDescription = eventDescription,
                                eventDate = selectedDate,
                                eventLocation = EventLocation(latitude, longitude, address),
                                eventType = eventType,
                                isFavorite = false,
                                userId = userId,
                                imageUrl = imageUrl
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
                    // Address not found, show error message
                    Snackbar.make(binding.root, "Invalid location", Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.addEventButton).show()
                }
            } else if(currentUser?.isAnonymous == true){
                Snackbar.make(binding.root, "You cannot post as an anonymous user, please login or signup", Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.addEventButton).show()
            } else {
                Snackbar.make(binding.root, "Please fill all the fields", Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.addEventButton).show()
            }
        }
        binding.takePictureButton.setOnClickListener{
            findNavController().navigate(R.id.action_timelineFragment_to_imageFragment)
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

