package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_attempts")
data class TestAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val testId: String,
    val testTitle: String,
    val className: String,
    val subject: String,
    val score: Int,
    val totalQuestions: Int,
    val accuracyPercentage: Int,
    val timeTakenSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarked_notes")
data class BookmarkNoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val className: String,
    val subject: String,
    val category: String, // Notes, PYQs, Model Paper
    val isDownloaded: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_mcqs")
data class CustomMcqEntity(
    @PrimaryKey val id: String,
    val className: String,
    val subject: String,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctIndex: Int,
    val explanation: String
)

@Entity(tableName = "student_profile")
data class StudentProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val email: String,
    val selectedClass: String,
    val rollNumber: String,
    val isLoggedIn: Boolean,
    val isAdmin: Boolean = false
)
