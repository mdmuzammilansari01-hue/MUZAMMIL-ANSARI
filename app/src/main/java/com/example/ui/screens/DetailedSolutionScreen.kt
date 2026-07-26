package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedSolutionScreen(viewModel: MainViewModel) {
    val result by viewModel.activeTestResult.collectAsState()
    val testResult = result ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detailed Solutions", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.TestResultScreen) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            itemsIndexed(testResult.testPaper.questions) { index, question ->
                val userOpt = testResult.userAnswers[index]
                val isCorrect = userOpt == question.correctIndex
                val isUnattempted = userOpt == null

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Q${index + 1}. ${question.questionText}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            when {
                                isCorrect -> Badge(containerColor = Color(0xFF2E7D32)) {
                                    Text("Correct (+4)", modifier = Modifier.padding(4.dp), color = Color.White)
                                }
                                isUnattempted -> Badge(containerColor = Color(0xFFEF6C00)) {
                                    Text("Unattempted (0)", modifier = Modifier.padding(4.dp), color = Color.White)
                                }
                                else -> Badge(containerColor = Color(0xFFC62828)) {
                                    Text("Wrong (-1)", modifier = Modifier.padding(4.dp), color = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Options status
                        question.options.forEachIndexed { optIdx, optionText ->
                            val isUserChosen = userOpt == optIdx
                            val isRightAnswer = optIdx == question.correctIndex

                            val (bg, border, textColor) = when {
                                isRightAnswer -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), Color(0xFF1B5E20))
                                isUserChosen -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), Color(0xFFB71C1C))
                                else -> Triple(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), Color.Transparent, MaterialTheme.colorScheme.onSurface)
                            }

                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = bg),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(1.dp, border, RoundedCornerShape(10.dp))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = "${('A' + optIdx)}. $optionText",
                                        fontWeight = if (isRightAnswer || isUserChosen) FontWeight.Bold else FontWeight.Normal,
                                        color = textColor,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (isRightAnswer) {
                                        Icon(Icons.Default.Check, contentDescription = "Correct", tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                    } else if (isUserChosen) {
                                        Icon(Icons.Default.Close, contentDescription = "Wrong Choice", tint = Color(0xFFC62828), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Explanation Box
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp)) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Explanation:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(question.explanation, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
