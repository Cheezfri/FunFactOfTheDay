package com.example.funfactoftheday.notifications

import com.example.funfactoftheday.database.models.FactModel
import java.time.LocalDateTime

class AlarmItem(
    val time: LocalDateTime,
    val messages: Array<String>
)