package com.example.marasaaapp.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import com.example.marasaaapp.model.AttendanceRecord
import com.example.marasaaapp.model.PaymentRecord
import com.example.marasaaapp.model.PaymentStatus
import com.example.marasaaapp.model.ReportCard
import com.example.marasaaapp.model.SalaryRecord
import com.example.marasaaapp.model.StaffAttendanceRecord
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PdfReportGenerator {

    /**
     * Generates a PDF file for a given [ReportCard] and saves it to the cache directory.
     */
    fun generatePdfFile(context: Context, reportCard: ReportCard): File {
        val pdfDocument = createPdfDocument(reportCard)
        val file = File(context.cacheDir, "ResultCard_${reportCard.studentName.replace("\\s+".toRegex(), "_")}_${reportCard.id}.pdf")
        try {
            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
        return file
    }

    /**
     * Triggers the Android PrintManager to print or save the result card as PDF.
     */
    fun printReportCard(context: Context, reportCard: ReportCard) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "Madrasaa_ResultCard_${reportCard.studentName}"

        val printAdapter = object : PrintDocumentAdapter() {
            private var pdfFile: File? = null

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }

                pdfFile = generatePdfFile(context, reportCard)

                val info = PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()

                callback?.onLayoutFinished(info, oldAttributes != newAttributes)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                var input: java.io.FileInputStream? = null
                var output: FileOutputStream? = null

                try {
                    val file = pdfFile ?: generatePdfFile(context, reportCard)
                    input = java.io.FileInputStream(file)
                    output = FileOutputStream(destination?.fileDescriptor)

                    val buf = ByteArray(1024)
                    var bytesRead: Int
                    while (input.read(buf).also { bytesRead = it } > 0) {
                        output.write(buf, 0, bytesRead)
                    }

                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                } finally {
                    try {
                        input?.close()
                        output?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        printManager.print(jobName, printAdapter, printAttributes)
    }

    /**
     * Generates a PDF file for Financial Statement and Payroll.
     */
    fun generateFinancialReportPdf(context: Context, payments: List<PaymentRecord>, salaries: List<SalaryRecord>): File {
        val pdfDocument = createFinancialPdfDocument(payments, salaries)
        val file = File(context.cacheDir, "Financial_Report_${System.currentTimeMillis()}.pdf")
        try {
            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
        return file
    }

    /**
     * Triggers printing of the Financial Report.
     */
    fun printFinancialReport(context: Context, payments: List<PaymentRecord>, salaries: List<SalaryRecord>) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "Madrasaa_Financial_Report"

        val printAdapter = object : PrintDocumentAdapter() {
            private var pdfFile: File? = null

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }

                pdfFile = generateFinancialReportPdf(context, payments, salaries)
                val info = PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()

                callback?.onLayoutFinished(info, oldAttributes != newAttributes)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                var input: java.io.FileInputStream? = null
                var output: FileOutputStream? = null

                try {
                    val file = pdfFile ?: generateFinancialReportPdf(context, payments, salaries)
                    input = java.io.FileInputStream(file)
                    output = FileOutputStream(destination?.fileDescriptor)

                    val buf = ByteArray(1024)
                    var bytesRead: Int
                    while (input.read(buf).also { bytesRead = it } > 0) {
                        output.write(buf, 0, bytesRead)
                    }

                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                } finally {
                    try {
                        input?.close()
                        output?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        printManager.print(jobName, printAdapter, printAttributes)
    }

    /**
     * Generates a PDF file for Attendance Registers.
     */
    fun generateAttendanceReportPdf(
        context: Context,
        studentAttendance: List<AttendanceRecord>,
        staffAttendance: List<StaffAttendanceRecord>
    ): File {
        val pdfDocument = createAttendancePdfDocument(studentAttendance, staffAttendance)
        val file = File(context.cacheDir, "Attendance_Report_${System.currentTimeMillis()}.pdf")
        try {
            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
        return file
    }

    /**
     * Triggers printing of the Attendance Report.
     */
    fun printAttendanceReport(
        context: Context,
        studentAttendance: List<AttendanceRecord>,
        staffAttendance: List<StaffAttendanceRecord>
    ) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "Madrasaa_Attendance_Report"

        val printAdapter = object : PrintDocumentAdapter() {
            private var pdfFile: File? = null

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }

                pdfFile = generateAttendanceReportPdf(context, studentAttendance, staffAttendance)
                val info = PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()

                callback?.onLayoutFinished(info, oldAttributes != newAttributes)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                var input: java.io.FileInputStream? = null
                var output: FileOutputStream? = null

                try {
                    val file = pdfFile ?: generateAttendanceReportPdf(context, studentAttendance, staffAttendance)
                    input = java.io.FileInputStream(file)
                    output = FileOutputStream(destination?.fileDescriptor)

                    val buf = ByteArray(1024)
                    var bytesRead: Int
                    while (input.read(buf).also { bytesRead = it } > 0) {
                        output.write(buf, 0, bytesRead)
                    }

                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                } finally {
                    try {
                        input?.close()
                        output?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        printManager.print(jobName, printAdapter, printAttributes)
    }

    private fun createPdfDocument(reportCard: ReportCard): PdfDocument {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        // Background
        paint.color = Color.rgb(250, 250, 252)
        canvas.drawRect(0f, 0f, 595f, 842f, paint)

        // Header Background Banner
        paint.color = Color.rgb(27, 94, 32)
        canvas.drawRect(0f, 0f, 595f, 100f, paint)

        // Header Title
        paint.color = Color.WHITE
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("MADRASAA IFAA ISLAMA", 30f, 45f, paint)

        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("School Management System • Academic Result Card", 30f, 68f, paint)
        canvas.drawText("Official Transcript", 440f, 45f, paint)

        // Gold Accent Line
        paint.color = Color.rgb(212, 175, 55)
        canvas.drawRect(0f, 100f, 595f, 105f, paint)

        // Student Info Section
        paint.color = Color.rgb(240, 242, 245)
        canvas.drawRoundRect(30f, 120f, 565f, 200f, 12f, 12f, paint)

        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Student Name: ${reportCard.studentName}", 45f, 145f, paint)
        canvas.drawText("Semester: ${reportCard.semesterName}", 320f, 145f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Grade Level: ${reportCard.gradeLevel}", 45f, 170f, paint)
        canvas.drawText("Issue Date: ${reportCard.issueDate}", 320f, 170f, paint)
        canvas.drawText("Student ID: ${reportCard.studentId}", 45f, 190f, paint)

        // Subject Grade Table Header
        val tableTop = 225f
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawRect(30f, tableTop, 565f, tableTop + 30f, paint)

        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("CODE", 40f, tableTop + 20f, paint)
        canvas.drawText("SUBJECT TITLE", 110f, tableTop + 20f, paint)
        canvas.drawText("ASSIGN (20)", 280f, tableTop + 20f, paint)
        canvas.drawText("MID (30)", 350f, tableTop + 20f, paint)
        canvas.drawText("FINAL (50)", 410f, tableTop + 20f, paint)
        canvas.drawText("TOTAL", 475f, tableTop + 20f, paint)
        canvas.drawText("GRADE", 525f, tableTop + 20f, paint)

        // Table Rows
        var currentY = tableTop + 30f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        reportCard.results.forEachIndexed { index, res ->
            val rowHeight = 28f
            if (index % 2 == 1) {
                paint.color = Color.rgb(248, 250, 252)
                canvas.drawRect(30f, currentY, 565f, currentY + rowHeight, paint)
            }

            paint.color = Color.rgb(30, 41, 59)
            paint.textSize = 9.5f
            canvas.drawText(res.courseCode, 40f, currentY + 18f, paint)

            val title = if (res.courseTitle.length > 25) res.courseTitle.take(23) + "..." else res.courseTitle
            canvas.drawText(title, 110f, currentY + 18f, paint)

            canvas.drawText(String.format(java.util.Locale.US, "%.1f", res.score.assignmentsMarks), 290f, currentY + 18f, paint)
            canvas.drawText(String.format(java.util.Locale.US, "%.1f", res.score.midtermMarks), 355f, currentY + 18f, paint)
            canvas.drawText(String.format(java.util.Locale.US, "%.1f", res.score.finalExamMarks), 415f, currentY + 18f, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(String.format(java.util.Locale.US, "%.1f", res.score.totalMarks), 475f, currentY + 18f, paint)

            paint.color = if (res.score.isPass) Color.rgb(22, 101, 52) else Color.rgb(185, 28, 28)
            canvas.drawText("${res.score.grade} (${if (res.score.isPass) "P" else "F"})", 525f, currentY + 18f, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            currentY += rowHeight
        }

        // Table Border Line
        paint.color = Color.rgb(203, 213, 225)
        paint.strokeWidth = 1f
        paint.style = Paint.Style.STROKE
        canvas.drawRect(30f, tableTop, 565f, currentY, paint)
        paint.style = Paint.Style.FILL

        // Summary Card Section
        val summaryTop = currentY + 20f
        paint.color = Color.rgb(238, 242, 255)
        canvas.drawRoundRect(30f, summaryTop, 565f, summaryTop + 85f, 12f, 12f, paint)

        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("ACADEMIC PERFORMANCE SUMMARY", 45f, summaryTop + 25f, paint)

        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Total Marks: ${String.format(java.util.Locale.US, "%.1f", reportCard.overallTotal)}", 45f, summaryTop + 50f, paint)
        canvas.drawText("Average Percentage: ${String.format(java.util.Locale.US, "%.1f%%", reportCard.averageScore)}", 210f, summaryTop + 50f, paint)
        canvas.drawText("Cumulative GPA: ${String.format(java.util.Locale.US, "%.2f / 4.00", reportCard.gpa)}", 400f, summaryTop + 50f, paint)

        // Overall Pass/Fail Badge
        paint.color = if (reportCard.isPassed) Color.rgb(16, 185, 129) else Color.rgb(239, 68, 68)
        canvas.drawRoundRect(400f, summaryTop + 15f, 545f, summaryTop + 38f, 6f, 6f, paint)

        paint.color = Color.WHITE
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(if (reportCard.isPassed) "STATUS: PASSED" else "STATUS: FAILED", 415f, summaryTop + 30f, paint)

        // Teacher Comments
        val commentTop = summaryTop + 105f
        if (reportCard.teacherComments.isNotEmpty()) {
            paint.color = Color.rgb(241, 245, 249)
            canvas.drawRoundRect(30f, commentTop, 565f, commentTop + 50f, 8f, 8f, paint)

            paint.color = Color.rgb(15, 23, 42)
            paint.textSize = 10.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Ustadh Remarks:", 45f, commentTop + 20f, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            canvas.drawText("\"${reportCard.teacherComments}\"", 45f, commentTop + 38f, paint)
        }

        // Footer Signatures
        val footerY = 770f
        paint.color = Color.rgb(148, 163, 184)
        canvas.drawLine(30f, footerY - 40f, 565f, footerY - 40f, paint)

        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Sheikh Ibrahim Al-Hassan", 50f, footerY, paint)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Academy Principal & Director", 50f, footerY + 15f, paint)
        canvas.drawText("___________________________", 50f, footerY - 10f, paint)

        canvas.drawText("Official Stamp / Seal", 380f, footerY, paint)
        canvas.drawText("Madrasaa Ifaa Islama Board", 380f, footerY + 15f, paint)
        canvas.drawText("___________________________", 380f, footerY - 10f, paint)

        pdfDocument.finishPage(page)
        return pdfDocument
    }

    private fun createFinancialPdfDocument(payments: List<PaymentRecord>, salaries: List<SalaryRecord>): PdfDocument {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        // Background
        paint.color = Color.rgb(250, 250, 252)
        canvas.drawRect(0f, 0f, 595f, 842f, paint)

        // Banner
        paint.color = Color.rgb(27, 94, 32)
        canvas.drawRect(0f, 0f, 595f, 100f, paint)

        paint.color = Color.WHITE
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("MADRASAA IFAA ISLAMA", 30f, 45f, paint)

        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Financial Statement & Staff Payroll Statement", 30f, 68f, paint)

        // Gold Line
        paint.color = Color.rgb(212, 175, 55)
        canvas.drawRect(0f, 100f, 595f, 105f, paint)

        // Totals Card
        val totalRevenue = payments.filter { it.status == PaymentStatus.PAID }.sumOf { it.amount }
        val totalPending = payments.filter { it.status != PaymentStatus.PAID }.sumOf { it.amount }
        val totalSalaries = salaries.filter { it.status == PaymentStatus.PAID }.sumOf { it.netSalary }

        paint.color = Color.rgb(240, 242, 245)
        canvas.drawRoundRect(30f, 120f, 565f, 190f, 12f, 12f, paint)

        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("FINANCIAL OVERVIEW SUMMARY", 45f, 142f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Collected Revenue: $${totalRevenue.toInt()}", 45f, 168f, paint)
        canvas.drawText("Pending Fees: $${totalPending.toInt()}", 220f, 168f, paint)
        canvas.drawText("Salaries Paid Out: $${totalSalaries.toInt()}", 390f, 168f, paint)

        // Section 1: Staff Salary Payroll Table
        var y = 215f
        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Staff Payroll History", 30f, y, paint)

        y += 12f
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawRect(30f, y, 565f, y + 24f, paint)

        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("STAFF NAME", 40f, y + 16f, paint)
        canvas.drawText("ROLE", 180f, y + 16f, paint)
        canvas.drawText("MONTH", 310f, y + 16f, paint)
        canvas.drawText("BASE", 380f, y + 16f, paint)
        canvas.drawText("NET SALARY", 440f, y + 16f, paint)
        canvas.drawText("STATUS", 515f, y + 16f, paint)

        y += 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        salaries.forEach { sal ->
            paint.color = Color.rgb(30, 41, 59)
            canvas.drawText(if (sal.staffName.length > 20) sal.staffName.take(18) + "..." else sal.staffName, 40f, y + 16f, paint)
            canvas.drawText(if (sal.roleTitle.length > 20) sal.roleTitle.take(18) + "..." else sal.roleTitle, 180f, y + 16f, paint)
            canvas.drawText(sal.monthYear, 310f, y + 16f, paint)
            canvas.drawText("$${sal.baseSalary.toInt()}", 380f, y + 16f, paint)
            canvas.drawText("$${sal.netSalary.toInt()}", 440f, y + 16f, paint)
            canvas.drawText(sal.status.label, 515f, y + 16f, paint)
            y += 22f
        }

        // Section 2: Student Fee Payments Table
        y += 20f
        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Student Fee Receipts & Transactions", 30f, y, paint)

        y += 12f
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawRect(30f, y, 565f, y + 24f, paint)

        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("STUDENT NAME", 40f, y + 16f, paint)
        canvas.drawText("FEE TYPE", 180f, y + 16f, paint)
        canvas.drawText("AMOUNT", 310f, y + 16f, paint)
        canvas.drawText("DUE DATE", 380f, y + 16f, paint)
        canvas.drawText("REF / TRANS", 450f, y + 16f, paint)
        canvas.drawText("STATUS", 520f, y + 16f, paint)

        y += 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        payments.forEach { pay ->
            paint.color = Color.rgb(30, 41, 59)
            canvas.drawText(pay.studentName, 40f, y + 16f, paint)
            canvas.drawText(pay.feeType.label, 180f, y + 16f, paint)
            canvas.drawText("$${pay.amount.toInt()}", 310f, y + 16f, paint)
            canvas.drawText(pay.dueDate, 380f, y + 16f, paint)
            canvas.drawText(pay.transactionRef ?: "N/A", 450f, y + 16f, paint)
            canvas.drawText(pay.status.label, 520f, y + 16f, paint)
            y += 22f
        }

        // Footer
        val footerY = 770f
        paint.color = Color.rgb(148, 163, 184)
        canvas.drawLine(30f, footerY - 20f, 565f, footerY - 20f, paint)

        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Bursar / Financial Officer", 50f, footerY + 15f, paint)
        canvas.drawText("Executive Principal Approval", 380f, footerY + 15f, paint)

        pdfDocument.finishPage(page)
        return pdfDocument
    }

    private fun createAttendancePdfDocument(
        studentAttendance: List<AttendanceRecord>,
        staffAttendance: List<StaffAttendanceRecord>
    ): PdfDocument {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        // Background
        paint.color = Color.rgb(250, 250, 252)
        canvas.drawRect(0f, 0f, 595f, 842f, paint)

        // Banner
        paint.color = Color.rgb(27, 94, 32)
        canvas.drawRect(0f, 0f, 595f, 100f, paint)

        paint.color = Color.WHITE
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("MADRASAA IFAA ISLAMA", 30f, 45f, paint)

        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Official Student & Staff Attendance Register", 30f, 68f, paint)

        // Gold Line
        paint.color = Color.rgb(212, 175, 55)
        canvas.drawRect(0f, 100f, 595f, 105f, paint)

        // Section 1: Student Attendance Table
        var y = 130f
        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Student Class Attendance Register", 30f, y, paint)

        y += 12f
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawRect(30f, y, 565f, y + 24f, paint)

        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("STUDENT NAME", 40f, y + 16f, paint)
        canvas.drawText("COURSE / CLASS", 180f, y + 16f, paint)
        canvas.drawText("DATE", 350f, y + 16f, paint)
        canvas.drawText("STATUS", 440f, y + 16f, paint)
        canvas.drawText("REMARKS", 500f, y + 16f, paint)

        y += 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        studentAttendance.forEach { att ->
            paint.color = Color.rgb(30, 41, 59)
            canvas.drawText(att.studentName, 40f, y + 16f, paint)
            canvas.drawText(if (att.courseName.length > 22) att.courseName.take(20) + "..." else att.courseName, 180f, y + 16f, paint)
            canvas.drawText(att.date, 350f, y + 16f, paint)
            canvas.drawText(att.status.label, 440f, y + 16f, paint)
            canvas.drawText(if (att.notes.length > 12) att.notes.take(10) + "..." else att.notes, 500f, y + 16f, paint)
            y += 22f
        }

        // Section 2: Staff Attendance Table
        y += 25f
        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Staff & Faculty Daily Attendance Register", 30f, y, paint)

        y += 12f
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawRect(30f, y, 565f, y + 24f, paint)

        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("STAFF NAME", 40f, y + 16f, paint)
        canvas.drawText("DATE", 220f, y + 16f, paint)
        canvas.drawText("CHECK-IN / OUT", 310f, y + 16f, paint)
        canvas.drawText("STATUS", 440f, y + 16f, paint)
        canvas.drawText("REMARKS", 500f, y + 16f, paint)

        y += 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        staffAttendance.forEach { satt ->
            paint.color = Color.rgb(30, 41, 59)
            canvas.drawText(satt.staffName, 40f, y + 16f, paint)
            canvas.drawText(satt.date, 220f, y + 16f, paint)
            canvas.drawText("${satt.checkInTime} - ${satt.checkOutTime}", 310f, y + 16f, paint)
            canvas.drawText(satt.status.label, 440f, y + 16f, paint)
            canvas.drawText(if (satt.notes.length > 12) satt.notes.take(10) + "..." else satt.notes, 500f, y + 16f, paint)
            y += 22f
        }

        // Footer
        val footerY = 770f
        paint.color = Color.rgb(148, 163, 184)
        canvas.drawLine(30f, footerY - 20f, 565f, footerY - 20f, paint)

        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Academic Registrar", 50f, footerY + 15f, paint)
        canvas.drawText("Academy Principal Signature", 380f, footerY + 15f, paint)

        pdfDocument.finishPage(page)
        return pdfDocument
    }
}
