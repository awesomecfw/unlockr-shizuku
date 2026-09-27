package com.example.myapp

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

private val black = Color.rgb(0, 0, 0)
private val white = Color.rgb(245, 245, 245)
private val gray = Color.rgb(145, 145, 145)
private val darkGray = Color.rgb(34, 34, 34)

private lateinit var root: FrameLayout
private lateinit var page: LinearLayout
private lateinit var drawer: LinearLayout
private lateinit var overlay: View

private val prefs by lazy {
    getSharedPreferences("unlockr", MODE_PRIVATE)
}

private var selectedPackage: String? = null
private var selectedName: String? = null
private var appFilter = "all"

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    requestWindowFeature(Window.FEATURE_NO_TITLE)

    window.statusBarColor = black
    window.navigationBarColor = black

    buildBase()

    if (!prefs.getBoolean("adb_setup_done", false)) {
        adbSetup()
    } else {
        apps()
    }
}

private fun buildBase() {
    root = FrameLayout(this)
    root.setBackgroundColor(black)

    page = LinearLayout(this)
    page.orientation = LinearLayout.VERTICAL
    page.setBackgroundColor(black)

    root.addView(
        page,
        FrameLayout.LayoutParams(-1, -1)
    )

    overlay = View(this)
    overlay.setBackgroundColor(Color.argb(155, 0, 0, 0))
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
    drawer.setBackgroundColor(Color.rgb(8, 8, 8))
    drawer.setPadding(
        dp(22),
        dp(25),
        dp(18),
        dp(20)
    )
    drawer.visibility = View.GONE

    val drawerParams = FrameLayout.LayoutParams(
        dp(285),
        -1
    )

    drawerParams.gravity = Gravity.START

    root.addView(
        drawer,
        drawerParams
    )

    setContentView(root)
}

private fun header() {
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
    menu.textSize = 23f
    menu.setTextColor(white)
    menu.gravity = Gravity.CENTER
    menu.typeface = mono()

    menu.setOnClickListener {
        openDrawer()
    }

    bar.addView(
        menu,
        LinearLayout.LayoutParams(
            dp(42),
            dp(58)
        )
    )

    val logo = LinearLayout(this)

    logo.orientation = LinearLayout.HORIZONTAL
    logo.gravity = Gravity.CENTER_VERTICAL

    val unlock = TextView(this)

    unlock.text = "unlock"
    unlock.textSize = 20f
    unlock.setTextColor(white)
    unlock.typeface =
        Typeface.create(
            mono(),
            Typeface.BOLD
        )
    unlock.letterSpacing = -0.05f

    val r = TextView(this)

    r.text = "r"
    r.textSize = 20f
    r.setTextColor(gray)
    r.typeface =
        Typeface.create(
            mono(),
            Typeface.BOLD
        )
    r.letterSpacing = -0.05f

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
        if (
            prefs.getBoolean(
                "adb_connected",
                false
            )
        ) {
            "connected"
        } else {
            "disconnected"
        }

    status.textSize = 11f
    status.setTextColor(gray)
    status.typeface = mono()

    bar.addView(
        status,
        LinearLayout.LayoutParams(
            -2,
            dp(58)
        )
    )

    page.addView(
        bar,
        LinearLayout.LayoutParams(
            -1,
            dp(58)
        )
    )

    line(page)
}

