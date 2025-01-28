package com.bmexcs.pickpic

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider

class GoogleSignInActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var statusText: TextView
    private lateinit var userInfoText: TextView
    private lateinit var signInButton: Button
    private lateinit var signOutButton: Button

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)

        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Create main layout
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }

        // Title
        TextView(this).apply {
            text = "OAuth Test Page"
            textSize = 24f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 32)
            mainLayout.addView(this)
        }

        // Status text
        statusText = TextView(this).apply {
            text = "Status: Not signed in"
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 16)
            mainLayout.addView(this)
        }

        // User info text
        userInfoText = TextView(this).apply {
            text = "User Info: None"
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 32)
            mainLayout.addView(this)
        }

        // Sign In button
        signInButton = Button(this).apply {
            text = "Sign In with Google"
            setPadding(32, 16, 32, 16)
            setOnClickListener { signIn() }
            mainLayout.addView(this)
        }

        // Sign Out button
        signOutButton = Button(this).apply {
            text = "Sign Out"
            setPadding(32, 16, 32, 16)
            setOnClickListener { signOut() }
            isEnabled = false
            mainLayout.addView(this)
        }

        setContentView(mainLayout)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Accesses client_id from google-services.json
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Update UI if user is already signed in
        updateUI(auth.currentUser != null)
    }

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {
            updateUI(false)
        }
    }

    private fun updateUI(signedIn: Boolean) {
        if (signedIn) {
            val user: FirebaseUser? = auth.currentUser
            statusText.text = "Status: Signed in"
            userInfoText.text = "User Info: ${user?.displayName}, ${user?.email}"
            signInButton.isEnabled = false
            signOutButton.isEnabled = true
        } else {
            statusText.text = "Status: Not signed in"
            userInfoText.text = "User Info: None"
            signInButton.isEnabled = true
            signOutButton.isEnabled = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                firebaseAuthWithGoogle(account)
            }
        } catch (e: ApiException) {
            updateUI(false)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateUI(true)
                } else {
                    updateUI(false)
                }
            }
    }
}
