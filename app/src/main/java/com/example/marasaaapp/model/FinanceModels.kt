package com.example.marasaaapp.model

enum class PaymentStatus(val label: String) {
    PAID("Paid"),
    PENDING("Pending"),
    OVERDUE("Overdue")
}

enum class FeeType(val label: String) {
    TUITION("Tuition Fee"),
    BOOKS("Books & Materials"),
    REGISTRATION("Registration Fee"),
    TRANSPORT("Transport Fee")
}

data class PaymentRecord(
    val id: String,
    val studentId: String,
    val studentName: String,
    val feeType: FeeType,
    val amount: Double,
    val dueDate: String,
    val paymentDate: String?,
    val status: PaymentStatus,
    val transactionRef: String? = null
)

data class SalaryRecord(
    val id: String,
    val staffId: String,
    val staffName: String,
    val roleTitle: String,
    val monthYear: String,
    val baseSalary: Double,
    val bonus: Double,
    val deductions: Double,
    val netSalary: Double = baseSalary + bonus - deductions,
    val paymentDate: String?,
    val status: PaymentStatus
)

data class MonthlyFinanceStat(
    val month: String,
    val revenue: Double,
    val expense: Double
)

