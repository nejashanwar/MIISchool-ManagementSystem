package com.example.marasaaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.marasaaapp.model.ChatMessage
import com.example.marasaaapp.model.DeliveryStatus
import com.example.marasaaapp.model.UserRole

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val messageId: String,
    val senderId: String,
    val receiverId: String,
    val text: String,
    val timestamp: String,
    val deliveryStatus: DeliveryStatus,
    val senderName: String = "",
    val senderRole: UserRole = UserRole.STUDENT,
    val receiverName: String = "",
    val receiverRole: UserRole = UserRole.TEACHER,
    val isRead: Boolean = false,
    val isBroadcast: Boolean = false
) {
    fun toDomainModel(): ChatMessage {
        return ChatMessage(
            id = messageId,
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            receiverId = receiverId,
            receiverName = receiverName,
            receiverRole = receiverRole,
            text = text,
            timestamp = timestamp,
            isRead = isRead,
            deliveryStatus = deliveryStatus,
            isBroadcast = isBroadcast
        )
    }

    companion object {
        fun fromDomainModel(msg: ChatMessage): ChatMessageEntity {
            return ChatMessageEntity(
                messageId = msg.id,
                senderId = msg.senderId,
                receiverId = msg.receiverId,
                text = msg.text,
                timestamp = msg.timestamp,
                deliveryStatus = msg.deliveryStatus,
                senderName = msg.senderName,
                senderRole = msg.senderRole,
                receiverName = msg.receiverName,
                receiverRole = msg.receiverRole,
                isRead = msg.isRead,
                isBroadcast = msg.isBroadcast
            )
        }
    }
}
