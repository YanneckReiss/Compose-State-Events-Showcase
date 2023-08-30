package de.yanneckreiss.composestateeventsshowcase.data.time_provider

import java.text.SimpleDateFormat
import java.util.Date

interface TimeProvider {

    fun getTimestampFromNow(): String {
        return SimpleDateFormat.getTimeInstance().format(Date())
    }
}
