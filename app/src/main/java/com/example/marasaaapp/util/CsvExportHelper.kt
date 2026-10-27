package com.example.marasaaapp.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.marasaaapp.model.AttendanceRecord
import com.example.marasaaapp.model.PaymentRecord
import com.example.marasaaapp.model.PaymentStatus
import com.example.marasaaapp.model.ReportCard
import com.example.marasaaapp.model.SalaryRecord
import com.example.marasaaapp.model.StaffAttendanceRecord
import java.io.File
import java.io.FileWriter
import java.util.Locale

object CsvExportHelper {

    /**
     * Generates CSV formatted text for a given [ReportCard].
     */
    fun generateCsvContent(reportCard: ReportCard): String {
        val sb = StringBuilder()
        sb.append("MADRASAA IFAA ISLAMA - STUDENT ACADEMIC REPORT CARD\n")
        sb.append("Student Name,${escapeCsv(reportCard.studentName)}\n")
        sb.append("Student ID,${escapeCsv(reportCard.studentId)}\n")
        sb.append("Grade Level,${escapeCsv(reportCard.gradeLevel)}\n")
        sb.append("Semester,${escapeCsv(reportCard.semesterName)}\n")
        sb.append("Issue Date,${escapeCsv(reportCard.issueDate)}\n")
        sb.append("Cumulative GPA,${String.format(Locale.US, "%.2f", reportCard.gpa)}\n")
        sb.append("Average Percentage,${String.format(Locale.US, "%.1f%%", reportCard.averageScore)}\n")
        sb.append("Overall Status,${if (reportCard.isPassed) "PASSED" else "FAILED"}\n\n")

        sb.append("Course Code,Course Title,Assignments (20),Midterm (30),Final Exam (50),Total Marks (100),Letter Grade,Grade Point,Status\n")

        for (res in reportCard.results) {
            val score = res.score
            sb.append("${escapeCsv(res.courseCode)},")
            sb.append("${escapeCsv(res.courseTitle)},")
            sb.append("${String.format(Locale.US, "%.1f", score.assignmentsMarks)},")
            sb.append("${String.format(Locale.US, "%.1f", score.midtermMarks)},")
            sb.append("${String.format(Locale.US, "%.1f", score.finalExamMarks)},")
            sb.append("${String.format(Locale.US, "%.1f", score.totalMarks)},")
            sb.append("${score.grade},")
            sb.append("${String.format(Locale.US, "%.2f", score.gradePoint)},")
            sb.append("${if (score.isPass) "PASS" else "FAIL"}\n")
        }

        if (reportCard.teacherComments.isNotEmpty()) {
            sb.append("\nTeacher Comments,${escapeCsv(reportCard.teacherComments)}\n")
        }

        return sb.toString()
    }