private fun adbSetup() {
    page.removeAllViews()
    header()

    val scroll = ScrollView(this)

    val content = LinearLayout(this)

    content.orientation =
        LinearLayout.VERTICAL

    content.setPadding(
        dp(20),
        dp(22),
        dp(20),
        dp(30)
    )

    text(
        content,
        "wireless adb",
        21f,
        white,
        true
    )

    space(content, 7)

    text(
        content,
        "connect unlockr to this device through wireless debugging.",
        12f,
        gray
    )

    space(content, 24)

    button(
        content,
        "OPEN DEVELOPER OPTIONS"
    ) {
        try {
            startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
                )
            )
        } catch (_: Exception) {
            startActivity(
                Intent(Settings.ACTION_SETTINGS)
            )
        }
    }

    space(content, 20)

    text(
        content,
        "device address",
        11f,
        gray,
        true
    )

    space(content, 7)

    val ip = field(
        "192.168.1.100",
        prefs.getString(
            "adb_ip",
            ""
        ) ?: ""
    )

    content.addView(
        ip,
        LinearLayout.LayoutParams(
            -1,
            dp(43)
        )
    )

    space(content, 15)

    text(
        content,
        "pairing port",
        11f,
        gray,
        true
    )

    space(content, 7)

    val pairingPort = field(
        "37000",
        prefs.getString(
            "pairing_port",
            ""
        ) ?: ""
    )

    content.addView(
        pairingPort,
        LinearLayout.LayoutParams(
            -1,
            dp(43)
        )
    )

    space(content, 15)

    text(
        content,
        "pairing code",
        11f,
        gray,
        true
    )

    space(content, 7)

    val pairingCode = field(
        "123456",
        prefs.getString(
            "pairing_code",
            ""
        ) ?: ""
    )

    pairingCode.inputType =
        InputType.TYPE_CLASS_NUMBER

    content.addView(
        pairingCode,
        LinearLayout.LayoutParams(
            -1,
            dp(43)
        )
    )

    space(content, 15)

    text(
        content,
        "adb port",
        11f,
        gray,
        true
    )

    space(content, 7)

    val adbPort = field(
        "5555",
        prefs.getString(
            "adb_port",
            ""
        ) ?: ""
    )

    adbPort.inputType =
        InputType.TYPE_CLASS_NUMBER

    content.addView(
        adbPort,
        LinearLayout.LayoutParams(
            -1,
            dp(43)
        )
    )

    space(content, 20)

    button(
        content,
        "SAVE CONNECTION"
    ) {
        saveAdb(
            ip.text.toString(),
            pairingPort.text.toString(),
            pairingCode.text.toString(),
            adbPort.text.toString()
        )

        Toast.makeText(
            this,
            "connection saved",
            Toast.LENGTH_SHORT
        ).show()
    }

    space(content, 8)

    button(
        content,
        "PAIR"
    ) {
        saveAdb(
            ip.text.toString(),
            pairingPort.text.toString(),
            pairingCode.text.toString(),
            adbPort.text.toString()
        )

        showPairCommands()
    }

    space(content, 8)

    button(
        content,
        "CONNECT"
    ) {
        saveAdb(
            ip.text.toString(),
            pairingPort.text.toString(),
            pairingCode.text.toString(),
            adbPort.text.toString()
        )

        showConnectCommands()
    }

    space(content, 20)

    val statusBox = bordered()

    text(
        statusBox,
        "status",
        11f,
        gray,
        true
    )

    space(statusBox, 6)

    text(
        statusBox,
        if (
            prefs.getBoolean(
                "adb_connected",
                false
            )
        ) {
            "connected"
        } else {
            "not connected"
        },
        14f,
        white
    )

    content.addView(
        statusBox,
        LinearLayout.LayoutParams(
            -1,
            dp(75)
        )
    )

    space(content, 25)

    smallButton(
        content,
        "skip"
    ) {
        prefs.edit()
            .putBoolean(
                "adb_setup_done",
                true
            )
            .apply()

        apps()
    }

    scroll.addView(content)

    page.addView(
        scroll,
        LinearLayout.LayoutParams(
            -1,
            0,
            1f
        )
    )
}

private fun saveAdb(
    ip: String,
    pairingPort: String,
    pairingCode: String,
    adbPort: String
) {
    prefs.edit()
        .putString(
            "adb_ip",
            ip.trim()
        )
        .putString(
            "pairing_port",
            pairingPort.trim()
        )
        .putString(
            "pairing_code",
            pairingCode.trim()
        )
        .putString(
            "adb_port",
            adbPort.trim()
        )
        .apply()
}

private fun showPairCommands() {
    val ip =
        prefs.getString(
            "adb_ip",
            ""
        ) ?: ""

    val port =
        prefs.getString(
            "pairing_port",
            ""
        ) ?: ""

    val code =
        prefs.getString(
            "pairing_code",
            ""
        ) ?: ""

    page.removeAllViews()
    header()

    val content = LinearLayout(this)

    content.orientation =
        LinearLayout.VERTICAL

    content.setPadding(
        dp(20),
        dp(20),
        dp(20),
        dp(30)
    )

    text(
        content,
        "pair device",
        20f,
        white,
        true
    )

    space(content, 10)

    text(
        content,
        "use the command below with adb.",
        12f,
        gray
    )

    space(content, 20)

    terminalBox(
        content,
        "> adb pair $ip:$port\n" +
        "Enter pairing code: $code"
    )

    space(content, 18)

    text(
        content,
        "after adb reports successful pairing, use CONNECT.",
        11f,
        gray
    )

    space(content, 20)

    button(
        content,
        "CONNECT"
    ) {
        showConnectCommands()
    }

    space(content, 8)

    button(
        content,
        "BACK"
    ) {
        adbSetup()
    }

    page.addView(
        content,
        LinearLayout.LayoutParams(
            -1,
            0,
            1f
        )
    )
}

