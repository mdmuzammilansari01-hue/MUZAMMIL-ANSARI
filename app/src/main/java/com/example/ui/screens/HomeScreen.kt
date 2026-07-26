package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.TestPaper
import com.example.ui.components.JacBottomNavigationBar
import com.example.ui.components.JacSearchBar
import com.example.ui.components.JacTopAppBar
import com.example.ui.components.QuickFeatureCard
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val studentProfile by viewModel.studentProfile.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val dynamicTests by viewModel.dynamicTestList.collectAsState()
    val testHistory by viewModel.testHistory.collectAsState()

    val studentName = studentProfile?.name?.ifBlank { "Md. Muzammil Ansari" } ?: "Md. Muzammil Ansari"
    val studentRoll = studentProfile?.rollNumber?.ifBlank { "20260012" } ?: "20260012"

    val dailyQuiz = dynamicTests.find { it.isDailyQuiz } ?: dynamicTests.firstOrNull()

    Scaffold(
        topBar = {
            JacTopAppBar(
                title = "JAC Test Hub",
                subtitle = "Jharkhand Academic Council Prep",
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.AboutSettings) },
                        modifier = Modifier.testTag("btn_top_settings")
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "About", tint = Color.White)
                    }
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("btn_top_logout")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            JacBottomNavigationBar(
                currentScreen = currentScreen,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Welcome Header Card
            Card(
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Welcome, $studentName",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Roll No: $studentRoll | $selectedClass",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                )
                            }
                        }
                        Badge(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Text(text = "Active", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    JacSearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.setSearch(it) },
                        placeholder = "Search JAC test papers, formulas, notes..."
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Banner Image
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Box(modifier = Modifier.height(140.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner_1784888499535),
                        contentDescription = "JAC Study Hub Hero Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.7f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "JAC Board Exam 2026 Special",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Model Papers, Mock Tests & Detailed Notes",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Features Grid Section
            PaddingSectionHeader(title = "Core Features", subtitle = "Select module to begin study")

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    QuickFeatureCard(
                        title = "Classes",
                        subtitle = "Class 8 to 12",
                        icon = Icons.Default.Class,
                        gradientColors = listOf(Color(0xFF006C4C), Color(0xFF2E7D32)),
                        onClick = { viewModel.navigateTo(Screen.Classes) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    QuickFeatureCard(
                        title = "Subjects",
                        subtitle = "Physics, Chem, Math...",
                        icon = Icons.Default.Subject,
                        gradientColors = listOf(Color(0xFF004D40), Color(0xFF00796B)),
                        onClick = { viewModel.navigateTo(Screen.Subjects) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    QuickFeatureCard(
                        title = "Mock Tests",
                        subtitle = "MCQ & Timer",
                        icon = Icons.Default.Quiz,
                        gradientColors = listOf(Color(0xFF1B5E20), Color(0xFF388E3C)),
                        onClick = { viewModel.navigateTo(Screen.TestsList) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    QuickFeatureCard(
                        title = "Notes & PDFs",
                        subtitle = "Chapter Guides",
                        icon = Icons.Default.Book,
                        gradientColors = listOf(Color(0xFF0D47A1), Color(0xFF1976D2)),
                        onClick = { viewModel.navigateTo(Screen.Notes) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    QuickFeatureCard(
                        title = "Video Lectures",
                        subtitle = "Educator Sessions",
                        icon = Icons.Default.VideoLibrary,
                        gradientColors = listOf(Color(0xFF4A148C), Color(0xFF7B1FA2)),
                        onClick = { viewModel.navigateTo(Screen.Videos) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    QuickFeatureCard(
                        title = "Performance",
                        subtitle = "Accuracy & Charts",
                        icon = Icons.Default.Analytics,
                        gradientColors = listOf(Color(0xFFE65100), Color(0xFFF57C00)),
                        onClick = { viewModel.navigateTo(Screen.Performance) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Daily Quiz Highlight
            if (dailyQuiz != null) {
                PaddingSectionHeader(title = "Daily Challenge Quiz", subtitle = "Test your daily knowledge")

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = dailyQuiz.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text("5 Mins", modifier = Modifier.padding(4.dp), color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${dailyQuiz.questions.size} Questions | ${dailyQuiz.className} | ${dailyQuiz.subject}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.startTest(dailyQuiz) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .align(Alignment.End)
                                .testTag("btn_start_daily_quiz")
                        ) {
                            Text("Start Daily Quiz")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Leaderboard & Admin Quick Buttons
            PaddingSectionHeader(title = "Extra Portals", subtitle = "Ranks & Admin tools")

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(Screen.LeaderboardScreen) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = "Rank", tint = Color(0xFFFFB300), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Rank & Leaderboard", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Top JAC Scorers", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.navigateTo(Screen.AdminPanel) }
                        .testTag("card_admin_portal")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Admin Panel", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Upload Notes & MCQs", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PaddingSectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
