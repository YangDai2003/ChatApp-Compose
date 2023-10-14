package com.yangdai.chatapp.main

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.elevation.SurfaceColors
import com.yangdai.chatapp.R


class SettingsActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {

    private var defaultSharedPrefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = SurfaceColors.SURFACE_0.getColor(this)
        setContentView(R.layout.settings_activity)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)))
        supportActionBar!!.elevation = 0f
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        defaultSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        defaultSharedPrefs!!.registerOnSharedPreferenceChangeListener(this)
        if (defaultSharedPrefs!!.getBoolean("screen", false)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish() // 处理返回逻辑，例如关闭当前活动
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        defaultSharedPrefs!!.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {

    }

    class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {

        private var themePref: Preference? = null
        private var languagePref: Preference? = null
        private var defaultSharedPrefs: SharedPreferences? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            defaultSharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            themePref = findPreference("theme")
            languagePref = findPreference("language")
            languagePref?.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            languagePref?.onPreferenceClickListener = this
            themePref?.onPreferenceClickListener = this
        }

        override fun onPreferenceClick(preference: Preference): Boolean {
            if ("theme" == preference.key) {
                val editor = defaultSharedPrefs!!.edit()
                val orig = defaultSharedPrefs!!.getInt("themeSetting", 2)
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.theme))
                    .setSingleChoiceItems(
                        arrayOf(
                            getString(R.string.light),
                            getString(R.string.dark),
                            getString(R.string.systemTheme)
                        ),
                        defaultSharedPrefs!!.getInt("themeSetting", 2)
                    ) { _: DialogInterface, which: Int ->
                        editor.putInt("themeSetting", which)
                        editor.apply()
                    }
                    .setPositiveButton(
                        android.R.string.ok
                    ) { _, _ ->
                        when (defaultSharedPrefs!!.getInt("themeSetting", 2)) {
                            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                            else -> {}
                        }
                    }
                    .setCancelable(true)
                    .setNegativeButton(
                        android.R.string.cancel
                    ) { dialog, _ ->
                        editor.putInt("themeSetting", orig)
                        editor.apply()
                        dialog.dismiss()
                    }
                    .show()
            } else if ("language" == preference.key) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    try {
                        val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
                        intent.data =
                            Uri.fromParts("package", requireContext().packageName, null)
                        startActivity(intent)
                    } catch (e: Exception) {
                        try {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data =
                                Uri.fromParts("package", requireContext().packageName, null)
                            startActivity(intent)
                        } catch (ignored: Exception) {
                        }
                    }
                }
            }
            return true
        }
    }
}