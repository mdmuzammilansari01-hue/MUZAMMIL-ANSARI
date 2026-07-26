package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.JacBottomNavigationBar
import com.example.ui.components.JacTopAppBar
import com.example.ui.viewmodel.MainViewModel

@Composable
fun PerformanceScreen(viewModel: MainViewModel) {
    val history by viewModel.testHistory.collectAsState()
    val studentProfile by viewModel.studentProfile.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()

    val totalAttempts = history.size
    val avgAccuracy = if (history.isNotEmpty()) history.map { it.accuracyPercentage }.average().toInt() else 85
    val highestScore = history.maxOfOrNull { it.score } ?: 18

    Scaffold(
        topBar = {
            JacTopAppBar(
                title = "Performance Dashboard",
                subtitle = "Student Analytics & Progress"
            )
        },
        bottomBar = {
            JacBottomNavigationBar(
                currentScreen = currentScreen,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                // Key Metrics
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Overall Accuracy", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                            Text("$avgAccuracy%", fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, color = Color.White)
                            Text("Tests Completed: $totalAttempts", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Growth",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }

            // Visual Progress Canvas Chart
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Test Score Growth Trend", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Custom Canvas Chart
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .padding(horizontal = 12.dp)
                        ) {
                            val lineColor = MaterialTheme.colorScheme.primary
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val points = listOf(
                                    Offset(0f, size.height * 0.7f),
                                    Offset(size.width * 0.25f, size.height * 0.5f),
                                    Offset(size.width * 0.5f, size.height * 0.6f),
                                    Offset(size.width * 0.75f, size.height * 0.25f),
                                    Offset(size.width, size.height * 0.15f)
                                )

                                for (i in 0 until points.size - 1) {
                                    drawLine(
                                        color = lineColor,
                                        start = points[i],
                                        end = points[i + 1],
                                        strokeWidth = 6.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }

                                points.forEach { pt ->
                                    drawCircle(
                                        color = Color.White,
                                        radius = 8.dp.toPx(),
                                        center = pt
                                    )
                                    drawCircle(
                                        color = lineColor,
                                        radius = 5.dp.toPx(),
                                        center = pt
                                    )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text("Test 1", fontSize = 11.sp, color = Color.Gray)
                            Text("Test 2", fontSize = 11.sp, color = Color.Gray)
                            Text("Test 3", fontSize = 11.sp, color = Color.Gray)
                            Text("Test 4", fontSize = 11.sp, color = Color.Gray)
                            Text("Recent", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Strong vs Weak Topics
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Strong Topics", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1B5E20))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("• Electrostatics (Physics)", fontSize = 12.sp, color = Color(0xFF2E7D32))
                            Text("• Solutions (Chemistry)", fontSize = 12.sp, color = Color(0xFF2E7D32))
                            Text("• A.P. & Algebra (Math)", fontSize = 12.sp, color = Color(0xFF2E7D32))
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PriorityHigh, contentDescription = null, tint = Color(0xFFEF6C00), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Focus Topics", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFE65100))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("• Wave Optics Derivation", fontSize = 12.sp, color = Color(0xFFEF6C00))
                            Text("• Organic Conversions", fontSize = 12.sp, color = Color(0xFFEF6C00))
                            Text("• Heights & Distances", fontSize = 12.sp, color = Color(0xFFEF6C00))
                        }
                    }
                }
            }

            // Test History Section
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test History (${history.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            if (history.isEmpty()) {
                item {
                    Text(
                        "No completed tests yet. Take a mock test or daily quiz to generate analytics!",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(history) { attempt ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Column {
                                Text(attempt.testTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${attempt.className} • ${attempt.subject}", fontSize = 11.sp, color = Color.Gray)
                            }

                            Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                                Text(
                                    "${attempt.score} Marks (${attempt.accuracyPercentage}%)",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
