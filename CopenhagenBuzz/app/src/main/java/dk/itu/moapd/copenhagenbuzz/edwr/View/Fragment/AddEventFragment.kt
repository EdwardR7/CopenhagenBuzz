    package dk.itu.moapd.copenhagenbuzz.edwr.View.Fragment
    import android.Manifest
    import android.app.Activity.RESULT_OK
    import android.content.ActivityNotFoundException
    import android.content.ContentValues
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.graphics.Bitmap
    import android.location.Geocoder
    import android.net.Uri
    import android.os.Bundle
    import android.provider.MediaStore
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.core.app.ActivityCompat
    import androidx.fragment.app.Fragment
    import androidx.fragment.app.activityViewModels
    import com.google.android.material.datepicker.MaterialDatePicker
    import com.google.android.material.snackbar.Snackbar
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.FirebaseDatabase
    import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event
    import dk.itu.moapd.copenhagenbuzz.edwr.Model.EventLocation
    import dk.itu.moapd.copenhagenbuzz.edwr.ViewModel.DataViewModel
    import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentAddeventBinding
    import java.io.IOException
    import java.util.Calendar
    import java.util.Locale
    import java.util.UUID

    class AddEventFragment : Fragment() {
        private var _binding: FragmentAddeventBinding? = null
        private val currentUser = FirebaseAuth.getInstance().currentUser
        private val binding get() = _binding!!

        private var imageIDRandom = UUID.randomUUID()
        private var imageID = imageIDRandom.toString()

        private val viewModel: DataViewModel by activityViewModels()

        private val REQUEST_IMAGE_CAPTURE = 1

        private val REQUEST_IMAGE_GALLERY = 2

        private var finalImageUrl: String = ""

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
            binding.takePictureButton.setOnClickListener{
                if (checkCameraPermission())
                    dispatchTakePictureIntent()
                else
                    requestCameraPermissions()
            }

            binding.galleryButton.setOnClickListener{
                if (checkGalleryPermission())
                    openGallery()
                else
                    requestGalleryPermissions()
            }
                binding.addEventButton.setOnClickListener {
                val eventName = binding.editTextEventName.text.toString().trim()
                val eventLocation = binding.editTextEventLocation.text.toString().trim()
                val eventType = binding.editEventType.text.toString().trim()
                val eventDescription = binding.editTextEventDesc.text.toString().trim()

                if (finalImageUrl != ""){
                if (eventName.isNotEmpty() && eventType.isNotEmpty() && eventDescription.isNotEmpty()
                    && eventLocation.isNotEmpty() && currentUser?.isAnonymous != true) {
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
                                    imageUrl = finalImageUrl
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
            }else {
                    Snackbar.make(binding.root, "Remmember to add an image", Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.addEventButton).show()
            }}

        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
        private fun checkGalleryPermission() =
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED


        private fun requestGalleryPermissions() {
            if (!checkGalleryPermission()) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    34
                )
            }
        }

        private fun checkCameraPermission() =
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        private fun requestCameraPermissions() {
            if (!checkCameraPermission()) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    34
                )
            }
        }

        private fun dispatchTakePictureIntent() {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                Snackbar.make(binding.root, "Could not open camera", Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.addEventButton).show()
            }
        }

        private fun saveImageToGallery(bitmap: Bitmap): Uri? {
            val contentResolver = requireContext().contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "MyImage_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }

            val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            return try {
                imageUri?.let { uri ->
                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                    uri
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }


        private fun uploadImageToFirebaseStorage(imageUri: Uri) {
            viewModel.uploadImage(imageUri) { returnImageUrl ->
                if (returnImageUrl.isNotEmpty()) {
                    Snackbar.make(binding.root, "Image uploaded successfully!", Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.addEventButton).show()
                    finalImageUrl = returnImageUrl
                    // Now you can proceed with adding the event to the database
                } else {
                    Snackbar.make(binding.root, "Failed to upload image", Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.addEventButton).show()
                }
            }
        }

        private fun openGallery() {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            try {
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
            } catch (e: ActivityNotFoundException) {
                Snackbar.make(binding.root, "Could not open gallery", Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.addEventButton).show()
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                // Handle result of the camera action
                val imageBitmap = data?.extras?.get("data") as Bitmap
                val imageUri = saveImageToGallery(imageBitmap)
                if (imageUri != null) {
                    uploadImageToFirebaseStorage(imageUri)
                }
            } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
                // Handle result of the gallery action
                val imageUri = data?.data
                if (imageUri != null) {
                    uploadImageToFirebaseStorage(imageUri)
                }
            }
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

