package com.example.medical_schedule_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // For back button
import androidx.compose.material.icons.automirrored.filled.Logout // Correct Logout Icon
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel // For potentially injecting AuthViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
// import com.example.medical_schedule_app.R // Keep if you use drawable resources
import com.example.medical_schedule_app.navigation.NavigationRoutes
import com.example.medical_schedule_app.ui.auth.AuthViewModel // Import AuthViewModel
import kotlinx.coroutines.launch

// ... (Color definitions remain the same) ...
val MediumBlue = Color(0xFF3D6FB4)
val ScreenBackgroundColor = Color(0xFFF0F7FC)
val LightMediumBlueForSelection = Color(0xFF5C8DD0)

/**
 * The narrow, fixed side navigation bar with icons.
 */
@Composable
fun SideNavigationBar(
    navController: NavController,
    onMenuClick: () -> Unit,
    onLogout: () -> Unit // Add onLogout lambda
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(60.dp)
            .background(MediumBlue),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // ... (Menu and Profile icons remain the same) ...
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
            IconButton(onClick = {
                navController.navigate(NavigationRoutes.PROFILE) {
                    launchSingleTop = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(onClick = {
                onLogout() // Call the logout logic
                // Navigation happens after logout in the calling screen or NavGraph
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout, // Correct Logout Icon
                    contentDescription = "Logout",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

/**
 * The Top App Bar component.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onProfileClick: () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title, color = Color.White, fontWeight = FontWeight.SemiBold) },
        navigationIcon = navigationIcon ?: {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MediumBlue,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }
        }
    )
}


/**
 * Content for the wider ModalNavigationDrawer.
 */
@Composable
fun AppDrawerContent(
    navController: NavController,
    onCloseDrawer: () -> Unit,
    onLogout: () -> Unit // Add onLogout lambda
) {
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerContainerColor = MediumBlue,
        drawerContentColor = Color.White
    ) {
        Spacer(Modifier.height(20.dp))

        val itemPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
        val itemColors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = LightMediumBlueForSelection,
            unselectedIconColor = Color.White,
            selectedIconColor = Color.White,
            unselectedTextColor = Color.White,
            selectedTextColor = Color.White
        )

        // ... (Medicare and Profile NavigationDrawerItems remain the same) ...
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.GridView, contentDescription = "Medicare", tint = Color.White) },
            label = { Text("Medicare", fontSize = 16.sp) },
            selected = false,
            onClick = {
                onCloseDrawer()
                // Example: navController.navigate("medicare_route") { launchSingleTop = true }
            },
            modifier = Modifier.padding(itemPadding),
            colors = itemColors
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White) },
            label = { Text("Profile", fontSize = 16.sp) },
            selected = false,
            onClick = {
                onCloseDrawer()
                navController.navigate(NavigationRoutes.PROFILE) {
                    launchSingleTop = true
                }
            },
            modifier = Modifier.padding(itemPadding),
            colors = itemColors
        )


        NavigationDrawerItem(
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.White) },
            label = { Text("Logout", fontSize = 16.sp) },
            selected = false,
            onClick = {
                onCloseDrawer()
                onLogout() // Call the logout logic
                // Navigation happens after logout in the calling screen or NavGraph
            },
            modifier = Modifier.padding(itemPadding),
            colors = itemColors
        )
    }
}

/**
 * The main app structure combining SideNavigationBar, TopAppBar, NavigationDrawer and content area.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalAppBar(
    navController: NavController,
    screenTitle: String,
    showBackButton: Boolean = false,
    authViewModel: AuthViewModel = hiltViewModel(), // Inject AuthViewModel here
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Define the onLogout action centrally
    val performLogoutAndNavigate = {
        authViewModel.logout()
        navController.navigate(NavigationRoutes.AUTH) {
            popUpTo(navController.graph.id) { // Clear back stack up to the start of the graph
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                navController = navController,
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onLogout = performLogoutAndNavigate // Pass the combined action
            )
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            SideNavigationBar(
                navController = navController,
                onMenuClick = {
                    scope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    }
                },
                onLogout = performLogoutAndNavigate // Pass the combined action
            )
            Scaffold(
                modifier = Modifier.weight(1f),
                topBar = {
                    AppTopBar(
                        title = screenTitle,
                        onProfileClick = {
                            if (drawerState.isOpen) {
                                scope.launch { drawerState.close() }
                            }
                            navController.navigate(NavigationRoutes.PROFILE) {
                                launchSingleTop = true
                            }
                        },
                        navigationIcon = if (showBackButton) {
                            {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                    )
                                }
                            }
                        } else null
                    )
                },
                containerColor = ScreenBackgroundColor,
                content = content
            )
        }
    }
}


// Previews
@Preview(showBackground = true, widthDp = 60)
@Composable
fun SideNavigationBarPreviewThemed() {
    // For preview, provide a dummy onLogout
    SideNavigationBar(navController = rememberNavController(), onMenuClick = {}, onLogout = {})
}

@Preview(showBackground = true)
@Composable
fun AppTopBarPreviewThemed() {
    AppTopBar(title = "Screen Title", onProfileClick = {})
}

@Preview(showBackground = true, widthDp = 280)
@Composable
fun AppDrawerContentPreviewThemed() {
    // For preview, provide a dummy onLogout
    AppDrawerContent(navController = rememberNavController(), onCloseDrawer = {}, onLogout = {})
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun MedicalAppBarPreviewThemed() {
    MedicalAppBar(
        navController = rememberNavController(),
        screenTitle = "Dashboard"
        // authViewModel will be provided by Hilt in actual use
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ScreenBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Text("Main Screen Content Goes Here", fontSize = 24.sp, color = MediumBlue)
        }
    }
}