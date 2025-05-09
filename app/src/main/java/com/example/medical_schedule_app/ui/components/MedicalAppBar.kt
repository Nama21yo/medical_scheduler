package com.example.medical_schedule_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medical_schedule_app.navigation.NavigationRoutes
import com.example.medical_schedule_app.ui.auth.AuthViewModel
import kotlinx.coroutines.launch

val MediumBlue = Color(0xFF3D6FB4)
val ScreenBackgroundColor = Color(0xFFF0F7FC)
val LightMediumBlueForSelection = Color(0xFF5C8DD0)

@Composable
fun SideNavigationBar(
    navController: NavController,
    onMenuClick: () -> Unit,
    authViewModel: AuthViewModel,
    onNavigateToAuth: () -> Unit
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
                authViewModel.logout()
                onNavigateToAuth()
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

@Composable
fun AppDrawerContent(
    navController: NavController,
    onCloseDrawer: () -> Unit,
    authViewModel: AuthViewModel,
    onNavigateToAuth: () -> Unit
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
            onClick = { onCloseDrawer() },
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
                authViewModel.logout()
                onNavigateToAuth()
            },
            modifier = Modifier.padding(itemPadding),
            colors = itemColors
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalAppBar(
    navController: NavController,
    screenTitle: String,
    showBackButton: Boolean = false,
    authViewModel: AuthViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navigateToAuthScreenAction = {
        navController.navigate(NavigationRoutes.AUTH) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                navController = navController,
                onCloseDrawer = { scope.launch { drawerState.close() } },
                authViewModel = authViewModel,
                onNavigateToAuth = navigateToAuthScreenAction
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
                authViewModel = authViewModel,
                onNavigateToAuth = navigateToAuthScreenAction
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