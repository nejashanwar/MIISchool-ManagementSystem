package com.example.marasaaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marasaaapp.model.PaymentStatus
import com.example.marasaaapp.model.SalaryRecord

@Entity(tableName = "salary_records")
data class SalaryEntity(
    @PrimaryKey val salaryId: String,
    val staffId: String,
    val staffName: String,
    val baseSalary: Double,
    val bonus: Double,
    val deductions: Double,
    val netSalary: Double,
    val date: String,
    val roleTitle: String = "",
    val monthYear: String = "November 2024",
    val status: PaymentStatus = PaymentStatus.PAID,
    val paymentDate: String? = null
) {
    fun toDomainModel(): SalaryRecord {
        return SalaryRecord(
            id = salaryId,
            staffId = staffId,
            staffName = staffName,
            roleTitle = roleTitle,
            monthYear = monthYear,
            baseSalary = baseSalary,
            bonus = bonus,
            deductions = deductions,
            netSalary = netSalary,
            paymentDate = paymentDate ?: date,
            status = status
        )
    }

    companion object {
        fun fromDomainModel(record: SalaryRecord): SalaryEntity {
            return SalaryEntity(
                salaryId = record.id,
                staffId = record.staffId,
                staffName = record.staffName,
                baseSalary = record.baseSalary,
                bonus = record.bonus,
                deductions = record.deductions,
                netSalary = record.netSalary,
                date = record.paymentDate ?: "2024-10-25",
                roleTitle = record.roleTitle,
                monthYear = record.monthYear,
                status = record.status,
                paymentDate = record.paymentDate
            )
        }
    }
}
