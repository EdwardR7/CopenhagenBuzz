package dk.itu.moapd.copenhagenbuzz.edwr.View.Service;

import android.app.Service;
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
class LocationService : Service() {

    companion object {
        private const val PACKAGE_NAME = "dk.itu.moapd.geolocation"
        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST = "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"
        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"
    }

    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }

    /**
     * The binder instance for this service.
     */
    private val localBinder = LocalBinder()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback
    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object :LocationCallback() {
            override fun onLocationResult(locationResult:LocationResult){
                super.onLocationResult(locationResult)

                val currentLocation = locationResult.lastLocation
                val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
                intent.putExtra(EXTRA_LOCATION, currentLocation)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        // Return the communication channel to the service.
        return localBinder
    }
}
