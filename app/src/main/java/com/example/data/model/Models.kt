package com.example.data.model

data class Question(
    val id: String,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val subject: String,
    val className: String,
    val difficulty: String = "Medium"
)

data class TestPaper(
    val id: String,
    val title: String,
    val className: String,
    val subject: String,
    val durationMinutes: Int,
    val totalMarks: Int,
    val questions: List<Question>,
    val isDailyQuiz: Boolean = false,
    val isPYQ: Boolean = false,
    val year: String? = null,
    val isPublished: Boolean = true
)

data class StudyNote(
    val id: String,
    val title: String,
    val className: String,
    val subject: String,
    val category: String, // Notes, Chapter Summary, PYQ
    val pdfUrl: String,
    val pagesCount: Int,
    val fileSizeMb: String,
    val contentSummary: List<String>
)

data class VideoLecture(
    val id: String,
    val title: String,
    val className: String,
    val subject: String,
    val educatorName: String,
    val duration: String,
    val videoUrl: String,
    val thumbnailColorHex: String
)

data class LeaderboardEntry(
    val rank: Int,
    val studentName: String,
    val district: String,
    val score: Int,
    val totalScore: Int,
    val accuracy: String
)

enum class UserAuthTab {
    LOGIN, REGISTER, FORGOT_PASSWORD
}

data class TestResult(
    val testPaper: TestPaper,
    val userAnswers: Map<Int, Int>, // Question index -> Option index
    val timeSpentSeconds: Int,
    val negativeMarkingEnabled: Boolean,
    val score: Int,
    val maxScore: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val unattemptedCount: Int,
    val accuracyPercentage: Int,
    val rank: Int
)