    /**
     * Export CSV content to a file in cache and launch an Intent chooser to share/export it.
     */
    fun exportAndShareCsv(context: Context, reportCard: ReportCard): File? {
        val csvContent = generateCsvContent(reportCard)
        val fileName = "ResultCard_${reportCard.studentName.replace("\\s+".toRegex(), "_")}_${reportCard.id}.csv"
        val file = File(context.cacheDir, fileName)

        try {
            FileWriter(file).use { writer ->
                writer.write(csvContent)
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "Result Card - ${reportCard.studentName}")
                putExtra(Intent.EXTRA_TEXT, "Attached is the academic result card for ${reportCard.studentName} (${reportCard.semesterName}).")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Export / Share Result Card CSV")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Generates CSV formatted text for Financial & Payroll Statement.
     */
    fun generateFinancialCsvContent(payments: List<PaymentRecord>, salaries: List<SalaryRecord>): String {
        val sb = StringBuilder()
        sb.append("MADRASAA IFAA ISLAMA - FINANCIAL & PAYROLL STATEMENT\n")
        val totalRev = payments.filter { it.status == PaymentStatus.PAID }.sumOf { it.amount }
        val totalPending = payments.filter { it.status != PaymentStatus.PAID }.sumOf { it.amount }
        val totalSal = salaries.filter { it.status == PaymentStatus.PAID }.sumOf { it.netSalary }

        sb.append("Total Revenue Collected,${String.format(Locale.US, "$%.2f", totalRev)}\n")
        sb.append("Total Pending Fees,${String.format(Locale.US, "$%.2f", totalPending)}\n")
        sb.append("Total Salaries Paid Out,${String.format(Locale.US, "$%.2f", totalSal)}\n\n")

        sb.append("STAFF SALARY PAYROLL HISTORY\n")
        sb.append("Staff Name,Role Title,Month/Year,Base Salary,Bonus,Deductions,Net Salary,Payment Date,Status\n")

        for (sal in salaries) {
            sb.append("${escapeCsv(sal.staffName)},")
            sb.append("${escapeCsv(sal.roleTitle)},")
            sb.append("${escapeCsv(sal.monthYear)},")
            sb.append("${String.format(Locale.US, "%.2f", sal.baseSalary)},")
            sb.append("${String.format(Locale.US, "%.2f", sal.bonus)},")
            sb.append("${String.format(Locale.US, "%.2f", sal.deductions)},")
            sb.append("${String.format(Locale.US, "%.2f", sal.netSalary)},")
            sb.append("${sal.paymentDate ?: "N/A"},")
            sb.append("${sal.status.label}\n")
        }

        sb.append("\nSTUDENT FEE TRANSACTIONS\n")
        sb.append("Student Name,Fee Type,Amount,Due Date,Payment Date,Status,Transaction Ref\n")

        for (pay in payments) {
            sb.append("${escapeCsv(pay.studentName)},")
            sb.append("${escapeCsv(pay.feeType.label)},")
            sb.append("${String.format(Locale.US, "%.2f", pay.amount)},")
            sb.append("${pay.dueDate},")
            sb.append("${pay.paymentDate ?: "N/A"},")
            sb.append("${pay.status.label},")
            sb.append("${pay.transactionRef ?: "N/A"}\n")
        }

        return sb.toString()
    }

    /**
     * Export Financial CSV to cache and share via Intent.
     */
    fun exportFinancialCsv(context: Context, payments: List<PaymentRecord>, salaries: List<SalaryRecord>): File? {
        val content = generateFinancialCsvContent(payments, salaries)
        val fileName = "Financial_Statement_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)

        try {
            FileWriter(file).use { writer -> writer.write(content) }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "Financial Statement - Madrasaa Ifaa Islama")
                putExtra(Intent.EXTRA_TEXT, "Attached is the financial and payroll report CSV.")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "Export Financial Report CSV")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Generates CSV formatted text for Attendance Registers.
     */
    fun generateAttendanceCsvContent(
        studentAttendance: List<AttendanceRecord>,
        staffAttendance: List<StaffAttendanceRecord>
    ): String {
        val sb = StringBuilder()
        sb.append("MADRASAA IFAA ISLAMA - ATTENDANCE REGISTERS REPORT\n\n")

        sb.append("STUDENT CLASS ATTENDANCE\n")
        sb.append("Student Name,Course/Class,Date,Status,Notes\n")
        for (att in studentAttendance) {
            sb.append("${escapeCsv(att.studentName)},")
            sb.append("${escapeCsv(att.courseName)},")
            sb.append("${att.date},")
            sb.append("${att.status.label},")
            sb.append("${escapeCsv(att.notes)}\n")
        }

        sb.append("\nSTAFF DAILY ATTENDANCE\n")
        sb.append("Staff Name,Date,Check-In,Check-Out,Status,Notes\n")
        for (satt in staffAttendance) {
            sb.append("${escapeCsv(satt.staffName)},")
            sb.append("${satt.date},")
            sb.append("${satt.checkInTime},")
            sb.append("${satt.checkOutTime},")
            sb.append("${satt.status.label},")
            sb.append("${escapeCsv(satt.notes)}\n")
        }

        return sb.toString()
    }

    /**
     * Export Attendance CSV to cache and share via Intent.
     */
    fun exportAttendanceCsv(
        context: Context,
        studentAttendance: List<AttendanceRecord>,
        staffAttendance: List<StaffAttendanceRecord>
    ): File? {
        val content = generateAttendanceCsvContent(studentAttendance, staffAttendance)
        val fileName = "Attendance_Register_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)

        try {
            FileWriter(file).use { writer -> writer.write(content) }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "Attendance Register Report - Madrasaa Ifaa Islama")
                putExtra(Intent.EXTRA_TEXT, "Attached is the student & staff attendance register CSV.")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "Export Attendance CSV")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}
