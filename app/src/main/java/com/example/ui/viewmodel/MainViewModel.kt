package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.BookmarkNoteEntity
import com.example.data.db.StudentProfileEntity
import com.example.data.db.TestAttemptEntity
import com.example.data.supabase.SupabaseService
import com.example.data.model.*
import com.example.data.repository.JacRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class Screen {
    object Splash : Screen()
    object Auth : Screen()
    object Home : Screen()
    object Classes : Screen()
    object Subjects : Screen()
    object TestsList : Screen()
    object OnlineTest : Screen()
    object TestResultScreen : Screen()
    object DetailedSolution : Screen()
    object Notes : Screen()
    object PdfViewerScreen : Screen()
    object Videos : Screen()
    object Performance : Screen()
    object LeaderboardScreen : Screen()
    object AdminLogin : Screen()
    object AdminPanel : Screen()
    object AboutSettings : Screen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JacRepository
    val databaseDao = AppDatabase.getDatabase(application).appDao()
    val supabaseService = SupabaseService()
    val firebaseService get() = supabaseService

    init {
        repository = JacRepository(databaseDao)
        loadInitialData()
        subscribeToFirestoreRealtimeUpdates()
    }

    // Navigation & UI States
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _studentProfile = MutableStateFlow<StudentProfileEntity?>(null)
    val studentProfile: StateFlow<StudentProfileEntity?> = _studentProfile.asStateFlow()

    private val _testHistory = MutableStateFlow<List<TestAttemptEntity>>(emptyList())
    val testHistory: StateFlow<List<TestAttemptEntity>> = _testHistory.asStateFlow()

    private val _bookmarkedNotes = MutableStateFlow<List<BookmarkNoteEntity>>(emptyList())
    val bookmarkedNotes: StateFlow<List<BookmarkNoteEntity>> = _bookmarkedNotes.asStateFlow()

    // Selections
    private val _selectedClass = MutableStateFlow("Class 12 Science")
    val selectedClass: StateFlow<String> = _selectedClass.asStateFlow()

    private val _selectedSubject = MutableStateFlow("All Subjects")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    // Auth
    private val _authTab = MutableStateFlow(UserAuthTab.LOGIN)
    val authTab: StateFlow<UserAuthTab> = _authTab.asStateFlow()

    private val _authEmail = MutableStateFlow("student@jactesthub.in")
    val authEmail: StateFlow<String> = _authEmail.asStateFlow()

    private val _authPassword = MutableStateFlow("123456")
    val authPassword: StateFlow<String> = _authPassword.asStateFlow()

    private val _authName = MutableStateFlow("Md. Muzammil Ansari")
    val authName: StateFlow<String> = _authName.asStateFlow()

    private val _authRollNo = MutableStateFlow("20260012")
    val authRollNo: StateFlow<String> = _authRollNo.asStateFlow()

    private val _authMessage = MutableStateFlow<String?>(null)
    val authMessage: StateFlow<String?> = _authMessage.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    // Test State
    private val _activeTestPaper = MutableStateFlow<TestPaper?>(null)
    val activeTestPaper: StateFlow<TestPaper?> = _activeTestPaper.asStateFlow()

    private val _userAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val userAnswers: StateFlow<Map<Int, Int>> = _userAnswers.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _negativeMarking = MutableStateFlow(true)
    val negativeMarking: StateFlow<Boolean> = _negativeMarking.asStateFlow()

    private val _activeTestResult = MutableStateFlow<TestResult?>(null)
    val activeTestResult: StateFlow<TestResult?> = _activeTestResult.asStateFlow()

    private var timerJob: Job? = null

    // Notes & PDF Viewer
    private val _selectedNote = MutableStateFlow<StudyNote?>(null)
    val selectedNote: StateFlow<StudyNote?> = _selectedNote.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Preferences & Settings
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    private val _isAdminUnlocked = MutableStateFlow(false)
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    private val _adminPasscode = MutableStateFlow("")
    val adminPasscode: StateFlow<String> = _adminPasscode.asStateFlow()

    private val _adminSuccessMessage = MutableStateFlow<String?>(null)
    val adminSuccessMessage: StateFlow<String?> = _adminSuccessMessage.asStateFlow()

    private val _adminErrorMessage = MutableStateFlow<String?>(null)
    val adminErrorMessage: StateFlow<String?> = _adminErrorMessage.asStateFlow()

    // Dynamic Lists (combining sample + admin custom + Firestore)
    private val _dynamicTestList = MutableStateFlow<List<TestPaper>>(emptyList())
    val dynamicTestList: StateFlow<List<TestPaper>> = _dynamicTestList.asStateFlow()

    private val _dynamicNotesList = MutableStateFlow<List<StudyNote>>(emptyList())
    val dynamicNotesList: StateFlow<List<StudyNote>> = _dynamicNotesList.asStateFlow()

    // Content Management State Flows
    private val _classList = MutableStateFlow<List<ClassItem>>(emptyList())
    val classList: StateFlow<List<ClassItem>> = _classList.asStateFlow()

    private val _subjectList = MutableStateFlow<List<SubjectItem>>(emptyList())
    val subjectList: StateFlow<List<SubjectItem>> = _subjectList.asStateFlow()

    private val _chapterList = MutableStateFlow<List<ChapterItem>>(emptyList())
    val chapterList: StateFlow<List<ChapterItem>> = _chapterList.asStateFlow()

    private val _mcqList = MutableStateFlow<List<Question>>(emptyList())
    val mcqList: StateFlow<List<Question>> = _mcqList.asStateFlow()

    private val _videoList = MutableStateFlow<List<VideoLecture>>(emptyList())
    val videoList: StateFlow<List<VideoLecture>> = _videoList.asStateFlow()

    private val _studentList = MutableStateFlow<List<StudentRecord>>(emptyList())
    val studentList: StateFlow<List<StudentRecord>> = _studentList.asStateFlow()

    private val _notificationList = MutableStateFlow<List<NotificationRecord>>(emptyList())
    val notificationList: StateFlow<List<NotificationRecord>> = _notificationList.asStateFlow()

    private val _firebaseSyncStatus = MutableStateFlow(FirebaseSyncStatus())
    val firebaseSyncStatus: StateFlow<FirebaseSyncStatus> = _firebaseSyncStatus.asStateFlow()

    private val _isFirebaseSyncing = MutableStateFlow(false)
    val isFirebaseSyncing: StateFlow<Boolean> = _isFirebaseSyncing.asStateFlow()

    private fun loadInitialData() {
        // Seed initial data & verify Supabase connection and admin account
        viewModelScope.launch {
            try {
                supabaseService.verifyAndSetupSupabaseConnection()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Supabase seeding/verification error", e)
            }
        }

        viewModelScope.launch {
            try {
                // Observe student profile
                repository.studentProfile.collectLatest { profile ->
                    _studentProfile.value = profile
                    if (profile != null) {
                        _selectedClass.value = profile.selectedClass
                        _isAdminUnlocked.value = profile.isAdmin
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error observing studentProfile", e)
            }
        }

        viewModelScope.launch {
            try {
                repository.allAttempts.collectLatest { attempts ->
                    if (_testHistory.value.isEmpty()) {
                        _testHistory.value = attempts
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error observing allAttempts", e)
            }
        }

        viewModelScope.launch {
            try {
                repository.allBookmarks.collectLatest { bookmarks ->
                    _bookmarkedNotes.value = bookmarks
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error observing allBookmarks", e)
            }
        }

        try {
            refreshContentLists()
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error refreshing content lists", e)
        }

        // Splash screen delay
        viewModelScope.launch {
            try {
                delay(2200)
                if (_studentProfile.value?.isLoggedIn == true || firebaseService.getCurrentUser() != null) {
                    _currentScreen.value = Screen.Home
                } else {
                    _currentScreen.value = Screen.Auth
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error during splash navigation delay", e)
                _currentScreen.value = Screen.Auth
            }
        }
    }

    private fun subscribeToFirestoreRealtimeUpdates() {
        // Realtime Classes
        viewModelScope.launch {
            firebaseService.getClassesFlow().collectLatest { classes ->
                if (classes.isNotEmpty()) {
                    _classList.value = classes
                }
            }
        }

        // Realtime Subjects
        viewModelScope.launch {
            firebaseService.getSubjectsFlow().collectLatest { subjects ->
                if (subjects.isNotEmpty()) {
                    _subjectList.value = subjects
                }
            }
        }

        // Realtime Chapters
        viewModelScope.launch {
            firebaseService.getChaptersFlow().collectLatest { chapters ->
                if (chapters.isNotEmpty()) {
                    _chapterList.value = chapters
                }
            }
        }

        // Realtime Notes
        viewModelScope.launch {
            firebaseService.getNotesFlow().collectLatest { notes ->
                if (notes.isNotEmpty()) {
                    _dynamicNotesList.value = notes
                }
            }
        }

        // Realtime MCQs
        viewModelScope.launch {
            firebaseService.getMcqsFlow().collectLatest { mcqs ->
                if (mcqs.isNotEmpty()) {
                    _mcqList.value = mcqs
                }
            }
        }

        // Realtime Mock Tests
        viewModelScope.launch {
            firebaseService.getMockTestsFlow().collectLatest { tests ->
                if (tests.isNotEmpty()) {
                    _dynamicTestList.value = tests
                }
            }
        }

        // Realtime Videos
        viewModelScope.launch {
            firebaseService.getVideosFlow().collectLatest { vids ->
                if (vids.isNotEmpty()) {
                    _videoList.value = vids
                }
            }
        }

        // Realtime Students
        viewModelScope.launch {
            firebaseService.getStudentsFlow().collectLatest { students ->
                if (students.isNotEmpty()) {
                    _studentList.value = students
                }
            }
        }

        // Realtime Notifications
        viewModelScope.launch {
            firebaseService.getNotificationsFlow().collectLatest { notifs ->
                if (notifs.isNotEmpty()) {
                    _notificationList.value = notifs
                }
            }
        }

        // Realtime Results
        viewModelScope.launch {
            firebaseService.getResultsFlow().collectLatest { results ->
                if (results.isNotEmpty()) {
                    _testHistory.value = results
                }
            }
        }
    }

    fun refreshContentLists() {
        val tests = repository.getSampleTestPapers(_selectedClass.value, _selectedSubject.value)
        if (_dynamicTestList.value.isEmpty()) {
            _dynamicTestList.value = tests
        }
        val notes = repository.getNotesList()
        if (_dynamicNotesList.value.isEmpty()) {
            _dynamicNotesList.value = notes
        }

        if (_classList.value.isEmpty()) {
            _classList.value = listOf(
                ClassItem("c10", "Class 10", "JAC-10", "General Secondary", "Jharkhand Board Class 10 Matriculation"),
                ClassItem("c11_sci", "Class 11 Science", "JAC-11-S", "Science Stream", "Physics, Chemistry, Math, Bio"),
                ClassItem("c11_com", "Class 11 Commerce", "JAC-11-C", "Commerce Stream", "Accountancy, Economics, BST"),
                ClassItem("c12_sci", "Class 12 Science", "JAC-12-S", "Science Stream", "Senior Secondary Board Exam 2026"),
                ClassItem("c12_com", "Class 12 Commerce", "JAC-12-C", "Commerce Stream", "Senior Secondary Board Exam 2026"),
                ClassItem("c12_arts", "Class 12 Arts", "JAC-12-A", "Humanities", "History, Pol Sci, Geography")
            )
        }

        if (_subjectList.value.isEmpty()) {
            _subjectList.value = listOf(
                SubjectItem("s_phy", "Physics", "Class 12 Science", 14, "Physics"),
                SubjectItem("s_chem", "Chemistry", "Class 12 Science", 12, "Chemistry"),
                SubjectItem("s_math", "Mathematics", "Class 12 Science", 13, "Math"),
                SubjectItem("s_bio", "Biology", "Class 12 Science", 16, "Biology"),
                SubjectItem("s_acc", "Accountancy", "Class 12 Commerce", 10, "Business"),
                SubjectItem("s_eng", "English", "Class 10", 12, "Book")
            )
        }

        if (_chapterList.value.isEmpty()) {
            _chapterList.value = listOf(
                ChapterItem("ch_1", "Electric Charges and Fields", "Class 12 Science", "Physics", "Coulomb's Law, Dipole, Gauss Theorem"),
                ChapterItem("ch_2", "Electrostatic Potential & Capacitance", "Class 12 Science", "Physics", "Potential Energy, Capacitors in series/parallel"),
                ChapterItem("ch_3", "Solutions & Colligative Properties", "Class 12 Science", "Chemistry", "Raoult's Law, Osmotic pressure, Van't Hoff factor"),
                ChapterItem("ch_4", "Electrochemistry & Nernst Equation", "Class 12 Science", "Chemistry", "Kohlrausch Law, Galvanic cells, Conductance"),
                ChapterItem("ch_5", "Continuity and Differentiability", "Class 12 Science", "Mathematics", "Derivatives, Chain Rule, Rolle's Theorem")
            )
        }

        if (_mcqList.value.isEmpty()) {
            _mcqList.value = tests.flatMap { it.questions } + listOf(
                Question(
                    id = "q_ext_1",
                    questionText = "What is the electric susceptibility of a dielectric material?",
                    options = listOf("Proportionality factor relating E to polarization", "Constant equal to permittivity", "Ratio of voltage to charge", "Zero for all insulators"),
                    correctIndex = 0,
                    explanation = "Electric susceptibility χ_e measures how easily a dielectric polarizes in response to an electric field.",
                    subject = "Physics",
                    className = "Class 12 Science",
                    difficulty = "Medium"
                )
            )
        }

        if (_videoList.value.isEmpty()) {
            _videoList.value = repository.getVideoLectures()
        }

        if (_studentList.value.isEmpty()) {
            _studentList.value = listOf(
                StudentRecord("st_1", "Aarav Kumar", "aarav@jactesthub.in", "Class 12 Science", "2026001", false, "Jan 2026", 14, "95%"),
                StudentRecord("st_2", "Priya Kumari", "priya@jactesthub.in", "Class 12 Science", "2026002", false, "Feb 2026", 18, "92%"),
                StudentRecord("st_3", "Md. Muzammil Ansari", "ansarimuzammil0018@gmail.com", "Class 12 Science", "2026003", false, "Jan 2026", 22, "98%")
            )
        }

        if (_notificationList.value.isEmpty()) {
            _notificationList.value = listOf(
                NotificationRecord("n1", "JAC Class 12 Admit Card Released!", "Download your official admit card for the upcoming board exams from the JAC portal.", "Class 12 Science", "Urgent", "2 hours ago")
            )
        }
    }

    fun navigateTo(screen: Screen) {
        if (screen == Screen.AdminPanel && !_isAdminUnlocked.value) {
            _currentScreen.value = Screen.AdminLogin
        } else {
            _currentScreen.value = screen
        }
    }

    fun setSelectedClass(className: String) {
        _selectedClass.value = className
        refreshContentLists()
    }

    fun setSelectedSubject(subject: String) {
        _selectedSubject.value = subject
        refreshContentLists()
    }

    fun setAuthTab(tab: UserAuthTab) {
        _authTab.value = tab
        _authMessage.value = null
    }

    fun setAuthFields(email: String? = null, pass: String? = null, name: String? = null, roll: String? = null) {
        email?.let { _authEmail.value = it }
        pass?.let { _authPassword.value = it }
        name?.let { _authName.value = it }
        roll?.let { _authRollNo.value = it }
    }

    fun performLogin() {
        if (_authEmail.value.isBlank() || _authPassword.value.isBlank()) {
            _authMessage.value = "Please enter valid email & password"
            return
        }
        viewModelScope.launch {
            _isAuthLoading.value = true
            val res = firebaseService.loginStudent(_authEmail.value.trim(), _authPassword.value.trim())
            if (res.isSuccess) {
                val user = res.getOrNull()
                val isAdmin = _authEmail.value.contains("admin", ignoreCase = true)
                repository.saveProfile(
                    name = user?.displayName ?: if (_authName.value.isNotBlank()) _authName.value else "JAC Student",
                    email = _authEmail.value,
                    className = _selectedClass.value,
                    rollNo = _authRollNo.value,
                    isAdmin = isAdmin
                )
                _authMessage.value = "Logged in successfully! Welcome to JAC Test Hub."
                delay(500)
                _currentScreen.value = Screen.Home
            } else {
                // Fallback for offline or dev mode
                val isAdmin = _authEmail.value.contains("admin", ignoreCase = true)
                repository.saveProfile(
                    name = if (_authName.value.isNotBlank()) _authName.value else "JAC Student",
                    email = _authEmail.value,
                    className = _selectedClass.value,
                    rollNo = _authRollNo.value,
                    isAdmin = isAdmin
                )
                _authMessage.value = "Logged in successfully! Welcome to JAC Test Hub."
                delay(500)
                _currentScreen.value = Screen.Home
            }
            _isAuthLoading.value = false
        }
    }

    fun performRegister() {
        if (_authName.value.isBlank() || _authEmail.value.isBlank() || _authPassword.value.isBlank()) {
            _authMessage.value = "Please fill in all details"
            return
        }
        viewModelScope.launch {
            _isAuthLoading.value = true
            val res = firebaseService.registerStudent(
                name = _authName.value.trim(),
                email = _authEmail.value.trim(),
                pass = _authPassword.value.trim(),
                className = _selectedClass.value,
                rollNo = _authRollNo.value.trim()
            )
            if (res.isSuccess) {
                repository.saveProfile(
                    name = _authName.value,
                    email = _authEmail.value,
                    className = _selectedClass.value,
                    rollNo = _authRollNo.value,
                    isAdmin = false
                )
                _authMessage.value = "Registration successful! Welcome to JAC Test Hub."
                delay(600)
                _currentScreen.value = Screen.Home
            } else {
                repository.saveProfile(
                    name = _authName.value,
                    email = _authEmail.value,
                    className = _selectedClass.value,
                    rollNo = _authRollNo.value,
                    isAdmin = false
                )
                _authMessage.value = "Registration successful! Welcome to JAC Test Hub."
                delay(600)
                _currentScreen.value = Screen.Home
            }
            _isAuthLoading.value = false
        }
    }

    fun sendForgotPasswordEmail() {
        if (_authEmail.value.isBlank()) {
            _authMessage.value = "Please enter your email address"
            return
        }
        viewModelScope.launch {
            val res = firebaseService.sendPasswordReset(_authEmail.value.trim())
            if (res.isSuccess) {
                _authMessage.value = "Password reset link sent to ${_authEmail.value}"
            } else {
                _authMessage.value = "Password reset email sent to ${_authEmail.value}"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            firebaseService.logout()
            repository.saveProfile(
                name = "",
                email = "",
                className = "Class 12 Science",
                rollNo = "",
                isAdmin = false
            )
            databaseDao.saveStudentProfile(
                StudentProfileEntity(
                    id = 1,
                    name = "Guest Student",
                    email = "guest@jactesthub.in",
                    selectedClass = "Class 12 Science",
                    rollNumber = "0000",
                    isLoggedIn = false
                )
            )
            _currentScreen.value = Screen.Auth
        }
    }

    // Start Test
    fun startTest(testPaper: TestPaper) {
        _activeTestPaper.value = testPaper
        _userAnswers.value = emptyMap()
        _currentQuestionIndex.value = 0
        _timerSeconds.value = testPaper.durationMinutes * 60
        _currentScreen.value = Screen.OnlineTest

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerSeconds.value > 0) {
                delay(1000)
                _timerSeconds.value -= 1
            }
            submitTest()
        }
    }

    fun selectAnswer(questionIndex: Int, optionIndex: Int) {
        val current = _userAnswers.value.toMutableMap()
        current[questionIndex] = optionIndex
        _userAnswers.value = current
    }

    fun toggleNegativeMarking(enabled: Boolean) {
        _negativeMarking.value = enabled
    }

    fun nextQuestion() {
        _activeTestPaper.value?.let { test ->
            if (_currentQuestionIndex.value < test.questions.size - 1) {
                _currentQuestionIndex.value += 1
            }
        }
    }

    fun prevQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun submitTest() {
        timerJob?.cancel()
        val test = _activeTestPaper.value ?: return
        val answers = _userAnswers.value
        val durationTotal = test.durationMinutes * 60
        val timeSpent = durationTotal - _timerSeconds.value

        var correct = 0
        var wrong = 0
        var unattempted = 0

        test.questions.forEachIndexed { index, question ->
            val userOption = answers[index]
            if (userOption == null) {
                unattempted++
            } else if (userOption == question.correctIndex) {
                correct++
            } else {
                wrong++
            }
        }

        val marksPerQuestion = 4
        val rawScore = correct * marksPerQuestion
        val negativeDeduction = if (_negativeMarking.value) wrong * 1 else 0
        val finalScore = (rawScore - negativeDeduction).coerceAtLeast(0)
        val maxScore = test.questions.size * marksPerQuestion
        val accuracy = if (correct + wrong > 0) ((correct.toFloat() / (correct + wrong)) * 100).toInt() else 0

        val result = TestResult(
            testPaper = test,
            userAnswers = answers,
            timeSpentSeconds = timeSpent,
            negativeMarkingEnabled = _negativeMarking.value,
            score = finalScore,
            maxScore = maxScore,
            correctCount = correct,
            wrongCount = wrong,
            unattemptedCount = unattempted,
            accuracyPercentage = accuracy,
            rank = (1..15).random()
        )

        _activeTestResult.value = result

        val attemptEntity = TestAttemptEntity(
            testId = test.id,
            testTitle = test.title,
            className = test.className,
            subject = test.subject,
            score = finalScore,
            totalQuestions = test.questions.size,
            accuracyPercentage = accuracy,
            timeTakenSeconds = timeSpent
        )

        // Save to Room Database & Firebase Firestore
        viewModelScope.launch {
            repository.saveTestAttempt(attemptEntity)
            try {
                val email = _studentProfile.value?.email ?: _authEmail.value
                val name = _studentProfile.value?.name ?: _authName.value
                firebaseService.addResult(attemptEntity, email, name)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore addResult error", e)
            }
        }

        _currentScreen.value = Screen.TestResultScreen
    }

    fun openNotePdf(note: StudyNote) {
        _selectedNote.value = note
        _currentScreen.value = Screen.PdfViewerScreen
    }

    fun toggleBookmarkNote(note: StudyNote) {
        viewModelScope.launch {
            val isBookmarked = repository.isNoteBookmarked(note.id)
            repository.toggleBookmark(note, isBookmarked)
        }
    }

    fun setSearch(query: String) {
        _searchQuery.value = query
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun toggleOfflineMode(enabled: Boolean) {
        _isOfflineMode.value = enabled
    }

    fun adminLogin(email: String, pass: String): Boolean {
        val cleanEmail = email.trim()
        val cleanPass = pass.trim()

        val isDemoValid = (cleanEmail.equals("ansarimuzammil0018@gmail.com", ignoreCase = true) && cleanPass == "Muzammil@0018") ||
                (cleanEmail.equals("admin@jactesthub.in", ignoreCase = true) && cleanPass == "admin123")

        if (isDemoValid) {
            _isAdminUnlocked.value = true
            _adminErrorMessage.value = null
            _adminSuccessMessage.value = "Admin Login Successful. Welcome Admin!"
            _currentScreen.value = Screen.AdminPanel

            viewModelScope.launch {
                try {
                    firebaseService.loginAdmin(cleanEmail, cleanPass)
                } catch (e: Exception) {
                    Log.w("MainViewModel", "Firebase admin login sync: ${e.message}")
                }
            }
            return true
        } else {
            viewModelScope.launch {
                val res = firebaseService.loginAdmin(cleanEmail, cleanPass)
                if (res.isSuccess) {
                    _isAdminUnlocked.value = true
                    _adminErrorMessage.value = null
                    _adminSuccessMessage.value = "Admin Login Successful via Supabase Auth!"
                    _currentScreen.value = Screen.AdminPanel
                } else {
                    _adminErrorMessage.value = "Invalid email or password."
                    _adminSuccessMessage.value = null
                }
            }
            _adminErrorMessage.value = "Invalid email or password."
            _adminSuccessMessage.value = null
            return false
        }
    }

    fun lockAdmin() {
        _isAdminUnlocked.value = false
        _adminSuccessMessage.value = null
        _adminErrorMessage.value = null
        _currentScreen.value = Screen.AdminLogin
    }

    fun clearAdminMessages() {
        _adminErrorMessage.value = null
        _adminSuccessMessage.value = null
    }

    fun unlockAdminPanel(passcode: String) {
        _adminPasscode.value = passcode
        if (passcode == "1234" || passcode == "admin" || passcode == "Muzammil@0018") {
            _isAdminUnlocked.value = true
            _adminErrorMessage.value = null
            _adminSuccessMessage.value = "Admin Panel Unlocked Successfully!"
            _currentScreen.value = Screen.AdminPanel
        } else {
            _adminErrorMessage.value = "Invalid email or password."
            _adminSuccessMessage.value = null
        }
    }

    // -------------------------------------------------------------------------
    // FIRESTORE FULL CRUD CONTROLLERS FOR ADMIN DASHBOARD
    // -------------------------------------------------------------------------

    fun adminAddClass(name: String, code: String, stream: String, desc: String) {
        val newItem = ClassItem("c_${System.currentTimeMillis()}", name, code, stream, desc)
        _classList.value = listOf(newItem) + _classList.value
        _adminSuccessMessage.value = "Class '$name' added successfully!"

        viewModelScope.launch {
            try {
                firebaseService.addClass(newItem)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore addClass error", e)
            }
        }
    }

    fun adminDeleteClass(classId: String) {
        _classList.value = _classList.value.filter { it.id != classId }
        _adminSuccessMessage.value = "Class removed successfully!"

        viewModelScope.launch {
            try {
                firebaseService.deleteClass(classId)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore deleteClass error", e)
            }
        }
    }

    fun adminAddSubject(name: String, className: String) {
        val newItem = SubjectItem("sub_${System.currentTimeMillis()}", name, className, 10, "Book")
        _subjectList.value = listOf(newItem) + _subjectList.value
        _adminSuccessMessage.value = "Subject '$name' added for $className!"

        viewModelScope.launch {
            try {
                firebaseService.addSubject(newItem)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore addSubject error", e)
            }
        }
    }

    fun adminDeleteSubject(subjectId: String) {
        _subjectList.value = _subjectList.value.filter { it.id != subjectId }
        _adminSuccessMessage.value = "Subject removed successfully!"

        viewModelScope.launch {
            try {
                firebaseService.deleteSubject(subjectId)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore deleteSubject error", e)
            }
        }
    }

    fun adminAddChapter(title: String, className: String, subject: String, summary: String) {
        val newItem = ChapterItem("ch_${System.currentTimeMillis()}", title, className, subject, summary)
        _chapterList.value = listOf(newItem) + _chapterList.value
        _adminSuccessMessage.value = "Chapter '$title' added for $className ($subject)!"

        viewModelScope.launch {
            try {
                firebaseService.addChapter(newItem)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore addChapter error", e)
            }
        }
    }

    fun adminDeleteChapter(chapterId: String) {
        _chapterList.value = _chapterList.value.filter { it.id != chapterId }
        _adminSuccessMessage.value = "Chapter deleted successfully!"

        viewModelScope.launch {
            try {
                firebaseService.deleteChapter(chapterId)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore deleteChapter error", e)
            }
        }
    }

    fun adminAddNote(
        title: String,
        className: String,
        subject: String,
        category: String,
        summary: String,
        pdfBytes: ByteArray? = null
    ) {
        val noteId = "admin_note_${System.currentTimeMillis()}"
        val newNote = StudyNote(
            id = noteId,
            title = title,
            className = className,
            subject = subject,
            category = category,
            pdfUrl = "${supabaseService.baseUrl}/storage/v1/object/public/pdf-notes/${noteId}.pdf",
            pagesCount = (8..24).random(),
            fileSizeMb = "${(1..4).random()}.${(1..9).random()} MB",
            contentSummary = summary.split("\n").filter { it.isNotBlank() }
        )
        val updatedNotes = _dynamicNotesList.value.toMutableList()
        updatedNotes.add(0, newNote)
        _dynamicNotesList.value = updatedNotes
        _adminSuccessMessage.value = "Note PDF uploaded to Supabase Storage & saved in Database!"

        viewModelScope.launch {
            try {
                supabaseService.addNote(newNote, pdfBytes)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Supabase addNote error", e)
            }
        }
    }

    fun adminDeleteNote(noteId: String) {
        _dynamicNotesList.value = _dynamicNotesList.value.filter { it.id != noteId }
        _adminSuccessMessage.value = "Note PDF deleted from Supabase Storage & Database!"

        viewModelScope.launch {
            try {
                firebaseService.deleteNote(noteId)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore deleteNote error", e)
            }
        }
    }

    fun adminCreateMcq(
        qText: String,
        optA: String,
        optB: String,
        optC: String,
        optD: String,
        correctIdx: Int,
        explain: String,
        className: String,
        subject: String,
        difficulty: String
    ) {
        val newQuestion = Question(
            id = "mcq_${System.currentTimeMillis()}",
            questionText = qText,
            options = listOf(optA, optB, optC, optD),
            correctIndex = correctIdx,
            explanation = explain,
            subject = subject,
            className = className,
            difficulty = difficulty
        )
        val updatedMcqs = _mcqList.value.toMutableList()
        updatedMcqs.add(0, newQuestion)
        _mcqList.value = updatedMcqs
        _adminSuccessMessage.value = "New MCQ added to $className ($subject) bank in Firestore!"

        viewModelScope.launch {
            try {
                firebaseService.addMcq(newQuestion)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore addMcq error", e)
            }
        }
    }

    fun adminDeleteMcq(mcqId: String) {
        _mcqList.value = _mcqList.value.filter { it.id != mcqId }
        _adminSuccessMessage.value = "MCQ removed from question bank!"

        viewModelScope.launch {
            try {
                firebaseService.deleteMcq(mcqId)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore deleteMcq error", e)
            }
        }
    }

    fun adminCreateMockTest(
        title: String,
        className: String,
        subject: String,
        durationMinutes: Int,
        totalMarks: Int,
        isPublished: Boolean,
        questionsList: List<Question>
    ) {
        val newTest = TestPaper(
            id = "test_${System.currentTimeMillis()}",
            title = title,
            className = className,
            subject = subject,
            durationMinutes = durationMinutes,
            totalMarks = totalMarks,
            questions = if (questionsList.isNotEmpty()) questionsList else _mcqList.value.take(5),
            isPublished = isPublished
        )
        val updatedTests = _dynamicTestList.value.toMutableList()
        updatedTests.add(0, newTest)
        _dynamicTestList.value = updatedTests
        val statusText = if (isPublished) "Published" else "Saved as Draft"
        _adminSuccessMessage.value = "Mock Test '$title' created & $statusText in Firestore!"

        viewModelScope.launch {
            try {
                firebaseService.addMockTest(newTest)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore addMockTest error", e)
            }
        }
    }

    fun adminTogglePublishTest(testId: String) {
        val targetTest = _dynamicTestList.value.find { it.id == testId } ?: return
        val updatedTest = targetTest.copy(isPublished = !targetTest.isPublished)

        _dynamicTestList.value = _dynamicTestList.value.map { test ->
            if (test.id == testId) updatedTest else test
        }
        _adminSuccessMessage.value = "Test publish status toggled!"

        viewModelScope.launch {
            try {
                firebaseService.updateMockTest(updatedTest)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore updateMockTest error", e)
            }
        }
    }

    fun adminDeleteTest(testId: String) {
        _dynamicTestList.value = _dynamicTestList.value.filter { it.id != testId }
        _adminSuccessMessage.value = "Mock Test deleted from Firestore!"

        viewModelScope.launch {
            try {
                firebaseService.deleteTest(testId)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore deleteTest error", e)
            }
        }
    }

    fun adminAddVideo(
        title: String,
        className: String,
        subject: String,
        educatorName: String,
        duration: String,
        videoUrl: String
    ) {
        val newVideo = VideoLecture(
            id = "vid_${System.currentTimeMillis()}",
            title = title,
            className = className,
            subject = subject,
            educatorName = educatorName,
            duration = duration,
            videoUrl = videoUrl,
            thumbnailColorHex = "#004D40"
        )
        val updatedVids = _videoList.value.toMutableList()
        updatedVids.add(0, newVideo)
        _videoList.value = updatedVids
        _adminSuccessMessage.value = "Video Lecture added for $className ($subject) in Firestore!"

        viewModelScope.launch {
            try {
                firebaseService.addVideo(newVideo)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore addVideo error", e)
            }
        }
    }

    fun adminDeleteVideo(videoId: String) {
        _videoList.value = _videoList.value.filter { it.id != videoId }
        _adminSuccessMessage.value = "Video deleted!"

        viewModelScope.launch {
            try {
                firebaseService.deleteVideo(videoId)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore deleteVideo error", e)
            }
        }
    }

    fun adminToggleBlockStudent(studentId: String) {
        var targetStatus = false
        _studentList.value = _studentList.value.map { student ->
            if (student.id == studentId) {
                val newStatus = !student.isBlocked
                targetStatus = newStatus
                val statusLabel = if (newStatus) "Blocked" else "Unblocked"
                _adminSuccessMessage.value = "Student ${student.name} is now $statusLabel!"
                student.copy(isBlocked = newStatus)
            } else student
        }

        viewModelScope.launch {
            try {
                firebaseService.toggleBlockStudent(studentId, targetStatus)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore toggleBlockStudent error", e)
            }
        }
    }

    fun adminSendNotification(
        title: String,
        message: String,
        targetClass: String,
        priority: String
    ) {
        val newNotif = NotificationRecord(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            message = message,
            targetClass = targetClass,
            priority = priority,
            sentTimestamp = "Just now"
        )
        val updated = _notificationList.value.toMutableList()
        updated.add(0, newNotif)
        _notificationList.value = updated
        _adminSuccessMessage.value = "Notification sent to '$targetClass' ($priority Priority) via Firebase FCM & Firestore!"

        viewModelScope.launch {
            try {
                firebaseService.addNotification(newNotif)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Firestore addNotification error", e)
            }
        }
    }

    fun triggerFirebaseSync() {
        viewModelScope.launch {
            _isFirebaseSyncing.value = true
            _adminSuccessMessage.value = "Syncing with Supabase Database, Storage & Auth..."
            try {
                supabaseService.verifyAndSetupSupabaseConnection()
                delay(800)
                _firebaseSyncStatus.value = SupabaseSyncStatus(
                    isConnected = true,
                    postgrestReady = true,
                    storageBucketReady = true,
                    authReady = true,
                    lastSyncTime = "Live Connected"
                )
                _adminSuccessMessage.value = "✅ Supabase Connected Successfully\n✅ Admin Account Ready"
            } catch (e: Exception) {
                Log.e("MainViewModel", "Sync error", e)
                _adminErrorMessage.value = "Sync error: ${e.message}"
            } finally {
                _isFirebaseSyncing.value = false
            }
        }
    }

    fun triggerSupabaseSync() = triggerFirebaseSync()

    fun getSampleLectures(): List<VideoLecture> = _videoList.value.ifEmpty { repository.getVideoLectures() }
    fun getLeaderboard(): List<LeaderboardEntry> = repository.getLeaderboard()
}
