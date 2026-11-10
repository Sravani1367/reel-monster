package com.example.reelmonster

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.TextView

class MonsterOverlayService : Service() {
    private lateinit var wm: WindowManager
    private var bubble: TextView? = null
    private var params: WindowManager.LayoutParams? = null
    override fun onCreate() { super.onCreate(); startForeground(42, notification()); wm = getSystemService(WINDOW_SERVICE) as WindowManager; showBubble() }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY
    private fun notification(): Notification {
        val channel = "reel_monster_running"
        if (Build.VERSION.SDK_INT >= 26) getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(channel,"Reel Monster",NotificationManager.IMPORTANCE_LOW))
        val pending = PendingIntent.getActivity(this,0,Intent(this,MainActivity::class.java),PendingIntent.FLAG_IMMUTABLE)
        return Notification.Builder(this,channel).setContentTitle("Reel Monster is watching your scroll").setContentText("Tap the floating monster to count manually").setSmallIcon(android.R.drawable.ic_media_play).setContentIntent(pending).build()
    }
    private fun showBubble() {
        val view = TextView(this).apply { text = "👾\n${Stats.count(this@MonsterOverlayService)}"; textSize = 17f; gravity = Gravity.CENTER; setTextColor(Color.WHITE); typeface = Typeface.DEFAULT_BOLD; background = GradientDrawable().apply { shape = GradientDrawable.OVAL; setColor(Color.rgb(232,62,140)); setStroke(3,Color.WHITE) }; elevation = 12f; setPadding(10,10,10,10) }
        val type = if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
        val p = WindowManager.LayoutParams(76,76,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT).apply { gravity = Gravity.TOP or Gravity.END; x = 18; y = 190 }
        var downX = 0f; var downY = 0f; var startX = 0; var startY = 0; var moved = false
        view.setOnTouchListener { _, e ->
            when(e.action) {
                MotionEvent.ACTION_DOWN -> { downX=e.rawX; downY=e.rawY; startX=p.x; startY=p.y; moved=false; true }
                MotionEvent.ACTION_MOVE -> { val dx=(e.rawX-downX).toInt(); val dy=(e.rawY-downY).toInt(); if (kotlin.math.abs(dx)>5 || kotlin.math.abs(dy)>5) moved=true; p.x=startX-dx; p.y=startY+dy; wm.updateViewLayout(view,p); true }
                MotionEvent.ACTION_UP -> { if (!moved && Stats.level(this)==1) { Stats.add(this); updateBubble() }; true }
                else -> true
            }
        }
        bubble=view; params=p; wm.addView(view,p)
    }
    fun updateBubble() { bubble?.text = "👾\n${Stats.count(this)}" }
    override fun onDestroy() { bubble?.let { try { wm.removeView(it) } catch (_: Exception) {} }; bubble=null; super.onDestroy() }
    override fun onBind(intent: Intent?): IBinder? = null
}
