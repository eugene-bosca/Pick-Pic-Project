package com.bmexcs.pickpic.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.bmexcs.pickpic.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val redirectQR = intent.getBooleanExtra("QR_REQUIRE_LOGIN", false)
        val eventId = intent.getStringExtra("EVENT_ID")

        if (redirectQR) {
            val context = this.applicationContext
            val auth: FirebaseAuth = FirebaseAuth.getInstance()

            // if the auth state changes we redirect back to the QR
            auth.addAuthStateListener { firebaseAuth ->
                if (firebaseAuth.currentUser != null) {
                    val intent = Intent(context, InviteLinkActivity::class.java).apply {
                        putExtra("EVENT_ID", eventId)
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                }
            }
        }



        setContent {
            Navigation(
                navController = rememberNavController()
            )
        }
    }

}
