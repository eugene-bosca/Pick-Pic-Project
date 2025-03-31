package com.bmexcs.pickpic.presentation;

import com.bmexcs.pickpic.presentation.screens.InvitedQRView
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "InviteLinkActivity"

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
        Log.d(TAG, "Invalid Link: Tried to deeplink with incorrect action")
        return null
    }

    if (data == null) {
        Log.d(TAG, "Invalid Link: URI data is null")
        return null
    }

    // Check if the path matches the expected pattern: /event/{eventId}
    val pathSegments = data.pathSegments
    if (pathSegments.size >= 2 && pathSegments[0] == "event") {
        return pathSegments[1] // Return the event ID which is the second path segment
    }

    Log.d(TAG, "Invalid Link: URL structure doesn't match expected pattern")
    return null
}
