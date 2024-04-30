package dk.itu.moapd.copenhagenbuzz.edwr.View.Fragment

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dk.itu.moapd.copenhagenbuzz.edwr.Model.Event
import dk.itu.moapd.copenhagenbuzz.edwr.View.Service.LocationService
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.FragmentMapsBinding

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var locationService: LocationService? = null
    private var locationServiceBound = false
    private val drawingPoints = mutableListOf<LatLng>()
    private var isDrawingModeEnabled = false
    private val eventsList: MutableList<Event> = mutableListOf()
    private var drawnPolyline: PolylineOptions? = null

    companion object {
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        private const val COPENHAGEN_LATITUDE = 55.6761
        private const val COPENHAGEN_LONGITUDE = 12.5683
        private const val ZOOM_LEVEL = 12f
    }
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            locationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            locationService = null
            locationServiceBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("events")

        _binding = FragmentMapsBinding.bind(view) // Bind the view
        val mapFragment = childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.toggleDrawingButton.setOnClickListener {
            toggleDrawingMode()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val copenhagen = LatLng(COPENHAGEN_LATITUDE, COPENHAGEN_LONGITUDE)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(copenhagen, ZOOM_LEVEL))

        // Move the Google Maps UI buttons under the OS top bar.
        googleMap.setPadding(0, 100, 0, 0)

        googleMap.setOnMapClickListener { point ->
            if (isDrawingModeEnabled) {
                // Add the clicked point to the list
                drawingPoints.add(point)
                // Draw the polyline
                drawPolyline()
            }
        }

        // Enable the location layer. Request the permission if it is not granted.
        if (checkPermission()) {
            googleMap.isMyLocationEnabled = true
            locationService?.setLocationChangeListener(object : LocationService.LocationChangeListener {

                override fun onLocationChanged(location: Location) {
                    Log.d(tag, location.latitude.toString())
                    val userLocation = LatLng(location.latitude, location.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                }
            })
        } else {
            requestUserPermissions()
        }

        // Populate the map with markers representing upcoming events
        fetchEvents()
    }

    private fun drawPolyline() {
        if (drawnPolyline != null) {
            googleMap.clear() // Clear existing polylines and markers
            // Redraw event markers
            eventsList.forEach { addMarker(it) }
        }
        val polylineOptions = PolylineOptions()
            .addAll(drawingPoints)
            .width(5f)
            .color(Color.RED)
        googleMap.addPolyline(polylineOptions)
        drawnPolyline = polylineOptions
    }

    private fun fetchEvents() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventsList.clear()
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    event?.let {
                        eventsList.add(it)
                        addMarker(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun addMarker(event: Event) {
        val eventLocation = LatLng(event.eventLocation.latitude, event.eventLocation.longitude)
        googleMap.addMarker(
            MarkerOptions()
                .position(eventLocation)
                .title(event.eventName)
        )
    }

    private fun toggleDrawingMode() {
        isDrawingModeEnabled = !isDrawingModeEnabled
        if (isDrawingModeEnabled) {
            // Enable drawing mode
            binding.toggleDrawingButton.text = "Drawing Mode Enabled"
        } else {
            // Disable drawing mode
            binding.toggleDrawingButton.text = "Toggle Drawing Mode"
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestUserPermissions() {
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        // Bind to the service.
        Intent(requireContext(), LocationService::class.java).let { serviceIntent ->
            requireActivity().bindService(
                serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }
    override fun onStop() {
        // Unbind from the service.
        if (locationServiceBound) {
            requireActivity().unbindService(serviceConnection)
            locationServiceBound = false
        }
        super.onStop()
    }
}
