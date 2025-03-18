package com.bmexcs.pickpic.presentation;

import InvitedQRView
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint

private val TAG = "InviteLinkActivity"

@AndroidEntryPoint
class InviteLinkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val eventId: String? = parseDeepLink(intent)
        val intentEventId = intent.getStringExtra("EVENT_ID")

        setContent {
            if (intentEventId != null) {
                InvitedQRView(eventId = intentEventId)
            } else if (eventId != null) {
                InvitedQRView(eventId = eventId)
            }
        }
    }
}

private fun parseDeepLink(intent: Intent?): String? {
    val data: Uri?

    if (intent?.action == Intent.ACTION_VIEW) {
        data = intent.data
    } else {
        showError("Invalid Link")
        Log.d(TAG, "Tried to deeplink with incorrect action")
        return ""
    }
    val eventId: String?

    if (data != null) {
        eventId = data.getQueryParameter("eventId")
    } else {
        showError("Invalid Link")
        Log.d(TAG, "eventId parameter not found")
        return ""
    }
    return eventId
}

private fun showError(message: String) {
    Log.d(TAG, message)
}
