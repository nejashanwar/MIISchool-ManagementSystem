package com.example.marasaaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marasaaapp.model.User
import com.example.marasaaapp.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val phone: String,
    val password: String = "password123",
    val avatarUrl: String? = null,
    val isOnline: Boolean = true
) {
    fun toDomainModel(): User {
        return User(
            id = userId,
            name = name,
            email = email,
            role = role,
            avatarUrl = avatarUrl,
            phone = phone,
            isOnline = isOnline
        )
    }

    companion object {
        fun fromDomainModel(user: User, password: String = "password123"): UserEntity {
            return UserEntity(
                userId = user.id,
                name = user.name,
                email = user.email,
                role = user.role,
                phone = user.phone,
                password = password,
                avatarUrl = user.avatarUrl,
                isOnline = user.isOnline
            )
        }
    }
}
