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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medical_schedule_app.navigation.NavigationRoutes
import com.example.medical_schedule_app.ui.auth.AuthViewModel
import kotlinx.coroutines.launch

// Color definitions remain the same
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
    authViewModel: AuthViewModel = hiltViewModel(), // Injects its own AuthViewModel
    onNavigateToAuth: () -> Unit                   // Receives navigation lambda
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(56.dp)
            .background(MediumBlue),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            IconButton(onClick = {
                navController.navigate(NavigationRoutes.PROFILE) {
                    launchSingleTop = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(onClick = {
                authViewModel.logout()    // Call logout on its ViewModel instance
                onNavigateToAuth()        // Call the navigation lambda
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
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
                    modifier = Modifier.size(32.dp),
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
    authViewModel: AuthViewModel = hiltViewModel(), // Injects its own AuthViewModel
    onNavigateToAuth: () -> Unit                   // Receives navigation lambda
) {
    ModalDrawerSheet(
        modifier = Modifier.width(260.dp),
        drawerContainerColor = MediumBlue,
        drawerContentColor = Color.White
    ) {
        Spacer(Modifier.height(16.dp))

        val itemPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 6.dp)
        val itemColors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = LightMediumBlueForSelection,
            unselectedIconColor = Color.White,
            selectedIconColor = Color.White,
            unselectedTextColor = Color.White,
            selectedTextColor = Color.White
        )

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
                authViewModel.logout()    // Call logout on its ViewModel instance
                onNavigateToAuth()        // Call the navigation lambda
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
    // authViewModel is kept here as per your structure, though not directly used for logout by MedicalAppBar itself anymore.
    // Hilt will manage its instance. If it's unused by MedicalAppBar directly, it could be removed.
    authViewModel: AuthViewModel = hiltViewModel(),
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Define the navigation action for logout
    val navigateToAuthScreenAction = {
        navController.navigate(NavigationRoutes.AUTH) {
            popUpTo(navController.graph.id) {
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
                // authViewModel will be injected within AppDrawerContent
                onNavigateToAuth = navigateToAuthScreenAction // Pass the navigation action
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
                // authViewModel will be injected within SideNavigationBar
                onNavigateToAuth = navigateToAuthScreenAction // Pass the navigation action
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
@Preview(showBackground = true, widthDp = 56)
@Composable
fun SideNavigationBarPreviewThemed() {
    // For preview, hiltViewModel() might require setup or a fake/stub.
    // If it works in your preview environment, this is fine.
    // Otherwise, you might need to provide a simple AuthViewModel stub for previews.
    SideNavigationBar(
        navController = rememberNavController(),
        onMenuClick = {},
        authViewModel = hiltViewModel(), // Or a preview-specific stub
        onNavigateToAuth = {}
    )
}

@Preview(showBackground = true)
@Composable
fun AppTopBarPreviewThemed() {
    AppTopBar(title = "Screen Title", onProfileClick = {})
}

@Preview(showBackground = true, widthDp = 260)
@Composable
fun AppDrawerContentPreviewThemed() {
    AppDrawerContent(
        navController = rememberNavController(),
        onCloseDrawer = {},
        authViewModel = hiltViewModel(), // Or a preview-specific stub
        onNavigateToAuth = {}
    )
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun MedicalAppBarPreviewThemed() {
    MedicalAppBar(
        navController = rememberNavController(),
        screenTitle = "Dashboard"
        // authViewModel will be provided by Hilt in actual use or preview setup
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