package com.bmexcs.pickpic.presentation.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

@Composable
fun ImageFull(
    imageUrl: String?,
    onDismiss: () -> Unit
) {
    if (imageUrl != null) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false, // Allow full-screen width
                dismissOnBackPress = true, // Dismiss on back press
                dismissOnClickOutside = true // Dismiss on outside click
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onDismiss() } // Dismiss on click anywhere
            ) {
                // Full-screen image
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Full Screen Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
