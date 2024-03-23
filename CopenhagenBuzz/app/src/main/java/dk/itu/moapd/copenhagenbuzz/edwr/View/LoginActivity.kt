package dk.itu.moapd.copenhagenbuzz.edwr.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dk.itu.moapd.copenhagenbuzz.edwr.View.MainActivity
import dk.itu.moapd.copenhagenbuzz.edwr.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){

            loginButton.setOnClickListener{
                val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                    putExtra("isLoggedIn", true)
                }
                startActivity(intent)
                finish()
            }

            guestButton.setOnClickListener{
                val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                    putExtra("isLoggedIn", false)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}