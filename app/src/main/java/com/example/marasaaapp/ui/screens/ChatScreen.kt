package com.example.marasaaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marasaaapp.model.DeliveryStatus
import com.example.marasaaapp.model.UserRole
import com.example.marasaaapp.repository.SchoolRepository

@Composable
fun ChatScreen(
    repository: SchoolRepository = SchoolRepository
) {
    val activeUser by repository.activeUser.collectAsState()
    val chatMessages by repository.chatMessages.collectAsState()
    val usersList = SchoolRepository.usersList

    // Recipient Selection State
    val defaultTargetUser = usersList.firstOrNull { it.id != activeUser.id } ?: usersList.first()
    var selectedRecipient by remember { mutableStateOf(defaultTargetUser) }
    var selectedRoleFilter by remember { mutableStateOf<UserRole?>(null) }
    var chatViewMode by remember { mutableStateOf(ChatViewMode.DIRECT) }

    var messageText by remember { mutableStateOf("") }

    // Admin Broadcast Dialog State
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastTitleInput by remember { mutableStateOf("") }
    var broadcastBodyInput by remember { mutableStateOf("") }
    var actionNotice by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Telegram-Style Input Bar
            Surface(
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding()
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    // Quick recipient indicator & tip
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (chatViewMode == ChatViewMode.DIRECT) "To: ${selectedRecipient.name} (${selectedRecipient.role.label})" else "Broadcasting to Community Feed",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
//                        Text(
//                            text = "Tap ✓ on message for read status",
//                            style = MaterialTheme.typography.labelSmall,
//                            fontSize = 10.sp,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Input Bar Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Rounded pill container for text field & attachments
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Emoji Icon
                                IconButton(
                                    onClick = { actionNotice = "Emoji picker tapped" },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.InsertEmoticon,
                                        contentDescription = "Emoji",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Text Field
                                TextField(
                                    value = messageText,
                                    onValueChange = { messageText = it },
                                    placeholder = { Text("Message...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    modifier = Modifier.weight(1f),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    singleLine = false,
                                    maxLines = 4
                                )

                                // Attachment Icon
                                IconButton(
                                    onClick = { actionNotice = "Attachment picker opened" },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AttachFile,
                                        contentDescription = "Attach File",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Circular Send / Mic Button (Transforms based on input)
                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    repository.sendChatMessage(
                                        receiverId = selectedRecipient.id,
                                        receiverName = selectedRecipient.name,
                                        receiverRole = selectedRecipient.role,
                                        text = messageText
                                    )
                                    messageText = ""
                                    actionNotice = "Message sent successfully!"
                                } else {
                                    actionNotice = "Voice recording feature tapped"
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                imageVector = if (messageText.isNotBlank()) Icons.AutoMirrored.Filled.Send else Icons.Default.Mic,
                                contentDescription = if (messageText.isNotBlank()) "Send Message" else "Voice Note",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. Telegram-Style Top Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Contact Info & Avatar
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Initials Circle with Accent Color
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                val initials = if (chatViewMode == ChatViewMode.DIRECT) {
                                    selectedRecipient.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                                } else {
                                    "MC"
                                }
                                Text(
                                    text = initials.uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                // Online green indicator dot
                                if (chatViewMode == ChatViewMode.DIRECT && selectedRecipient.isOnline) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                            .align(Alignment.BottomEnd)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (chatViewMode == ChatViewMode.DIRECT) selectedRecipient.name else "Madrasaa Community Hub",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (chatViewMode == ChatViewMode.DIRECT) {
                                        if (selectedRecipient.isOnline) "online" else "Teacher • Madrasaa Ifaa Islama"
                                    } else {
                                        "All Roles & Members Feed"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (chatViewMode == ChatViewMode.DIRECT && selectedRecipient.isOnline) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                                    maxLines = 1
                                )
                            }
                        }

                        // Quick Action Icons
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (activeUser.role == UserRole.ADMIN) {
                                IconButton(onClick = { showBroadcastDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Campaign,
                                        contentDescription = "Broadcast Announcement",
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                            IconButton(onClick = { actionNotice = "Search in chat tapped" }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { actionNotice = "More options tapped" }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More Options",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // View Mode Tabs & Recipient Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = chatViewMode == ChatViewMode.DIRECT,
                            onClick = { chatViewMode = ChatViewMode.DIRECT },
                            label = { Text("Direct Chat", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = chatViewMode == ChatViewMode.ALL_COMMUNITY,
                            onClick = { chatViewMode = ChatViewMode.ALL_COMMUNITY },
                            label = { Text("Community Feed", fontSize = 11.sp) }
                        )
                    }

                    if (chatViewMode == ChatViewMode.DIRECT) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            usersList.filter { it.id != activeUser.id }.take(4).forEach { usr ->
                                FilterChip(
                                    selected = selectedRecipient.id == usr.id,
                                    onClick = { selectedRecipient = usr },
                                    label = { Text(usr.name.split(" ").firstOrNull() ?: usr.name, fontSize = 10.sp) }
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(
                                selected = selectedRoleFilter == null,
                                onClick = { selectedRoleFilter = null },
                                label = { Text("All Roles", fontSize = 10.sp) }
                            )
                            UserRole.entries.forEach { role ->
                                FilterChip(
                                    selected = selectedRoleFilter == role,
                                    onClick = { selectedRoleFilter = role },
                                    label = { Text(role.name, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
            }



            // Filter Messages
            val filteredMessages = chatMessages.filter { msg ->
                if (msg.isBroadcast) return@filter true
                if (chatViewMode == ChatViewMode.DIRECT) {
                    (msg.senderId == activeUser.id && msg.receiverId == selectedRecipient.id) ||
                            (msg.senderId == selectedRecipient.id && msg.receiverId == activeUser.id)
                } else {
                    if (selectedRoleFilter != null) {
                        msg.senderRole == selectedRoleFilter || msg.receiverRole == selectedRoleFilter
                    } else {
                        msg.senderId == activeUser.id || msg.receiverId == activeUser.id || msg.receiverId == "all"
                    }
                }
            }

            // 2. Message List with Floating Date Dividers & Telegram Bubbles
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredMessages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No messages yet",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Send a message to start conversation with ${selectedRecipient.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    // 3. Floating Date Divider: "TODAY" pill chip at top of message list
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                            ) {
                                Text(
                                    text = "TODAY",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                items(filteredMessages) { msg ->
                    val isMe = msg.senderId == activeUser.id

                    if (msg.isBroadcast) {
                        // Broadcast Announcement Card
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Campaign,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "MADRASAA BROADCAST",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Posted by ${msg.senderName} • ${msg.timestamp}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        // Telegram Message Bubble (Right for me, Left for recipient)
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Surface(
                                color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMe) 16.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 16.dp
                                ),
                                tonalElevation = 2.dp,
                                modifier = Modifier.widthIn(max = 260.dp)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                    // Sender info if group / community or received message
                                    if (!isMe) {
                                        Text(
                                            text = msg.senderName,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                    }

                                    // Message Text
                                    Text(
                                        text = msg.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Footer: Time & Double-Check Delivery Status (`✓✓`) in bottom-right corner
                                    Row(
                                        modifier = Modifier.align(Alignment.End),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = msg.timestamp,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 10.sp,
                                            color = if (isMe) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                                        )

                                        // Clickable Checkmark Status (✓ / ✓✓)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clickable { repository.toggleMessageStatus(msg.id) }
                                                .padding(2.dp)
                                        ) {
                                            Text(
                                                text = if (msg.deliveryStatus == DeliveryStatus.SENT) "✓" else "✓✓",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isMe) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Broadcast Dialog
    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("Broadcast Community Announcement") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = broadcastTitleInput,
                        onValueChange = { broadcastTitleInput = it },
                        label = { Text("Announcement Headline") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = broadcastBodyInput,
                        onValueChange = { broadcastBodyInput = it },
                        label = { Text("Announcement Details") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (broadcastTitleInput.isNotBlank() && broadcastBodyInput.isNotBlank()) {
                            repository.broadcastAnnouncement(
                                title = broadcastTitleInput,
                                text = broadcastBodyInput
                            )
                            actionNotice = "Broadcast announcement posted successfully!"
                            showBroadcastDialog = false
                            broadcastTitleInput = ""
                            broadcastBodyInput = ""
                        }
                    }
                ) {
                    Text("Post Broadcast")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

enum class ChatViewMode {
    DIRECT,
    ALL_COMMUNITY
}
