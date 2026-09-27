package com.example.myapp

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import kotlin.math.roundToInt

class MainActivity : Activity() {

    private val black = Color.rgb(0, 0, 0)
    private val white = Color.rgb(245, 245, 245)
    private val gray = Color.rgb(125, 125, 125)
    private val muted = Color.rgb(75, 75, 75)
    private val panel = Color.rgb(13, 13, 13)
    private val border = Color.rgb(38, 38, 38)

    private lateinit var root: FrameLayout
    private lateinit var content: FrameLayout
    private lateinit var drawer: LinearLayout
    private lateinit var drawerOverlay: View

    private var rainbowAnimator: ValueAnimator? = null
    private var rainbowHue = 0f
    private var ledView: LedView? = null

    private var serviceActive = false
    private var shellActive = false

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.statusBarColor = black
        window.navigationBarColor = black

        buildBase()
        showRoot()
    }

    private fun buildBase() {
        root = FrameLayout(this)
        root.setBackgroundColor(black)

        content = FrameLayout(this)

        root.addView(
            content,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )

        drawerOverlay = View(this)
        drawerOverlay.setBackgroundColor(Color.argb(170, 0, 0, 0))
        drawerOverlay.visibility = View.GONE

        drawerOverlay.setOnClickListener {
            closeDrawer()
        }

        root.addView(
            drawerOverlay,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )

        drawer = LinearLayout(this)
        drawer.orientation = LinearLayout.VERTICAL
        drawer.setBackgroundColor(Color.rgb(7, 7, 7))
        drawer.setPadding(
            dp(20),
            dp(22),
            dp(20),
            dp(20)
        )

        val drawerParams = FrameLayout.LayoutParams(
            dp(285),
            -1
        )

        drawerParams.gravity = Gravity.START
        drawerParams.leftMargin = -dp(285)

        root.addView(
            drawer,
            drawerParams
        )

        buildDrawer()

        setContentView(root)
    }

    private fun buildDrawer() {
        drawer.removeAllViews()

        val logo = LinearLayout(this)
        logo.gravity = Gravity.CENTER_VERTICAL

        val unlock = TextView(this)
        unlock.text = "unlock"
        unlock.textSize = 21f
        unlock.setTextColor(white)
        unlock.typeface = Typeface.create(
            Typeface.MONOSPACE,
            Typeface.BOLD
        )

        val r = TextView(this)
        r.text = "r"
        r.textSize = 21f
        r.setTextColor(gray)
        r.typeface = Typeface.create(
            Typeface.MONOSPACE,
            Typeface.BOLD
        )

        logo.addView(unlock)
        logo.addView(r)

        drawer.addView(
            logo,
            LinearLayout.LayoutParams(
                -1,
                dp(58)
            )
        )

        divider(drawer)

        drawerItem("root") {
            closeDrawer()
            showRoot()
        }

        drawerItem("authorizations") {
            closeDrawer()
            showAuthorizations()
        }

        drawerItem("tools") {
            closeDrawer()
            showTools()
        }

        drawerItem("terminal") {
            closeDrawer()
            showTerminal()
        }

        drawerItem("settings") {
            closeDrawer()
            showSettings()
        }
    }

    private fun drawerItem(
        title: String,
        action: () -> Unit
    ) {
        val item = TextView(this)

        item.text = title
        item.textSize = 14f
        item.setTextColor(white)
        item.gravity = Gravity.CENTER_VERTICAL
        item.typeface = Typeface.MONOSPACE

        item.setPadding(
            dp(8),
            0,
            0,
            0
        )

        item.setOnClickListener {
            action()
        }

        drawer.addView(
            item,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            )
        )
    }

    private fun openDrawer() {
        drawerOverlay.visibility = View.VISIBLE

        drawer.animate()
            .translationX(dp(285).toFloat())
            .setDuration(240)
            .setInterpolator(LinearInterpolator())
            .start()
    }

    private fun closeDrawer() {
        drawer.animate()
            .translationX(0f)
            .setDuration(220)
            .setInterpolator(LinearInterpolator())
            .withEndAction {
                drawerOverlay.visibility = View.GONE
            }
            .start()
    }

    private fun header() {
        val bar = LinearLayout(this)

        bar.orientation = LinearLayout.HORIZONTAL
        bar.gravity = Gravity.CENTER_VERTICAL
        bar.setPadding(
            dp(10),
            0,
            dp(14),
            0
        )

        val menu = TextView(this)

        menu.text = "☰"
        menu.textSize = 23f
        menu.setTextColor(white)
        menu.gravity = Gravity.CENTER
        menu.typeface = Typeface.MONOSPACE

        menu.setOnClickListener {
            openDrawer()
        }

        bar.addView(
            menu,
            LinearLayout.LayoutParams(
                dp(48),
                dp(58)
            )
        )

        val logo = LinearLayout(this)

        logo.gravity = Gravity.CENTER_VERTICAL

        val unlock = TextView(this)
        unlock.text = "unlock"
        unlock.textSize = 20f
        unlock.setTextColor(white)
        unlock.typeface = Typeface.create(
            Typeface.MONOSPACE,
            Typeface.BOLD
        )

        val r = TextView(this)
        r.text = "r"
        r.textSize = 20f
        r.setTextColor(gray)
        r.typeface = Typeface.create(
            Typeface.MONOSPACE,
            Typeface.BOLD
        )

        logo.addView(unlock)
        logo.addView(r)

        bar.addView(
            logo,
            LinearLayout.LayoutParams(
                0,
                dp(58),
                1f
            )
        )

        val status = TextView(this)

        status.text =
            if (serviceActive) {
                "active"
            } else {
                "inactive"
            }

        status.textSize = 10f
        status.setTextColor(
            if (serviceActive) white else gray
        )
        status.typeface = Typeface.MONOSPACE

        bar.addView(
            status,
            LinearLayout.LayoutParams(
                -2,
                dp(58)
            )
        )

        content.addView(
            bar,
            FrameLayout.LayoutParams(
                -1,
                dp(58)
            )
        )

        divider(content)
    }

    private fun clearPage() {
        content.removeAllViews()
        header()
    }

    private fun showRoot() {
        clearPage()

        val scroll = ScrollView(this)

        val page = LinearLayout(this)
        page.orientation = LinearLayout.VERTICAL
        page.gravity = Gravity.CENTER_HORIZONTAL

        page.setPadding(
            dp(18),
            dp(18),
            dp(18),
            dp(30)
        )

        val title = TextView(this)

        title.text = "unlockr"
        title.textSize = 26f
        title.setTextColor(white)
        title.gravity = Gravity.CENTER
        title.typeface = Typeface.create(
            Typeface.MONOSPACE,
            Typeface.BOLD
        )

        page.addView(
            title,
            LinearLayout.LayoutParams(
                -1,
                dp(52)
            )
        )

        space(page, 12)

        val ring = ProgressRing(this)

        ring.setProgress(
            if (serviceActive) 100f else 0f
        )

        page.addView(
            ring,
            LinearLayout.LayoutParams(
                dp(190),
                dp(190)
            )
        )

        space(page, 20)

        val rootButton = button("ROOT")

        rootButton.setOnClickListener {
            if (serviceActive) {
                stopServiceUi(ring)
            } else {
                startServiceUi(ring)
            }
        }

        page.addView(
            rootButton,
            LinearLayout.LayoutParams(
                dp(180),
                dp(50)
            )
        )

        space(page, 14)

        val serviceStatus = TextView(this)

        serviceStatus.text =
            if (serviceActive) {
                "service active"
            } else {
                "service inactive"
            }

        serviceStatus.textSize = 12f
        serviceStatus.setTextColor(gray)
        serviceStatus.gravity = Gravity.CENTER
        serviceStatus.typeface = Typeface.MONOSPACE

        page.addView(
            serviceStatus,
            LinearLayout.LayoutParams(
                -1,
                dp(32)
            )
        )

        space(page, 28)

        val info = bordered()

        text(info, "device info", 12f, gray, true)
        space(info, 12)

        infoRow(info, "model", "Quest 2")
        infoRow(info, "android", "Android 12")
        infoRow(
            info,
            "unlockr",
            if (serviceActive) "active" else "inactive"
        )
        infoRow(info, "authorization", "ready")
        infoRow(
            info,
            "shell",
            if (shellActive) "active" else "inactive"
        )

        page.addView(
            info,
            LinearLayout.LayoutParams(
                -1,
                dp(190)
            )
        )

        scroll.addView(page)

        content.addView(
            scroll,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )

        rootButton.text =
            if (serviceActive) "STOP" else "ROOT"
    }

    private fun startServiceUi(ring: ProgressRing) {
        val button = findRootButton()
        button?.isEnabled = false

        ring.animateProgress(
            100f,
            1200
        ) {
            serviceActive = true
            shellActive = true
            showRoot()
        }
    }

    private fun stopServiceUi(ring: ProgressRing) {
        ring.animateProgress(
            0f,
            500
        ) {
            serviceActive = false
            shellActive = false
            showRoot()
        }
    }

    private fun findRootButton(): Button? {
        fun search(view: View): Button? {
            if (view is Button) {
                if (
                    view.text.toString() == "ROOT" ||
                    view.text.toString() == "STOP"
                ) {
                    return view
                }
            }

            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val result = search(view.getChildAt(i))
                    if (result != null) {
                        return result
                    }
                }
            }

            return null
        }

        return search(content)
    }

    private fun showAuthorizations() {
        clearPage()

        val scroll = ScrollView(this)

        val page = LinearLayout(this)
        page.orientation = LinearLayout.VERTICAL
        page.setPadding(
            dp(16),
            dp(18),
            dp(16),
            dp(30)
        )

        text(
            page,
            "authorizations",
            21f,
            white,
            true
        )

        space(page, 7)

        text(
            page,
            "apps requesting access to unlockr.",
            12f,
            gray
        )

        space(page, 22)

        authorizationApp(
            page,
            "terminal",
            "com.example.terminal",
            true
        )

        authorizationApp(
            page,
            "quest tools",
            "com.awesomecfw.questtools",
            true
        )

        authorizationApp(
            page,
            "adb bridge",
            "com.example.adbbridge",
            false
        )

        scroll.addView(page)

        content.addView(
            scroll,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )
    }

    private fun authorizationApp(
        parent: LinearLayout,
        name: String,
        packageName: String,
        authorized: Boolean
    ) {
        val box = bordered()

        val top = LinearLayout(this)
        top.gravity = Gravity.CENTER_VERTICAL

        val names = LinearLayout(this)
        names.orientation = LinearLayout.VERTICAL

        text(
            names,
            name,
            14f,
            white,
            true
        )

        space(names, 3)

        text(
            names,
            packageName,
            10f,
            gray
        )

        top.addView(
            names,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        val grant = Button(this)

        grant.text =
            if (authorized) "REVOKE" else "GRANT"

        grant.textSize = 10f
        grant.setTextColor(
            if (authorized) white else black
        )
        grant.setBackgroundColor(
            if (authorized) Color.rgb(35, 35, 35)
            else white
        )

        grant.setOnClickListener {
            grant.text =
                if (
                    grant.text.toString() == "GRANT"
                ) {
                    "REVOKE"
                } else {
                    "GRANT"
                }

            grant.setTextColor(
                if (grant.text.toString() == "REVOKE") {
                    white
                } else {
                    black
                }
            )

            grant.setBackgroundColor(
                if (grant.text.toString() == "REVOKE") {
                    Color.rgb(35, 35, 35)
                } else {
                    white
                }
            )
        }

        top.addView(
            grant,
            LinearLayout.LayoutParams(
                dp(82),
                dp(38)
            )
        )

        box.addView(
            top,
            LinearLayout.LayoutParams(
                -1,
                dp(62)
            )
        )

        parent.addView(
            box,
            LinearLayout.LayoutParams(
                -1,
                dp(84)
            )
        )

        space(parent, 8)
    }

    private fun showTools() {
        clearPage()

        val scroll = ScrollView(this)

        val page = LinearLayout(this)
        page.orientation = LinearLayout.VERTICAL

        page.setPadding(
            dp(16),
            dp(18),
            dp(16),
            dp(30)
        )

        text(
            page,
            "tools",
            21f,
            white,
            true
        )

        space(page, 18)

        text(
            page,
            "status led",
            13f,
            white,
            true
        )

        space(page, 8)

        val ledBox = bordered()

        ledView = LedView(this)

        ledBox.addView(
            ledView,
            LinearLayout.LayoutParams(
                -1,
                dp(180)
            )
        )

        space(ledBox, 12)

        val colors = arrayOf(
            "red",
            "green",
            "blue",
            "white",
            "yellow",
            "purple",
            "cyan",
            "rainbow"
        )

        for (color in colors) {
            val b = smallButton(
                ledBox,
                color
            )

            b.setOnClickListener {
                setLed(color)
            }

            ledBox.addView(
                b,
                LinearLayout.LayoutParams(
                    -1,
                    dp(42)
                )
            )

            space(ledBox, 6)
        }

        val custom = EditText(this)

        custom.hint = "R,G,B"
        custom.setHintTextColor(gray)
        custom.setTextColor(white)
        custom.textSize = 12f
        custom.typeface = Typeface.MONOSPACE
        custom.setSingleLine(true)
        custom.setPadding(
            dp(12),
            0,
            dp(12),
            0
        )
        custom.setBackgroundColor(
            Color.rgb(20, 20, 20)
        )

        ledBox.addView(
            custom,
            LinearLayout.LayoutParams(
                -1,
                dp(44)
            )
        )

        space(ledBox, 7)

        val customButton = smallButton(
            ledBox,
            "CUSTOM RGB"
        )

        customButton.setOnClickListener {
            val parts = custom.text
                .toString()
                .split(",")

            if (parts.size == 3) {
                try {
                    val r = parts[0].trim().toInt()
                    val g = parts[1].trim().toInt()
                    val b = parts[2].trim().toInt()

                    stopRainbow()

                    ledView?.setLedColor(
                        Color.rgb(
                            r.coerceIn(0, 255),
                            g.coerceIn(0, 255),
                            b.coerceIn(0, 255)
                        )
                    )
                } catch (_: Exception) {
                    toast("invalid rgb")
                }
            } else {
                toast("use R,G,B")
            }
        }

        ledBox.addView(
            customButton,
            LinearLayout.LayoutParams(
                -1,
                dp(42)
            )
        )

        space(ledBox, 7)

        val off = smallButton(
            ledBox,
            "OFF"
        )

        off.setOnClickListener {
            stopRainbow()
            ledView?.setLedColor(black)
        }

        ledBox.addView(
            off,
            LinearLayout.LayoutParams(
                -1,
                dp(42)
            )
        )

        page.addView(
            ledBox,
            LinearLayout.LayoutParams(
                -1,
                dp(650)
            )
        )

        space(page, 20)

        text(
            page,
            "device",
            13f,
            white,
            true
        )

        space(page, 8)

        val verbose = smallButton(
            page,
            "VERBOSE BOOT"
        )

        verbose.setOnClickListener {
            toast("verbose boot requested")
        }

        page.addView(
            verbose,
            LinearLayout.LayoutParams(
                -1,
                dp(44)
            )
        )

        space(page, 7)

        val reboot = smallButton(
            page,
            "REBOOT"
        )

        reboot.setOnClickListener {
            toast("reboot requested")
        }

        page.addView(
            reboot,
            LinearLayout.LayoutParams(
                -1,
                dp(44)
            )
        )

        scroll.addView(page)

        content.addView(
            scroll,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )
    }

    private fun setLed(color: String) {
        when (color) {
            "red" -> {
                stopRainbow()
                ledView?.setLedColor(Color.RED)
            }

            "green" -> {
                stopRainbow()
                ledView?.setLedColor(Color.GREEN)
            }

            "blue" -> {
                stopRainbow()
                ledView?.setLedColor(Color.BLUE)
            }

            "white" -> {
                stopRainbow()
                ledView?.setLedColor(Color.WHITE)
            }

            "yellow" -> {
                stopRainbow()
                ledView?.setLedColor(Color.YELLOW)
            }

            "purple" -> {
                stopRainbow()
                ledView?.setLedColor(Color.MAGENTA)
            }

            "cyan" -> {
                stopRainbow()
                ledView?.setLedColor(Color.CYAN)
            }

            "rainbow" -> {
                if (rainbowAnimator != null) {
                    stopRainbow()
                    ledView?.setLedColor(black)
                } else {
                    startRainbow()
                }
            }
        }
    }

    private fun startRainbow() {
        rainbowAnimator?.cancel()

        val animator = ValueAnimator.ofFloat(
            0f,
            360f
        ).apply {
            duration = 3000L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                val hue =
                    animation.animatedValue as Float

                rainbowHue = hue

                val color = Color.HSVToColor(
                    floatArrayOf(
                        hue,
                        1f,
                        1f
                    )
                )

                ledView?.setLedColor(color)
            }
        }

        rainbowAnimator = animator

        animator.start()
    }

    private fun stopRainbow() {
        rainbowAnimator?.let { animator ->
            animator.cancel()
        }

        rainbowAnimator = null
    }

    private fun showTerminal() {
        clearPage()

        val page = LinearLayout(this)
        page.orientation = LinearLayout.VERTICAL

        page.setPadding(
            dp(16),
            dp(18),
            dp(16),
            dp(20)
        )

        text(
            page,
            "terminal",
            21f,
            white,
            true
        )

        space(page, 12)

        val terminal = EditText(this)

        terminal.setText(
            "> unlockr terminal\n" +
            "> type help for commands\n\n"
        )

        terminal.setTextColor(white)
        terminal.setHintTextColor(gray)
        terminal.textSize = 12f
        terminal.typeface = Typeface.MONOSPACE
        terminal.gravity = Gravity.TOP
        terminal.setPadding(
            dp(14),
            dp(14),
            dp(14),
            dp(14)
        )
        terminal.setSingleLine(false)
        terminal.setBackgroundColor(
            Color.rgb(8, 8, 8)
        )

        page.addView(
            terminal,
            LinearLayout.LayoutParams(
                -1,
                dp(420)
            )
        )

        space(page, 8)

        val command = EditText(this)

        command.hint = "command"
        command.setHintTextColor(gray)
        command.setTextColor(white)
        command.textSize = 12f
        command.typeface = Typeface.MONOSPACE
        command.setSingleLine(true)
        command.setPadding(
            dp(12),
            0,
            dp(12),
            0
        )
        command.setBackgroundColor(
            Color.rgb(18, 18, 18)
        )

        page.addView(
            command,
            LinearLayout.LayoutParams(
                -1,
                dp(46)
            )
        )

        space(page, 8)

        val run = button("RUN")

        run.setOnClickListener {
            val cmd = command.text
                .toString()
                .trim()

            if (cmd.isEmpty()) {
                return@setOnClickListener
            }

            terminal.append(
                "\n> $cmd\n"
            )

            when (cmd.lowercase()) {
                "help" -> terminal.append(
                    "status\nroot\nstop\nled red\nled green\nled blue\nled off\nclear\n"
                )

                "status" -> terminal.append(
                    "unlockr: " +
                        if (serviceActive) "active" else "inactive" +
                        "\nshell: " +
                        if (shellActive) "active" else "inactive" +
                        "\n"
                )

                "root" -> {
                    serviceActive = true
                    shellActive = true
                    terminal.append(
                        "unlockr service active\n"
                    )
                }

                "stop" -> {
                    serviceActive = false
                    shellActive = false
                    terminal.append(
                        "unlockr service stopped\n"
                    )
                }

                "led red" -> {
                    setLed("red")
                    terminal.append("led: red\n")
                }

                "led green" -> {
                    setLed("green")
                    terminal.append("led: green\n")
                }

                "led blue" -> {
                    setLed("blue")
                    terminal.append("led: blue\n")
                }

                "led off" -> {
                    stopRainbow()
                    ledView?.setLedColor(black)
                    terminal.append("led: off\n")
                }

                "clear" -> {
                    terminal.setText("")
                }

                else -> terminal.append(
                    "unknown command\n"
                )
            }

            command.setText("")
        }

        page.addView(
            run,
            LinearLayout.LayoutParams(
                -1,
                dp(48)
            )
        )

        content.addView(
            page,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )
    }

    private fun showSettings() {
        clearPage()

        val scroll = ScrollView(this)

        val page = LinearLayout(this)
        page.orientation = LinearLayout.VERTICAL

        page.setPadding(
            dp(16),
            dp(18),
            dp(16),
            dp(30)
        )

        text(
            page,
            "settings",
            21f,
            white,
            true
        )

        space(page, 18)

        settingSwitch(
            page,
            "start service on boot",
            true
        )

        settingSwitch(
            page,
            "authorization prompts",
            true
        )

        settingSwitch(
            page,
            "verbose logging",
            false
        )

        space(page, 22)

        val reset = smallButton(
            page,
            "RESET SETTINGS"
        )

        reset.setOnClickListener {
            getSharedPreferences(
                "unlockr",
                MODE_PRIVATE
            ).edit()
                .clear()
                .apply()

            serviceActive = false
            shellActive = false

            toast("settings reset")

            showSettings()
        }

        page.addView(
            reset,
            LinearLayout.LayoutParams(
                -1,
                dp(46)
            )
        )

        scroll.addView(page)

        content.addView(
            scroll,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )
    }

    private fun settingSwitch(
        parent: LinearLayout,
        label: String,
        checked: Boolean
    ) {
        val sw = Switch(this)

        sw.text = label
        sw.textSize = 13f
        sw.setTextColor(white)
        sw.typeface = Typeface.MONOSPACE
        sw.isChecked = checked

        sw.setPadding(
            0,
            dp(8),
            0,
            dp(8)
        )

        parent.addView(
            sw,
            LinearLayout.LayoutParams(
                -1,
                dp(58)
            )
        )
    }

    private fun infoRow(
        parent: LinearLayout,
        key: String,
        value: String
    ) {
        val row = LinearLayout(this)

        row.gravity = Gravity.CENTER_VERTICAL

        val left = TextView(this)
        left.text = key
        left.textSize = 11f
        left.setTextColor(gray)
        left.typeface = Typeface.MONOSPACE

        val right = TextView(this)
        right.text = value
        right.textSize = 11f
        right.setTextColor(white)
        right.typeface = Typeface.MONOSPACE
        right.gravity = Gravity.END

        row.addView(
            left,
            LinearLayout.LayoutParams(
                0,
                dp(28),
                1f
            )
        )

        row.addView(
            right,
            LinearLayout.LayoutParams(
                0,
                dp(28),
                1f
            )
        )

        parent.addView(row)
    }

    private fun text(
        parent: ViewGroup,
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        val view = TextView(this)

        view.text = value
        view.textSize = size
        view.setTextColor(color)
        view.typeface = Typeface.create(
            Typeface.MONOSPACE,
            if (bold) Typeface.BOLD else Typeface.NORMAL
        )

        parent.addView(
            view,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        return view
    }

    private fun button(
        title: String
    ): Button {
        val b = Button(this)

        b.text = title
        b.textSize = 12f
        b.setTextColor(black)
        b.typeface = Typeface.create(
            Typeface.MONOSPACE,
            Typeface.BOLD
        )
        b.setBackgroundColor(white)
        b.isAllCaps = false

        return b
    }

    private fun smallButton(
        parent: ViewGroup,
        title: String
    ): Button {
        val b = Button(this)

        b.text = title
        b.textSize = 10f
        b.setTextColor(white)
        b.typeface = Typeface.MONOSPACE
        b.setBackgroundColor(
            Color.rgb(22, 22, 22)
        )
        b.isAllCaps = false

        parent.addView(
            b,
            LinearLayout.LayoutParams(
                -1,
                dp(44)
            )
        )

        return b
    }

    private fun bordered(): LinearLayout {
        val box = LinearLayout(this)

        box.orientation = LinearLayout.VERTICAL
        box.setBackgroundColor(panel)
        box.setPadding(
            dp(14),
            dp(14),
            dp(14),
            dp(14)
        )

        return box
    }

    private fun divider(parent: ViewGroup) {
        val line = View(this)

        line.setBackgroundColor(border)

        parent.addView(
            line,
            LinearLayout.LayoutParams(
                -1,
                dp(1)
            )
        )
    }

    private fun space(
        parent: ViewGroup,
        amount: Int
    ) {
        val v = View(this)

        parent.addView(
            v,
            LinearLayout.LayoutParams(
                1,
                dp(amount)
            )
        )
    }

    private fun spaceHorizontal(
        parent: ViewGroup,
        amount: Int
    ) {
        val v = View(this)

        parent.addView(
            v,
            LinearLayout.LayoutParams(
                dp(amount),
                1
            )
        )
    }

    private fun toast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun dp(value: Int): Int {
        return (
            value *
                resources.displayMetrics.density
            ).roundToInt()
    }

    private fun terminalBox(
        parent: LinearLayout,
        value: String
    ) {
        val box = bordered()

        val output = TextView(this)

        output.text = value
        output.textSize = 12f
        output.setTextColor(white)
        output.typeface = Typeface.MONOSPACE
        output.setPadding(
            dp(12),
            dp(12),
            dp(12),
            dp(12)
        )

        box.addView(
            output,
            LinearLayout.LayoutParams(
                -1,
                dp(150)
            )
        )

        parent.addView(
            box,
            LinearLayout.LayoutParams(
                -1,
                dp(180)
            )
        )
    }

    private fun smallOutlineButton(
        title: String
    ): Button {
        val b = Button(this)

        b.text = title
        b.textSize = 9f
        b.setTextColor(white)
        b.typeface = Typeface.MONOSPACE
        b.setBackgroundColor(
            Color.rgb(25, 25, 25)
        )
        b.isAllCaps = false

        return b
    }

    override fun onDestroy() {
        rainbowAnimator?.cancel()
        rainbowAnimator = null
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private class ProgressRing(
        context: Context
    ) : View(context) {

        private val track = Paint(Paint.ANTI_ALIAS_FLAG)
        private val progress = Paint(Paint.ANTI_ALIAS_FLAG)
        private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        private var value = 0f
        private var animator: ValueAnimator? = null

        init {
            track.style = Paint.Style.STROKE
            track.strokeWidth = 7f
            track.color = Color.rgb(
                35,
                35,
                35
            )
            track.strokeCap = Paint.Cap.ROUND

            progress.style = Paint.Style.STROKE
            progress.strokeWidth = 7f
            progress.color = Color.WHITE
            progress.strokeCap = Paint.Cap.ROUND

            textPaint.color = Color.WHITE
            textPaint.textSize = 30f
            textPaint.typeface = Typeface.MONOSPACE
            textPaint.textAlign = Paint.Align.CENTER
        }

        fun setProgress(v: Float) {
            value = v.coerceIn(
                0f,
                100f
            )
            invalidate()
        }

        fun animateProgress(
            target: Float,
            durationMs: Long,
            done: () -> Unit
        ) {
            animator?.cancel()

            val start = value

            val next = ValueAnimator.ofFloat(
                start,
                target
            ).apply {
                duration = durationMs
                interpolator =
                    android.view.animation.DecelerateInterpolator()

                addUpdateListener {
                    value =
                        it.animatedValue as Float

                    invalidate()
                }

                doOnEnd {
                    done()
                }
            }

            animator = next
            next.start()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val cx = width / 2f
            val cy = height / 2f
            val radius =
                (width.coerceAtMost(height) / 2f) - 18f

            val rect = RectF(
                cx - radius,
                cy - radius,
                cx + radius,
                cy + radius
            )

            canvas.drawArc(
                rect,
                -90f,
                360f,
                false,
                track
            )

            canvas.drawArc(
                rect,
                -90f,
                360f * value / 100f,
                false,
                progress
            )

            val label =
                "${value.roundToInt()}%"

            val baseline =
                cy -
                    (
                        textPaint.ascent() +
                            textPaint.descent()
                    ) / 2f

            canvas.drawText(
                label,
                cx,
                baseline,
                textPaint
            )
        }

        private fun ValueAnimator.doOnEnd(
            action: () -> Unit
        ) {
            addListener(
                object :
                    android.animation.AnimatorListenerAdapter() {

                    override fun onAnimationEnd(
                        animation: android.animation.Animator
                    ) {
                        action()
                    }
                }
            )
        }
    }

    private class LedView(
        context: Context
    ) : View(context) {

        private val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        private var ledColor = Color.BLACK

        init {
            setBackgroundColor(
                Color.rgb(8, 8, 8)
            )
        }

        fun setLedColor(color: Int) {
            ledColor = color
            invalidate()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val cx = width / 2f
            val cy = height / 2f

            paint.color = Color.rgb(
                2,
                2,
                2
            )

            canvas.drawCircle(
                cx,
                cy,
                35f,
                paint
            )

            if (ledColor != Color.BLACK) {
                paint.color = ledColor
                paint.setShadowLayer(
                    28f,
                    0f,
                    0f,
                    ledColor
                )
                setLayerType(
                    LAYER_TYPE_SOFTWARE,
                    paint
                )

                canvas.drawCircle(
                    cx,
                    cy,
                    7f,
                    paint
                )

                paint.clearShadowLayer()
            } else {
                paint.color = Color.BLACK

                canvas.drawCircle(
                    cx,
                    cy,
                    5f,
                    paint
                )
            }
        }
    }
}
