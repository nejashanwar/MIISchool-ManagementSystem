package com.example.marasaaapp.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.marasaaapp.model.UserRole
import com.example.marasaaapp.repository.SchoolRepository
import com.example.marasaaapp.ui.LoginScreen
import com.example.marasaaapp.ui.components.RoleSwitcherTopBar
import com.example.marasaaapp.ui.dashboard.AdminDashboardScreen
import com.example.marasaaapp.ui.dashboard.ParentDashboardScreen
import com.example.marasaaapp.ui.dashboard.StudentDashboardScreen
import com.example.marasaaapp.ui.dashboard.TeacherDashboardScreen
import com.example.marasaaapp.ui.screens.ChatScreen
import com.example.marasaaapp.ui.screens.DirectoryScreen
import com.example.marasaaapp.ui.screens.ProfileScreen
import com.example.marasaaapp.ui.screens.TimetableScreen

enum class MainTab(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    TIMETABLE("Timetable", Icons.Default.Schedule),
    MESSAGES("Messages", Icons.AutoMirrored.Filled.Chat),
    DIRECTORY("Directory", Icons.Default.People)
}

@Composable
fun MainAppScaffold(
    repository: SchoolRepository = SchoolRepository
) {
    val isLoggedIn by repository.isLoggedIn.collectAsState()
    val activeRole by repository.activeRole.collectAsState()
    val activeUser by repository.activeUser.collectAsState()

    if (!isLoggedIn) {
        LoginScreen(repository = repository)
    } else {
        var selectedTab by remember { mutableStateOf(MainTab.DASHBOARD) }
        var showProfileScreen by remember { mutableStateOf(false) }

        if (showProfileScreen) {
            ProfileScreen(
                repository = repository,
                onDismiss = { showProfileScreen = false }
            )
        } else {
            Scaffold(
                topBar = {
                    RoleSwitcherTopBar(
                        activeRole = activeRole,
                        activeUser = activeUser,
                        onRoleSelected = { newRole ->
                            repository.switchRole(newRole)
                        },
                        onLogout = {
                            repository.logout()
                        },
                        onProfileClicked = {
                            showProfileScreen = true
                        },
                        repository = repository
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        MainTab.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = selectedTab == tab,
                                onClick = { selectedTab = tab },
                                icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label) }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (selectedTab) {
                        MainTab.DASHBOARD -> {
                            when (activeRole) {
                                UserRole.ADMIN -> AdminDashboardScreen(repository)
                                UserRole.TEACHER -> TeacherDashboardScreen(repository)
                                UserRole.STUDENT -> StudentDashboardScreen(repository)
                                UserRole.PARENT -> ParentDashboardScreen(repository)
                            }
                        }
                        MainTab.TIMETABLE -> TimetableScreen(repository)
                        MainTab.MESSAGES -> ChatScreen(repository)
                        MainTab.DIRECTORY -> DirectoryScreen(repository)
                    }
                }
            }
        }
    }
}
