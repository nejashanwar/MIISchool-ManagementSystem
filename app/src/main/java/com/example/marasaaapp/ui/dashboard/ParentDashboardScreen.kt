package com.example.marasaaapp.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marasaaapp.model.PaymentStatus
import com.example.marasaaapp.repository.SchoolRepository
import com.example.marasaaapp.ui.components.*
import com.example.marasaaapp.util.PdfReportGenerator
import java.util.Locale

@Composable
fun ParentDashboardScreen(
    repository: SchoolRepository = SchoolRepository
) {
    val context = LocalContext.current
    val activeUser by repository.activeUser.collectAsState()
    val students by repository.students.collectAsState()
    val reportCards by repository.reportCards.collectAsState()
    val payments by repository.paymentRecords.collectAsState()
    val busRoutes by repository.busRoutes.collectAsState()

    val myChildren = students.filter { it.parentId == activeUser.id || it.parentId == "usr_parent_1" }
    var selectedChildId by remember { mutableStateOf(myChildren.firstOrNull()?.userId ?: "usr_student_1") }

    val selectedChild = myChildren.firstOrNull { it.userId == selectedChildId } ?: myChildren.firstOrNull()
    val childPayments = payments.filter { it.studentId == selectedChild?.userId }
    val selectedBus = busRoutes.firstOrNull { it.studentId == selectedChild?.userId } ?: busRoutes.firstOrNull()

    var actionNotice by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 1. Parent Welcome Header
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            text = "Parent & Guardian Portal • Filament View",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = activeUser.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Linked Children: ${myChildren.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.FamilyRestroom,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // 2. Child Selection Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Select Child:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(myChildren, key = { it.userId }) { child ->
                        FilterChip(
                            selected = child.userId == selectedChildId,
                            onClick = { selectedChildId = child.userId },
                            label = { Text("${child.name} (${child.gradeLevel})", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ChildCare,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            modifier = Modifier.height(30.dp)
                        )
                    }
                }
            }
        }

        if (actionNotice != null) {
            item {
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = actionNotice!!,
                        color = Color(0xFF065F46),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }

        // 3. Bus & Driver Information Card
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bus & Driver Information",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (selectedBus != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(10.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsBus,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(6.dp).size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = selectedBus.routeName,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Bus ${selectedBus.busNumber} • Plate: ${selectedBus.vehiclePlate}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                val isEnRoute = selectedBus.currentStatus.contains("Route", ignoreCase = true) || selectedBus.currentStatus.contains("Arrived", ignoreCase = true)
                                FilamentStatusBadge(text = selectedBus.currentStatus, isSuccess = isEnRoute)
                            }

                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Driver: ${selectedBus.driverName}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Phone: ${selectedBus.driverPhone}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        val intent = android.content.Intent(
                                            android.content.Intent.ACTION_DIAL,
                                            android.net.Uri.parse("tel:${selectedBus.driverPhone}")
                                        )
                                        try {
                                            context.startActivity(intent)
                                            actionNotice = "Calling driver ${selectedBus.driverName}..."
                                        } catch (e: Exception) {
                                            actionNotice = "Unable to open phone dialer."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Call", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Children Progress Table
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Children Progress Table",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                val currentChildList = myChildren.filter { it.userId == selectedChildId }
                FilamentTable(
                    headers = listOf("Child Name", "Grade", "GPA", "Status", "Action"),
                    isEmpty = currentChildList.isEmpty(),
                    emptyMessage = "No child selected."
                ) {
                    currentChildList.forEachIndexed { index, child ->
                        val rc = reportCards.firstOrNull { it.studentId == child.userId }
                        FilamentTableRow(isEven = index % 2 == 1) {
                            Text(text = child.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.3f))
                            Text(text = child.gradeLevel, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            Text(text = String.format(Locale.US, "%.2f", rc?.gpa ?: 3.8), fontWeight = FontWeight.SemiBold, fontSize = 11.sp, modifier = Modifier.weight(0.8f))
                            FilamentStatusBadge(text = if (rc?.isPassed == true) "Passed" else "At Risk", isSuccess = rc?.isPassed ?: true, modifier = Modifier.weight(1f))
                            FilamentRowActions(
                                modifier = Modifier.weight(0.8f),
                                onView = {
                                    rc?.let { PdfReportGenerator.printReportCard(context, it) }
                                    actionNotice = "Viewing report card for ${child.name}"
                                }
                            )
                        }
                    }
                }
            }
        }

        // 5. Fee Statement Table
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Fee Statement & Ledger",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                FilamentTable(
                    headers = listOf("Fee ID", "Fee Type", "Amount", "Due Date", "Status", "Action"),
                    isEmpty = childPayments.isEmpty(),
                    emptyMessage = "No fee statements found for this child."
                ) {
                    childPayments.forEachIndexed { index, pay ->
                        FilamentTableRow(isEven = index % 2 == 1) {
                            Text(text = pay.id, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            Text(text = pay.feeType.label, fontSize = 11.sp, modifier = Modifier.weight(1.2f))
                            Text(text = "$${pay.amount}", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(0.8f))
                            Text(text = pay.dueDate, fontSize = 10.sp, modifier = Modifier.weight(1f))
                            val isPaid = pay.status == PaymentStatus.PAID
                            FilamentStatusBadge(text = pay.status.label, isSuccess = isPaid, modifier = Modifier.weight(1f))
                            FilamentRowActions(
                                modifier = Modifier.weight(0.8f),
                                onView = { actionNotice = "Viewing receipt for ${pay.id}" },
                                onEdit = { actionNotice = "Initiating payment for ${pay.id}" }
                            )
                        }
                    }
                }
            }
        }
    }
}
