package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.R
import com.bmexcs.pickpic.navigation.Route
import com.bmexcs.pickpic.presentation.viewmodels.CreateEventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreenView(
    navController: NavHostController,
    viewModel: CreateEventViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.padding(16.dp)) {

        EditableEventNameField()
        Spacer(modifier = Modifier.height(100.dp))
        UserSearchField()

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            ElevatedButton(onClick = {  }) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Join Events Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Copy Event Link")
            }
            ElevatedButton(onClick = { navController.navigate(Route.CreateEvent.route) }) {
                Icon(
                    painter = painterResource(R.drawable.add_circle_24px),
                    contentDescription = "Create Event Icon",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Create")
            }
        }
    }
}

@Composable
fun EditableEventNameField() {
    var eventName by remember { mutableStateOf("") }

    Text(
        text = "New Event Name",
        fontSize = 18.sp,
        modifier = Modifier.padding(start = 8.dp)
    )

    Spacer(modifier = Modifier.height(4.dp))

    OutlinedTextField(
        value = eventName,
        onValueChange = {newValue -> eventName = newValue},
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@ExperimentalMaterial3Api
@Composable
fun UserSearchField() {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    Text(
        text = "People with access",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp)
    )

    SearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = { active = false }, // Handles search action
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text("Search users...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { query = "" }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear Search")
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    ){}
}