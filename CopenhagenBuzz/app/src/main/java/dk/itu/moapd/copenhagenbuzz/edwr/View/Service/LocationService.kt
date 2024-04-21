package dk.itu.moapd.copenhagenbuzz.edwr.View.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import android.location.Location;

class LocationService : Service() {

    companion object {
        private const val PACKAGE_NAME = "dk.itu.moapd.geolocation"
        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST = "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"
        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"
        internal const val EXTRA_LOCATION_LATITUDE = "$PACKAGE_NAME.extra.LOCATION_LATITUDE"
        internal const val EXTRA_LOCATION_LONGITUDE = "$PACKAGE_NAME.extra.LOCATION_LONGITUDE"
    }

    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }

    private val localBinder = LocalBinder()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationChangeListener: LocationChangeListener? = null // Add this line

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val currentLocation = locationResult.lastLocation
                val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
                currentLocation?.let { intent.putExtra(EXTRA_LOCATION_LATITUDE, it.latitude) }
                currentLocation?.let { intent.putExtra(EXTRA_LOCATION_LONGITUDE, it.longitude) }
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

                // Notify the registered listener of the new location
                if (currentLocation != null) {
                    locationChangeListener?.onLocationChanged(currentLocation.latitude, currentLocation.longitude)
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return localBinder
    }

    // Add this method to set the location change listener
    fun setLocationChangeListener(listener: LocationChangeListener) {
        locationChangeListener = listener
    }

    // Add this interface for components interested in location updates
    interface LocationChangeListener {
        fun onLocationChanged(latitude: Double, longitude: Double)
    }
}
