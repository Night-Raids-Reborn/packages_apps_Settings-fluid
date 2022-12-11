/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.network;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.BasePreferenceController;

public class ActiveSimPreferenceController extends BasePreferenceController
        implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private static final String TAG = "ActiveSimPreferenceController";

    private static final String PREFERENCE_KEY = "active_sim_settings";
    private static final String ACTIVE_SIM_MODE_PROPERTY = "persist.sim.activesim";
    private static final String ACTIVE_SIM_DEFAULT_INDEX = "0";

    private final String[] mListValues;
    private final String[] mListEntries;

    private TelephonyManager mTelephonyManager;
    private SubscriptionManager mSubscriptionManager;
    private Preference mPreference;

    public ActiveSimPreferenceController(Context context) {
        super(context, PREFERENCE_KEY);
        mTelephonyManager = context.getSystemService(TelephonyManager.class);
        mSubscriptionManager = context.getSystemService(SubscriptionManager.class);
        mListValues = context.getResources().getStringArray(R.array.active_sim_values);
        mListEntries = context.getResources().getStringArray(R.array.active_sim_entries);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);

        mPreference = screen.findPreference(getPreferenceKey());
        if (SubscriptionUtil.showToggleForPhysicalSim(mSubscriptionManager)) {
            mPreference.setVisible(false);
        } else {
            mPreference.setVisible(true);
        }
    }

    public String getDefaultModeIndex() {
        return ACTIVE_SIM_DEFAULT_INDEX;
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public String getPreferenceKey() {
        return PREFERENCE_KEY;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue = Integer.valueOf(newValue.toString());
        SystemProperties.set(ACTIVE_SIM_MODE_PROPERTY, newValue.toString());
        switch (intValue) {
            case 0:
                mTelephonyManager.setSimPowerStateForSlot(0, 1);
                mTelephonyManager.setSimPowerStateForSlot(1, 1);
                break;
            case 1:
                mTelephonyManager.setSimPowerStateForSlot(0, 1);
                mTelephonyManager.setSimPowerStateForSlot(1, 0);
                break;
            case 2:
                mTelephonyManager.setSimPowerStateForSlot(0, 0);
                mTelephonyManager.setSimPowerStateForSlot(1, 1);
                break;
            case 3:
                mTelephonyManager.setSimPowerStateForSlot(0, 0);
                mTelephonyManager.setSimPowerStateForSlot(1, 0);
                break;
            default:
                mTelephonyManager.setSimPowerStateForSlot(0, 1);
                mTelephonyManager.setSimPowerStateForSlot(1, 1);
                break;
        }
        updateState(mPreference);
        return true;
    }

    @Override
    public void updateState(Preference preference) {
        final ListPreference listPreference = (ListPreference) preference;
        final String currentValue = SystemProperties.get(ACTIVE_SIM_MODE_PROPERTY, getDefaultModeIndex());

        int index = Integer.valueOf(getDefaultModeIndex());
        for (int i = 0; i < mListValues.length; i++) {
            if (TextUtils.equals(currentValue, mListValues[i])) {
                index = i;
                break;
            }
        }
        listPreference.setValue(mListValues[index]);
        listPreference.setSummary(mListEntries[index]);
    }
}
