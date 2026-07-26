package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClassItem
import com.example.data.model.Question
import com.example.data.model.StudentRecord
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen

private val AdminPrimaryGreen = Color(0xFF006C4C)
private val AdminSecondaryGreen = Color(0xFF2E7D32)
private val AdminDarkGreen = Color(0xFF004D40)
private val AdminLightBg = Color(0xFFF1F8E9)
private val AdminAccentGold = Color(0xFFFBC02D)
private val AdminErrorRed = Color(0xFFD32F2F)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdminPanelScreen(viewModel: MainViewModel) {
    val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsState()
    val successMsg by viewModel.adminSuccessMessage.collectAsState()
    val errorMsg by viewModel.adminErrorMessage.collectAsState()

    if (!isAdminUnlocked) {
        AdminLoginScreen(viewModel = viewModel)
        return
    }

    // State collections
    val classList by viewModel.classList.collectAsState()
    val subjectList by viewModel.subjectList.collectAsState()
    val chapterList by viewModel.chapterList.collectAsState()
    val notesList by viewModel.dynamicNotesList.collectAsState()
    val mcqList by viewModel.mcqList.collectAsState()
    val testList by viewModel.dynamicTestList.collectAsState()
    val videoList by viewModel.videoList.collectAsState()
    val studentList by viewModel.studentList.collectAsState()
    val notificationList by viewModel.notificationList.collectAsState()
    val firebaseSyncStatus by viewModel.firebaseSyncStatus.collectAsState()
    val isFirebaseSyncing by viewModel.isFirebaseSyncing.collectAsState()

    var activeTabIdx by remember { mutableStateOf(0) }
    val tabs = listOf(
        "Dashboard",
        "Content",
        "Notes (PDF)",
        "MCQs",
        "Mock Tests",
        "Videos",
        "Students",
        "Notifications"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "JAC Admin Control Panel",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Supabase Live Connected • Master Dashboard",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        modifier = Modifier.testTag("btn_admin_home_back")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Student Portal", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.triggerFirebaseSync() },
                        enabled = !isFirebaseSyncing,
                        modifier = Modifier.testTag("btn_firebase_sync")
                    ) {
                        if (isFirebaseSyncing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.CloudSync, contentDescription = "Sync with Firebase", tint = Color.White)
                        }
                    }
                    IconButton(
                        onClick = { viewModel.lockAdmin() },
                        modifier = Modifier.testTag("btn_lock_admin")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = "Lock Admin Session", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminPrimaryGreen)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AdminLightBg)
        ) {
            // Success / Status Banner
            AnimatedVisibility(visible = successMsg != null || errorMsg != null) {
                val isErr = errorMsg != null
                val messageText = errorMsg ?: successMsg ?: ""
                Surface(
                    color = if (isErr) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = if (isErr) Icons.Default.Error else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isErr) AdminErrorRed else AdminPrimaryGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = messageText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isErr) AdminErrorRed else AdminDarkGreen
                            )
                        }
                        IconButton(
                            onClick = { viewModel.clearAdminMessages() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text("✕", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Top Navigation Tab Strip
            ScrollableTabRow(
                selectedTabIndex = activeTabIdx,
                containerColor = Color.White,
                contentColor = AdminPrimaryGreen,
                edgePadding = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = activeTabIdx == index,
                        onClick = {
                            activeTabIdx = index
                            viewModel.clearAdminMessages()
                        },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (activeTabIdx == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                color = if (activeTabIdx == index) AdminPrimaryGreen else Color.DarkGray
                            )
                        },
                        modifier = Modifier.testTag("admin_tab_$index")
                    )
                }
            }

            // Tab View Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (activeTabIdx) {
                    0 -> DashboardOverviewTab(
                        viewModel = viewModel,
                        studentCount = studentList.size,
                        testCount = testList.size,
                        noteCount = notesList.size,
                        videoCount = videoList.size,
                        classCount = classList.size,
                        onTabSelect = { activeTabIdx = it }
                    )
                    1 -> ContentManagementTab(viewModel = viewModel, classList = classList, subjectList = subjectList, chapterList = chapterList)
                    2 -> NotesManagementTab(viewModel = viewModel, notesList = notesList, classList = classList, subjectList = subjectList)
                    3 -> McqManagementTab(viewModel = viewModel, mcqList = mcqList, classList = classList, subjectList = subjectList)
                    4 -> MockTestManagementTab(viewModel = viewModel, testList = testList, mcqList = mcqList, classList = classList, subjectList = subjectList)
                    5 -> VideoManagementTab(viewModel = viewModel, videoList = videoList, classList = classList, subjectList = subjectList)
                    6 -> StudentManagementTab(viewModel = viewModel, studentList = studentList, classList = classList)
                    7 -> NotificationTab(viewModel = viewModel, notificationList = notificationList, classList = classList)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 0: DASHBOARD OVERVIEW
