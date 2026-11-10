package com.example.reelmonster

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Stats {
    private const val PREF = "reel_monster_stats"
    private fun prefs(c: Context) = c.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    fun todayKey(): String = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
    fun count(c: Context, key: String = todayKey()) = prefs(c).getInt("count_$key", 0)
    fun total(c: Context) = prefs(c).getInt("total", 0)
    fun add(c: Context) {
        val p = prefs(c); val key = todayKey()
        p.edit().putInt("count_$key", count(c, key) + 1).putInt("total", total(c) + 1).apply()
    }
    fun lastSeven(c: Context): List<Int> {
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.US)
        return (6 downTo 0).map { days ->
            val cal = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -days) }
            count(c, fmt.format(cal.time))
        }
    }
    fun level(c: Context) = prefs(c).getInt("level", 1)
    fun setLevel(c: Context, level: Int) { prefs(c).edit().putInt("level", level).apply() }
    fun reset(c: Context) { prefs(c).edit().clear().apply() }
}
