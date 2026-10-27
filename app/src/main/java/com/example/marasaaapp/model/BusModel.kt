package com.example.marasaaapp.model

data class BusRouteInfo(
    val id: String,
    val routeName: String,
    val busNumber: String,
    val driverName: String,
    val driverPhone: String,
    val vehiclePlate: String,
    val morningPickupTime: String,
    val eveningDropTime: String,
    val currentStatus: String, // e.g., "On Time - En Route", "Arrived at Madrasaa", "Delayed - Traffic"
    val studentId: String
)
