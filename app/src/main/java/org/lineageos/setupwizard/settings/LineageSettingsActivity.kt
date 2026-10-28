/*
 * SPDX-FileCopyrightText: 2016 The CyanogenMod Project
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.setupwizard.settings

import android.content.Context
import android.os.Bundle
import android.os.UserHandle
import lineageos.hardware.LineageHardwareManager
import lineageos.providers.LineageSettings
import org.lineageos.setupwizard.DISABLE_NAV_KEYS
import org.lineageos.setupwizard.R
import org.lineageos.setupwizard.SetupWizardApp
import org.lineageos.setupwizard.base.BaseSetupWizardActivity

class LineageSettingsActivity : BaseSetupWizardActivity() {

    private val navKeysItem by lazy { toggle(R.id.nav_keys_item) }

    private var supportsKeyDisabler = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportsKeyDisabler = isKeyDisablerSupported(this)
        navKeysItem.isVisible = supportsKeyDisabler
        navKeysItem.setOnCheckedChangeListener { isChecked ->
            SetupWizardApp.settingsBundle.putBoolean(DISABLE_NAV_KEYS, isChecked)
        }
    }

    override fun onResume() {
        super.onResume()
        updateDisableNavkeysOption()
    }

    override val layoutResId = R.layout.setup_lineage_settings

    override val titleResId = R.string.setup_services

    override val iconResId = R.drawable.logo

    override val itemEntriesResId = R.xml.lineage_settings_items

    private fun updateDisableNavkeysOption() {
        if (supportsKeyDisabler) {
            val myPageBundle = SetupWizardApp.settingsBundle
            val enabled =
                LineageSettings.System.getIntForUser(
                    contentResolver,
                    LineageSettings.System.FORCE_SHOW_NAVBAR,
                    0,
                    UserHandle.USER_CURRENT,
                ) != 0
            val checked =
                if (myPageBundle.containsKey(DISABLE_NAV_KEYS)) {
                    myPageBundle.getBoolean(DISABLE_NAV_KEYS)
                } else {
                    enabled
                }
            navKeysItem.isChecked = checked
            myPageBundle.putBoolean(DISABLE_NAV_KEYS, checked)
        }
    }

    companion object {
        private fun isKeyDisablerSupported(context: Context): Boolean {
            val hardware = LineageHardwareManager.getInstance(context)
            return hardware.isSupported(LineageHardwareManager.FEATURE_KEY_DISABLE)
        }
    }
}
