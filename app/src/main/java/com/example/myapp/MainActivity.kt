package com.example.myapp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout
    private lateinit var statusText: TextView
    private lateinit var progress: ProgressBar
    private lateinit var percent: TextView

    private val white = Color.rgb(245, 245, 245)
    private val gray = Color.rgb(145, 145, 145)
    private val dark = Color.rgb(12, 12, 12)
    private val panel = Color.rgb(20, 20, 20)
    private val line = Color.rgb(38, 38, 38)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        showRoot()
    }

    private fun base(): LinearLayout {
        root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.BLACK)

        val top = LinearLayout(this)
        top.orientation = LinearLayout.HORIZONTAL
        top.gravity = Gravity.CENTER_VERTICAL
        top.setPadding(22, 18, 22, 12)

        val title = TextView(this)
        title.text = "unlockr"
        title.textSize = 25f
        title.setTextColor(white)
        title.typeface = Typeface.create("sans", Typeface.NORMAL)

        top.addView(
            title,
            LinearLayout.LayoutParams(0, 55, 1f)
        )

        val menu = TextView(this)
        menu.text = "☰"
        menu.textSize = 25f
        menu.setTextColor(white)
        menu.gravity = Gravity.CENTER

        menu.setOnClickListener {
            showMenu()
        }

        top.addView(
            menu,
            LinearLayout.LayoutParams(55, 55)
        )

        root.addView(top)

        val divider = View(this)
        divider.setBackgroundColor(line)

        root.addView(
            divider,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                1
            )
        )

        content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(22, 18, 22, 22)

        root.addView(
            content,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        return root
    }

    private fun setScreen(view: LinearLayout) {
        setContentView(base())

        content.removeAllViews()

        view.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        content.addView(view)
    }

    private fun showRoot() {
        val page = LinearLayout(this)
        page.orientation = LinearLayout.VERTICAL

        val title = TextView(this)
        title.text = "unlockr"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        title.setTextColor(white)
        title.setPadding(0, 10, 0, 4)

        page.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "privileged device service"
        subtitle.textSize = 13f
        subtitle.gravity = Gravity.CENTER
        subtitle.setTextColor(gray)

        page.addView(subtitle)

        val space1 = Space(this)

        page.addView(
            space1,
            LinearLayout.LayoutParams(
                1,
                35
            )
        )

        val circle = FrameLayout(this)
        circle.layoutParams = LinearLayout.LayoutParams(
            220,
            220
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }

        progress = ProgressBar(
            this,
            null,
            android.R.attr.progressBarStyleHorizontal
        )

        progress.max = 100
        progress.progress = 0
        progress.rotation = -90f

        circle.addView(
            progress,
            FrameLayout.LayoutParams(
                200,
                200
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        percent = TextView(this)
        percent.text = "0%"
        percent.textSize = 27f
        percent.setTextColor(white)
        percent.gravity = Gravity.CENTER
        percent.typeface = Typeface.DEFAULT_BOLD

        circle.addView(
            percent,
            FrameLayout.LayoutParams(
                200,
                200
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        page.addView(circle)

        val rootButton = Button(this)
        rootButton.text = "ROOT"
        rootButton.textSize = 14f
        rootButton.setTextColor(Color.BLACK)
        rootButton.setBackgroundColor(white)

        rootButton.setOnClickListener {
            startUnlockr(rootButton)
        }

        page.addView(
            rootButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                52
            ).apply {
                topMargin = 22
            }
        )

        statusText = TextView(this)
        statusText.text = "service inactive"
        statusText.textSize = 13f
        statusText.gravity = Gravity.CENTER
        statusText.setTextColor(gray)

        page.addView(
            statusText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                40
            )
        )

        val infoTitle = TextView(this)
        infoTitle.text = "device"
        infoTitle.textSize = 14f
        infoTitle.setTextColor(white)
        infoTitle.typeface = Typeface.DEFAULT_BOLD
        infoTitle.setPadding(0, 20, 0, 10)

        page.addView(infoTitle)

        addInfo(page, "model", "Quest 2")
        addInfo(page, "android", "Android 12")
        addInfo(page, "unlockr", "inactive")
        addInfo(page, "authorization", "ready")
        addInfo(page, "shell", "inactive")

        setScreen(page)
    }

    private fun addInfo(
        page: LinearLayout,
        name: String,
        value: String
    ) {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL

        val left = TextView(this)
        left.text = name
        left.textSize = 13f
        left.setTextColor(gray)

        val right = TextView(this)
        right.text = value
        right.textSize = 13f
        right.setTextColor(white)
        right.gravity = Gravity.RIGHT

        row.addView(
            left,
            LinearLayout.LayoutParams(
                0,
                38,
                1f
            )
        )

        row.addView(
            right,
            LinearLayout.LayoutParams(
                0,
                38,
                1f
            )
        )

        page.addView(row)
    }

    private fun startUnlockr(button: Button) {
        button.isEnabled = false
        statusText.text = "starting service..."

        var value = 0

        val timer = object : Runnable {
            override fun run() {
                value += 5

                if (value > 100) value = 100

                progress.progress = value
                percent.text = "$value%"

                if (value < 100) {
                    percent.postDelayed(this, 70)
                } else {
                    statusText.text = "service active"
                    button.text = "ACTIVE"
                    button.setTextColor(white)
                    button.setBackgroundColor(Color.rgb(35, 35, 35))
                }
            }
        }

        percent.post(timer)
    }

    private fun showMenu() {
        val menu = PopupWindow(
            this,
            270,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        )

        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.setBackgroundColor(Color.rgb(10, 10, 10))
        box.setPadding(20, 35, 20, 20)

        val title = TextView(this)
        title.text = "unlockr"
        title.textSize = 22f
        title.setTextColor(white)
        title.setPadding(5, 0, 5, 30)

        box.addView(title)

        addMenuItem(box, "root") {
            menu.dismiss()
            showRoot()
        }

        addMenuItem(box, "authorizations") {
            menu.dismiss()
            showAuthorizations()
        }

        addMenuItem(box, "tools") {
            menu.dismiss()
            showTools()
        }

        addMenuItem(box, "terminal") {
            menu.dismiss()
            showTerminal()
        }

        addMenuItem(box, "settings") {
            menu.dismiss()
            showSettings()
        }

        menu.contentView = box
        menu.showAtLocation(
            root,
            Gravity.LEFT or Gravity.TOP,
            0,
            0
        )
    }

    private fun addMenuItem(
        box: LinearLayout,
        text: String,
        action: () -> Unit
    ) {
        val item = TextView(this)
        item.text = text
        item.textSize = 16f
        item.setTextColor(white)
        item.gravity = Gravity.CENTER_VERTICAL
        item.setPadding(12, 0, 12, 0)

        item.setOnClickListener {
            action()
        }

        box.addView(
            item,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                54
            )
        )
    }

    private fun showAuthorizations() {
        val page = makePage("authorizations")

        addCard(
            page,
            "terminal",
            "requesting access",
            "GRANT"
        )

        addCard(
            page,
            "quest tools",
            "authorized",
            "REVOKE"
        )

        addCard(
            page,
            "adb bridge",
            "requesting access",
            "GRANT"
        )

        setScreen(page)
    }

    private fun addCard(
        page: LinearLayout,
        name: String,
        state: String,
        action: String
    ) {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.HORIZONTAL
        card.gravity = Gravity.CENTER_VERTICAL
        card.setPadding(16, 10, 10, 10)
        card.setBackgroundColor(panel)

        val info = LinearLayout(this)
        info.orientation = LinearLayout.VERTICAL

        val nameText = TextView(this)
        nameText.text = name
        nameText.textSize = 15f
        nameText.setTextColor(white)

        val stateText = TextView(this)
        stateText.text = state
        stateText.textSize = 12f
        stateText.setTextColor(gray)

        info.addView(nameText)
        info.addView(stateText)

        val button = Button(this)
        button.text = action
        button.textSize = 11f

        button.setOnClickListener {
            if (button.text.toString() == "GRANT") {
                button.text = "REVOKE"
                stateText.text = "authorized"
            } else {
                button.text = "GRANT"
                stateText.text = "requesting access"
            }
        }

        card.addView(
            info,
            LinearLayout.LayoutParams(
                0,
                65,
                1f
            )
        )

        card.addView(
            button,
            LinearLayout.LayoutParams(
                105,
                50
            )
        )

        page.addView(
            card,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                75
            ).apply {
                bottomMargin = 10
            }
        )
    }

    private fun showTools() {
        val page = makePage("tools")

        val ledTitle = sectionTitle("status led")
        page.addView(ledTitle)

        val ledStatus = TextView(this)
        ledStatus.text = "LED: off"
        ledStatus.textSize = 13f
        ledStatus.setTextColor(gray)
        ledStatus.setPadding(0, 5, 0, 12)

        page.addView(ledStatus)

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
            val button = Button(this)
            button.text = color.uppercase(Locale.US)

            button.setOnClickListener {
                ledStatus.text = "LED: $color"
            }

            page.addView(
                button,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    45
                ).apply {
                    bottomMargin = 5
                }
            )
        }

        val off = Button(this)
        off.text = "TURN LED OFF"

        off.setOnClickListener {
            ledStatus.text = "LED: off"
        }

        page.addView(off)

        page.addView(sectionTitle("device"))

        val verbose = Button(this)
        verbose.text = "VERBOSE BOOT"

        verbose.setOnClickListener {
            Toast.makeText(
                this,
                "verbose boot requested",
                Toast.LENGTH_SHORT
            ).show()
        }

        page.addView(verbose)

        val reboot = Button(this)
        reboot.text = "REBOOT"

        reboot.setOnClickListener {
            Toast.makeText(
                this,
                "reboot requested",
                Toast.LENGTH_SHORT
            ).show()
        }

        page.addView(reboot)

        setScreen(page)
    }

    private fun showTerminal() {
        val page = makePage("terminal")

        val terminal = TextView(this)
        terminal.text =
            "unlockr terminal\n\n" +
            "type commands below\n\n" +
            "$ status\n" +
            "service: inactive\n" +
            "authorization: ready\n" +
            "shell: inactive\n"

        terminal.textSize = 13f
        terminal.typeface = Typeface.MONOSPACE
        terminal.setTextColor(white)
        terminal.setPadding(15, 15, 15, 15)
        terminal.setBackgroundColor(Color.rgb(5, 5, 5))

        page.addView(
            terminal,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val input = EditText(this)
        input.hint = "command"
        input.setHintTextColor(gray)
        input.setTextColor(white)
        input.typeface = Typeface.MONOSPACE
        input.singleLine = true

        page.addView(
            input,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                55
            )
        )

        val run = Button(this)
        run.text = "RUN"

        run.setOnClickListener {
            val command = input.text.toString().trim()

            if (command.isEmpty()) return@setOnClickListener

            when (command.lowercase(Locale.US)) {
                "help" -> {
                    terminal.append(
                        "\n> help\n" +
                        "status\n" +
                        "root\n" +
                        "stop\n" +
                        "clear\n"
                    )
                }

                "status" -> {
                    terminal.append(
                        "\n> status\n" +
                        "unlockr: ready\n" +
                        "authorization: ready\n"
                    )
                }

                "root" -> {
                    terminal.append(
                        "\n> root\n" +
                        "requesting unlockr service...\n"
                    )
                }

                "stop" -> {
                    terminal.append(
                        "\n> stop\n" +
                        "service stopped\n"
                    )
                }

                "clear" -> {
                    terminal.text = ""
                }

                else -> {
                    terminal.append(
                        "\n> $command\n" +
                        "unknown command\n"
                    )
                }
            }

            input.text.clear()
        }

        page.addView(run)

        setScreen(page)
    }

    private fun showSettings() {
        val page = makePage("settings")

        addSwitch(
            page,
            "start service on boot",
            true
        )

        addSwitch(
            page,
            "authorization prompts",
            true
        )

        addSwitch(
            page,
            "verbose logging",
            false
        )

        val reset = Button(this)
        reset.text = "RESET SETTINGS"

        reset.setOnClickListener {
            Toast.makeText(
                this,
                "settings reset",
                Toast.LENGTH_SHORT
            ).show()
        }

        page.addView(
            reset,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                50
            ).apply {
                topMargin = 25
            }
        )

        setScreen(page)
    }

    private fun addSwitch(
        page: LinearLayout,
        text: String,
        enabled: Boolean
    ) {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL

        val label = TextView(this)
        label.text = text
        label.textSize = 14f
        label.setTextColor(white)

        val toggle = Switch(this)
        toggle.isChecked = enabled

        row.addView(
            label,
            LinearLayout.LayoutParams(
                0,
                55,
                1f
            )
        )

        row.addView(toggle)

        page.addView(row)
    }

    private fun makePage(titleText: String): LinearLayout {
        val page = LinearLayout(this)
        page.orientation = LinearLayout.VERTICAL

        val title = TextView(this)
        title.text = titleText
        title.textSize = 24f
        title.setTextColor(white)
        title.typeface = Typeface.DEFAULT_BOLD
        title.setPadding(0, 5, 0, 20)

        page.addView(title)

        return page
    }

    private fun sectionTitle(text: String): TextView {
        val title = TextView(this)
        title.text = text
        title.textSize = 15f
        title.setTextColor(white)
        title.typeface = Typeface.DEFAULT_BOLD
        title.setPadding(0, 15, 0, 10)
        return title
    }
}
