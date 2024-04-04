package dk.itu.moapd.copenhagenbuzz.edwr.View

import dk.itu.moapd.copenhagenbuzz.edwr.Adapter.FavoriteAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import dk.itu.moapd.copenhagenbuzz.edwr.DATABASE_URL
import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event
import dk.itu.moapd.copenhagenbuzz.edwr.R
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerViewFavorites = binding.RecyclerViewFavorites
        recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())

        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            val query = Firebase.database(DATABASE_URL!!).reference
                .child("users")
                .child(userId)
                .child("favorites")
                .orderByChild("eventDate")


            // Firebase query to fetch all events from the database ordered by startDate
            val options = FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event::class.java)
                .setLifecycleOwner(this)
                .build()

            favoriteAdapter = FavoriteAdapter(requireContext(), options)
            recyclerViewFavorites.adapter = favoriteAdapter
        }
    }
}
