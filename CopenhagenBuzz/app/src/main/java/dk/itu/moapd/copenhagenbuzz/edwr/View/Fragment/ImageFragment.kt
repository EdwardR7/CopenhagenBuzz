package dk.itu.moapd.copenhagenbuzz.edwr.View.Fragment

import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.copenhagenbuzz.edwr.Manifest
import dk.itu.moapd.copenhagenbuzz.edwr.R
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentImageBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageFragment  : Fragment(){

    private var _binding: FragmentImageBinding? = null


    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private val viewModel: ViewModel by activityViewModels()

    private var imageCapture: ImageCapture? = null

    private var imageUri: Uri? = null

    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        cameraPermissionResult(result)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentImageBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Request camera permissions.
        if (checkPermission())
            startCamera()
        else
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)

        // The current selected camera.
        viewModel.selector.observe(viewLifecycleOwner) {
            cameraSelector = it ?: CameraSelector.DEFAULT_BACK_CAMERA
        }

        // Define the UI behavior.
        binding.apply {

            // Set up the listener for take photo button.
            buttonImageCapture.setOnClickListener {
                takePhoto()
            }

            // Set up the listener for the change camera button.
            buttonCameraSwitch.apply {

                // Disable the button until the camera is set up
                isEnabled = false

                setOnClickListener {
                    viewModel.onCameraSelectorChanged(
                        if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
                            CameraSelector.DEFAULT_BACK_CAMERA
                        else
                            CameraSelector.DEFAULT_FRONT_CAMERA
                    )

                    // Re-start use cases to update selected camera.
                    startCamera()
                }
            }

            // Set up the listener for the photo view button.
            buttonImageViewer.setOnClickListener {
                imageUri?.let { uri ->
                    val bundle = bundleOf("ARG_IMAGE" to uri.toString())

                    // Navigate to the `ImageFragment` passing the `Image` instance as an argument.
                    requireActivity().findNavController(R.id.fragment_container_view)
                        .navigate(R.id.action_imageFragment_to_timelineFragment, bundle)
                }
            }
        }
    }
    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun updateCameraSwitchButton(provider: ProcessCameraProvider) {
        binding.buttonCameraSwitch.isEnabled = try {
            hasBackCamera(provider) && hasFrontCamera(provider)
        } catch (exception: CameraInfoUnavailableException) {
            false
        }

    }
    private fun hasBackCamera(provider: ProcessCameraProvider) =
        provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
    private fun hasFrontCamera(provider: ProcessCameraProvider) =
        provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
                updateCameraSwitchButton(cameraProvider)
            } catch(ex: Exception) {
                Snackbar.make(binding.root, "Use case binding failed: $ex", Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.buttonImageCapture).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val timestamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
        val filename = "IMG_$timestamp.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }
        val outputFileOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    imageUri = output.savedUri
                    Snackbar.make(binding.root, "Photo capture succeeded: $filename.jpg", Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.buttonImageCapture).show()
                }
                override fun onError(exception: ImageCaptureException) {
                    Snackbar.make(binding.root, "Photo capture failed: ${exception.message}", Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.buttonImageCapture).show()
                }
            }
        )
    }

    private fun cameraPermissionResult(isGranted: Boolean) {
        // Use the takeIf function to conditionally execute code based on the permission result
        isGranted.takeIf { it }?.run {
            startCamera()
        } ?: requireActivity().finish()
    }
}