package cn.edu.sicnu.account.data

import java.util.*

class Time {

    companion object {
        val c: Calendar = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)+1
        val day = c.get(Calendar.DAY_OF_MONTH)
    }
}