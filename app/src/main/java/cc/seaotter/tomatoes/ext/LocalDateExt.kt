package cc.seaotter.tomatoes.ext

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.ZoneId
import java.util.Date

fun LocalDate.toDate(): Date {
    return Date.from(this.toJavaLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
}