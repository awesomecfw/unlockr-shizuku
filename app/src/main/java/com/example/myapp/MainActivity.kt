package com.example.myapp

import android.app.Activity
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.*
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.*
import android.text.InputType
import java.net.URL
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : Activity() {

    private val black = Color.BLACK
    private val white = Color.rgb(245, 245, 245)
    private val gray = Color.rgb(102, 102, 102)
    private val lightGray = Color.rgb(170, 170, 170)
    private val border = Color.rgb(25, 25, 25)
    private val dark = Color.rgb(8, 8, 8)

    private lateinit var root: FrameLayout
    private lateinit var content: FrameLayout
    private lateinit var drawer: LinearLayout
    private lateinit var overlay: View
    private lateinit var headerStatus: TextView
    private lateinit var headerDot: View

    private var serviceActive = false
    private var progress = 0f

    private var ledColor = Color.BLACK
    private var rainbowActive = false
    private var rainbowAnimator: ValueAnimator? = null

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.statusBarColor = black
        window.navigationBarColor = black

        build()
        showPage("root")
    }

    private fun build() {
        root = FrameLayout(this)
        root.setBackgroundColor(black)

        val main = LinearLayout(this)
        main.orientation = LinearLayout.VERTICAL
        main.setBackgroundColor(black)

        root.addView(
            main,
            FrameLayout.LayoutParams(-1, -1)
        )

        main.addView(
            makeHeader(),
            LinearLayout.LayoutParams(
                -1,
                dp(54)
            )
        )

        val divider = View(this)
        divider.setBackgroundColor(border)

        main.addView(
            divider,
            LinearLayout.LayoutParams(
                -1,
                dp(1)
            )
        )

        content = FrameLayout(this)
        content.setBackgroundColor(black)

        main.addView(
            content,
            LinearLayout.LayoutParams(
                -1,
                0,
                1f
            )
        )

        overlay = View(this)
        overlay.setBackgroundColor(Color.argb(128, 0, 0, 0))
        overlay.visibility = View.GONE
        overlay.setOnClickListener {
            closeDrawer()
        }

        root.addView(
            overlay,
            FrameLayout.LayoutParams(-1, -1)
        )

        drawer = LinearLayout(this)
        drawer.orientation = LinearLayout.VERTICAL
        drawer.setBackgroundColor(Color.rgb(5, 5, 5))
        drawer.setPadding(
            dp(10),
            dp(16),
            dp(10),
            dp(16)
        )

        val drawerParams = FrameLayout.LayoutParams(
            dp(245),
            -1
        )

        drawerParams.gravity = Gravity.START
        drawerParams.topMargin = dp(55)
        drawer.translationX = -dp(260).toFloat()

        root.addView(drawer, drawerParams)

        setContentView(root)
    }

    private fun makeHeader(): View {
        val bar = LinearLayout(this)
        bar.orientation = LinearLayout.HORIZONTAL
        bar.gravity = Gravity.CENTER_VERTICAL
        bar.setPadding(
            dp(16),
            0,
            dp(16),
            0
        )

        val menu = TextView(this)
        menu.text = "☰"
        menu.textSize = 20f
        menu.setTextColor(Color.rgb(170, 170, 170))
        menu.gravity = Gravity.CENTER
        menu.typeface = mono()

        menu.setOnClickListener {
            if (drawer.translationX < 0) {
                openDrawer()
            } else {
                closeDrawer()
            }
        }

        bar.addView(
            menu,
            LinearLayout.LayoutParams(
                dp(28),
                dp(28)
            )
        )

        val logo = LinearLayout(this)
        logo.orientation = LinearLayout.HORIZONTAL
        logo.gravity = Gravity.CENTER_VERTICAL

        val unlock = TextView(this)
        unlock.text = "unlock"
        unlock.textSize = 20f
        unlock.setTextColor(white)
        unlock.typeface = Typeface.create(
            mono(),
            Typeface.BOLD
        )
        unlock.letterSpacing = -0.05f

        val r = TextView(this)
        r.text = "r"
        r.textSize = 20f
        r.setTextColor(Color.rgb(102, 102, 102))
        r.typeface = Typeface.create(
            mono(),
            Typeface.BOLD
        )
        r.letterSpacing = -0.05f

        logo.addView(unlock)
        logo.addView(r)

        val logoParams = LinearLayout.LayoutParams(
            -2,
            -1
        )

        logoParams.leftMargin = dp(13)

        bar.addView(
            logo,
            logoParams
        )

        val spacer = Space(this)

        bar.addView(
            spacer,
            LinearLayout.LayoutParams(
                0,
                1,
                1f
            )
        )

        val dot = View(this)
        dot.background = circleDrawable(Color.rgb(85, 85, 85))
        headerDot = dot

        bar.addView(
            dot,
            LinearLayout.LayoutParams(
                dp(6),
                dp(6)
            )
        )

        val status = TextView(this)
        status.text = "offline"
        status.textSize = 11f
        status.setTextColor(gray)
        status.typeface = mono()
        headerStatus = status

        val statusParams = LinearLayout.LayoutParams(
            -2,
            -1
        )

        statusParams.leftMargin = dp(6)

        bar.addView(status, statusParams)

        return bar
    }

    private fun showPage(name: String) {
        closeDrawer()

        content.removeAllViews()

        when (name) {
            "root" -> rootPage()
            "authorizations" -> authorizationPage()
            "tools" -> toolsPage()
            "terminal" -> terminalPage()
            "settings" -> settingsPage()
        }
    }

    private fun openDrawer() {
        drawer.removeAllViews()

        drawerItem(
            "root",
            true
        ) {
            showPage("root")
        }

        drawerItem(
            "authorizations",
            false
        ) {
            showPage("authorizations")
        }

        drawerItem(
            "tools",
            false
        ) {
            showPage("tools")
        }

        drawerItem(
            "terminal",
            false
        ) {
            showPage("terminal")
        }

        drawerItem(
            "settings",
            false
        ) {
            showPage("settings")
        }

        overlay.visibility = View.VISIBLE
        overlay.alpha = 0f

        overlay.animate()
            .alpha(1f)
            .setDuration(180)
            .start()

        drawer.animate()
            .translationX(0f)
            .setDuration(220)
            .setInterpolator(
                DecelerateInterpolator()
            )
            .start()
    }

    private fun closeDrawer() {
        if (!::drawer.isInitialized) return

        overlay.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                overlay.visibility = View.GONE
            }
            .start()

        drawer.animate()
            .translationX(-dp(260).toFloat())
            .setDuration(180)
            .setInterpolator(
                DecelerateInterpolator()
            )
            .start()
    }

    private fun drawerItem(
        label: String,
        active: Boolean,
        action: () -> Unit
    ) {
        val item = TextView(this)

        item.text =
            if (active) {
                "> $label"
            } else {
                label
            }

        item.textSize = 13f
        item.setTextColor(
            if (active) white else gray
        )
        item.typeface = mono()
        item.gravity = Gravity.CENTER_VERTICAL
        item.setPadding(
            dp(13),
            0,
            dp(13),
            0
        )

        if (active) {
            item.background =
                solidDrawable(
                    Color.rgb(11, 11, 11)
                )
        }

        item.setOnClickListener {
            action()
        }

        drawer.addView(
            item,
            LinearLayout.LayoutParams(
                -1,
                dp(40)
            )
        )
    }

    /* ROOT */

    private fun rootPage() {
        val scroll = ScrollView(this)
        scroll.setBackgroundColor(black)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER_HORIZONTAL

        layout.setPadding(
            dp(18),
            dp(32),
            dp(18),
            dp(35)
        )

        val title = unlockrText(27f)

        layout.addView(
            title,
            LinearLayout.LayoutParams(
                -2,
                dp(40)
            )
        )

        val titleMargin = title.layoutParams
            as LinearLayout.LayoutParams

        titleMargin.bottomMargin = dp(30)

        val progressView = ProgressRing(this)

        layout.addView(
            progressView,
            LinearLayout.LayoutParams(
                dp(150),
                dp(150)
            )
        )

        val ringParams =
            progressView.layoutParams
                as LinearLayout.LayoutParams

        ringParams.bottomMargin = dp(27)

        val rootButton = TextView(this)

        rootButton.text =
            if (serviceActive) "STOP"
            else "ROOT"

        rootButton.textSize = 12f
        rootButton.setTextColor(black)
        rootButton.typeface =
            Typeface.create(
                mono(),
                Typeface.BOLD
            )
        rootButton.gravity = Gravity.CENTER
        rootButton.background =
            solidDrawable(white)

        layout.addView(
            rootButton,
            LinearLayout.LayoutParams(
                dp(150),
                dp(42)
            )
        )

        val status = TextView(this)

        status.text =
            if (serviceActive) {
                "service active"
            } else {
                "service inactive"
            }

        status.textSize = 11f
        status.setTextColor(
            if (serviceActive) lightGray else Color.rgb(
                85,
                85,
                85
            )
        )
        status.gravity = Gravity.CENTER
        status.typeface = mono()
        status.setPadding(
            0,
            dp(18),
            0,
            0
        )

        layout.addView(
            status,
            LinearLayout.LayoutParams(
                -1,
                dp(45)
            )
        )

        val info = LinearLayout(this)
        info.orientation = LinearLayout.VERTICAL

        val infoParams = LinearLayout.LayoutParams(
            -1,
            -2
        )

        infoParams.topMargin = dp(45)

        layout.addView(info, infoParams)

        divider(info)

        val deviceTitle = TextView(this)
        deviceTitle.text = "DEVICE"
        deviceTitle.textSize = 10f
        deviceTitle.setTextColor(gray)
        deviceTitle.typeface = mono()

        info.addView(
            deviceTitle,
            LinearLayout.LayoutParams(
                -1,
                dp(22)
            )
        )

        deviceRow(
            info,
            "model",
            android.os.Build.MODEL
        )

        deviceRow(
            info,
            "android",
            "Android " +
                    android.os.Build.VERSION.RELEASE
        )

        val serviceValue = deviceRow(
            info,
            "unlockr",
            if (serviceActive) "active" else "inactive"
        )

        val authValue = deviceRow(
            info,
            "authorization",
            "ready"
        )

        val shellValue = deviceRow(
            info,
            "shell",
            if (serviceActive) "active" else "inactive"
        )

        rootButton.setOnClickListener {
            if (serviceActive) {
                serviceActive = false
                progress = 0f

                progressView.animateTo(0f)

                rootButton.text = "ROOT"
                status.text = "service inactive"
                status.setTextColor(
                    Color.rgb(85, 85, 85)
                )

                headerStatus.text = "offline"
                headerDot.background =
                    circleDrawable(
                        Color.rgb(85, 85, 85)
                    )

                updateDeviceText(
                    serviceValue,
                    "inactive"
                )

                updateDeviceText(
                    shellValue,
                    "inactive"
                )

                terminalWrite(
                    "unlockr service stopped"
                )

                toast("service stopped")

                return@setOnClickListener
            }

            rootButton.isEnabled = false
            rootButton.alpha = .5f
            rootButton.text = "STARTING"

            status.text =
                "starting unlockr service..."

            status.setTextColor(
                Color.rgb(85, 85, 85)
            )

            progressView.animateTo(0f)

            val steps = arrayOf(
                12f to "initializing service...",
                25f to "preparing privileged bridge...",
                41f to "starting binder service...",
                57f to "registering authorization manager...",
                72f to "starting shell interface...",
                86f to "finalizing service...",
                100f to "unlockr service active"
            )

            var index = 0

            val timer = object : Runnable {
                override fun run() {
                    if (index >= steps.size) {
                        serviceActive = true

                        rootButton.isEnabled = true
                        rootButton.alpha = 1f
                        rootButton.text = "STOP"

                        status.text =
                            "service active"

                        status.setTextColor(
                            lightGray
                        )

                        headerStatus.text =
                            "connected"

                        headerDot.background =
                            circleDrawable(white)

                        updateDeviceText(
                            serviceValue,
                            "active"
                        )

                        updateDeviceText(
                            shellValue,
                            "active"
                        )

                        terminalWrite(
                            "unlockr service started"
                        )

                        terminalWrite(
                            "authorization manager ready"
                        )

                        terminalWrite(
                            "shell interface ready"
                        )

                        toast(
                            "unlockr service active"
                        )

                        return
                    }

                    val step = steps[index]

                    progressView.animateTo(
                        step.first
                    )

                    status.text = step.second

                    index++

                    handler.postDelayed(
                        this,
                        280
                    )
                }
            }

            handler.post(timer)
        }

        scroll.addView(
            layout,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        content.addView(
            scroll,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )
    }

    private fun unlockrText(
        size: Float
    ): LinearLayout {
        val box = LinearLayout(this)
        box.orientation = LinearLayout.HORIZONTAL

        val unlock = TextView(this)
        unlock.text = "unlock"
        unlock.textSize = size
        unlock.setTextColor(white)
        unlock.typeface =
            Typeface.create(
                mono(),
                Typeface.BOLD
            )
        unlock.letterSpacing = -0.07f

        val r = TextView(this)
        r.text = "r"
        r.textSize = size
        r.setTextColor(
            Color.rgb(102, 102, 102)
        )
        r.typeface =
            Typeface.create(
                mono(),
                Typeface.BOLD
            )
        r.letterSpacing = -0.07f

        box.addView(unlock)
        box.addView(r)

        return box
    }

    private fun deviceRow(
        parent: LinearLayout,
        label: String,
        value: String
    ): TextView {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL

        val left = TextView(this)
        left.text = label
        left.textSize = 10f
        left.setTextColor(
            Color.rgb(85, 85, 85)
        )
        left.typeface = mono()

        row.addView(
            left,
            LinearLayout.LayoutParams(
                0,
                dp(34),
                1f
            )
        )

        val right = TextView(this)
        right.text = value
        right.textSize = 10f
        right.setTextColor(lightGray)
        right.typeface = mono()
        right.gravity = Gravity.RIGHT

        row.addView(
            right,
            LinearLayout.LayoutParams(
                0,
                dp(34),
                1f
            )
        )

        divider(parent)
        parent.addView(row)

        return right
    }

    private fun updateDeviceText(
        text: TextView,
        value: String
    ) {
        text.text = value
    }

    /* AUTHORIZATIONS */

    private fun authorizationPage() {
        val scroll = ScrollView(this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        layout.setPadding(
            dp(18),
            dp(24),
            dp(18),
            dp(35)
        )

        title(
            layout,
            "authorizations"
        )

        subtitle(
            layout,
            "applications requesting access to the unlockr service."
        )

        authorizationCard(
            layout,
            "terminal",
            "com.unlockr.terminal"
        )

        authorizationCard(
            layout,
            "quest tools",
            "com.awesomecfw.questtools"
        )

        authorizationCard(
            layout,
            "adb bridge",
            "com.unlockr.adbbridge"
        )

        scroll.addView(layout)

        content.addView(
            scroll,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )
    }

    private fun authorizationCard(
        parent: LinearLayout,
        name: String,
        pkg: String
    ) {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(
            dp(14),
            dp(14),
            dp(14),
            dp(14)
        )
        card.background =
            strokeDrawable(
                dark,
                Color.rgb(27, 27, 27)
            )

        val nameText = TextView(this)
        nameText.text = name
        nameText.textSize = 12f
        nameText.setTextColor(white)
        nameText.typeface = mono()

        card.addView(nameText)

        val packageText = TextView(this)
        packageText.text = pkg
        packageText.textSize = 10f
        packageText.setTextColor(
            Color.rgb(85, 85, 85)
        )
        packageText.typeface = mono()

        val packageParams =
            LinearLayout.LayoutParams(
                -1,
                -2
            )

        packageParams.topMargin = dp(5)

        card.addView(
            packageText,
            packageParams
        )

        val actions = LinearLayout(this)
        actions.orientation = LinearLayout.HORIZONTAL

        val grant = buttonView(
            "grant",
            true
        )

        val revoke = buttonView(
            "revoke",
            false
        )

        val actionParams =
            LinearLayout.LayoutParams(
                dp(65),
                dp(34)
            )

        actionParams.topMargin = dp(12)

        actions.addView(
            grant,
            actionParams
        )

        val gap = Space(this)

        actions.addView(
            gap,
            LinearLayout.LayoutParams(
                dp(7),
                1
            )
        )

        actions.addView(
            revoke,
            LinearLayout.LayoutParams(
                dp(65),
                dp(34)
            )
        )

        card.addView(actions)

        grant.setOnClickListener {
            grant.text = "granted"
            grant.background =
                strokeDrawable(
                    dark,
                    Color.rgb(41, 41, 41)
                )

            card.background =
                strokeDrawable(
                    dark,
                    Color.rgb(51, 51, 51)
                )

            toast("authorization granted")
        }

        revoke.setOnClickListener {
            grant.text = "grant"
            grant.background =
                solidDrawable(white)

            grant.setTextColor(black)

            card.background =
                strokeDrawable(
                    dark,
                    Color.rgb(27, 27, 27)
                )

            toast("authorization revoked")
        }

        val params =
            LinearLayout.LayoutParams(
                -1,
                -2
            )

        params.bottomMargin = dp(9)

        parent.addView(card, params)
    }

    /* TOOLS */

    private fun toolsPage() {
        val scroll = ScrollView(this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        layout.setPadding(
            dp(18),
            dp(24),
            dp(18),
            dp(35)
        )

        title(layout, "tools")

        sectionTitle(
            layout,
            "status led"
        )

        val preview = FrameLayout(this)
        preview.setBackgroundColor(
            Color.rgb(5, 5, 5)
        )
        preview.background =
            strokeDrawable(
                Color.rgb(5, 5, 5),
                Color.rgb(23, 23, 23)
            )

        val image = ImageView(this)
        image.scaleType =
            ImageView.ScaleType.FIT_CENTER

        preview.addView(
            image,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )

        val glow = View(this)

        glow.background =
            circleDrawable(Color.BLACK)

        val glowParams = FrameLayout.LayoutParams(
            dp(5),
            dp(5)
        )

        glowParams.leftMargin =
            (dp(18) + dp(18)) +
                    (dp(280) * .588f)
                .toInt()

        glowParams.topMargin =
            dp(245 / 2) +
                    (dp(170) * .224f)
                .toInt()

        preview.addView(
            glow,
            glowParams
        )

        val previewParams =
            LinearLayout.LayoutParams(
                -1,
                dp(245)
            )

        previewParams.bottomMargin = dp(12)

        layout.addView(
            preview,
            previewParams
        )

        loadQuestImage(image)

        val ledGrid = LinearLayout(this)
        ledGrid.orientation =
            LinearLayout.VERTICAL

        val colors = arrayOf(
            "red" to Color.rgb(255, 34, 34),
            "green" to Color.rgb(34, 255, 85),
            "blue" to Color.rgb(51, 136, 255),
            "white" to Color.WHITE,
            "yellow" to Color.rgb(255, 255, 34),
            "purple" to Color.rgb(255, 34, 255),
            "cyan" to Color.rgb(34, 255, 255)
        )

        var row: LinearLayout? = null

        colors.forEachIndexed { index, item ->

            if (index % 4 == 0) {
                row = LinearLayout(this)
                row!!.orientation =
                    LinearLayout.HORIZONTAL

                ledGrid.addView(
                    row,
                    LinearLayout.LayoutParams(
                        -1,
                        dp(36)
                    )
                )
            }

            val b = TextView(this)
            b.text = item.first
            b.textSize = 10f
            b.setTextColor(lightGray)
            b.typeface = mono()
            b.gravity = Gravity.CENTER
            b.background =
                strokeDrawable(
                    Color.rgb(8, 8, 8),
                    Color.rgb(37, 37, 37)
                )

            b.setOnClickListener {
                stopRainbow()
                applyGlow(
                    glow,
                    item.second
                )
                toast(
                    "led set to " +
                            item.first
                )
            }

            val params =
                LinearLayout.LayoutParams(
                    0,
                    dp(36),
                    1f
                )

            params.setMargins(
                dp(3),
                0,
                dp(3),
                dp(7)
            )

            row!!.addView(b, params)
        }

        val rainbow = TextView(this)
        rainbow.text = "rainbow"
        rainbow.textSize = 10f
        rainbow.setTextColor(lightGray)
        rainbow.typeface = mono()
        rainbow.gravity = Gravity.CENTER
        rainbow.background =
            strokeDrawable(
                Color.rgb(8, 8, 8),
                Color.rgb(37, 37, 37)
            )

        rainbow.setOnClickListener {
            if (rainbowActive) {
                stopRainbow()
                applyGlow(
                    glow,
                    Color.BLACK
                )
                toast("rainbow stopped")
            } else {
                startRainbow(glow)
                toast("rainbow mode")
            }
        }

        val rainbowRow =
            LinearLayout(this)

        rainbowRow.addView(
            rainbow,
            LinearLayout.LayoutParams(
                0,
                dp(36),
                1f
            )
        )

        ledGrid.addView(
            rainbowRow,
            LinearLayout.LayoutParams(
                -1,
                dp(36)
            )
        )

        layout.addView(ledGrid)

        val customRow = LinearLayout(this)
        customRow.orientation =
            LinearLayout.HORIZONTAL
        customRow.gravity =
            Gravity.CENTER_VERTICAL

        divider(customRow)

        val customText = LinearLayout(this)
        customText.orientation =
            LinearLayout.VERTICAL

        val customTitle = TextView(this)
        customTitle.text = "custom rgb"
        customTitle.textSize = 12f
        customTitle.setTextColor(white)
        customTitle.typeface = mono()

        customText.addView(customTitle)

        val rgbText = TextView(this)
        rgbText.text = "255, 255, 255"
        rgbText.textSize = 10f
        rgbText.setTextColor(
            Color.rgb(85, 85, 85)
        )
        rgbText.typeface = mono()

        customText.addView(
            rgbText,
            LinearLayout.LayoutParams(
                -1,
                dp(22)
            )
        )

        customRow.addView(
            customText,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        val custom = EditText(this)
        custom.inputType =
            InputType.TYPE_CLASS_TEXT
        custom.setSingleLine(true)
        custom.setText("#ffffff")
        custom.setTextColor(white)
        custom.textSize = 10f
        custom.typeface = mono()
        custom.background =
            strokeDrawable(
                Color.rgb(8, 8, 8),
                Color.rgb(41, 41, 41)
            )
        custom.setPadding(
            dp(7),
            0,
            dp(7),
            0
        )

        customRow.addView(
            custom,
            LinearLayout.LayoutParams(
                dp(70),
                dp(36)
            )
        )

        val apply = buttonView(
            "apply",
            false
        )

        val applyParams =
            LinearLayout.LayoutParams(
                dp(55),
                dp(36)
            )

        applyParams.leftMargin = dp(8)

        customRow.addView(
            apply,
            applyParams
        )

        layout.addView(
            customRow,
            LinearLayout.LayoutParams(
                -1,
                dp(60)
            )
        )

        apply.setOnClickListener {
            try {
                val color =
                    Color.parseColor(
                        custom.text.toString()
                    )

                stopRainbow()
                applyGlow(
                    glow,
                    color
                )

                val r = Color.red(color)
                val g = Color.green(color)
                val b = Color.blue(color)

                rgbText.text =
                    "$r, $g, $b"

                toast("custom rgb applied")
            } catch (_: Exception) {
                toast("invalid color")
            }
        }

        val off = buttonView(
            "turn led off",
            false
        )

        val offParams =
            LinearLayout.LayoutParams(
                -1,
                dp(38)
            )

        offParams.topMargin = dp(10)

        layout.addView(off, offParams)

        off.setOnClickListener {
            stopRainbow()
            applyGlow(
                glow,
                Color.BLACK
            )
            rgbText.text = "off"
            toast("led turned off")
        }

        sectionTitle(
            layout,
            "device"
        )

        settingRow(
            layout,
            "verbose boot",
            "show additional boot information",
            "enable"
        ) { button ->
            button.text =
                if (button.text == "enable")
                    "disable"
                else
                    "enable"

            terminalWrite(
                "verbose boot " +
                        if (button.text == "disable")
                            "enabled"
                        else
                            "disabled"
            )

            toast(
                if (button.text == "disable")
                    "verbose boot enabled"
                else
                    "verbose boot disabled"
            )
        }

        settingRow(
            layout,
            "reboot",
            "restart the device",
            "reboot"
        ) {
            showModal(
                "reboot device",
                "this would request a device reboot through the Unlockr privileged service."
            ) {
                terminalWrite(
                    "reboot request sent"
                )
                toast(
                    "reboot request sent"
                )
            }
        }

        scroll.addView(layout)

        content.addView(
            scroll,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )
    }

    private fun loadQuestImage(
        image: ImageView
    ) {
        Thread {
            try {
                val url = URL(
                    "https://raw.githubusercontent.com/awesomecfw/unlockr-tools/refs/heads/main/quest2led.png"
                )

                val stream = url.openStream()
                val bitmap =
                    BitmapFactory.decodeStream(stream)

                stream.close()

                runOnUiThread {
                    image.setImageBitmap(bitmap)
                }
            } catch (_: Exception) {
            }
        }.start()
    }

    private fun applyGlow(
        view: View,
        color: Int
    ) {
        ledColor = color

        if (color == Color.BLACK) {
            view.background =
                circleDrawable(Color.BLACK)
            view.elevation = 0f
            return
        }

        view.background =
            circleDrawable(color)

        view.elevation = dp(5).toFloat()
    }

    private fun startRainbow(
        view: View
    ) {
        if (rainbowActive) return

        rainbowActive = true

        rainbowAnimator =
            ValueAnimator.ofFloat(
                0f,
                360f
            )

        rainbowAnimator!!.duration = 4000
        rainbowAnimator!!.repeatCount =
            ValueAnimator.INFINITE
        rainbowAnimator!!.interpolator =
            android.view.animation.LinearInterpolator()

        rainbowAnimator!!.addUpdateListener {
            val hsv = floatArrayOf(
                it.animatedValue as Float,
                1f,
                1f
            )

            applyGlow(
                view,
                Color.HSVToColor(hsv)
            )
        }

        rainbowAnimator!!.start()
    }

    private fun stopRainbow() {
        rainbowActive = false

        rainbowAnimator?.cancel()
        rainbowAnimator = null
    }

    /* TERMINAL */

    private var terminalOutput: TextView? = null

    private fun terminalPage() {
        val layout = LinearLayout(this)
        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            dp(18),
            dp(24),
            dp(18),
            dp(20)
        )

        title(layout, "terminal")

        val terminal = ScrollView(this)

        terminal.background =
            strokeDrawable(
                Color.rgb(5, 5, 5),
                Color.rgb(24, 24, 24)
            )

        val output = TextView(this)
        output.text =
            "unlockr terminal\n" +
                    "service: " +
                    if (serviceActive)
                        "active"
                    else
                        "inactive" +
                    "\n" +
                    "type \"help\" for commands\n\n" +
                    "$ "

        output.textSize = 11f
        output.setTextColor(lightGray)
        output.typeface = mono()
        output.setPadding(
            dp(12),
            dp(12),
            dp(12),
            dp(12)
        )

        terminalOutput = output

        terminal.addView(
            output,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        layout.addView(
            terminal,
            LinearLayout.LayoutParams(
                -1,
                0,
                1f
            )
        )

        val input = EditText(this)
        input.hint = "enter command..."
        input.setHintTextColor(gray)
        input.setTextColor(white)
        input.textSize = 11f
        input.typeface = mono()
        input.setSingleLine(true)
        input.background =
            solidDrawable(black)
        input.setPadding(
            0,
            dp(12),
            0,
            dp(12)
        )

        input.setOnEditorActionListener { _, _, _ ->
            executeTerminal(
                input.text.toString()
            )
            input.text.clear()
            true
        }

        layout.addView(
            input,
            LinearLayout.LayoutParams(
                -1,
                dp(45)
            )
        )

        content.addView(
            layout,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )
    }

    private fun terminalWrite(
        text: String
    ) {
        val output = terminalOutput ?: return

        output.append(
            if (output.text.isNotEmpty())
                "\n$text"
            else
                text
        )
    }

    private fun executeTerminal(
        command: String
    ) {
        val cmd = command.trim().lowercase()

        if (cmd.isEmpty()) return

        terminalWrite("$ $command")

        when (cmd) {
            "help" -> {
                terminalWrite(
                    "commands:\n" +
                            "  help\n" +
                            "  status\n" +
                            "  root\n" +
                            "  stop\n" +
                            "  led red\n" +
                            "  led green\n" +
                            "  led blue\n" +
                            "  led off\n" +
                            "  clear"
                )
            }

            "status" -> {
                terminalWrite(
                    "service: " +
                            if (serviceActive)
                                "active"
                            else
                                "inactive"
                )
            }

            "root" -> {
                if (serviceActive) {
                    terminalWrite(
                        "service already active"
                    )
                } else {
                    terminalWrite(
                        "starting unlockr service..."
                    )
                    showPage("root")
                }
            }

            "stop" -> {
                if (serviceActive) {
                    serviceActive = false
                    terminalWrite(
                        "unlockr service stopped"
                    )
                } else {
                    terminalWrite(
                        "service already inactive"
                    )
                }
            }

            "led red" -> {
                terminalWrite(
                    "led set to red"
                )
            }

            "led green" -> {
                terminalWrite(
                    "led set to green"
                )
            }

            "led blue" -> {
                terminalWrite(
                    "led set to blue"
                )
            }

            "led off" -> {
                terminalWrite(
                    "led turned off"
                )
            }

            "clear" -> {
                terminalOutput?.text = ""
            }

            else -> {
                terminalWrite(
                    "unknown command: $command"
                )
            }
        }
    }

    /* SETTINGS */

    private fun settingsPage() {
        val scroll = ScrollView(this)

        val layout = LinearLayout(this)
        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            dp(18),
            dp(24),
            dp(18),
            dp(35)
        )

        title(layout, "settings")

        settingSwitch(
            layout,
            "start service on boot",
            "automatically start unlockr",
            false
        )

        settingSwitch(
            layout,
            "authorization prompts",
            "ask before granting app access",
            true
        )

        settingSwitch(
            layout,
            "verbose logging",
            "show additional service logs",
            false
        )

        val reset = buttonView(
            "reset settings",
            false
        )

        val resetParams =
            LinearLayout.LayoutParams(
                -1,
                dp(38)
            )

        resetParams.topMargin = dp(10)

        layout.addView(
            reset,
            resetParams
        )

        reset.setOnClickListener {
            toast("settings reset")
        }

        scroll.addView(layout)

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
        name: String,
        sub: String,
        enabled: Boolean
    ) {
        val row = LinearLayout(this)
        row.orientation =
            LinearLayout.HORIZONTAL
        row.gravity =
            Gravity.CENTER_VERTICAL

        divider(parent)

        val textBox =
            LinearLayout(this)

        textBox.orientation =
            LinearLayout.VERTICAL

        val title = TextView(this)
        title.text = name
        title.textSize = 12f
        title.setTextColor(white)
        title.typeface = mono()

        textBox.addView(title)

        val description = TextView(this)
        description.text = sub
        description.textSize = 10f
        description.setTextColor(
            Color.rgb(85, 85, 85)
        )
        description.typeface = mono()

        val descParams =
            LinearLayout.LayoutParams(
                -1,
                -2
            )

        descParams.topMargin = dp(5)

        textBox.addView(
            description,
            descParams
        )

        row.addView(
            textBox,
            LinearLayout.LayoutParams(
                0,
                dp(55),
                1f
            )
        )

        val sw = SwitchView(
            this,
            enabled
        )

        row.addView(
            sw,
            LinearLayout.LayoutParams(
                dp(38),
                dp(20)
            )
        )

        sw.setOnClickListener {
            sw.toggle()
            toast(
                if (sw.enabledState)
                    "setting enabled"
                else
                    "setting disabled"
            )
        }

        parent.addView(row)
    }

    /* GENERIC UI */

    private fun title(
        parent: LinearLayout,
        value: String
    ) {
        val t = TextView(this)
        t.text = value
        t.textSize = 16f
        t.setTextColor(white)
        t.typeface = mono()

        parent.addView(
            t,
            LinearLayout.LayoutParams(
                -1,
                dp(28)
            )
        )
    }

    private fun subtitle(
        parent: LinearLayout,
        value: String
    ) {
        val t = TextView(this)
        t.text = value
        t.textSize = 11f
        t.setTextColor(gray)
        t.typeface = mono()
        t.setLineSpacing(0f, 1.6f)

        val p =
            LinearLayout.LayoutParams(
                -1,
                -2
            )

        p.topMargin = -dp(5)
        p.bottomMargin = dp(20)

        parent.addView(t, p)
    }

    private fun sectionTitle(
        parent: LinearLayout,
        value: String
    ) {
        val t = TextView(this)
        t.text = value.uppercase()
        t.textSize = 10f
        t.setTextColor(
            Color.rgb(119, 119, 119)
        )
        t.typeface = mono()

        val p =
            LinearLayout.LayoutParams(
                -1,
                dp(25)
            )

        p.topMargin = dp(12)
        p.bottomMargin = dp(2)

        parent.addView(t, p)
    }

    private fun settingRow(
        parent: LinearLayout,
        name: String,
        sub: String,
        buttonText: String,
        action: (TextView) -> Unit
    ) {
        divider(parent)

        val row = LinearLayout(this)
        row.orientation =
            LinearLayout.HORIZONTAL
        row.gravity =
            Gravity.CENTER_VERTICAL

        val textBox =
            LinearLayout(this)

        textBox.orientation =
            LinearLayout.VERTICAL

        val nameText = TextView(this)
        nameText.text = name
        nameText.textSize = 12f
        nameText.setTextColor(white)
        nameText.typeface = mono()

        textBox.addView(nameText)

        val subText = TextView(this)
        subText.text = sub
        subText.textSize = 10f
        subText.setTextColor(
            Color.rgb(85, 85, 85)
        )
        subText.typeface = mono()

        textBox.addView(
            subText,
            LinearLayout.LayoutParams(
                -1,
                dp(25)
            )
        )

        row.addView(
            textBox,
            LinearLayout.LayoutParams(
                0,
                dp(65),
                1f
            )
        )

        val button = buttonView(
            buttonText,
            false
        )

        row.addView(
            button,
            LinearLayout.LayoutParams(
                dp(65),
                dp(34)
            )
        )

        button.setOnClickListener {
            action(button)
        }

        parent.addView(row)
    }

    private fun buttonView(
        text: String,
        primary: Boolean
    ): TextView {
        val b = TextView(this)

        b.text = text
        b.textSize = 11f
        b.gravity = Gravity.CENTER
        b.typeface = mono()

        if (primary) {
            b.setTextColor(black)
            b.background =
                solidDrawable(white)
        } else {
            b.setTextColor(
                Color.rgb(221, 221, 221)
            )
            b.background =
                strokeDrawable(
                    Color.rgb(10, 10, 10),
                    Color.rgb(41, 41, 41)
                )
        }

        return b
    }

    private fun divider(
        parent: LinearLayout
    ) {
        val line = View(this)
        line.setBackgroundColor(
            Color.rgb(23, 23, 23)
        )

        parent.addView(
            line,
            LinearLayout.LayoutParams(
                -1,
                dp(1)
            )
        )
    }

    private fun toast(
        text: String
    ) {
        val t = TextView(this)

        t.text = text
        t.textSize = 10f
        t.setTextColor(black)
        t.typeface = mono()
        t.gravity = Gravity.CENTER
        t.setPadding(
            dp(13),
            dp(9),
            dp(13),
            dp(9)
        )
        t.background =
            solidDrawable(white)

        val params =
            FrameLayout.LayoutParams(
                -2,
                dp(35)
            )

        params.gravity =
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

        params.bottomMargin = dp(20)

        root.addView(t, params)

        t.alpha = 0f
        t.translationY = dp(20).toFloat()

        t.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(180)
            .start()

        handler.postDelayed({
            t.animate()
                .alpha(0f)
                .translationY(dp(20).toFloat())
                .setDuration(180)
                .withEndAction {
                    root.removeView(t)
                }
                .start()
        }, 1800)
    }

    private fun showModal(
        title: String,
        message: String,
        action: () -> Unit
    ) {
        val modal = FrameLayout(this)
        modal.setBackgroundColor(
            Color.argb(185, 0, 0, 0)
        )

        val box = LinearLayout(this)
        box.orientation =
            LinearLayout.VERTICAL
        box.setPadding(
            dp(18),
            dp(18),
            dp(18),
            dp(18)
        )
        box.background =
            strokeDrawable(
                Color.rgb(8, 8, 8),
                Color.rgb(41, 41, 41)
            )

        val titleView = TextView(this)
        titleView.text = title
        titleView.textSize = 13f
        titleView.setTextColor(white)
        titleView.typeface = mono()

        box.addView(titleView)

        val text = TextView(this)
        text.text = message
        text.textSize = 10f
        text.setTextColor(
            Color.rgb(119, 119, 119)
        )
        text.typeface = mono()
        text.setLineSpacing(
            0f,
            1.7f
        )

        val textParams =
            LinearLayout.LayoutParams(
                -1,
                -2
            )

        textParams.topMargin = dp(15)

        box.addView(
            text,
            textParams
        )

        val actions = LinearLayout(this)
        actions.orientation =
            LinearLayout.HORIZONTAL

        val confirm = buttonView(
            "confirm",
            true
        )

        val cancel = buttonView(
            "cancel",
            false
        )

        actions.addView(
            confirm,
            LinearLayout.LayoutParams(
                0,
                dp(36),
                1f
            )
        )

        val gap = Space(this)

        actions.addView(
            gap,
            LinearLayout.LayoutParams(
                dp(7),
                1
            )
        )

        actions.addView(
            cancel,
            LinearLayout.LayoutParams(
                0,
                dp(36),
                1f
            )
        )

        val actionParams =
            LinearLayout.LayoutParams(
                -1,
                dp(36)
            )

        actionParams.topMargin = dp(17)

        box.addView(
            actions,
            actionParams
        )

        val boxParams =
            FrameLayout.LayoutParams(
                dp(350),
                -2
            )

        boxParams.gravity =
            Gravity.CENTER

        modal.addView(
            box,
            boxParams
        )

        root.addView(
            modal,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )

        modal.alpha = 0f
        box.scaleX = .96f
        box.scaleY = .96f

        modal.animate()
            .alpha(1f)
            .setDuration(180)
            .start()

        box.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(180)
            .start()

        cancel.setOnClickListener {
            modal.animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction {
                    root.removeView(modal)
                }
                .start()
        }

        confirm.setOnClickListener {
            action()

            modal.animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction {
                    root.removeView(modal)
                }
                .start()
        }
    }

    /* DRAWABLES */

    private fun solidDrawable(
        color: Int
    ): GradientDrawable {
        val d = GradientDrawable()
        d.setColor(color)
        return d
    }

    private fun strokeDrawable(
        color: Int,
        stroke: Int
    ): GradientDrawable {
        val d = GradientDrawable()
        d.setColor(color)
        d.setStroke(
            dp(1),
            stroke
        )
        return d
    }

    private fun circleDrawable(
        color: Int
    ): GradientDrawable {
        val d = GradientDrawable()
        d.shape =
            GradientDrawable.OVAL
        d.setColor(color)
        return d
    }

    private fun mono(): Typeface {
        return Typeface.MONOSPACE
    }

    private fun dp(
        value: Int
    ): Int {
        return (
                value *
                        resources.displayMetrics.density
                ).toInt()
    }

    override fun onBackPressed() {
        if (
            drawer.translationX >= 0
        ) {
            closeDrawer()
            return
        }

        showPage("root")
    }

    /* CUSTOM PROGRESS RING */

    private class ProgressRing(
        context: android.content.Context
    ) : View(context) {

        private val bgPaint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        private val progressPaint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        private val textPaint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        private var value = 0f
        private var displayed = 0f

        init {
            bgPaint.style = Paint.Style.STROKE
            bgPaint.strokeWidth = 7f
            bgPaint.color =
                Color.rgb(23, 23, 23)

            progressPaint.style =
                Paint.Style.STROKE

            progressPaint.strokeWidth = 7f
            progressPaint.strokeCap =
                Paint.Cap.ROUND

            progressPaint.color =
                Color.WHITE

            textPaint.color =
                Color.WHITE

            textPaint.textSize = 19f
            textPaint.typeface =
                Typeface.MONOSPACE

            textPaint.textAlign =
                Paint.Align.CENTER
        }

        override fun onDraw(
            canvas: Canvas
        ) {
            super.onDraw(canvas)

            val cx = width / 2f
            val cy = height / 2f

            val radius =
                minOf(width, height) / 2f -
                        7f

            canvas.drawCircle(
                cx,
                cy,
                radius,
                bgPaint
            )

            val rect = RectF(
                cx - radius,
                cy - radius,
                cx + radius,
                cy + radius
            )

            canvas.drawArc(
                rect,
                -90f,
                360f * displayed / 100f,
                false,
                progressPaint
            )

            val percent =
                "${displayed.toInt()}%"

            val baseline =
                cy -
                        (
                                textPaint.ascent() +
                                        textPaint.descent()
                                ) / 2f

            canvas.drawText(
                percent,
                cx,
                baseline,
                textPaint
            )
        }

        fun animateTo(
            target: Float
        ) {
            value = target

            val start = displayed

            ValueAnimator.ofFloat(
                start,
                target
            ).apply {
                duration = 150
                interpolator =
                    DecelerateInterpolator()

                addUpdateListener {
                    displayed =
                        it.animatedValue as Float

                    invalidate()
                }

                start()
            }
        }
    }

    private class SwitchView(
        context: android.content.Context,
        initial: Boolean
    ) : View(context) {

        var enabledState = initial
            private set

        private val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        init {
            setWillNotDraw(false)
        }

        override fun onDraw(
            canvas: Canvas
        ) {
            super.onDraw(canvas)

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            paint.color =
                if (enabledState)
                    Color.WHITE
                else
                    Color.rgb(51, 51, 51)

            canvas.drawRect(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                paint
            )

            paint.style =
                Paint.Style.FILL

            paint.color =
                if (enabledState)
                    Color.WHITE
                else
                    Color.rgb(85, 85, 85)

            val x =
                if (enabledState)
                    width - 9f
                else
                    9f

            canvas.drawRect(
                x - 6f,
                4f,
                x + 6f,
                height - 4f,
                paint
            )
        }

        fun toggle() {
            enabledState = !enabledState

            animate()
                .setDuration(150)
                .start()

            invalidate()
        }
    }
}
