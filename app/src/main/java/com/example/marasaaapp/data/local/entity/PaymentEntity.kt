package com.example.marasaaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marasaaapp.model.FeeType
import com.example.marasaaapp.model.PaymentRecord
import com.example.marasaaapp.model.PaymentStatus

@Entity(tableName = "payment_records")
data class PaymentEntity(
    @PrimaryKey val paymentId: String,
    val studentId: String,
    val amount: Double,
    val feeType: FeeType,
    val status: PaymentStatus,
    val date: String,
    val studentName: String = "",
    val dueDate: String = "2024-11-01",
    val paymentDate: String? = null,
    val transactionRef: String? = null
) {
    fun toDomainModel(): PaymentRecord {
        return PaymentRecord(
            id = paymentId,
            studentId = studentId,
            studentName = studentName,
            feeType = feeType,
            amount = amount,
            dueDate = dueDate,
            paymentDate = paymentDate ?: date,
            status = status,
            transactionRef = transactionRef
        )
    }

    companion object {
        fun fromDomainModel(record: PaymentRecord): PaymentEntity {
            return PaymentEntity(
                paymentId = record.id,
                studentId = record.studentId,
                amount = record.amount,
                feeType = record.feeType,
                status = record.status,
                date = record.paymentDate ?: record.dueDate,
                studentName = record.studentName,
                dueDate = record.dueDate,
                paymentDate = record.paymentDate,
                transactionRef = record.transactionRef
            )
        }
    }
}
