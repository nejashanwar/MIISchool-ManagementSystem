package com.example.marasaaapp.data.local.dao

import androidx.room.*
import com.example.marasaaapp.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {

    // --- Users ---
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    // --- Student Profiles ---
    @Query("SELECT * FROM student_profiles")
    fun getAllStudentProfiles(): Flow<List<StudentProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentProfile(profile: StudentProfileEntity)

    @Update
    suspend fun updateStudentProfile(profile: StudentProfileEntity)

    @Delete
    suspend fun deleteStudentProfile(profile: StudentProfileEntity)

    // --- Teacher Profiles ---
    @Query("SELECT * FROM teacher_profiles")
    fun getAllTeacherProfiles(): Flow<List<TeacherProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacherProfile(profile: TeacherProfileEntity)

    @Update
    suspend fun updateTeacherProfile(profile: TeacherProfileEntity)

    @Delete
    suspend fun deleteTeacherProfile(profile: TeacherProfileEntity)

    // --- Staff Profiles ---
    @Query("SELECT * FROM staff_profiles")
    fun getAllStaffProfiles(): Flow<List<StaffProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffProfile(profile: StaffProfileEntity)

    @Update
    suspend fun updateStaffProfile(profile: StaffProfileEntity)

    @Delete
    suspend fun deleteStaffProfile(profile: StaffProfileEntity)

    // --- Courses ---
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)

    // --- Semesters ---
    @Query("SELECT * FROM semesters")
    fun getAllSemesters(): Flow<List<SemesterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: SemesterEntity)

    // --- Enrollments ---
    @Query("SELECT * FROM enrollments")
    fun getAllEnrollments(): Flow<List<EnrollmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: EnrollmentEntity)

    // --- Scores ---
    @Query("SELECT * FROM scores")
    fun getAllScores(): Flow<List<ScoreEntity>>

    @Query("SELECT * FROM scores WHERE studentId = :studentId")
    fun getScoresForStudent(studentId: String): Flow<List<ScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: ScoreEntity)

    @Update
    suspend fun updateScore(score: ScoreEntity)

    @Delete
    suspend fun deleteScore(score: ScoreEntity)

    // --- Attendance ---
    @Query("SELECT * FROM attendance_records")
    fun getAllAttendanceRecords(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId")
    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Delete
    suspend fun deleteAttendance(attendance: AttendanceEntity)

    // --- Staff Attendance ---
    @Query("SELECT * FROM staff_attendance_records")
    fun getAllStaffAttendanceRecords(): Flow<List<StaffAttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffAttendance(attendance: StaffAttendanceEntity)

    // --- Payments ---
    @Query("SELECT * FROM payment_records")
    fun getAllPaymentRecords(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payment_records WHERE studentId = :studentId")
    fun getPaymentsForStudent(studentId: String): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)

    // --- Salaries ---
    @Query("SELECT * FROM salary_records")
    fun getAllSalaryRecords(): Flow<List<SalaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalary(salary: SalaryEntity)

    @Update
    suspend fun updateSalary(salary: SalaryEntity)

    @Delete
    suspend fun deleteSalary(salary: SalaryEntity)

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE senderId = :userId OR receiverId = :userId OR receiverId = 'all' ORDER BY timestamp ASC")
    fun getMessagesForUser(userId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Update
    suspend fun updateChatMessage(message: ChatMessageEntity)

    @Delete
    suspend fun deleteChatMessage(message: ChatMessageEntity)
}
