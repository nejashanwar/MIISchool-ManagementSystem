package com.example.marasaaapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.marasaaapp.data.local.converter.Converters
import com.example.marasaaapp.data.local.dao.SchoolDao
import com.example.marasaaapp.data.local.entity.*
import com.example.marasaaapp.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        StudentProfileEntity::class,
        TeacherProfileEntity::class,
        StaffProfileEntity::class,
        CourseEntity::class,
        SemesterEntity::class,
        EnrollmentEntity::class,
        ScoreEntity::class,
        AttendanceEntity::class,
        StaffAttendanceEntity::class,
        PaymentEntity::class,
        SalaryEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MadrasaaDatabase : RoomDatabase() {

    abstract fun schoolDao(): SchoolDao

    companion object {
        @Volatile
        private var INSTANCE: MadrasaaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): MadrasaaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MadrasaaDatabase::class.java,
                    "madrasaa_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(MadrasaaDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class MadrasaaDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.schoolDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: SchoolDao) {
                // 1. Users
                val adminUser = UserEntity("usr_admin_1", "Sheikh Ibrahim Al-Hassan", "ibrahim.principal@madrasaa-ifaa.edu", UserRole.ADMIN, "+251 91 123 4567", "admin123")
                val teacherUser = UserEntity("usr_teacher_1", "Ustadh Abdurrahman Mahmoud", "abdurrahman@madrasaa-ifaa.edu", UserRole.TEACHER, "+251 91 234 5678", "teacher123")
                val teacher2 = UserEntity("usr_teacher_2", "Ustadha Mariam Hassan", "mariam@madrasaa-ifaa.edu", UserRole.TEACHER, "+251 91 234 5679", "teacher123")
                val teacher3 = UserEntity("usr_teacher_3", "Ustadh Bilal Omar", "bilal@madrasaa-ifaa.edu", UserRole.TEACHER, "+251 91 234 5680", "teacher123")
                val studentUser = UserEntity("usr_student_1", "Ahmed Ali", "ahmed.ali@student.madrasaa-ifaa.edu", UserRole.STUDENT, "+251 91 345 6789", "student123")
                val student2 = UserEntity("usr_student_2", "Fatima Hassan", "fatima.h@student.madrasaa-ifaa.edu", UserRole.STUDENT, "+251 91 345 6790", "student123")
                val student3 = UserEntity("usr_student_3", "Mohamed Omar", "mohamed.o@student.madrasaa-ifaa.edu", UserRole.STUDENT, "+251 91 345 6791", "student123")
                val parentUser = UserEntity("usr_parent_1", "Ali Ahmed (Parent)", "ali.ahmed@parent.com", UserRole.PARENT, "+251 91 456 7890", "parent123")
                val parent2 = UserEntity("usr_parent_2", "Hassan Mahmoud (Parent)", "hassan.m@parent.com", UserRole.PARENT, "+251 91 456 7891", "parent123")

                listOf(adminUser, teacherUser, teacher2, teacher3, studentUser, student2, student3, parentUser, parent2).forEach {
                    dao.insertUser(it)
                }

                // 2. Student Profiles
                dao.insertStudentProfile(StudentProfileEntity("std_prof_1", "usr_student_1", "MII-2024-001", "Ahmed Ali", "Grade 8 - Tahfeedh", "usr_parent_1", "Ali Ahmed", "2010-05-14", "2021-09-01", "Male"))
                dao.insertStudentProfile(StudentProfileEntity("std_prof_2", "usr_student_2", "MII-2024-002", "Fatima Hassan", "Grade 8 - Tahfeedh", "usr_parent_2", "Hassan Mahmoud", "2010-08-22", "2021-09-01", "Female"))
                dao.insertStudentProfile(StudentProfileEntity("std_prof_3", "usr_student_3", "MII-2024-003", "Mohamed Omar", "Grade 10 - Shariah", "usr_parent_1", "Ali Ahmed", "2008-11-03", "2019-09-01", "Male"))

                // 3. Teacher Profiles
                dao.insertTeacherProfile(TeacherProfileEntity("tch_prof_1", "usr_teacher_1", "STAFF-101", "Ustadh Abdurrahman Mahmoud", "B.A. Islamic Studies & Ijazah", "Quran Recitation & Tajweed", "2018-08-15"))
                dao.insertTeacherProfile(TeacherProfileEntity("tch_prof_2", "usr_teacher_2", "STAFF-102", "Ustadha Mariam Hassan", "M.A. Arabic Literature", "Classical Arabic & Seerah", "2019-01-10"))
                dao.insertTeacherProfile(TeacherProfileEntity("tch_prof_3", "usr_teacher_3", "STAFF-103", "Ustadh Bilal Omar", "B.S. Islamic Jurisprudence", "Fiqh & Aqeedah", "2020-09-01"))

                // 4. Staff Profiles
                dao.insertStaffProfile(StaffProfileEntity("stf_prof_1", "usr_admin_1", "STAFF-001", "Sheikh Ibrahim Al-Hassan", "Academy Principal & General Director", "Administration", "2015-06-01"))
                dao.insertStaffProfile(StaffProfileEntity("stf_prof_2", "usr_teacher_1", "STAFF-101", "Ustadh Abdurrahman Mahmoud", "Head of Quranic Studies", "Academics", "2018-08-15"))

                // 5. Semesters
                dao.insertSemester(SemesterEntity("sem_1", "Semester 1, 2024/2025", "2024-09-01", "2025-01-31", true))
                dao.insertSemester(SemesterEntity("sem_2", "Semester 2, 2024/2025", "2025-02-15", "2025-06-30", false))

                // 6. Courses
                val courses = listOf(
                    CourseEntity("crs_101", "Quran Hifz (Memorization)", "QRN-101", "usr_teacher_1", "Semester 1, 2024/2025", "Comprehensive Quranic memorization.", "Grade 8 - Tahfeedh", 4, "Ustadh Abdurrahman Mahmoud"),
                    CourseEntity("crs_102", "Tajweed (Recitation Rules)", "TJW-102", "usr_teacher_1", "Semester 1, 2024/2025", "Detailed study of Makharij and Tajweed rules.", "Grade 8 - Tahfeedh", 3, "Ustadh Abdurrahman Mahmoud"),
                    CourseEntity("crs_201", "Arabic Grammar (Nahw/Sarf)", "ARB-201", "usr_teacher_2", "Semester 1, 2024/2025", "Grammar, Morphology, and vocabulary.", "Grade 8 - Tahfeedh", 3, "Ustadha Mariam Hassan"),
                    CourseEntity("crs_202", "Islamic Jurisprudence (Fiqh)", "FQH-202", "usr_teacher_3", "Semester 1, 2024/2025", "Study of Taharah, Salah, Zakah, Sawm.", "Grade 10 - Shariah", 3, "Ustadh Bilal Omar"),
                    CourseEntity("crs_105", "Aqeedah (Islamic Creed)", "AQD-105", "usr_teacher_3", "Semester 1, 2024/2025", "Fundamentals of Islamic faith and Tawheed.", "Grade 8 - Tahfeedh", 3, "Ustadh Bilal Omar"),
                    CourseEntity("crs_103", "Seerah & Islamic History", "HIS-103", "usr_teacher_2", "Semester 1, 2024/2025", "Life of Prophet Muhammad (PBUH).", "Grade 8 - Tahfeedh", 2, "Ustadha Mariam Hassan"),
                    CourseEntity("crs_104", "Mathematics & Arithmetic", "MTH-104", "usr_teacher_2", "Semester 1, 2024/2025", "Algebra, Geometry, and calculations.", "Grade 8 - Tahfeedh", 3, "Ustadha Mariam Hassan")
                )
                courses.forEach { dao.insertCourse(it) }

                // 7. Enrollments
                listOf(
                    EnrollmentEntity("enr_1", "usr_student_1", "crs_101", "sem_1", "2024-09-01"),
                    EnrollmentEntity("enr_2", "usr_student_1", "crs_102", "sem_1", "2024-09-01"),
                    EnrollmentEntity("enr_3", "usr_student_1", "crs_201", "sem_1", "2024-09-01"),
                    EnrollmentEntity("enr_4", "usr_student_1", "crs_105", "sem_1", "2024-09-01"),
                    EnrollmentEntity("enr_5", "usr_student_1", "crs_103", "sem_1", "2024-09-01"),
                    EnrollmentEntity("enr_6", "usr_student_2", "crs_101", "sem_1", "2024-09-01"),
                    EnrollmentEntity("enr_7", "usr_student_2", "crs_102", "sem_1", "2024-09-01"),
                    EnrollmentEntity("enr_8", "usr_student_3", "crs_202", "sem_1", "2024-09-01")
                ).forEach { dao.insertEnrollment(it) }

                // 8. Scores
                listOf(
                    ScoreEntity("scr_1", "usr_student_1", "crs_101", 15.0, 23.0, 57.0, 95.0, "A+", true, "Quran Hifz", "Ustadh Abdurrahman"),
                    ScoreEntity("scr_2", "usr_student_1", "crs_102", 14.0, 21.0, 53.0, 88.0, "A", true, "Tajweed Rules", "Ustadh Abdurrahman"),
                    ScoreEntity("scr_3", "usr_student_1", "crs_201", 14.0, 20.0, 50.0, 84.0, "A-", true, "Arabic Grammar", "Ustadha Mariam"),
                    ScoreEntity("scr_4", "usr_student_1", "crs_105", 15.0, 22.0, 55.0, 92.0, "A+", true, "Aqeedah Creed", "Ustadh Bilal"),
                    ScoreEntity("scr_5", "usr_student_1", "crs_103", 15.0, 21.0, 54.0, 90.0, "A+", true, "Seerah History", "Ustadha Mariam"),
                    ScoreEntity("scr_6", "usr_student_1", "crs_104", 14.0, 21.0, 51.0, 86.0, "A", true, "Mathematics", "Ustadha Mariam")
                ).forEach { dao.insertScore(it) }

                // 9. Attendance
                listOf(
                    AttendanceEntity("att_1", "usr_student_1", "crs_101", "2024-10-21", AttendanceStatus.PRESENT, "Ahmed Ali", "Quran Hifz", "Punctual"),
                    AttendanceEntity("att_2", "usr_student_1", "crs_102", "2024-10-22", AttendanceStatus.PRESENT, "Ahmed Ali", "Tajweed Rules", "Punctual"),
                    AttendanceEntity("att_3", "usr_student_1", "crs_201", "2024-10-23", AttendanceStatus.LATE, "Ahmed Ali", "Arabic Grammar", "Arrived 10 mins late"),
                    AttendanceEntity("att_4", "usr_student_1", "crs_105", "2024-10-24", AttendanceStatus.PRESENT, "Ahmed Ali", "Aqeedah Creed", "Punctual"),
                    AttendanceEntity("att_5", "usr_student_1", "crs_103", "2024-10-25", AttendanceStatus.PRESENT, "Ahmed Ali", "Seerah History", "Active in class")
                ).forEach { dao.insertAttendance(it) }

                // 10. Staff Attendance
                listOf(
                    StaffAttendanceEntity("satt_1", "usr_teacher_1", "Ustadh Abdurrahman Mahmoud", "2024-10-21", AttendanceStatus.PRESENT, "07:45 AM", "03:30 PM", "Normal shift"),
                    StaffAttendanceEntity("satt_2", "usr_teacher_2", "Ustadha Mariam Hassan", "2024-10-21", AttendanceStatus.PRESENT, "07:50 AM", "03:30 PM", "Normal shift")
                ).forEach { dao.insertStaffAttendance(it) }

                // 11. Payments
                listOf(
                    PaymentEntity("pay_1", "usr_student_1", 150.0, FeeType.TUITION, PaymentStatus.PAID, "2024-09-03", "Ahmed Ali", "2024-09-05", "2024-09-03", "TXN-98421"),
                    PaymentEntity("pay_2", "usr_student_1", 45.0, FeeType.BOOKS, PaymentStatus.PAID, "2024-09-08", "Ahmed Ali", "2024-09-10", "2024-09-08", "TXN-98422"),
                    PaymentEntity("pay_3", "usr_student_2", 150.0, FeeType.TUITION, PaymentStatus.PAID, "2024-09-04", "Fatima Hassan", "2024-09-05", "2024-09-04", "TXN-98430")
                ).forEach { dao.insertPayment(it) }

                // 12. Salaries
                listOf(
                    SalaryEntity("sal_1", "usr_teacher_1", "Ustadh Abdurrahman Mahmoud", 1200.0, 150.0, 0.0, 1350.0, "2024-09-25", "Head of Quranic Studies", "September 2024", PaymentStatus.PAID, "2024-09-25"),
                    SalaryEntity("sal_2", "usr_teacher_2", "Ustadha Mariam Hassan", 1200.0, 100.0, 0.0, 1300.0, "2024-09-25", "Arabic & Seerah Educator", "September 2024", PaymentStatus.PAID, "2024-09-25")
                ).forEach { dao.insertSalary(it) }

                // 13. Chat Messages
                listOf(
                    ChatMessageEntity("msg_1", "usr_admin_1", "usr_teacher_1", "Assalamu Alaikum Ustadh, please submit the Quran memorization progress log for Grade 8.", "2024-10-24 09:15 AM", DeliveryStatus.SEEN, "Sheikh Ibrahim Al-Hassan", UserRole.ADMIN, "Ustadh Abdurrahman Mahmoud", UserRole.TEACHER, true, false),
                    ChatMessageEntity("msg_2", "usr_teacher_1", "usr_admin_1", "Wa Alaikum Assalam Sheikh Ibrahim. The report card data has been updated in the system.", "2024-10-24 09:30 AM", DeliveryStatus.SEEN, "Ustadh Abdurrahman Mahmoud", UserRole.TEACHER, "Sheikh Ibrahim Al-Hassan", UserRole.ADMIN, true, false),
                    ChatMessageEntity("msg_6", "usr_admin_1", "all", "📢 ANNOUNCEMENT: Midterm Academic Assembly scheduled for next Monday at 9:00 AM.", "2024-10-25 08:00 AM", DeliveryStatus.SENT, "Sheikh Ibrahim Al-Hassan", UserRole.ADMIN, "All Academy Community", UserRole.ADMIN, false, true)
                ).forEach { dao.insertChatMessage(it) }
            }
        }
    }
}
