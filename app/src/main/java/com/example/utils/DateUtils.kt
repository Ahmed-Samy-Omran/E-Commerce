package com.example.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestamp(timestamp: Timestamp?): String {
    return try {
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()) // Updated format
        val date = timestamp?.toDate()
        if (date != null) {
            sdf.format(date)
        } else {
            "No date"
        }
    } catch (e: Exception) {
        "Invalid date"
    }
}

fun parseFormattedDate(dateString: String): Timestamp? {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = sdf.parse(dateString)
        if (date != null) {
           Timestamp(date)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}