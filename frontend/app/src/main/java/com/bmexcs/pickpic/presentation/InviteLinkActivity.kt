// InviteActivity.kt
package pickpic.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bmexcs.pickpic.presentation.MainActivity

/// HELLO THIS IS INCOMPLETE

class InviteLinkActivity : AppCompatActivity() {

    private val TAG = "InviteLinkActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = getIntent()
        val data = intent.data

        if (data != null) {
            val inviteId = data.getQueryParameter("inviteId")
            val groupId = data.getQueryParameter("groupId")

            if (inviteId != null && groupId != null) {
                Log.d(TAG, "Invite ID: $inviteId, Group ID: $groupId")
                processInvite(inviteId, groupId)
            } else if (inviteId != null) {
                Log.d(TAG, "Invite ID: $inviteId")
                processInvite(inviteId, null)
            } else {
                Log.e(TAG, "Invalid invite link: Missing parameters")
                handleInvalidInvite()
            }
        } else {
            Log.e(TAG, "No data received from intent")
            handleNoData()
        }
    }

    private fun processInvite(inviteId: String, groupId: String?) {
        // Your logic to trigger the endpoint or navigate to a screen
        // Example: Navigate to MainActivity and pass invite data
        val mainIntent = Intent(this, MainActivity::class.java)
        if (groupId != null) {
            mainIntent.putExtra("inviteId", inviteId)
            mainIntent.putExtra("groupId", groupId)
        } else {
            mainIntent.putExtra("inviteId", inviteId)
        }

        startActivity(mainIntent)
        finish() // Close InviteActivity after processing
    }

    private fun handleInvalidInvite() {
        // Example: Navigate to an error activity or show an error message
        // For now, let's just navigate to MainActivity
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun handleNoData() {
        // Example: Navigate to MainActivity
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }
}