package dk.itu.moapd.copenhagenbuzz.edwr

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.google.android.material.color.DynamicColors
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.BuildConfig
import com.google.firebase.ktx.Firebase
import io.github.cdimascio.dotenv.dotenv

inline fun <reified T> T.TAG(): String = T::class.java.simpleName

val DATABASE_URL: String? = dotenv {
    directory = "/assets"
    filename = "env"
}["DATABASE_URL"]

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        //Places.initialize(this,BuildConfig.GOOGLE_MAPS_API_KEY)
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);


        // Apply dynamic colors to activities if available.
        DynamicColors.applyToActivitiesIfAvailable(this)

        // Enable disk persistence for the Firebase Realtime Database and keep it synchronized.
        if (DATABASE_URL != null) {
            Firebase.database(DATABASE_URL).setPersistenceEnabled(true)
            Firebase.database(DATABASE_URL).reference.keepSynced(true)
        }
    }
}