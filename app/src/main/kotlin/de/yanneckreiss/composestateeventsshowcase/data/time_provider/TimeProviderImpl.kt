package de.yanneckreiss.composestateeventsshowcase.data.time_provider

import java.text.SimpleDateFormat
import java.util.Date

class TimeProviderImpl : TimeProvider {

    override fun getTimestampFromNow(): String {
        return SimpleDateFormat.getTimeInstance().format(Date())
    }
}
