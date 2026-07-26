package com.example.data.model

data class ClassItem(
    val id: String,
    val name: String,
    val code: String,
    val stream: String,
    val description: String = ""
)

data class SubjectItem(
    val id: String,
    val name: String,
    val className: String,
    val chaptersCount: Int = 0,
    val iconName: String = "Book"
)

data class ChapterItem(
    val id: String,
    val title: String,
    val className: String,
    val subject: String,
    val summary: String = ""
)

data class StudentRecord(
    val id: String,
    val name: String,
    val email: String,
    val className: String,
    val rollNo: String,
    val isBlocked: Boolean = false,
    val joinedDate: String = "July 2026",
    val testsTaken: Int = 0,
    val avgScore: String = "0%"
)

data class NotificationRecord(
    val id: String,
    val title: String,
    val message: String,
    val targetClass: String, // "All Students" or specific
    val priority: String = "Normal", // Normal, High, Urgent
    val sentTimestamp: String = "Just now"
)

data class SupabaseSyncStatus(
    val isConnected: Boolean = true,
    val postgrestReady: Boolean = true,
    val storageBucketReady: Boolean = true,
    val authReady: Boolean = true,
    val lastSyncTime: String = "Live Connected"
)

typealias FirebaseSyncStatus = SupabaseSyncStatus
