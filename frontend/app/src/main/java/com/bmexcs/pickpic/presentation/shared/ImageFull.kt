package com.bmexcs.pickpic.presentation.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ImageFull(
    image: ImageRequest?,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    if (image != null) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false, // Allow full-screen width
                dismissOnBackPress = true, // Dismiss on back press
                dismissOnClickOutside = true // Dismiss on outside click
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onDismiss()
                    }
            ) {
                AsyncImage(
                    model = image,
                    contentDescription = "Full Screen Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().weight(1f)
                )
                Box(
                    modifier = Modifier.fillMaxWidth().weight(0.2f)
                ) {
                    content.invoke()
                }
            }
        }
    }
}