package com.example.marasaaapp

import com.example.marasaaapp.model.*
import com.example.marasaaapp.repository.SchoolRepository
import com.example.marasaaapp.util.CsvExportHelper
import org.junit.Assert.*
import org.junit.Test

class FinancialAttendanceAndChatUnitTest {

    @Test
    fun testSalaryNetCalculationAndPayoutRecording() {
        val baseSalary = 1200.0
        val bonus = 150.0
        val deductions = 50.0

        val salaryRecord = SalaryRecord(
            id = "test_sal_1",
            staffId = "usr_teacher_1",
            staffName = "Ustadh Abdurrahman Mahmoud",
            roleTitle = "Head of Quranic Studies",
            monthYear = "November 2024",
            baseSalary = baseSalary,
            bonus = bonus,
            deductions = deductions,
            paymentDate = "2024-11-25",
            status = PaymentStatus.PAID,
        )

        assertEquals(1300.0, salaryRecord.netSalary, 0.01)

        // Record payout in repository
        SchoolRepository.recordSalaryPayout(
            staffId = "usr_teacher_1",
            staffName = "Ustadh Abdurrahman Mahmoud",
            roleTitle = "Head of Quranic Studies",
            monthYear = "November 2024",
            baseSalary = baseSalary,
            bonus = bonus,
            deductions = deductions,
            status = PaymentStatus.PAID,
            paymentDate = "2024-11-25",
        )

        val latestSalaries = SchoolRepository.salaryRecords.value
        val recorded = latestSalaries.firstOrNull { (it.monthYear == "November 2024") && (it.staffId == "usr_teacher_1") }
        assertNotNull(recorded)
        assertEquals(1300.0, recorded!!.netSalary, 0.01)
        assertEquals(PaymentStatus.PAID, recorded.status)

        assertTrue(SchoolRepository.getTotalSalariesPaid() > 0.0)
    }

    @Test
    fun testStudentFeePaymentAndBalanceCalculation() {
        val studentId = "usr_student_3"

        // Check initial pending balance
        val initialBalance = SchoolRepository.getStudentFeeBalance(studentId)
        assertTrue(initialBalance > 0.0)

        // Record a new payment
        SchoolRepository.recordFeePayment(
            studentId = studentId,
            studentName = "Mohamed Omar",
            feeType = FeeType.TUITION,
            amount = 150.0,
            status = PaymentStatus.PAID,
            dueDate = "2024-09-05",
            paymentDate = "2024-10-25",
        )

        val totalRevenue = SchoolRepository.getTotalFeeRevenueCollected()
        assertTrue(totalRevenue >= 150.0)
    }

    @Test
    fun testAttendanceMarkingAndPercentageComputation() {
        val studentId = "usr_student_test_att"
        val studentName = "Test Student Attendance"

        SchoolRepository.addAttendanceRecord(studentId, studentName, "crs_101", "Quran", AttendanceStatus.PRESENT, "Punctual")
        SchoolRepository.addAttendanceRecord(studentId, studentName, "crs_102", "Tajweed", AttendanceStatus.PRESENT, "Punctual")
        SchoolRepository.addAttendanceRecord(studentId, studentName, "crs_201", "Arabic", AttendanceStatus.EXCUSED, "Sick")
        SchoolRepository.addAttendanceRecord(studentId, studentName, "crs_103", "History", AttendanceStatus.ABSENT, "Unexcused")

        val pct = SchoolRepository.getStudentAttendancePercentage(studentId)
        // 3 attended (2 present + 1 excused) out of 4 total = 75.0%
        assertEquals(75.0, pct, 0.01)
    }

    @Test
    fun testStaffAttendancePercentageComputation() {
        val staffId = "usr_staff_test_att"
        val staffName = "Test Staff Member"

        SchoolRepository.addStaffAttendanceRecord(staffId, staffName, "2024-10-21", AttendanceStatus.PRESENT, "08:00 AM", "04:00 PM", "On time")
        SchoolRepository.addStaffAttendanceRecord(staffId, staffName, "2024-10-22", AttendanceStatus.PRESENT, "08:00 AM", "04:00 PM", "On time")

        val pct = SchoolRepository.getStaffAttendancePercentage(staffId)
        assertEquals(100.0, pct, 0.01)
    }

    @Test
    fun testCrossRoleChatMessagingAndAdminBroadcast() {
        // Test direct message
        SchoolRepository.sendChatMessage(
            receiverId = "usr_teacher_1",
            receiverName = "Ustadh Abdurrahman",
            receiverRole = UserRole.TEACHER,
            text = "Assalamu Alaikum Ustadh",
        )

        val messages = SchoolRepository.chatMessages.value
        val directMsg = messages.lastOrNull { it.text == "Assalamu Alaikum Ustadh" }
        assertNotNull(directMsg)
        assertEquals("usr_teacher_1", directMsg!!.receiverId)

        // Test Admin broadcast announcement
        SchoolRepository.broadcastAnnouncement(
            title = "Holiday Notice",
            text = "Academy will remain closed on Friday for Eid celebrations.",
        )

        val broadcastMsgs = SchoolRepository.chatMessages.value
        val broadcast = broadcastMsgs.firstOrNull { it.isBroadcast && it.text.contains("Holiday Notice") }
        assertNotNull(broadcast)
        assertTrue(broadcast!!.isBroadcast)
        assertEquals("all", broadcast.receiverId)
    }

    @Test
    fun testFinancialAndAttendanceCsvGenerators() {
        val payments = SchoolRepository.paymentRecords.value
        val salaries = SchoolRepository.salaryRecords.value
        val studentAtt = SchoolRepository.attendanceRecords.value
        val staffAtt = SchoolRepository.staffAttendanceRecords.value

        val finCsv = CsvExportHelper.generateFinancialCsvContent(payments, salaries)
        assertTrue(finCsv.contains("MADRASAA IFAA ISLAMA - FINANCIAL & PAYROLL STATEMENT"))
        assertTrue(finCsv.contains("STAFF SALARY PAYROLL HISTORY"))
        assertTrue(finCsv.contains("STUDENT FEE TRANSACTIONS"))

        val attCsv = CsvExportHelper.generateAttendanceCsvContent(studentAtt, staffAtt)
        assertTrue(attCsv.contains("MADRASAA IFAA ISLAMA - ATTENDANCE REGISTERS REPORT"))
        assertTrue(attCsv.contains("STUDENT CLASS ATTENDANCE"))
        assertTrue(attCsv.contains("STAFF DAILY ATTENDANCE"))
    }
}
