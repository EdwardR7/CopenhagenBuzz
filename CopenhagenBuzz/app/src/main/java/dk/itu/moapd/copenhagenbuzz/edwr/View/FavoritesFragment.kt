package dk.itu.moapd.copenhagenbuzz.edwr.View
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dk.itu.moapd.copenhagenbuzz.edwr.ViewModel.DataViewModel
import dk.itu.moapd.copenhagenbuzz.edwr.Adapter.FavoriteAdapter
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoriteAdapter: FavoriteAdapter
    private val dataViewModel: DataViewModel by viewModels()

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

        dataViewModel.favorites.observe(viewLifecycleOwner) { favoriteEvents ->
            favoriteAdapter = FavoriteAdapter(requireContext(), favoriteEvents)
            recyclerViewFavorites.adapter = favoriteAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
