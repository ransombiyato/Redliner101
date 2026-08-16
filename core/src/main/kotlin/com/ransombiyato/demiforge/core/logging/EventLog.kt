package com.ransombiyato.demiforge.core.logging

import com.ransombiyato.demiforge.core.model.IssueSeverity
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList

data class LogEntry(
    val timestamp: Instant,
    val severity: IssueSeverity,
    val category: String,
    val message: String,
)

class EventLog {
    private val entries = CopyOnWriteArrayList<LogEntry>()

    fun add(severity: IssueSeverity, category: String, message: String) {
        entries += LogEntry(Instant.now(), severity, category, message)
    }

    fun info(category: String, message: String) = add(IssueSeverity.INFO, category, message)
    fun warn(category: String, message: String) = add(IssueSeverity.WARNING, category, message)
    fun error(category: String, message: String) = add(IssueSeverity.ERROR, category, message)
    fun snapshot(): List<LogEntry> = entries.toList()
    fun clear() = entries.clear()
}
