package com.example.reelmonster

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private lateinit var todayText: TextView
    private lateinit var totalText: TextView
    private lateinit var chart: BarsView
    private lateinit var levelSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
        refresh()
    }

    override fun onResume() { super.onResume(); if (::root.isInitialized) refresh() }

    private fun buildUi() {
        val scroll = ScrollView(this)
        root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(22, 28, 22, 28); setBackgroundColor(Color.rgb(247,248,252)) }
        scroll.addView(root)
        setContentView(scroll)

        val header = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val face = TextView(this).apply { text = "👾"; textSize = 38f }
        header.addView(face)
        val titleCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(12,0,0,0) }
        titleCol.addView(text("REEL MONSTER", 24, "#151B2E", true))
        titleCol.addView(text("Your tiny screen-time sidekick", 13, "#687086"))
        header.addView(titleCol); root.addView(header)
        root.addView(space(20))

        val hero = card("#171F35")
        hero.addView(text("TODAY'S REEL COUNT", 12, "#C5CBE0", true))
        todayText = text("0", 54, "#FFFFFF", true).apply { gravity = Gravity.CENTER; setPadding(0,8,0,0) }
        hero.addView(todayText)
        hero.addView(text("reels caught · let's touch some grass 🌱", 13, "#C5CBE0").apply { gravity = Gravity.CENTER })
        root.addView(hero)
        root.addView(space(14))

        val stats = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val totalCard = card("#FFFFFF"); totalCard.addView(text("ALL-TIME REELS", 11, "#687086", true)); totalText = text("0", 30, "#151B2E", true); totalCard.addView(totalText)
        val levelCard = card("#FCE8F1"); levelCard.addView(text("MONSTER LEVEL", 11, "#A21B5B", true)); levelCard.addView(text("🩷  Growing", 22, "#A21B5B", true)); stats.addView(totalCard, LinearLayout.LayoutParams(0, -2, 1f)); stats.addView(levelCard, LinearLayout.LayoutParams(0, -2, 1f).apply { leftMargin = 10 }); root.addView(stats)
        root.addView(space(18))

        root.addView(text("YOUR LAST 7 DAYS", 14, "#151B2E", true))
        chart = BarsView(this); root.addView(chart, LinearLayout.LayoutParams(-1, 145))
        root.addView(space(12))

        val settings = card("#FFFFFF")
        settings.addView(text("COUNTING MODE", 16, "#151B2E", true))
        levelSwitch = Switch(this).apply { text = "Level 2 · Automatic detection"; textSize = 14f; setTextColor(Color.rgb(48,55,75)); isChecked = Stats.level(this@MainActivity) == 2
            setOnCheckedChangeListener { _, checked -> Stats.setLevel(this@MainActivity, if (checked) 2 else 1); Toast.makeText(this@MainActivity, if (checked) "Automatic mode selected" else "Manual mode selected", Toast.LENGTH_SHORT).show() } }
        settings.addView(levelSwitch)
        settings.addView(text("Level 1: tap the floating monster yourself.\nLevel 2: detects likely Reels / Shorts screens using Android Accessibility. Detection depends on what each app exposes.", 12, "#687086"))
        root.addView(settings)
        root.addView(space(14))

        val start = button("START FLOATING MONSTER  👾", "#E83E8C") { startMonster() }
        root.addView(start)
        root.addView(space(9))
        root.addView(button("ALLOW DISPLAY OVER OTHER APPS", "#252D43") { openOverlaySettings() })
        root.addView(space(9))
        root.addView(button("ENABLE AUTOMATIC DETECTION", "#252D43") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) })
        root.addView(space(9))
        root.addView(button("STOP FLOATING MONSTER", "#687086") { stopService(Intent(this, MonsterOverlayService::class.java)); Toast.makeText(this, "Monster stopped", Toast.LENGTH_SHORT).show() })
        root.addView(space(16))
        root.addView(text("PRIVATE BY DESIGN · No account · No internet needed", 11, "#8991A5").apply { gravity = Gravity.CENTER })
    }

    private fun refresh() {
        if (!::todayText.isInitialized) return
        todayText.text = Stats.count(this).toString(); totalText.text = Stats.total(this).toString(); chart.invalidate()
        if (::levelSwitch.isInitialized) levelSwitch.isChecked = Stats.level(this) == 2
    }
    private fun startMonster() {
        if (!Settings.canDrawOverlays(this)) { openOverlaySettings(); Toast.makeText(this, "Allow permission, then tap Start again", Toast.LENGTH_LONG).show(); return }
        startService(Intent(this, MonsterOverlayService::class.java)); Toast.makeText(this, "Monster is floating! 👾", Toast.LENGTH_SHORT).show()
    }
    private fun openOverlaySettings() { startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))) }
    private fun text(s: String, size: Int, color: String, bold: Boolean = false) = TextView(this).apply { text = s; textSize = size.toFloat(); setTextColor(Color.parseColor(color)); if (bold) setTypeface(null, Typeface.BOLD) }
    private fun space(h: Int) = View(this).apply { layoutParams = LinearLayout.LayoutParams(1,h) }
    private fun card(color: String) = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(17,16,17,16); setBackgroundColor(Color.parseColor(color)) }
    private fun button(label: String, color: String, action: () -> Unit) = Button(this).apply { text = label; textSize = 13f; setTextColor(Color.WHITE); setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(color))); setOnClickListener { action() } }

    inner class BarsView(context: android.content.Context) : View(context) {
        private val paint = android.graphics.Paint(3)
        override fun onDraw(canvas: android.graphics.Canvas) {
            super.onDraw(canvas); val values = Stats.lastSeven(context); val max = (values.maxOrNull() ?: 0).coerceAtLeast(1); val labels = listOf("6d","5d","4d","3d","2d","1d","Today")
            val slot = width / 7f; val base = height - 25f
            values.forEachIndexed { i, v ->
                val bh = (height - 55f) * v / max
                paint.color = Color.rgb(232,62,140); canvas.drawRoundRect(i*slot+slot*.2f, base-bh, i*slot+slot*.8f, base, 9f,9f,paint)
                paint.color = Color.rgb(104,112,134); paint.textSize = 10f; paint.textAlign = android.graphics.Paint.Align.CENTER; canvas.drawText(labels[i], i*slot+slot/2, height-6f, paint)
                if (v > 0) { paint.color = Color.rgb(45,52,72); paint.textSize = 10f; canvas.drawText(v.toString(), i*slot+slot/2, base-bh-5, paint) }
            }
        }
    }
}
