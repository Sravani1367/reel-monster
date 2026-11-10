package com.example.reelmonster

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class ReelAccessibilityService : AccessibilityService() {
    private var lastFingerprint = ""
    private var lastCountAt = 0L
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || Stats.level(this) != 2) return
        val pkg = event.packageName?.toString() ?: return
        if (pkg != "com.instagram.android" && pkg != "com.google.android.youtube") return
        val words = StringBuilder()
        event.text?.forEach { words.append(it).append(' ') }
        val root = rootInActiveWindow
        collectText(root, words, 0)
        val content = words.toString().lowercase()
        val isShortVideo = when(pkg) {
            "com.instagram.android" -> content.contains("reels") || content.contains("reel") || content.contains("audio") && content.contains("follow")
            else -> content.contains("shorts") || content.contains("short video")
        }
        if (!isShortVideo) return
        val fingerprint = content.filter { it.isLetterOrDigit() }.take(180)
        val now = System.currentTimeMillis()
        if (fingerprint.isNotBlank() && fingerprint != lastFingerprint && now-lastCountAt > 1800) {
            lastFingerprint = fingerprint; lastCountAt = now; Stats.add(this)
        }
    }
    private fun collectText(node: AccessibilityNodeInfo?, out: StringBuilder, depth: Int) {
        if (node == null || depth > 12 || out.length > 5000) return
        node.text?.let { out.append(it).append(' ') }; node.contentDescription?.let { out.append(it).append(' ') }
        for (i in 0 until node.childCount) collectText(node.getChild(i), out, depth+1)
    }
    override fun onInterrupt() {}
}
