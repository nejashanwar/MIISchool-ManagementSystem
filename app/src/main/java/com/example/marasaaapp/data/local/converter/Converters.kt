package com.example.marasaaapp.data.local.converter

import androidx.room.TypeConverter
import com.example.marasaaapp.model.*

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name

    @TypeConverter
    fun toUserRole(value: String): UserRole {
        return try {
            UserRole.valueOf(value)
        } catch (e: Exception) {
            UserRole.STUDENT
        }
    }

    @TypeConverter
    fun fromAttendanceStatus(status: AttendanceStatus): String = status.name

    @TypeConverter
    fun toAttendanceStatus(value: String): AttendanceStatus {
        return try {
            AttendanceStatus.valueOf(value)
        } catch (e: Exception) {
            AttendanceStatus.PRESENT
        }
    }

    @TypeConverter
    fun fromFeeType(feeType: FeeType): String = feeType.name

    @TypeConverter
    fun toFeeType(value: String): FeeType {
        return try {
            FeeType.valueOf(value)
        } catch (e: Exception) {
            FeeType.TUITION
        }
    }

    @TypeConverter
    fun fromPaymentStatus(status: PaymentStatus): String = status.name

    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus {
        return try {
            PaymentStatus.valueOf(value)
        } catch (e: Exception) {
            PaymentStatus.PENDING
        }
    }

    @TypeConverter
    fun fromDeliveryStatus(status: DeliveryStatus): String = status.name

    @TypeConverter
    fun toDeliveryStatus(value: String): DeliveryStatus {
        return try {
            DeliveryStatus.valueOf(value)
        } catch (e: Exception) {
            DeliveryStatus.SENT
        }
    }
}