private fun showConnectCommands() {
    val ip =
        prefs.getString(
            "adb_ip",
            ""
        ) ?: ""

    val port =
        prefs.getString(
            "adb_port",
            ""
        ) ?: ""

    page.removeAllViews()
    header()

    val content = LinearLayout(this)

    content.orientation =
        LinearLayout.VERTICAL

    content.setPadding(
        dp(20),
        dp(20),
        dp(20),
        dp(30)
    )

    text(
        content,
        "connect",
        20f,
        white,
        true
    )

    space(content, 10)

    terminalBox(
        content,
        "> adb connect $ip:$port\n" +
        "> waiting for connection..."
    )

    space(content, 20)

    text(
        content,
        "unlockr cannot claim a connection until adb actually reports one.",
        11f,
        gray
    )

    space(content, 20)

    button(
        content,
        "MARK CONNECTED"
    ) {
        prefs.edit()
            .putBoolean(
                "adb_connected",
                true
            )
            .putBoolean(
                "adb_setup_done",
                true
            )
            .apply()

        Toast.makeText(
            this,
            "adb state saved",
            Toast.LENGTH_SHORT
        ).show()

        apps()
    }

    space(content, 8)

    button(
        content,
        "BACK"
    ) {
        adbSetup()
    }

    page.addView(
        content,
        LinearLayout.LayoutParams(
            -1,
            0,
            1f
        )
    )
}

private fun apps() {
    page.removeAllViews()
    header()

    val scroll = ScrollView(this)

    val content = LinearLayout(this)

    content.orientation =
        LinearLayout.VERTICAL

    content.setPadding(
        dp(16),
        dp(18),
        dp(16),
        dp(30)
    )

    val searchRow = LinearLayout(this)

    searchRow.orientation =
        LinearLayout.HORIZONTAL

    searchRow.gravity =
        Gravity.CENTER_VERTICAL

    val searchBox = bordered()

    searchBox.setPadding(
        0,
        0,
        0,
        0
    )

    val search = EditText(this)

    search.hint = "search apps..."
    search.setHintTextColor(gray)
    search.setTextColor(white)
    search.textSize = 13f
    search.typeface = mono()
    search.setSingleLine(true)

    search.setPadding(
        dp(12),
        0,
        dp(12),
        0
    )

    search.background = null

    searchBox.addView(
        search,
        LinearLayout.LayoutParams(
            -1,
            dp(42)
        )
    )

    searchRow.addView(
        searchBox,
        LinearLayout.LayoutParams(
            0,
            dp(44),
            1f
        )
    )

    spaceHorizontal(
        searchRow,
        7
    )

    val filter =
        smallOutlineButton(
            filterLabel()
        )

    searchRow.addView(
        filter,
        LinearLayout.LayoutParams(
            dp(78),
            dp(44)
        )
    )

    content.addView(
        searchRow,
        LinearLayout.LayoutParams(
            -1,
            dp(44)
        )
    )

    space(content, 16)

    val list = LinearLayout(this)

    list.orientation =
        LinearLayout.VERTICAL

    content.addView(list)

    filter.setOnClickListener {
        appFilter =
            when (appFilter) {
                "all" -> "games"
                "games" -> "patched"
                else -> "all"
            }

        filter.text =
            filterLabel()

        loadApps(
            list,
            search.text.toString()
        )
    }

    loadApps(
        list,
        ""
    )

    search.addTextChangedListener(
        object : android.text.TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                loadApps(
                    list,
                    s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(
                s: android.text.Editable?
            ) {
            }
        }
    )

    scroll.addView(content)

    page.addView(
        scroll,
        LinearLayout.LayoutParams(
            -1,
            0,
            1f
        )
    )
}

private fun filterLabel(): String {
    return when (appFilter) {
        "games" -> "GAMES"
        "patched" -> "PATCHED"
        else -> "ALL"
    }
}

private fun isGame(
    info: ApplicationInfo
): Boolean {
    if (
        android.os.Build.VERSION.SDK_INT >= 26
    ) {
        if (
            info.category ==
            ApplicationInfo.CATEGORY_GAME
        ) {
            return true
        }

        if (
            info.flags and
            ApplicationInfo.FLAG_IS_GAME != 0
        ) {
            return true
        }
    }

    return false
}

