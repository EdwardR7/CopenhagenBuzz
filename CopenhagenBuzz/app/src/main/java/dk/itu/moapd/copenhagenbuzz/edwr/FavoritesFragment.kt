package dk.itu.moapd.copenhagenbuzz.edwr
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var dataViewModel: DataViewModel

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

        dataViewModel = ViewModelProvider(requireActivity()).get(DataViewModel::class.java)

        val recyclerViewFavorites = binding.RecyclerViewFavorites
        favoriteAdapter = FavoriteAdapter(requireContext(), emptyList())
        recyclerViewFavorites.adapter = favoriteAdapter
        recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())

        dataViewModel.favorites.observe(viewLifecycleOwner) { favoriteEvents ->
            favoriteAdapter.updateFavorites(favoriteEvents)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
