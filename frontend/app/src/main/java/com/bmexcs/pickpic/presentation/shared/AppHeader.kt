import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.navigation.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    title: String, // Title for the TopAppBar
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit // Content of the screen with padding
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Navigation options in the side panel
                NavDrawerEntry("Home", navController, Route.Home.route)
                NavDrawerEntry("Profile", navController, Route.Profile.route)
                NavDrawerEntry("Support", navController, Route.Support.route)
                NavDrawerEntry("Events", navController, Route.Event.route)
                NavDrawerEntry("Ranking", navController, Route.Event.route)
            }
        }
    ) {
        Scaffold (
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                Log.d("Click",drawerState.isOpen.toString())
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                )
            }
        ) {
            innerPadding ->
            content(Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun NavDrawerEntry (
    text: String,
    navController: NavHostController,
    route: String // Route to navigate to. Defined as distinct objects in Route.kt
) {
    val padding = 16.dp
    Text(text, modifier = Modifier
        .padding(padding)
        .clickable {
            navController.navigate(route)
        }
    )
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = padding),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}