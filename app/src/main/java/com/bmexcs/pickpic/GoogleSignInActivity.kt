package com.bmexcs.pickpic

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.IOException

class GoogleSignInActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var welcomeText: TextView
    private lateinit var profileNameText: TextView
    private lateinit var signInButton: Button
    private lateinit var signOutButton: Button
    private lateinit var choosePhotoButton: Button
    private lateinit var mainLayout: LinearLayout

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val UPLOAD_URL = "YOUR_BACKEND_URL" // Replace with actual backend URL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(50, 50, 50, 50)
        }
        setContentView(mainLayout)

        // Welcome text
        welcomeText = TextView(this).apply {
            text = "Welcome! Please sign in"
            textSize = 20f
            gravity = Gravity.CENTER
        }
        mainLayout.addView(welcomeText)

        // User name text
        profileNameText = TextView(this).apply {
            textSize = 18f
            gravity = Gravity.CENTER
        }
        mainLayout.addView(profileNameText)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signInButton = Button(this).apply {
            text = "Sign in with Google"
            setOnClickListener { signIn() }
        }
        mainLayout.addView(signInButton)

        signOutButton = Button(this).apply {
            text = "Sign Out"
            setOnClickListener { signOut() }
            isEnabled = false
        }
        mainLayout.addView(signOutButton)

        choosePhotoButton = Button(this).apply {
            text = "Choose a Photo"
            setOnClickListener { choosePhoto() }
            isEnabled = false
        }
        mainLayout.addView(choosePhotoButton)

        // Check if user is already signed in
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener(this) {
            updateUI(null)
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            welcomeText.text = "Welcome back!"
            profileNameText.text = "Signed in as: ${account.displayName}"
            signInButton.isEnabled = false
            signOutButton.isEnabled = true
            choosePhotoButton.isEnabled = true
        } else {
            welcomeText.text = "Welcome! Please sign in"
            profileNameText.text = ""
            signInButton.isEnabled = true
            signOutButton.isEnabled = false
            choosePhotoButton.isEnabled = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            updateUI(account)
            Toast.makeText(this, "Signed in as ${account?.displayName}", Toast.LENGTH_SHORT).show()
        } catch (e: ApiException) {
            updateUI(null)
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadPhoto(it)
        }
    }

    private fun choosePhoto() {
        pickImage.launch("image/*")
    }

    private fun uploadPhoto(imageUri: Uri) {
        try {
            contentResolver.openInputStream(imageUri)?.use { inputStream ->
                val fileName = "temp_${System.currentTimeMillis()}.jpg"
                val tempFile = File(cacheDir, fileName)

                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        tempFile.name,
                        RequestBody.create("image/*".toMediaTypeOrNull(), tempFile)
                    )
                    .build()

                val request = Request.Builder()
                    .url(UPLOAD_URL)
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            Toast.makeText(
                                this@GoogleSignInActivity,
                                "Upload failed: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        tempFile.delete()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        tempFile.delete()
                        runOnUiThread {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@GoogleSignInActivity,
                                    "Upload successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@GoogleSignInActivity,
                                    "Upload failed: ${response.code}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Error processing image: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