private fun loadApps(
    list: LinearLayout,
    query: String
) {
    list.removeAllViews()

    val pm = packageManager

    val installed =
        pm.getInstalledApplications(
            PackageManager.GET_META_DATA
        )

    val apps =
        installed
            .filter {
                it.packageName != packageName &&
                pm.getLaunchIntentForPackage(
                    it.packageName
                ) != null
            }
            .filter {
                when (appFilter) {
                    "games" ->
                        isGame(it)

                    "patched" ->
                        prefs.getBoolean(
                            "patched_${it.packageName}",
                            false
                        )

                    else ->
                        true
                }
            }
            .sortedBy {
                pm.getApplicationLabel(it)
                    .toString()
                    .lowercase()
            }

    val filtered =
        apps.filter {
            val name =
                pm.getApplicationLabel(it)
                    .toString()

            name.contains(
                query,
                true
            ) ||
            it.packageName.contains(
                query,
                true
            )
        }

    if (filtered.isEmpty()) {
        text(
            list,
            when (appFilter) {
                "games" ->
                    "no games found"

                "patched" ->
                    "no patched apps"

                else ->
                    "no apps found"
            },
            13f,
            gray
        )

        return
    }

    for (info in filtered) {
        appRow(
            list,
            info
        )
    }
}

private fun appRow(
    list: LinearLayout,
    info: ApplicationInfo
) {
    val pm = packageManager

    val name =
        pm.getApplicationLabel(info)
            .toString()

    val pkg =
        info.packageName

    val row = LinearLayout(this)

    row.orientation =
        LinearLayout.VERTICAL

    row.setPadding(
        0,
        dp(12),
        0,
        dp(12)
    )

    val top = LinearLayout(this)

    top.orientation =
        LinearLayout.HORIZONTAL

    top.gravity =
        Gravity.CENTER_VERTICAL

    val names = LinearLayout(this)

    names.orientation =
        LinearLayout.VERTICAL

    text(
        names,
        name,
        14f,
        white,
        true
    )

    text(
        names,
        pkg,
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

    val open =
        smallOutlineButton(
            "OPEN"
        )

    open.setOnClickListener {
        openPackage(pkg)
    }

    top.addView(
        open,
        LinearLayout.LayoutParams(
            dp(62),
            dp(34)
        )
    )

    spaceHorizontal(
        top,
        6
    )

    val settings =
        smallOutlineButton(
            "SETTINGS"
        )

    settings.setOnClickListener {
        appSettings(
            name,
            pkg
        )
    }

    top.addView(
        settings,
        LinearLayout.LayoutParams(
            dp(75),
            dp(34)
        )
    )

    row.addView(top)

    space(
        row,
        8
    )

    val patched =
        prefs.getBoolean(
            "patched_$pkg",
            false
        )

    val patch =
        smallOutlineButton(
            if (patched) {
                "PATCHED"
            } else {
                "PATCH"
            }
        )

    patch.setOnClickListener {
        if (!patched) {
            patchTerminal(
                name,
                pkg
            )
        } else {
            patchedApp(
                name,
                pkg
            )
        }
    }

    row.addView(
        patch,
        LinearLayout.LayoutParams(
            -1,
            dp(34)
        )
    )

    line(row)

    list.addView(row)
}

private fun appSettings(
    name: String,
    pkg: String
) {
    selectedPackage = pkg
    selectedName = name

    page.removeAllViews()
    header()

    val scroll = ScrollView(this)

    val content = LinearLayout(this)

    content.orientation =
        LinearLayout.VERTICAL

    content.setPadding(
        dp(20),
        dp(20),
        dp(20),
        dp(30)
    )

    text(
        content,
        "app settings",
        20f,
        white,
        true
    )

    space(content, 6)

    text(
        content,
        name,
        15f,
        white
    )

    text(
        content,
        pkg,
        11f,
        gray
    )

    space(content, 25)

    settingAction(
        content,
        "OPEN"
    ) {
        openPackage(pkg)
    }

    settingAction(
        content,
        "FORCE STOP"
    ) {
        openSystemAppInfo(pkg)
    }

    settingAction(
        content,
        "APP INFO"
    ) {
        openSystemAppInfo(pkg)
    }

    settingAction(
        content,
        "UNINSTALL"
    ) {
        uninstallPackage(pkg)
    }

    space(content, 20)

    text(
        content,
        "package",
        11f,
        gray,
        true
    )

    space(content, 7)

    text(
        content,
        pkg,
        13f,
        white
    )

    scroll.addView(content)

    page.addView(
        scroll,
        LinearLayout.LayoutParams(
            -1,
            0,
            1f
        )
    )
}

private fun patchTerminal(
    name: String,
    pkg: String
) {
    page.removeAllViews()
    header()

    val scroll = ScrollView(this)

    val content = LinearLayout(this)

    content.orientation =
        LinearLayout.VERTICAL

    content.setPadding(
        dp(16),
        dp(18),
        dp(16),
        dp(30)
    )

    text(
        content,
        "patch",
        20f,
        white,
        true
    )

    space(content, 7)

    text(
        content,
        name,
        13f,
        gray
    )

    space(content, 18)

    val terminal = bordered()

    val output = TextView(this)

    output.text =
        "> unlockr patcher\n" +
        "> selected: $name\n" +
        "> package: $pkg\n" +
        "> checking adb...\n" +
        "> preparing patch environment...\n" +
        "> ready\n"

    output.textSize = 12f
    output.setTextColor(white)
    output.typeface = mono()

    output.setPadding(
        dp(14),
        dp(14),
        dp(14),
        dp(14)
    )

    terminal.addView(
        output,
        LinearLayout.LayoutParams(
            -1,
            dp(260)
        )
    )

    content.addView(terminal)

    space(content, 18)

    button(
        content,
        "START PATCH"
    ) {
        runPatchDemo(
            output,
            name,
            pkg
        )
    }

    space(content, 8)

    button(
        content,
        "CANCEL"
    ) {
        apps()
    }

    scroll.addView(content)

    page.addView(
        scroll,
        LinearLayout.LayoutParams(
            -1,
            0,
            1f
        )
    )
}

private fun runPatchDemo(
    output: TextView,
    name: String,
    pkg: String
) {
    val lines =
        arrayOf(
            "> locating apk...",
            "> reading package...",
            "> checking architecture...",
            "> preparing patched build...",
            "> rebuilding package...",
            "> signing patched package...",
            "> installing patched package...",
            "> patch complete"
        )

    var index = 0

    output.text =
        "> unlockr patcher\n" +
        "> selected: $name\n" +
        "> package: $pkg\n"

    val handler =
        android.os.Handler(
            android.os.Looper.getMainLooper()
        )

    val runnable =
        object : Runnable {

            override fun run() {
                if (
                    index <
                    lines.size
                ) {
                    output.append(
                        lines[index] +
                        "\n"
                    )

                    index++

                    handler.postDelayed(
                        this,
                        550
                    )
                } else {
                    prefs.edit()
                        .putBoolean(
                            "patched_$pkg",
                            true
                        )
                        .apply()

                    Toast.makeText(
                        this@MainActivity,
                        "patch finished",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    handler.post(runnable)
}

private fun patched() {
    page.removeAllViews()
    header()

    val scroll = ScrollView(this)

    val content = LinearLayout(this)

    content.orientation =
        LinearLayout.VERTICAL

    content.setPadding(
        dp(16),
        dp(18),
        dp(16),
        dp(30)
    )

    text(
        content,
        "patched",
        20f,
        white,
        true
    )

    space(content, 5)

    text(
        content,
        "patched apps tracked by unlockr",
        11f,
        gray
    )

    space(content, 18)

    val pm = packageManager

    val patched =
        prefs.all.keys
            .filter {
                it.startsWith(
                    "patched_"
                )
            }
            .map {
                it.removePrefix(
                    "patched_"
                )
            }

    if (patched.isEmpty()) {
        text(
            content,
            "no patched apps",
            13f,
            gray
        )
    } else {
        for (pkg in patched) {
            try {
                val info =
                    pm.getApplicationInfo(
                        pkg,
                        0
                    )

                val name =
                    pm.getApplicationLabel(
                        info
                    ).toString()

                val row =
                    LinearLayout(this)

                row.orientation =
                    LinearLayout.VERTICAL

                row.setPadding(
                    0,
                    dp(12),
                    0,
                    dp(12)
                )

                val top =
                    LinearLayout(this)

                top.orientation =
                    LinearLayout.HORIZONTAL

                top.gravity =
                    Gravity.CENTER_VERTICAL

                val names =
                    LinearLayout(this)

                names.orientation =
                    LinearLayout.VERTICAL

                text(
                    names,
                    name,
                    14f,
                    white,
                    true
                )

                text(
                    names,
                    pkg,
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

                val init =
                    smallOutlineButton(
                        "INIT"
                    )

                init.setOnClickListener {
                    Toast.makeText(
                        this,
                        "frida init: $name",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                top.addView(
                    init,
                    LinearLayout.LayoutParams(
                        dp(52),
                        dp(34)
                    )
                )

                spaceHorizontal(
                    top,
                    5
                )

                val mods =
                    smallOutlineButton(
                        "MODS"
                    )

                mods.setOnClickListener {
                    selectedPackage = pkg
                    selectedName = name
                    mods(name)
                }

                top.addView(
                    mods,
                    LinearLayout.LayoutParams(
                        dp(57),
                        dp(34)
                    )
                )

                spaceHorizontal(
                    top,
                    5
                )

                val open =
                    smallOutlineButton(
                        "OPEN"
                    )

                open.setOnClickListener {
                    openPackage(pkg)
                }

                top.addView(
                    open,
                    LinearLayout.LayoutParams(
                        dp(57),
                        dp(34)
                    )
                )

                row.addView(top)

                space(row, 7)

                val settings =
                    smallOutlineButton(
                        "SETTINGS"
                    )

                settings.setOnClickListener {
                    appSettings(
                        name,
                        pkg
                    )
                }

                row.addView(
                    settings,
                    LinearLayout.LayoutParams(
                        -1,
                        dp(34)
                    )
                )

                content.addView(row)

                line(content)
            } catch (_: Exception) {
            }
        }
    }

    scroll.addView(content)

    page.addView(
        scroll,
        LinearLayout.LayoutParams(
            -1,
            0,
            1f
        )
    )
}

private fun patchedApp(
    name: String,
    pkg: String
) {
    page.removeAllViews()
    header()

    val content = LinearLayout(this)

    content.orientation =
        LinearLayout.VERTICAL

    content.setPadding(
        dp(20),
        dp(20),
        dp(20),
        dp(30)
    )

    text(
        content,
        name,
        20f,
        white,
        true
    )

    text(
        content,
        pkg,
        11f,
        gray
    )

    space(content, 25)

    text(
        content,
        "status",
        11f,
        gray,
        true
    )

    space(content, 6)

    text(
        content,
        "patched",
        14f,
        white
    )

    space(content, 22)

    button(
        content,
        "INIT"
    ) {
        Toast.makeText(
            this,
            "frida init: $name",
            Toast.LENGTH_SHORT
        ).show()
    }

    space(content, 8)

    button(
        content,
        "MODS"
    ) {
        selectedPackage = pkg
        selectedName = name
        mods(name)
    }

    space(content, 8)

    button(
        content,
        "OPEN"
    ) {
        openPackage(pkg)
    }

    space(content, 8)

    button(
        content,
        "OPEN SETTINGS"
    ) {
        appSettings(
            name,
            pkg
        )
    }

    page.addView(
        content,
        LinearLayout.LayoutParams(
            -1,
            0,
            1f
        )
    )
}

private fun mods(
    appName: String = ""
) {
    page.removeAllViews()
    header()

    val scroll = ScrollView(this)

    val content = LinearLayout(this)

    content.orientation =
        LinearLayout.VERTICAL

    content.setPadding(
        dp(20),
        dp(20),
        dp(20),
        dp(30)
    )

    text(
        content,
        "mods",
        20f,
        white,
        true
    )

    if (appName.isNotEmpty()) {
        space(content, 5)

        text(
            content,
            appName,
            12f,
            gray
        )
    }

    space(content, 22)

    button(
        content,
        "IMPORT MOD"
    ) {
        val intent =
            Intent(
                Intent.ACTION_OPEN_DOCUMENT
            )

        intent.type =
            "text/javascript"

        intent.addCategory(
            Intent.CATEGORY_OPENABLE
        )

        startActivityForResult(
            intent,
            42
        )
    }

    space(content, 18)

    text(
        content,
        "loaded mods",
        11f,
        gray,
        true
    )

    space(content, 8)

    text(
        content,
        "no mods loaded",
        13f,
        gray
    )

    scroll.addView(content)

    page.addView(
        scroll,
        LinearLayout.LayoutParams(
            -1,
            0,
            1f
        )
    )
}

private fun terminal() {
    page.removeAllViews()
    header()

    val content = LinearLayout(this)

    content.orientation =
        LinearLayout.VERTICAL

    content.setPadding(
        dp(16),
        dp(18),
        dp(16),
        dp(30)
    )

    text(
        content,
        "terminal",
        20f,
        white,
        true
    )

    space(content, 15)

    terminalBox(
        content,
        "> unlockr terminal\n" +
        "> android environment\n" +
        "> ready\n\n" +
        "$ "
    )

    page.addView(
        content,
        LinearLayout.LayoutParams(
            -1,
            0,
            1f
        )
    )
}

private fun device() {
    page.removeAllViews()
    header()

    val scroll = ScrollView(this)

    val content = LinearLayout(this)

    content.orientation =
        LinearLayout.VERTICAL

    content.setPadding(
        dp(20),
        dp(20),
        dp(20),
        dp(30)
    )

    text(
        content,
        "device",
        20f,
        white,
        true
    )

    space(content, 20)

    val values =
        arrayOf(
            "model" to
                android.os.Build.MODEL,

            "manufacturer" to
                android.os.Build.MANUFACTURER,

            "android" to
                android.os.Build.VERSION.RELEASE,

            "sdk" to
                android.os.Build.VERSION.SDK_INT
                    .toString(),

            "package" to
                packageName
        )

    for ((key, value) in values) {
        val row = LinearLayout(this)

        row.orientation =
            LinearLayout.HORIZONTAL

        row.setPadding(
            0,
            dp(10),
            0,
            dp(10)
        )

        text(
            row,
            key,
            12f,
            gray
        )

        val valueText =
            TextView(this)

        valueText.text = value
        valueText.textSize = 12f
        valueText.setTextColor(white)
        valueText.typeface = mono()
        valueText.gravity =
            Gravity.RIGHT

        row.addView(
            valueText,
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        content.addView(row)

        line(content)
    }

    scroll.addView(content)

    page.addView(
        scroll,
        LinearLayout.LayoutParams(
            -1,
            0,
            1f
        )
    )
}

private fun openPackage(
    pkg: String
) {
    try {
        val intent =
            packageManager
                .getLaunchIntentForPackage(
                    pkg
                )

        if (intent != null) {
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                "app cannot be opened",
                Toast.LENGTH_SHORT
            ).show()
        }
    } catch (_: Exception) {
        Toast.makeText(
            this,
            "unable to open app",
            Toast.LENGTH_SHORT
        ).show()
    }
}

private fun openSystemAppInfo(
    pkg: String
) {
    try {
        val intent =
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse(
                    "package:$pkg"
                )
            )

        startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(
            this,
            "unable to open app info",
            Toast.LENGTH_SHORT
        ).show()
    }
}

private fun uninstallPackage(
    pkg: String
) {
    try {
        val intent =
            Intent(
                Intent.ACTION_DELETE,
                Uri.parse(
                    "package:$pkg"
                )
            )

        startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(
            this,
            "unable to uninstall",
            Toast.LENGTH_SHORT
        ).show()
    }
}

private fun openDrawer() {
    drawer.removeAllViews()

    text(
        drawer,
        "unlockr",
        20f,
        white,
        true
    )

    space(drawer, 28)

    drawerItem("apps") {
        closeDrawer()
        apps()
    }

    drawerItem("patched") {
        closeDrawer()
        patched()
    }

    drawerItem("mods") {
        closeDrawer()
        mods()
    }

    drawerItem("terminal") {
        closeDrawer()
        terminal()
    }

    drawerItem("device") {
        closeDrawer()
        device()
    }

    space(drawer, 20)

    line(drawer)

    space(drawer, 20)

    drawerItem("wireless adb") {
        closeDrawer()
        adbSetup()
    }

    drawer.visibility = View.VISIBLE
    overlay.visibility = View.VISIBLE
}

private fun closeDrawer() {
    drawer.visibility = View.GONE
    overlay.visibility = View.GONE
}

private fun drawerItem(
    label: String,
    action: () -> Unit
) {
    val item = TextView(this)

    item.text = label
    item.textSize = 14f
    item.setTextColor(white)
    item.typeface = mono()
    item.gravity =
        Gravity.CENTER_VERTICAL

    item.setPadding(
        0,
        dp(15),
        0,
        dp(15)
    )

    item.setOnClickListener {
        action()
    }

    drawer.addView(
        item,
        LinearLayout.LayoutParams(
            -1,
            dp(48)
        )
    )
}

private fun settingAction(
    parent: LinearLayout,
    label: String,
    action: () -> Unit
) {
    val button = TextView(this)

    button.text = label
    button.textSize = 12f
    button.setTextColor(white)

    button.typeface =
        Typeface.create(
            mono(),
            Typeface.BOLD
        )

    button.gravity =
        Gravity.CENTER

    val bg =
        android.graphics.drawable
            .GradientDrawable()

    bg.setColor(black)

    bg.setStroke(
        dp(1),
        darkGray
    )

    button.background = bg

    button.setOnClickListener {
        action()
    }

    parent.addView(
        button,
        LinearLayout.LayoutParams(
            -1,
            dp(44)
        )
    )

    space(parent, 8)
}

private fun button(
    parent: LinearLayout,
    label: String,
    action: () -> Unit
) {
    val b = TextView(this)

    b.text = label
    b.textSize = 12f
    b.setTextColor(white)

    b.typeface =
        Typeface.create(
            mono(),
            Typeface.BOLD
        )

    b.gravity =
        Gravity.CENTER

    val bg =
        android.graphics.drawable
            .GradientDrawable()

    bg.setColor(black)

    bg.setStroke(
        dp(1),
        darkGray
    )

    b.background = bg

    b.setOnClickListener {
        action()
    }

    parent.addView(
        b,
        LinearLayout.LayoutParams(
            -1,
            dp(44)
        )
    )
}

private fun smallButton(
    parent: LinearLayout,
    label: String,
    action: () -> Unit
) {
    val b = TextView(this)

    b.text = label
    b.textSize = 11f
    b.setTextColor(gray)
    b.typeface = mono()
    b.gravity =
        Gravity.CENTER

    b.setOnClickListener {
        action()
    }

    parent.addView(
        b,
        LinearLayout.LayoutParams(
            -1,
            dp(38)
        )
    )
}

private fun smallOutlineButton(
    label: String
): TextView {
    val b = TextView(this)

    b.text = label
    b.textSize = 9f
    b.setTextColor(white)

    b.typeface =
        Typeface.create(
            mono(),
            Typeface.BOLD
        )

    b.gravity =
        Gravity.CENTER

    val bg =
        android.graphics.drawable
            .GradientDrawable()

    bg.setColor(black)

    bg.setStroke(
        dp(1),
        darkGray
    )

    b.background = bg

    return b
}

private fun field(
    hint: String,
    value: String
): EditText {
    val e = EditText(this)

    e.hint = hint
    e.setHintTextColor(gray)
    e.setTextColor(white)
    e.textSize = 12f
    e.typeface = mono()
    e.setSingleLine(true)
    e.setText(value)

    e.setPadding(
        dp(12),
        0,
        dp(12),
        0
    )

    val bg =
        android.graphics.drawable
            .GradientDrawable()

    bg.setColor(black)

    bg.setStroke(
        dp(1),
        darkGray
    )

    e.background = bg

    return e
}

private fun bordered(): LinearLayout {
    val box = LinearLayout(this)

    box.orientation =
        LinearLayout.VERTICAL

    val bg =
        android.graphics.drawable
            .GradientDrawable()

    bg.setColor(black)

    bg.setStroke(
        dp(1),
        darkGray
    )

    box.background = bg

    box.setPadding(
        dp(14),
        dp(12),
        dp(14),
        dp(12)
    )

    return box
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
    output.typeface = mono()

    box.addView(
        output,
        LinearLayout.LayoutParams(
            -1,
            dp(180)
        )
    )

    parent.addView(
        box,
        LinearLayout.LayoutParams(
            -1,
            dp(205)
        )
    )
}

private fun text(
    parent: ViewGroup,
    value: String,
    size: Float,
    color: Int,
    bold: Boolean = false
) {
    val t = TextView(this)

    t.text = value
    t.textSize = size
    t.setTextColor(color)

    t.typeface =
        if (bold) {
            Typeface.create(
                mono(),
                Typeface.BOLD
            )
        } else {
            mono()
        }

    parent.addView(
        t,
        LinearLayout.LayoutParams(
            -1,
            -2
        )
    )
}

private fun line(
    parent: LinearLayout
) {
    val v = View(this)

    v.setBackgroundColor(
        darkGray
    )

    parent.addView(
        v,
        LinearLayout.LayoutParams(
            -1,
            dp(1)
        )
    )
}

private fun space(
    parent: LinearLayout,
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
    parent: LinearLayout,
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
        drawer.visibility ==
        View.VISIBLE
    ) {
        closeDrawer()
    } else {
        apps()
    }
}

}
