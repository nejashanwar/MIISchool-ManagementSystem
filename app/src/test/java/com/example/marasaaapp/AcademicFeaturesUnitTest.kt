package com.example.marasaaapp

import com.example.marasaaapp.model.*
import com.example.marasaaapp.repository.SchoolRepository
import com.example.marasaaapp.util.CsvExportHelper
import org.junit.Assert.*
import org.junit.Test

class AcademicFeaturesUnitTest {

    @Test
    fun testScoreGradeAndGPAComputation() {
        val scoreA = Score("crs_101", "Quran", "Ustadh", assignmentsMarks = 18.0, midtermMarks = 27.0, finalExamMarks = 48.0)
        assertEquals(45.0, scoreA.continuousAssessmentMarks, 0.01)
        assertEquals(93.0, scoreA.totalMarks, 0.01)
        assertEquals("A+", scoreA.grade)
        assertEquals(4.0, scoreA.gradePoint, 0.01)
        assertTrue(scoreA.isPass)

        val scoreF = Score("crs_102", "Tajweed", "Ustadh", assignmentsMarks = 8.0, midtermMarks = 12.0, finalExamMarks = 25.0)
        assertEquals(45.0, scoreF.totalMarks, 0.01)
        assertEquals("F", scoreF.grade)
        assertEquals(0.0, scoreF.gradePoint, 0.01)
        assertFalse(scoreF.isPass)
    }

    @Test
    fun testReportCardCentralizationAndGPA() {
        val score1 = Score("crs_101", "Quran", "Ustadh", assignmentsMarks = 18.0, midtermMarks = 27.0, finalExamMarks = 45.0) // 90 -> A+ (4.0)
        val score2 = Score("crs_102", "Tajweed", "Ustadh", assignmentsMarks = 15.0, midtermMarks = 22.0, finalExamMarks = 43.0) // 80 -> A- (3.5)

        val card = ReportCard(
            id = "test_rc",
            studentId = "usr_student_test",
            studentName = "Test Student",
            gradeLevel = "Grade 8",
            semesterName = "Semester 1",
            results = listOf(
                SubjectResult("QRN-101", "Holy Quran", score1),
                SubjectResult("TJW-102", "Tajweed Rules", score2),
            ),
            teacherComments = "Great work",
            issueDate = "2024-10-25",
        )

        assertEquals(170.0, card.overallTotal, 0.01)
        assertEquals(85.0, card.averageScore, 0.01)
        assertEquals(3.75, card.gpa, 0.01)
        assertTrue(card.isPassed)
    }

    @Test
    fun testRepositoryScoreUpdateCentralization() {
        val studentId = "usr_student_1"
        val courseId = "crs_101"

        SchoolRepository.updateOrSubmitScore(
            studentId = studentId,
            courseId = courseId,
            assignmentsMarks = 20.0,
            midtermMarks = 30.0,
            finalExamMarks = 50.0,
            comments = "Perfect Score!",
        )

        val updatedCard = SchoolRepository.reportCards.value.first { it.studentId == studentId }
        val quranResult = updatedCard.results.first { (it.score.subjectId == courseId) || (it.courseCode == "QRN-101") }

        assertEquals(100.0, quranResult.score.totalMarks, 0.01)
        assertEquals("A+", quranResult.score.grade)
        assertEquals("Perfect Score!", updatedCard.teacherComments)
    }

    @Test
    fun testCourseEnrollmentLogic() {
        val studentId = "usr_student_2"
        val courseId = "crs_202"
        val semesterId = "sem_1"

        val enrolled = SchoolRepository.enrollStudentInCourse(studentId, courseId, semesterId)
        assertTrue(enrolled)

        val reEnrolled = SchoolRepository.enrollStudentInCourse(studentId, courseId, semesterId)
        assertFalse(reEnrolled)
    }

    @Test
    fun testCsvGenerationFormat() {
        val score = Score("crs_101", "Quran", "Ustadh", assignmentsMarks = 15.0, midtermMarks = 25.0, finalExamMarks = 50.0)
        val card = ReportCard(
            id = "rc_csv",
            studentId = "usr_csv",
            studentName = "Ahmed Ali",
            gradeLevel = "Grade 8",
            semesterName = "Semester 1",
            results = listOf(SubjectResult("QRN-101", "Holy Quran", score)),
            teacherComments = "Good effort",
            issueDate = "2024-10-25"
        )

        val csv = CsvExportHelper.generateCsvContent(card)
        assertTrue(csv.contains("MADRASAA IFAA ISLAMA"))
        assertTrue(csv.contains("Ahmed Ali"))
        assertTrue(csv.contains("QRN-101"))
        assertTrue(csv.contains("Holy Quran"))
    }
}
