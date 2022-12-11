/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SettingsOnBoot extends BroadcastReceiver {
    private static final String ACTIVE_SIM_MODE_PROPERTY = "persist.sim.activesim";

    @Override
    public void onReceive(Context context, Intent broadcast) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        activeSimSettingSetup(context, tm);
    }

    private void activeSimSettingSetup(Context context, TelephonyManager tm) {
        if (tm == null) {
            return;
        }
        final String strValue = SystemProperties.get(ACTIVE_SIM_MODE_PROPERTY);
        final int intValue = Integer.valueOf(strValue);
        switch (intValue) {
            case 0:
                tm.setSimPowerStateForSlot(0, 1);
                tm.setSimPowerStateForSlot(1, 1);
                break;
            case 1:
                tm.setSimPowerStateForSlot(0, 1);
                tm.setSimPowerStateForSlot(1, 0);
                break;
            case 2:
                tm.setSimPowerStateForSlot(0, 0);
                tm.setSimPowerStateForSlot(1, 1);
                break;
            case 3:
                tm.setSimPowerStateForSlot(0, 0);
                tm.setSimPowerStateForSlot(1, 0);
                break;
            default:
                tm.setSimPowerStateForSlot(0, 1);
                tm.setSimPowerStateForSlot(1, 1);
                break;
        }
    }
}
