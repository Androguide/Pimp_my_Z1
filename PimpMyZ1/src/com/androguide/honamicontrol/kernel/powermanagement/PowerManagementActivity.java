/**   Copyright (C) 2013  Louis Teboul (a.k.a Androguide)
 *
 *    admin@pimpmyrom.org  || louisteboul@gmail.com
 *    http://pimpmyrom.org || http://androguide.fr
 *    71 quai Clémenceau, 69300 Caluire-et-Cuire, FRANCE.
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License along
 *      with this program; if not, write to the Free Software Foundation, Inc.,
 *      51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 **/

package com.androguide.honamicontrol.kernel.powermanagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.helpers.CMDProcessor.CMDProcessor;
import com.androguide.honamicontrol.helpers.CPUHelper;
import com.androguide.honamicontrol.helpers.Helpers;

import java.util.ArrayList;

public class PowerManagementActivity extends ActionBarActivity implements PowerManagementInterface {

    private int spinnerCounter = 0, ecoCoresCounter = 0, alucardCoresCounter = 0, hotplugCounter = 0;
    private Boolean isIntelliPlugOn;
    private LinearLayout mCardIntelliEco, mCardIntelliCores, mCardAlucardCores;
    private Spinner mEcoCoresSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_tools_power_management));
        setContentView(R.layout.card_power_management);
        final SharedPreferences bootPrefs = getSharedPreferences("BOOT_PREFS", 0);

        // Sched MC
        if (Helpers.doesFileExist(SCHED_MC_POWER_SAVINGS)) {
            Spinner schedMcSpinner = (Spinner) findViewById(R.id.sched_mc_spinner);
            ArrayList<String> schedMCEntries = new ArrayList<String>();
            schedMCEntries.add(getString(R.string.disabled));
            schedMCEntries.add(getString(R.string.moderate));
            schedMCEntries.add(getString(R.string.aggressive));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_row, schedMCEntries);
            schedMcSpinner.setAdapter(adapter);
            schedMcSpinner.setSelection(Integer.valueOf(CPUHelper.readOneLineNotRoot(SCHED_MC_POWER_SAVINGS)));
            schedMcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    bootPrefs.edit().putInt("SCHED_MC_LEVEL", i).commit();
                    if (spinnerCounter > 0)
                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + i + " > " + SCHED_MC_POWER_SAVINGS);
                    else
                        spinnerCounter++;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        // Hotplug Driver
        Spinner hotplugDriverSpinner = (Spinner) findViewById(R.id.hotplug_spinner);
        ArrayList<String> availableDrivers = new ArrayList<String>();
        availableDrivers.add("MPDecision");
        if (Helpers.doesFileExist(INTELLI_PLUG_TOGGLE))
            availableDrivers.add("Intelliplug");
        if (Helpers.doesFileExist(ALUCARD_HOTPLUG_TOGGLE))
            availableDrivers.add("Alucard Hotplug");
        ArrayAdapter<String> hotplugAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row, availableDrivers);
        hotplugDriverSpinner.setAdapter(hotplugAdapter);
        int intelliState = Integer.parseInt(CPUHelper.readOneLineNotRoot(INTELLI_PLUG_TOGGLE));
        int alucardState = Integer.parseInt(CPUHelper.readOneLineNotRoot(ALUCARD_HOTPLUG_TOGGLE));

        try {
            if (intelliState == 0 && alucardState == 0) {
                hotplugDriverSpinner.setSelection(0);
            } else if (intelliState == 1 && alucardState == 0) {
                hotplugDriverSpinner.setSelection(1);
            } else if (intelliState == 0 && alucardState == 1) {
                hotplugDriverSpinner.setSelection(2);
            } else {
                Toast.makeText(this, "It appears you have several hotplug drivers enabled! Switching back to MPDecision...", Toast.LENGTH_LONG).show();
                CMDProcessor.runSuCommand("echo 0 > " + INTELLI_PLUG_TOGGLE);
                CMDProcessor.runSuCommand("echo 0 > " + ALUCARD_HOTPLUG_TOGGLE);
                CMDProcessor.runSuCommand("start mpdecision");
                hotplugDriverSpinner.setSelection(0);
            }

        } catch (Exception e) {
            Log.e("PowerManagement", e.getMessage());
        }

        hotplugDriverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                bootPrefs.edit().putInt("HOTPLUG_DRIVER", pos).commit();
                switch (pos) {
                    case 0:
                        mCardIntelliEco.setVisibility(View.GONE);
                        mCardIntelliCores.setVisibility(View.GONE);
                        mCardAlucardCores.setVisibility(View.GONE);
                        isIntelliPlugOn = false;
                        if (hotplugCounter > 0) {
                            CMDProcessor.runSuCommand("echo 0 > " + INTELLI_PLUG_TOGGLE);
                            CMDProcessor.runSuCommand("echo 0 > " + ALUCARD_HOTPLUG_TOGGLE);
                            CMDProcessor.runSuCommand("start mpdecision");
                        } else hotplugCounter++;
                        break;
                    case 1:
                        mCardAlucardCores.setVisibility(View.GONE);
                        mCardIntelliEco.setVisibility(View.VISIBLE);
                        mCardIntelliCores.setVisibility(View.VISIBLE);
                        isIntelliPlugOn = true;
                        if (hotplugCounter > 0) {
                            CMDProcessor.runSuCommand("echo 0 > " + ALUCARD_HOTPLUG_TOGGLE);
                            CMDProcessor.runSuCommand("stop mpdecision");
                            CMDProcessor.runSuCommand("echo 1 > " + INTELLI_PLUG_TOGGLE);
                        } else hotplugCounter++;
                        break;
                    case 2:
                        mCardIntelliEco.setVisibility(View.GONE);
                        mCardIntelliCores.setVisibility(View.GONE);
                        mCardAlucardCores.setVisibility(View.VISIBLE);
                        isIntelliPlugOn = false;
                        if (hotplugCounter > 0) {
                            CMDProcessor.runSuCommand("echo 0 > " + INTELLI_PLUG_TOGGLE);
                            CMDProcessor.runSuCommand("stop mpdecision");
                            CMDProcessor.runSuCommand("echo 1 > " + ALUCARD_HOTPLUG_TOGGLE);
                        } else hotplugCounter++;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Intelliplug Eco Mode
        if (Helpers.doesFileExist(INTELLI_PLUG_ECO_MODE)) {
            mCardIntelliEco = (LinearLayout) findViewById(R.id.card_intelliplug_eco_mode);
            final Switch intelliEcoSwitch = (Switch) findViewById(R.id.intelliplug_eco_switch);
            int currEcoState = Integer.parseInt(CPUHelper.readOneLineNotRoot(INTELLI_PLUG_ECO_MODE));

            if (currEcoState == 0) {
                intelliEcoSwitch.setChecked(false);
                findViewById(R.id.intelliplug_eco_cores_spinner).setEnabled(false);
            } else if (currEcoState == 1) {
                intelliEcoSwitch.setChecked(true);
                findViewById(R.id.intelliplug_eco_cores_spinner).setEnabled(true);
            }

            intelliEcoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isOn) {
                    bootPrefs.edit().putBoolean("INTELLI_PLUG_ECO", isOn).commit();

                    if (isOn) {
                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 1 > " + INTELLI_PLUG_ECO_MODE);
                        findViewById(R.id.intelliplug_eco_cores_spinner).setEnabled(true);
                    } else {
                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + INTELLI_PLUG_ECO_MODE);
                        findViewById(R.id.intelliplug_eco_cores_spinner).setEnabled(false);
                    }
                }
            });
        }

        // Intelliplug eco cores
        if (Helpers.doesFileExist(INTELLI_PLUG_ECO_CORES)) {
            mCardIntelliCores = (LinearLayout) findViewById(R.id.card_intelliplug_eco_cores);
            Spinner intelliEcoCoresSpinner = (Spinner) findViewById(R.id.intelliplug_eco_cores_spinner);
            mEcoCoresSpinner = intelliEcoCoresSpinner;
            ArrayList<String> possibleEcoCores = new ArrayList<String>();
            possibleEcoCores.add("1");
            possibleEcoCores.add("2");
            possibleEcoCores.add("3");
            ArrayAdapter<String> ecoCoresAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row, possibleEcoCores);
            intelliEcoCoresSpinner.setAdapter(ecoCoresAdapter);
            intelliEcoCoresSpinner.setSelection(Integer.parseInt(CPUHelper.readOneLineNotRoot(INTELLI_PLUG_ECO_CORES)) - 1);
            if (!bootPrefs.getBoolean("INTELLI_PLUG_ECO", false))
                intelliEcoCoresSpinner.setEnabled(false);
            intelliEcoCoresSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int toApply = position + 1;
                    if (ecoCoresCounter > 0) {
                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + toApply + " > " + INTELLI_PLUG_ECO_CORES);
                        bootPrefs.edit().putInt("INTELLI_PLUG_ECO_CORES", toApply).commit();
                    } else {
                        ecoCoresCounter++;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        // Alucard eco cores
        if (Helpers.doesFileExist(ALUCARD_HOTPLUG_CORES)) {
            mCardAlucardCores = (LinearLayout) findViewById(R.id.card_alucard_cores);
            final Spinner alucardCoresSpinner = (Spinner) findViewById(R.id.alucard_cores_spinner);
            mEcoCoresSpinner = alucardCoresSpinner;
            ArrayList<String> possibleEcoCores = new ArrayList<String>();
            possibleEcoCores.add("1");
            possibleEcoCores.add("2");
            possibleEcoCores.add("3");
            possibleEcoCores.add("4");
            ArrayAdapter<String> ecoCoresAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row, possibleEcoCores);
            alucardCoresSpinner.setAdapter(ecoCoresAdapter);
            alucardCoresSpinner.setSelection(Integer.parseInt(CPUHelper.readOneLineNotRoot(ALUCARD_HOTPLUG_CORES)) - 1);
            alucardCoresSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int toApply = position + 1;
                    if (alucardCoresCounter > 0) {
                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + toApply + " > " + ALUCARD_HOTPLUG_CORES);
                        bootPrefs.edit().putInt("ALUCARD_CORES", toApply).commit();
                    } else {
                        alucardCoresCounter++;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        // Power Suspend
        if (Helpers.doesFileExist(POWER_SUSPEND_TOGGLE)) {
            Switch powerSuspendSwitch = (Switch) findViewById(R.id.power_suspend_switch);
            int isPowerSuspendOn = Integer.parseInt(CPUHelper.readOneLineNotRoot(POWER_SUSPEND_TOGGLE));
            if (isPowerSuspendOn == 0)
                powerSuspendSwitch.setChecked(false);
            else if (isPowerSuspendOn == 1)
                powerSuspendSwitch.setChecked(true);

            powerSuspendSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    bootPrefs.edit().putBoolean("POWER_SUSPEND", isChecked).commit();
                    if (isChecked)
                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 1 > " + POWER_SUSPEND_TOGGLE);
                    else {
                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + POWER_SUSPEND_TOGGLE);
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
