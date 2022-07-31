package com.example.celestialapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.celestialapp.R
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class Utils(private val context: Context) {
    /**
     * разница лет м/у датой снимка небесного тела и текущей датой
     */
    fun getYearAgo(text: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val photoDate = formatter.parse(text) as Date

        val photoCalendar = Calendar.getInstance()

        photoCalendar.time = photoDate

        val currentCalendar = Calendar.getInstance()

        val currentYearDays = currentCalendar.get(Calendar.DAY_OF_YEAR)

        val photoYearDays = photoCalendar.get(Calendar.DAY_OF_YEAR)

        val yearsAgo: Int = currentCalendar
            .get(Calendar.YEAR) - photoCalendar.get(Calendar.YEAR) - if (currentYearDays < photoYearDays) 1 else 0

        // делаем анализ давности
        // если менее 10 лет назад
        with(context) {
            if (yearsAgo < 10) {
                return when (yearsAgo.toString().last().digitToInt()) {
                    0 -> getString(R.string.this_year)
                    1 -> getString(R.string.year_ago)
                    in 2..4 -> getString(R.string.years_ago1, yearsAgo)
                    else -> getString(R.string.years_ago2, yearsAgo)
                }
            }

            // если менее 20 лет назад
            if (yearsAgo < 20) return getString(R.string.years_ago2, yearsAgo)

            // если больше 20 лет назад
            return when (yearsAgo.toString().last().digitToInt()) {
                1 -> getString(R.string.year_ago)
                in 2..4 -> getString(R.string.years_ago1, yearsAgo)
                else -> getString(R.string.years_ago2, yearsAgo)
            }
        }

    }

    /**
     * разница лет м/у датой снимка небесного тела и текущей датой
     */
    fun getTimeAgo(time: Long): String {
        val photoDate = Date(time * 1000)
        val photoCalendar = Calendar.getInstance()

        photoCalendar.time = photoDate

        val currentCalendar = Calendar.getInstance()

        val currentYearDays = currentCalendar.get(Calendar.DAY_OF_YEAR)

        val photoYearDays = photoCalendar.get(Calendar.DAY_OF_YEAR)

        // разница в годах
        val yearsDifference: Int = currentCalendar
            .get(Calendar.YEAR) - photoCalendar.get(Calendar.YEAR) - if (currentYearDays < photoYearDays) 1 else 0

        // разница в месяцах
        val monthsDifference: Int = currentCalendar
            .get(Calendar.MONTH) - photoCalendar.get(Calendar.MONTH)

        // разница в днях
        val daysDifference: Int = currentCalendar
            .get(Calendar.DAY_OF_MONTH) - photoCalendar.get(Calendar.DAY_OF_MONTH)

        with(context) {
            // если есть разница в годах
            if (yearsDifference > 0) {
                return when (yearsDifference.toString().last().digitToInt()) {
                    1 -> getString(R.string.year_ago)
                    in 2..4 -> getString(R.string.years_ago1, yearsDifference)
                    else -> getString(R.string.years_ago2, yearsDifference)
                }
            } else
            // если есть разница в месяцах
                if (monthsDifference > 0) {
                    return when (monthsDifference.toString().last().digitToInt()) {
                        1 -> getString(R.string.month_ago)
                        in 2..4 -> getString(R.string.months_ago1, monthsDifference)
                        else -> getString(R.string.months_ago2, monthsDifference)
                    }
                } else
                // если есть разница в днях
                    return when (daysDifference.toString().last().digitToInt()) {
                        0 -> getString(R.string.today)
                        1 -> getString(R.string.yesterday)
                        in 2..4 -> getString(R.string.days_ago1, daysDifference)
                        else -> getString(R.string.days_ago2, daysDifference)
                    }
        }
    }

    /**
     * конвертер bitmap в bytearray
     */
    fun getByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)

        return outputStream.toByteArray()
    }

    /**
     * конвертер bytearray в bitmap
     */
    fun getBitmap(byteArray: ByteArray): Bitmap =
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)


}