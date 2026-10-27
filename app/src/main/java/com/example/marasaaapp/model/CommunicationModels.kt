package com.example.marasaaapp.model

enum class DeliveryStatus(val label: String, val icon: String) {
    SENT("Sent", "✓"),
    DELIVERED("Delivered", "✓✓"),
    SEEN("Seen", "✓✓")
}

data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val senderRole: UserRole,
    val receiverId: String,
    val receiverName: String,
    val receiverRole: UserRole,
    val text: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val deliveryStatus: DeliveryStatus = DeliveryStatus.SENT,
    val isBroadcast: Boolean = false
)
