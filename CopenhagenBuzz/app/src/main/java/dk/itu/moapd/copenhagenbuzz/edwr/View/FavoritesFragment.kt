import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event
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

        val query = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(FirebaseAuth.getInstance().currentUser?.uid ?: "")
            .child("favorites")
            .orderByChild("eventDate")

        val options = FirebaseRecyclerOptions.Builder<Event>()
            .setQuery(query, Event::class.java)
            .build()

        favoriteAdapter = FavoriteAdapter(options, requireContext())
        recyclerViewFavorites.adapter = favoriteAdapter
    }

    override fun onStart() {
        super.onStart()
        favoriteAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        favoriteAdapter.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
