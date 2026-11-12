package com.win12.tvlauncher

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var rootContainer: FrameLayout
    private lateinit var cursorOverlay: CursorOverlayView
    private lateinit var widgetsView: DesktopWidgetsView
    private lateinit var taskbar: LinearLayout
    private lateinit var startMenu: LinearLayout
    private var isStartMenuOpen = false
    private var isDragging = false
    private var isScrollMode = false
    private var downTimeMillis = 0L
    private var currentThemeIndex = 0
    private val handler = Handler(Looper.getMainLooper())

    private var cursorX = 960f
    private var cursorY = 540f
    private var moveSpeed = 12f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        rootContainer = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        setContentView(rootContainer)

        ThemeManager.applyTheme(rootContainer, ThemeManager.themes[currentThemeIndex])

        widgetsView = DesktopWidgetsView(this).apply {
            layoutParams = FrameLayout.LayoutParams(320, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.TOP or Gravity.START
                setMargins(48, 48, 0, 0)
            }
        }
        rootContainer.addView(widgetsView)

        buildTaskbar()
        buildStartMenu()

        cursorOverlay = CursorOverlayView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        rootContainer.addView(cursorOverlay)
        cursorOverlay.updatePosition(cursorX, cursorY)

        val lockManager = LockScreenManager(this) {}
        rootContainer.addView(
            lockManager.lockOverlay,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        rootContainer.isFocusable = true
        rootContainer.isFocusableInTouchMode = true
        rootContainer.requestFocus()
    }

    private fun buildTaskbar() {
        taskbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#CC1E293B"))
                cornerRadius = 28f
                setStroke(1, Color.parseColor("#55FFFFFF"))
            }
            setPadding(24, 12, 24, 12)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                setMargins(0, 0, 0, 36)
            }
        }

        taskbar.addView(makeDockButton("\uD83D\uDD25", "\u5F00\u59CB") { toggleStartMenu() })
        taskbar.addView(makeDockButton("\uD83D\uDCF1", "\u5E94\u7528") { openAppDrawer() })
        taskbar.addView(makeDockButton("\uD83C\uDFA8", "\u4E3B\u9898") { cycleTheme() })
        taskbar.addView(makeDockButton("\u2699\uFE0F", "\u8BBE\u7F6E") {
            startActivity(Intent(Settings.ACTION_SETTINGS))
        })

        rootContainer.addView(taskbar)
    }

    private fun makeDockButton(emoji: String, label: String, onClick: () -> Unit): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(20, 8, 20, 8)
            setOnClickListener { onClick() }
            addView(TextView(this@MainActivity).apply {
                text = emoji
                textSize = 22f
                gravity = Gravity.CENTER
            })
            addView(TextView(this@MainActivity).apply {
                text = label
                setTextColor(Color.WHITE)
                textSize = 11f
                gravity = Gravity.CENTER
            })
        }
    }

    private fun buildStartMenu() {
        startMenu = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#EE0F172A"))
                cornerRadius = 20f
                setStroke(1, Color.parseColor("#44FFFFFF"))
            }
            setPadding(24, 24, 24, 24)
            visibility = View.GONE
            layoutParams = FrameLayout.LayoutParams(420, 520).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                setMargins(0, 0, 0, 120)
            }
        }

        startMenu.addView(TextView(this).apply {
            text = "Win12 \u5F00\u59CB\u83DC\u5355"
            setTextColor(Color.WHITE)
            textSize = 18f
            setPadding(0, 0, 0, 16)
        })

        val items = listOf(
            "\uD83D\uDCCA \u684C\u9762\u5C0F\u7EC4\u4EF6" to {
                Toast.makeText(this, "\u5C0F\u7EC4\u4EF6\u5DF2\u5728\u684C\u9762", Toast.LENGTH_SHORT).show()
            },
            "\uD83D\uDD12 \u9501\u5C4F" to { },
            "\uD83C\uDF10 \u6D4F\u89C8\u5668" to { openBrowser() },
            "\uD83D\uDCF1 \u6240\u6709\u5E94\u7528" to { openAppDrawer() },
            "\u2699\uFE0F \u7CFB\u7EDF\u8BBE\u7F6E" to {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        )

        for ((text, action) in items) {
            startMenu.addView(TextView(this).apply {
                this.text = text
                setTextColor(Color.WHITE)
                textSize = 16f
                setPadding(16, 14, 16, 14)
                background = GradientDrawable().apply {
                    setColor(Color.parseColor("#22FFFFFF"))
                    cornerRadius = 12f
                }
                setOnClickListener {
                    action()
                    toggleStartMenu()
                }
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 8) }
            })
        }
        rootContainer.addView(startMenu)
    }

    private fun toggleStartMenu() {
        isStartMenuOpen = !isStartMenuOpen
        startMenu.visibility = if (isStartMenuOpen) View.VISIBLE else View.GONE
    }

    private fun cycleTheme() {
        currentThemeIndex = (currentThemeIndex + 1) % ThemeManager.themes.size
        ThemeManager.applyTheme(rootContainer, ThemeManager.themes[currentThemeIndex])
        Toast.makeText(this, ThemeManager.themes[currentThemeIndex].name, Toast.LENGTH_SHORT).show()
    }

    private fun openBrowser() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.bing.com")))
        } catch (e: Exception) {
            Toast.makeText(this, "\u65E0\u6CD5\u6253\u5F00\u6D4F\u89C8\u5668", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openAppDrawer() {
        val pm = packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps = pm.queryIntentActivities(mainIntent, 0)
        val names = apps.take(12).map { it.loadLabel(pm).toString() }
        Toast.makeText(this, "\u5E94\u7528: ${names.joinToString(", ")}", Toast.LENGTH_LONG).show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                moveCursor(-moveSpeed, 0f)
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                moveCursor(moveSpeed, 0f)
                return true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                moveCursor(0f, -moveSpeed)
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                moveCursor(0f, moveSpeed)
                return true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                if (event?.repeatCount == 0) {
                    performClick()
                }
                return true
            }
            KeyEvent.KEYCODE_MENU -> {
                isScrollMode = !isScrollMode
                Toast.makeText(
                    this,
                    if (isScrollMode) "\u6EDA\u52A8\u6A21\u5F0F" else "\u70B9\u51FB\u6A21\u5F0F",
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }
            KeyEvent.KEYCODE_BACK -> {
                if (isStartMenuOpen) {
                    toggleStartMenu()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun moveCursor(dx: Float, dy: Float) {
        cursorX = (cursorX + dx).coerceIn(0f, rootContainer.width.toFloat().coerceAtLeast(1f))
        cursorY = (cursorY + dy).coerceIn(0f, rootContainer.height.toFloat().coerceAtLeast(1f))
        cursorOverlay.updatePosition(cursorX, cursorY)
        if (isDragging) {
            simulateTouchMove(cursorX, cursorY)
        }
    }

    private fun performClick() {
        cursorOverlay.triggerClickEffect()
        if (isScrollMode) return
        simulateTouchDown(cursorX, cursorY)
        handler.postDelayed({
            simulateTouchUp(cursorX, cursorY)
        }, 80)
    }

    private fun simulateTouchDown(x: Float, y: Float) {
        downTimeMillis = SystemClock.uptimeMillis()
        val downEvent = MotionEvent.obtain(
            downTimeMillis, downTimeMillis, MotionEvent.ACTION_DOWN, x, y, 0
        )
        rootContainer.dispatchTouchEvent(downEvent)
        downEvent.recycle()
        isDragging = true
    }

    private fun simulateTouchMove(x: Float, y: Float) {
        val moveTime = SystemClock.uptimeMillis()
        val moveEvent = MotionEvent.obtain(
            downTimeMillis, moveTime, MotionEvent.ACTION_MOVE, x, y, 0
        )
        rootContainer.dispatchTouchEvent(moveEvent)
        moveEvent.recycle()
    }

    private fun simulateTouchUp(x: Float, y: Float) {
        val upTime = SystemClock.uptimeMillis()
        val upEvent = MotionEvent.obtain(
            downTimeMillis, upTime, MotionEvent.ACTION_UP, x, y, 0
        )
        rootContainer.dispatchTouchEvent(upEvent)
        upEvent.recycle()
        isDragging = false
    }
}