// -----------------------------------------------------------------------------
@Composable
private fun DashboardOverviewTab(
    viewModel: MainViewModel,
    studentCount: Int,
    testCount: Int,
    noteCount: Int,
    videoCount: Int,
    classCount: Int,
    onTabSelect: (Int) -> Unit
) {
    val firebaseSyncStatus by viewModel.firebaseSyncStatus.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Welcome Header Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AdminPrimaryGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(20.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Welcome, Admin!",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Jharkhand Academic Council (JAC) Content & Student Administration Portal",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("System Metrics Overview", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AdminDarkGreen)
        Spacer(modifier = Modifier.height(8.dp))

        // 5 Metric Counters
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                title = "Students",
                value = "$studentCount",
                icon = Icons.Default.Group,
                color = Color(0xFF1E88E5),
                modifier = Modifier.weight(1f),
                onClick = { onTabSelect(6) }
            )
            StatCard(
                title = "Mock Tests",
                value = "$testCount",
                icon = Icons.Default.Quiz,
                color = Color(0xFF43A047),
                modifier = Modifier.weight(1f),
                onClick = { onTabSelect(4) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                title = "PDF Notes",
                value = "$noteCount",
                icon = Icons.Default.Description,
                color = Color(0xFFFB8C00),
                modifier = Modifier.weight(1f),
                onClick = { onTabSelect(2) }
            )
            StatCard(
                title = "Videos",
                value = "$videoCount",
                icon = Icons.Default.PlayCircle,
                color = Color(0xFF8E24AA),
                modifier = Modifier.weight(1f),
                onClick = { onTabSelect(5) }
            )
            StatCard(
                title = "Classes",
                value = "$classCount",
                icon = Icons.Default.School,
                color = Color(0xFF00897B),
                modifier = Modifier.weight(1f),
                onClick = { onTabSelect(1) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Firebase Cloud Infrastructure Health
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storage, contentDescription = null, tint = AdminPrimaryGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Supabase Integration & Cloud Sync", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AdminDarkGreen)
                    }

                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF2E7D32)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    FirebaseStatusItem("PostgREST DB", "Realtime Sync", true)
                    FirebaseStatusItem("PDF Bucket", "Storage Active", true)
                    FirebaseStatusItem("Supabase Auth", "Secured", true)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("✅ Supabase Connected Successfully", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1B5E20))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("✅ Admin Account Ready (ansarimuzammil0018@gmail.com)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color(0xFF2E7D32))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.triggerFirebaseSync() },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_trigger_cloud_sync")
                ) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trigger Supabase Cloud Backup & Sync", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Quick Administration Actions", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AdminDarkGreen)
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickActionButton("Add Test", Icons.Default.Add, Modifier.weight(1f)) { onTabSelect(4) }
            QuickActionButton("Upload Notes", Icons.Default.Description, Modifier.weight(1f)) { onTabSelect(2) }
            QuickActionButton("Add MCQ", Icons.Default.Quiz, Modifier.weight(1f)) { onTabSelect(3) }
            QuickActionButton("Send Alert", Icons.Default.Notifications, Modifier.weight(1f)) { onTabSelect(7) }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(color.copy(alpha = 0.15f))
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color.Black)
            Text(title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun FirebaseStatusItem(title: String, status: String, active: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = if (active) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = null,
            tint = if (active) Color(0xFF2E7D32) else Color.Red,
            modifier = Modifier.size(18.dp)
        )
        Text(title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AdminDarkGreen)
        Text(status, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
private fun QuickActionButton(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AdminPrimaryGreen),
        modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 4.dp)) {
            Icon(icon, contentDescription = null, tint = AdminPrimaryGreen, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, fontSize = 10.sp, color = AdminPrimaryGreen, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 1: CONTENT MANAGEMENT (CLASSES, SUBJECTS, CHAPTERS)
// -----------------------------------------------------------------------------
@Composable
private fun ContentManagementTab(
    viewModel: MainViewModel,
    classList: List<ClassItem>,
    subjectList: List<com.example.data.model.SubjectItem>,
    chapterList: List<com.example.data.model.ChapterItem>
) {
    var classNameInput by remember { mutableStateOf("") }
    var classCodeInput by remember { mutableStateOf("") }
    var classStreamInput by remember { mutableStateOf("Science") }

    var subjectNameInput by remember { mutableStateOf("") }
    var subjectClassSelect by remember { mutableStateOf("Class 12 Science") }

    var chapterTitleInput by remember { mutableStateOf("") }
    var chapterClassSelect by remember { mutableStateOf("Class 12 Science") }
    var chapterSubjectSelect by remember { mutableStateOf("Physics") }
    var chapterSummaryInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Manage Classes, Subjects & Chapters", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AdminDarkGreen)
        Spacer(modifier = Modifier.height(12.dp))

        // Add Class Section
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("➕ Add New Class", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AdminPrimaryGreen)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = classNameInput,
                        onValueChange = { classNameInput = it },
                        label = { Text("Class Name (e.g. Class 12 Science)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_add_class_name")
                    )
                    OutlinedTextField(
                        value = classCodeInput,
                        onValueChange = { classCodeInput = it },
                        label = { Text("Code (e.g. JAC-12-S)") },
                        singleLine = true,
                        modifier = Modifier.width(130.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (classNameInput.isNotBlank()) {
                            viewModel.adminAddClass(classNameInput, classCodeInput, classStreamInput, "JAC Curriculum Class")
                            classNameInput = ""
                            classCodeInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.align(Alignment.End).testTag("btn_save_class")
                ) {
                    Text("Add Class")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Class List Display
        Text("Existing Classes (${classList.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        classList.forEach { item ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = AdminPrimaryGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Code: ${item.code} • ${item.stream}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    IconButton(onClick = { viewModel.adminDeleteClass(item.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Class", tint = AdminErrorRed)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Add Subject Section
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📚 Add Subject to Class", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AdminPrimaryGreen)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = subjectNameInput,
                    onValueChange = { subjectNameInput = it },
                    label = { Text("Subject Name (e.g. Accountancy)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_add_subject_name")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = subjectClassSelect,
                    onValueChange = { subjectClassSelect = it },
                    label = { Text("Assigned Class") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (subjectNameInput.isNotBlank()) {
                            viewModel.adminAddSubject(subjectNameInput, subjectClassSelect)
                            subjectNameInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.align(Alignment.End).testTag("btn_save_subject")
                ) {
                    Text("Add Subject")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Subject List Display
        Text("Subjects List (${subjectList.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        subjectList.forEach { sub ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Book, contentDescription = null, tint = AdminSecondaryGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(sub.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Assigned to: ${sub.className}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    IconButton(onClick = { viewModel.adminDeleteSubject(sub.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Subject", tint = AdminErrorRed)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 2: NOTES MANAGEMENT
// -----------------------------------------------------------------------------
@Composable
private fun NotesManagementTab(
    viewModel: MainViewModel,
    notesList: List<com.example.data.model.StudyNote>,
    classList: List<ClassItem>,
    subjectList: List<com.example.data.model.SubjectItem>
) {
    var titleInput by remember { mutableStateOf("") }
    var classInput by remember { mutableStateOf("Class 12 Science") }
    var subjectInput by remember { mutableStateOf("Physics") }
    var categoryInput by remember { mutableStateOf("Notes") }
    var summaryInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Manage PDF Notes & Formula Sheets", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AdminDarkGreen)
        Spacer(modifier = Modifier.height(12.dp))

        // Upload Note Form
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📤 Upload New Note PDF", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AdminPrimaryGreen)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = titleInput,
                    onValueChange = { titleInput = it },
                    label = { Text("Note Title") },
                    placeholder = { Text("e.g. Optics Complete Formula Sheet 2026") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_note_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = classInput,
                        onValueChange = { classInput = it },
                        label = { Text("Class") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = subjectInput,
                        onValueChange = { subjectInput = it },
                        label = { Text("Subject") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = categoryInput,
                    onValueChange = { categoryInput = it },
                    label = { Text("Category (Notes, PYQ, Summary)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = summaryInput,
                    onValueChange = { summaryInput = it },
                    label = { Text("Key Topics Summary (One per line)") },
                    placeholder = { Text("Topic 1\nTopic 2\nTopic 3") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("input_note_summary")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (titleInput.isNotBlank()) {
                            viewModel.adminAddNote(titleInput, classInput, subjectInput, categoryInput, summaryInput)
                            titleInput = ""
                            summaryInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_upload_note_pdf")
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload PDF & Publish Note", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Uploaded PDF Notes Library (${notesList.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AdminDarkGreen)
        Spacer(modifier = Modifier.height(8.dp))

        notesList.forEach { note ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(note.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${note.className} • ${note.subject} • ${note.pagesCount} Pages (${note.fileSizeMb})", fontSize = 11.sp, color = Color.Gray)
                    }
                    Row {
                        IconButton(onClick = { viewModel.openNotePdf(note) }) {
                            Icon(Icons.Default.Description, contentDescription = "View PDF", tint = AdminPrimaryGreen)
                        }
                        IconButton(onClick = { viewModel.adminDeleteNote(note.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Note", tint = AdminErrorRed)
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 3: MCQ MANAGEMENT
// -----------------------------------------------------------------------------
@Composable
private fun McqManagementTab(
    viewModel: MainViewModel,
    mcqList: List<Question>,
    classList: List<ClassItem>,
    subjectList: List<com.example.data.model.SubjectItem>
) {
    var questionText by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctIdx by remember { mutableStateOf(0) }
    var explanation by remember { mutableStateOf("") }
    var classSelect by remember { mutableStateOf("Class 12 Science") }
    var subjectSelect by remember { mutableStateOf("Physics") }
    var difficultySelect by remember { mutableStateOf("Medium") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("MCQ Question Bank Creator", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AdminDarkGreen)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("❓ Create New Multiple Choice Question", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AdminPrimaryGreen)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Question Statement") },
                    placeholder = { Text("e.g. What is the SI unit of magnetic flux density?") },
                    modifier = Modifier.fillMaxWidth().testTag("input_mcq_text")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = optA,
                        onValueChange = { optA = it },
                        label = { Text("Option A") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_mcq_opta")
                    )
                    OutlinedTextField(
                        value = optB,
                        onValueChange = { optB = it },
                        label = { Text("Option B") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_mcq_optb")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = optC,
                        onValueChange = { optC = it },
                        label = { Text("Option C") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_mcq_optc")
                    )
                    OutlinedTextField(
                        value = optD,
                        onValueChange = { optD = it },
                        label = { Text("Option D") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_mcq_optd")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Correct Answer Selection:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AdminDarkGreen)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    listOf("Option A", "Option B", "Option C", "Option D").forEachIndexed { index, label ->
                        RadioButton(
                            selected = correctIdx == index,
                            onClick = { correctIdx = index },
                            colors = RadioButtonDefaults.colors(selectedColor = AdminPrimaryGreen)
                        )
                        Text(label, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    label = { Text("Solution / Explanation") },
                    placeholder = { Text("Explain step-by-step why this answer is correct...") },
                    modifier = Modifier.fillMaxWidth().testTag("input_mcq_explain")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = classSelect, onValueChange = { classSelect = it }, label = { Text("Class") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = subjectSelect, onValueChange = { subjectSelect = it }, label = { Text("Subject") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = difficultySelect, onValueChange = { difficultySelect = it }, label = { Text("Difficulty") }, singleLine = true, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (questionText.isNotBlank() && optA.isNotBlank() && optB.isNotBlank()) {
                            viewModel.adminCreateMcq(
                                qText = questionText,
                                optA = optA,
                                optB = optB,
                                optC = if (optC.isNotBlank()) optC else "None of these",
                                optD = if (optD.isNotBlank()) optD else "All of the above",
                                correctIdx = correctIdx,
                                explain = explanation,
                                className = classSelect,
                                subject = subjectSelect,
                                difficulty = difficultySelect
                            )
                            questionText = ""
                            optA = ""
                            optB = ""
                            optC = ""
                            optD = ""
                            explanation = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_add_mcq")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Question to Question Bank", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Existing MCQs in Question Bank (${mcqList.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AdminDarkGreen)
        Spacer(modifier = Modifier.height(8.dp))

        mcqList.forEachIndexed { idx, q ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(color = AdminPrimaryGreen, shape = RoundedCornerShape(6.dp)) {
                            Text("Q${idx + 1} • ${q.className} (${q.subject})", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        IconButton(onClick = { viewModel.adminDeleteMcq(q.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete MCQ", tint = AdminErrorRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(q.questionText, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(6.dp))
                    q.options.forEachIndexed { optIndex, text ->
                        val isCorrect = optIndex == q.correctIndex
                        Text(
                            text = "${if (optIndex == 0) "A" else if (optIndex == 1) "B" else if (optIndex == 2) "C" else "D"}. $text ${if (isCorrect) "✓ (Correct)" else ""}",
                            fontSize = 11.sp,
                            fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCorrect) Color(0xFF1B5E20) else Color.DarkGray
                        )
                    }

                    if (q.explanation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Explanation: ${q.explanation}", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 4: MOCK TEST MANAGEMENT
// -----------------------------------------------------------------------------
@Composable
private fun MockTestManagementTab(
    viewModel: MainViewModel,
    testList: List<com.example.data.model.TestPaper>,
    mcqList: List<Question>,
    classList: List<ClassItem>,
    subjectList: List<com.example.data.model.SubjectItem>
) {
    var titleInput by remember { mutableStateOf("") }
    var classSelect by remember { mutableStateOf("Class 12 Science") }
    var subjectSelect by remember { mutableStateOf("Physics") }
    var durationInput by remember { mutableStateOf("15") }
    var marksInput by remember { mutableStateOf("20") }
    var isPublishedSwitch by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Mock Test & Board Quiz Management", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AdminDarkGreen)
        Spacer(modifier = Modifier.height(12.dp))

        // Create Test Form
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📝 Create New Mock Test Paper", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AdminPrimaryGreen)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = titleInput,
                    onValueChange = { titleInput = it },
                    label = { Text("Test Title") },
                    placeholder = { Text("e.g. JAC Class 12 Physics Full Model Paper 2026") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_test_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = classSelect, onValueChange = { classSelect = it }, label = { Text("Class") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = subjectSelect, onValueChange = { subjectSelect = it }, label = { Text("Subject") }, singleLine = true, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = durationInput, onValueChange = { durationInput = it }, label = { Text("Duration (Mins)") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = marksInput, onValueChange = { marksInput = it }, label = { Text("Total Marks") }, singleLine = true, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Publish Test Immediately:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = isPublishedSwitch,
                        onCheckedChange = { isPublishedSwitch = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AdminPrimaryGreen)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (titleInput.isNotBlank()) {
                            viewModel.adminCreateMockTest(
                                title = titleInput,
                                className = classSelect,
                                subject = subjectSelect,
                                durationMinutes = durationInput.toIntOrNull() ?: 15,
                                totalMarks = marksInput.toIntOrNull() ?: 20,
                                isPublished = isPublishedSwitch,
                                questionsList = mcqList.take(5)
                            )
                            titleInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_create_mock_test")
                ) {
                    Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create Test & Assign MCQs", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Active Mock Test Papers (${testList.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AdminDarkGreen)
        Spacer(modifier = Modifier.height(8.dp))

        testList.forEach { test ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(test.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))

                        Surface(
                            color = if (test.isPublished) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (test.isPublished) "Published" else "Draft",
                                color = if (test.isPublished) Color(0xFF1B5E20) else Color(0xFFE65100),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${test.className} • ${test.subject} • ${test.durationMinutes} Mins • ${test.questions.size} Questions • ${test.totalMarks} Marks", fontSize = 11.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { viewModel.adminTogglePublishTest(test.id) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(if (test.isPublished) "Unpublish" else "Publish", fontSize = 11.sp)
                        }
                        IconButton(onClick = { viewModel.adminDeleteTest(test.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Test", tint = AdminErrorRed)
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 5: VIDEO MANAGEMENT
// -----------------------------------------------------------------------------
@Composable
private fun VideoManagementTab(
    viewModel: MainViewModel,
    videoList: List<com.example.data.model.VideoLecture>,
    classList: List<ClassItem>,
    subjectList: List<com.example.data.model.SubjectItem>
) {
    var titleInput by remember { mutableStateOf("") }
    var classSelect by remember { mutableStateOf("Class 12 Science") }
    var subjectSelect by remember { mutableStateOf("Physics") }
    var educatorInput by remember { mutableStateOf("Prof. R.K. Sharma") }
    var durationInput by remember { mutableStateOf("45 min") }
    var videoUrlInput by remember { mutableStateOf("https://www.youtube.com") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Manage YouTube Video Lectures", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AdminDarkGreen)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🎥 Upload Video Lecture Link", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AdminPrimaryGreen)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = titleInput, onValueChange = { titleInput = it }, label = { Text("Video Title") }, singleLine = true, modifier = Modifier.fillMaxWidth().testTag("input_video_title"))
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = classSelect, onValueChange = { classSelect = it }, label = { Text("Class") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = subjectSelect, onValueChange = { subjectSelect = it }, label = { Text("Subject") }, singleLine = true, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = educatorInput, onValueChange = { educatorInput = it }, label = { Text("Educator Name") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = durationInput, onValueChange = { durationInput = it }, label = { Text("Duration") }, singleLine = true, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = videoUrlInput, onValueChange = { videoUrlInput = it }, label = { Text("YouTube / Video URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (titleInput.isNotBlank()) {
                            viewModel.adminAddVideo(titleInput, classSelect, subjectSelect, educatorInput, durationInput, videoUrlInput)
                            titleInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_add_video")
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Video to Course", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Course Video Library (${videoList.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AdminDarkGreen)
        Spacer(modifier = Modifier.height(8.dp))

        videoList.forEach { vid ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(vid.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${vid.className} • ${vid.subject} • ${vid.educatorName} (${vid.duration})", fontSize = 11.sp, color = Color.Gray)
                    }
                    IconButton(onClick = { viewModel.adminDeleteVideo(vid.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Video", tint = AdminErrorRed)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 6: STUDENT MANAGEMENT
// -----------------------------------------------------------------------------
@Composable
private fun StudentManagementTab(
    viewModel: MainViewModel,
    studentList: List<StudentRecord>,
    classList: List<ClassItem>
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = studentList.filter {
        searchQuery.isBlank() ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true) ||
                it.rollNo.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Student Enrollment & Block Control", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AdminDarkGreen)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Students by Name / Roll / Email") },
            leadingIcon = { Icon(Icons.Default.PersonSearch, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("input_search_students")
        )

        Spacer(modifier = Modifier.height(12.dp))

        filteredList.forEach { student ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(student.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = if (student.isBlocked) AdminErrorRed else AdminPrimaryGreen,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (student.isBlocked) "Blocked" else "Active",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text("Roll: ${student.rollNo} • ${student.className}", fontSize = 11.sp, color = Color.Gray)
                        Text("Email: ${student.email} • Tests: ${student.testsTaken} (Avg: ${student.avgScore})", fontSize = 11.sp, color = Color.DarkGray)
                    }

                    OutlinedButton(
                        onClick = { viewModel.adminToggleBlockStudent(student.id) },
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (student.isBlocked) AdminPrimaryGreen else AdminErrorRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (student.isBlocked) "Unblock" else "Block",
                            color = if (student.isBlocked) AdminPrimaryGreen else AdminErrorRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 7: NOTIFICATION BROADCAST
// -----------------------------------------------------------------------------
@Composable
private fun NotificationTab(
    viewModel: MainViewModel,
    notificationList: List<com.example.data.model.NotificationRecord>,
    classList: List<ClassItem>
) {
    var notifTitle by remember { mutableStateOf("") }
    var notifBody by remember { mutableStateOf("") }
    var targetClass by remember { mutableStateOf("All Students") }
    var priority by remember { mutableStateOf("High") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Supabase Broadcast & Realtime Notifications", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AdminDarkGreen)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🔔 Send Notification Alert to Students", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AdminPrimaryGreen)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notifTitle,
                    onValueChange = { notifTitle = it },
                    label = { Text("Notification Header Title") },
                    placeholder = { Text("e.g. Board Exam Schedule Announced") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_notif_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notifBody,
                    onValueChange = { notifBody = it },
                    label = { Text("Notification Body Text") },
                    placeholder = { Text("Enter detailed message content...") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("input_notif_body")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = targetClass, onValueChange = { targetClass = it }, label = { Text("Target Class") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = priority, onValueChange = { priority = it }, label = { Text("Priority") }, singleLine = true, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (notifTitle.isNotBlank() && notifBody.isNotBlank()) {
                            viewModel.adminSendNotification(notifTitle, notifBody, targetClass, priority)
                            notifTitle = ""
                            notifBody = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_send_fcm_notif")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Broadcast Notification Now", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Sent Notification History (${notificationList.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AdminDarkGreen)
        Spacer(modifier = Modifier.height(8.dp))

        notificationList.forEach { notif ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Surface(color = AdminPrimaryGreen, shape = RoundedCornerShape(6.dp)) {
                            Text(notif.targetClass, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(notif.message, fontSize = 12.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Priority: ${notif.priority} • Sent: ${notif.sentTimestamp}", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}
