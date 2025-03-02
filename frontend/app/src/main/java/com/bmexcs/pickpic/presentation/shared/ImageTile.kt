package com.bmexcs.pickpic.presentation.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImageTile(imageUrl: String, onClick: (String) -> Unit) {
    val cornerRadius = 12.dp

    ElevatedCard(
        modifier = Modifier
            .size(width = 150.dp, height = 225.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .clickable { onClick(imageUrl) },
        shape = RoundedCornerShape(cornerRadius)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Dog image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
        )
    }
}

