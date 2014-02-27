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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.cards.CardSpinnerSchedMC;
import com.androguide.honamicontrol.cards.CardSpinnerSchedMCDisabled;
import com.androguide.honamicontrol.cards.CardSwitchDisabled;
import com.androguide.honamicontrol.cards.CardSwitchPlugin;
import com.androguide.honamicontrol.helpers.CPUHelper;
import com.androguide.honamicontrol.helpers.Helpers;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

import java.util.ArrayList;

public class PowerManagementActivity extends ActionBarActivity implements PowerManagementInterface {

    private int spinnerCounter = 0;
    private Boolean isIntelliPlugOn;
    private Switch ecoModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_tools_power_management));
        setContentView(R.layout.cardsui);
        final SharedPreferences bootPrefs = getSharedPreferences("BOOT_PREFS", 0);

        CardUI cardsUI = (CardUI) findViewById(R.id.cardsui);
        cardsUI.addStack(new CardStack(""));
        cardsUI.addStack(new CardStack(""));

        if (Helpers.doesFileExist(SCHED_MC_POWER_SAVINGS)) {
            ArrayList<String> schedMCEntries = new ArrayList<String>();
            schedMCEntries.add(getString(R.string.disabled));
            schedMCEntries.add(getString(R.string.moderate));
            schedMCEntries.add(getString(R.string.aggressive));
            cardsUI.addCard(new CardSpinnerSchedMC(
                    getString(R.string.sched_mc),
                    getString(R.string.sched_mc_desc),
                    "#1abc9c",
                    SCHED_MC_POWER_SAVINGS,
                    Integer.valueOf(CPUHelper.readOneLineNotRoot(SCHED_MC_POWER_SAVINGS)),
                    schedMCEntries,
                    this,
                    new AdapterView.OnItemSelectedListener() {
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
                    }
            ));
        } else {
            ArrayList<String> spinnerEntries = new ArrayList<String>();
            spinnerEntries.add("No Kernel Support");
            cardsUI.addCard(new CardSpinnerSchedMCDisabled(
                    getString(R.string.sched_mc),
                    "Sorry, your kernel does not seem to support Sched_MC power savings",
                    "#c74b46",
                    "",
                    0,
                    spinnerEntries,
                    this,
                    null)
            );
        }

        if (Helpers.doesFileExist(INTELLI_PLUG_TOGGLE)) {
            isIntelliPlugOn = getIsIntelliPlugOn();
            cardsUI.addCard(new CardSwitchPlugin(
                    getString(R.string.intelli_plug),
                    getString(R.string.intelli_plug_desc),
                    "#1abc9c",
                    PowerManagementInterface.INTELLI_PLUG_TOGGLE,
                    this,
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                            isIntelliPlugOn = isOn;
                            bootPrefs.edit().putBoolean("INTELLI_PLUG", isOn).commit();
                            if (isOn)
                                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 1 > " + INTELLI_PLUG_TOGGLE);
                            else {
                                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + INTELLI_PLUG_TOGGLE);
                                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + INTELLI_PLUG_ECO_MODE);
                                if (ecoModeSwitch != null)
                                    ecoModeSwitch.setChecked(false);
                            }
                        }
                    }
            ));
        } else {
            cardsUI.addCard(new CardSwitchDisabled(
                    getString(R.string.intelli_plug),
                    "Sorry, your kernel does not seem to support the Intelli Plug hotplug driver",
                    "#c74b46",
                    "",
                    this,
                    null)
            );
        }

        if (Helpers.doesFileExist(INTELLI_PLUG_ECO_MODE)) {
            CardSwitchPlugin cardEcoMode = new CardSwitchPlugin(
                    getString(R.string.intelli_plug_eco),
                    getString(R.string.intelli_plug_eco_desc),
                    "#1abc9c",
                    PowerManagementInterface.INTELLI_PLUG_ECO_MODE,
                    this,
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                            bootPrefs.edit().putBoolean("INTELLI_PLUG_ECO", isOn).commit();
                            ecoModeSwitch = (Switch) compoundButton;
                            if (isOn && isIntelliPlugOn)
                                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 1 > " + INTELLI_PLUG_ECO_MODE);
                            else if (isOn && !isIntelliPlugOn) {
                                Toast.makeText(PowerManagementActivity.this, "Intelli_plug is not enabled", Toast.LENGTH_LONG).show();
                                compoundButton.setChecked(false);
                            } else
                                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + INTELLI_PLUG_ECO_MODE);
                        }
                    }
            );
            cardsUI.addCard(cardEcoMode);
        } else {
            cardsUI.addCard(new CardSwitchDisabled(
                    getString(R.string.intelli_plug_eco),
                    "Sorry, your kernel does not seem to support the Intelli Plug eco mode",
                    "#c74b46",
                    "",
                    this,
                    null)
            );
        }

        if (Helpers.doesFileExist(POWER_SUSPEND_TOGGLE)) {
            cardsUI.addCard(new CardSwitchPlugin(
                    getString(R.string.power_suspend),
                    getString(R.string.power_suspend_desc),
                    "#1abc9c",
                    PowerManagementInterface.POWER_SUSPEND_TOGGLE,
                    this,
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                            bootPrefs.edit().putBoolean("POWER_SUSPEND", isOn).commit();
                            if (isOn)
                                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 1 > " + POWER_SUSPEND_TOGGLE);
                            else {
                                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + POWER_SUSPEND_TOGGLE);
                            }
                        }
                    }
            ));
        } else {
            cardsUI.addCard(new CardSwitchDisabled(
                    getString(R.string.power_suspend),
                    "Sorry, your kernel does not seem to support the power_suspend Power Management driver",
                    "#c74b46",
                    "",
                    this,
                    null)
            );
        }

        cardsUI.refresh();
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

    private Boolean getIsIntelliPlugOn() {
        return !CPUHelper.readOneLineNotRoot(INTELLI_PLUG_TOGGLE).equals("0");
    }

}
