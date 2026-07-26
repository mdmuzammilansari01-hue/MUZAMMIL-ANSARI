package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Test Attempts
    @Query("SELECT * FROM test_attempts ORDER BY timestamp DESC")
    fun getAllTestAttempts(): Flow<List<TestAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestAttempt(attempt: TestAttemptEntity)

    @Query("DELETE FROM test_attempts")
    suspend fun clearTestHistory()

    // Bookmarked Notes
    @Query("SELECT * FROM bookmarked_notes ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkNoteEntity)

    @Query("DELETE FROM bookmarked_notes WHERE id = :id")
    suspend fun deleteBookmark(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_notes WHERE id = :id)")
    suspend fun isBookmarked(id: String): Boolean

    // Custom MCQs (Admin added)
    @Query("SELECT * FROM custom_mcqs WHERE className = :className AND subject = :subject")
    fun getCustomMcqs(className: String, subject: String): Flow<List<CustomMcqEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomMcq(mcq: CustomMcqEntity)

    // Student Profile
    @Query("SELECT * FROM student_profile WHERE id = 1")
    fun getStudentProfile(): Flow<StudentProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStudentProfile(profile: StudentProfileEntity)
}
