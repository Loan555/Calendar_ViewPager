package com.loan555.myviewpager.model

import android.util.Log
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.MonthDay
import java.util.*
import kotlin.collections.ArrayList

class DataDateTime() {

    val calendarList = ArrayList<CalendarDateModel>()
    private val sdf = SimpleDateFormat("MMMM yyyy")
    private val cal = Calendar.getInstance()
    private val currentDate = Calendar.getInstance()
    private val dates = ArrayList<Date>()

    fun testForMain() {

        cal.set(2021, 1, 1)// thang chay tu 0 dmn

        val abc = sdf.format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

//
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val start = 3//thu tru di 1     (0->6)

        monthCalendar.firstDayOfWeek = start// set thu bat dau cho tuan de tinh so tuan trong thang
        val tuantrongtha = cal.getActualMaximum(Calendar.WEEK_OF_MONTH) //so tuan trong thang
        val maxDateInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH) // so ngay trong thang

        Log.d(
            "ddd",
            "so ngay trong thang $maxDateInMonth --- curentdate= ${sdf.format(monthCalendar.time)}"
        )

        // lui lai ngay bat dau lay cho list
        while (true) {
            if (monthCalendar.time.day == start && monthCalendar.time.date > 0) {
                break
            }
            monthCalendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        while (dates.size < (tuantrongtha) * 7) {
            dates.add(monthCalendar.time)
            calendarList.add(CalendarDateModel(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
            if (monthCalendar.time.day == start && dates.size > maxDateInMonth) {
                Log.d("ddd", "den day ko")
                break
            }
        }

        var str = ""
        for (i in 0 until calendarList.size) {
            str += calendarList[i].data.date.toString() + "| " + calendarList[i].data.day.toString() + "| " + sdf.format(
                calendarList[i].data.time
            ) + "\n"
        }
        Log.d("ddd", "asdasd = $str")
    }

    fun getListForMonth(startDay:Int, cal: Calendar): ArrayList<CalendarDateModel> {

        val monthCalendar = cal.clone() as Calendar

        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val start = startDay//thu tru di 1     (0->6)

        monthCalendar.firstDayOfWeek = start// set thu bat dau cho tuan de tinh so tuan trong thang

        Log.d("aaaa","ngay dau cua tuan la: ${monthCalendar.firstDayOfWeek }")

        val maxWeekInMonth = cal.getActualMaximum(Calendar.WEEK_OF_MONTH)
        val maxDateInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH) // so ngay trong thang

        Log.d(
            "ddd",
            "so ngay trong thang $maxDateInMonth --- curentdate= ${sdf.format(monthCalendar.time)}"
        )

        // lui lai ngay bat dau lay cho list
        while (true) {
            if (monthCalendar.time.day == start && monthCalendar.time.date > 0) {
                break
            }
            monthCalendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        while (dates.size < 6*7) {
            dates.add(monthCalendar.time)
            calendarList.add(CalendarDateModel(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendarList
    }

}

data class CalendarDateModel(var data: Date)
