package dk.itu.moapd.copenhagenbuzz.edwr.View

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.edwr.R
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.DialogUserInfoBinding
import com.squareup.picasso.Picasso

class UserInfoDialogFragment : DialogFragment() {

    private var _binding: DialogUserInfoBinding? = null
    private val binding get() = _binding!!

    // Initialize FirebaseAuth
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Get the current user
        val currentUser = auth.currentUser

        // Populate the dialog view with user information.
        currentUser?.let { user ->
            with(binding) {
                textViewName.text = user.displayName ?: ""
                textViewEmail.text = user.email ?: ""
                user.photoUrl?.let { url ->
                    imageViewPhoto.imageTintMode = null
                    Picasso.get().load(url).into(imageViewPhoto)
                }
            }
        }

        // Create and return a new dialog.
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.user_info_title)
            .setView(binding.root)
            .setPositiveButton("undefined") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
