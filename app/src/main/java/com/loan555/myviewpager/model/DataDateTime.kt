package com.loan555.myviewpager.model

import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class DataDateTime(private val dataNoteList: DataNoteList) {

    private val calendarList = ArrayList<CalendarDateModel>()
    private val dates = ArrayList<Date>()

    fun getListForMonth(startDay: Int, cal: Calendar): ArrayList<CalendarDateModel> {
        val monthCalendar = cal.clone() as Calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        monthCalendar.firstDayOfWeek =
            startDay// set thu bat dau cho tuan de tinh so tuan trong thang
        Log.d("aaaa", "ngay dau cua tuan la: ${monthCalendar.firstDayOfWeek}")
        // lui lai ngay bat dau lay cho list
        while (true) {
            if (monthCalendar.time.day == startDay && monthCalendar.time.date > 0) {
                break
            }
            monthCalendar.add(Calendar.DAY_OF_MONTH, -1)
        }
        while (dates.size < 6 * 7) {
            var checkEvent = false
            dates.add(monthCalendar.time)
            dataNoteList.getList().forEach {
                if (it.date.data.year == monthCalendar.time.year && it.date.data.month == monthCalendar.time.month && it.date.data.date == monthCalendar.time.date) {
                    checkEvent = true
                    return@forEach
                }
            }
            calendarList.add(CalendarDateModel(monthCalendar.time, checkEvent))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return calendarList
    }
}

data class CalendarDateModel(var data: Date, var haveEvent: Boolean)
