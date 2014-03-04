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

package com.androguide.honamicontrol.kernel.iotweaks;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.cards.CardSpinner;
import com.androguide.honamicontrol.cards.CardSwitchDisabled;
import com.androguide.honamicontrol.cards.CardSwitchPlugin;
import com.androguide.honamicontrol.helpers.CPUHelper;
import com.androguide.honamicontrol.helpers.Helpers;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

import java.util.ArrayList;
import java.util.Arrays;

public class IOTweaksActivity extends ActionBarActivity implements IOTweaksInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_tools_io_tweaks));
        setContentView(R.layout.cardsui);

        final SharedPreferences bootPrefs = getSharedPreferences("BOOT_PREFS", 0);
        CardUI cardsUI = (CardUI) findViewById(R.id.cardsui);
        cardsUI.addStack(new CardStack(""));
        cardsUI.addStack(new CardStack(""));

        if (Helpers.doesFileExist(DYNAMIC_FSYNC_TOGGLE)) {
            cardsUI.addCard(new CardSwitchPlugin(
                    getString(R.string.dynamic_fsync),
                    getString(R.string.dynamic_fsync_desc) +
                            getString(R.string.dynamic_fsync_version) + CPUHelper.readOneLine(DYNAMIC_FSYNC_VERSION) + "\'",
                    "#1abc9c",
                    DYNAMIC_FSYNC_TOGGLE,
                    this,
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                            bootPrefs.edit().putBoolean("DYNAMIC_FSYNC", isOn).commit();
                            if (isOn)
                                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 1 > " + DYNAMIC_FSYNC_TOGGLE);
                            else
                                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + DYNAMIC_FSYNC_TOGGLE);
                        }
                    }
            ));
        } else {
            cardsUI.addCard(new CardSwitchDisabled(
                    getString(R.string.dynamic_fsync),
                    "Sorry, your kernel does not seem to support Dynamic Fsync.",
                    "#c74b46",
                    "",
                    this,
                    null)
            );
        }

        if (Helpers.doesFileExist(IO_SCHEDULER)) {
            final ArrayList<String> ioScheds = CPUHelper.getAvailableIOSchedulers();
            cardsUI.addCard(new CardSpinner("I/O Scheduler (eMMC)",
                    "Defines the I/O Scheduler to use for the internal storage",
                    "#1abc9c",
                    IO_SCHEDULER,
                    ioScheds.indexOf(CPUHelper.getIOScheduler()),
                    ioScheds,
                    this,
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + ioScheds.get(position) + " > " + IO_SCHEDULER);
                            bootPrefs.edit().putString("IO_SCHEDULER", ioScheds.get(position)).commit();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    }));
        }

        if (Helpers.doesFileExist(IO_SCHEDULER_SD)) {
            final ArrayList<String> ioScheds = CPUHelper.getAvailableIOSchedulersSD();
            cardsUI.addCard(new CardSpinner("I/O Scheduler (SD-Card)",
                    "Defines the I/O Scheduler to use for the external storage/SD-Card",
                    "#1abc9c",
                    IO_SCHEDULER_SD,
                    ioScheds.indexOf(CPUHelper.getIOSchedulerSD()),
                    ioScheds,
                    this,
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + ioScheds.get(position) + " > " + IO_SCHEDULER_SD);
                            bootPrefs.edit().putString("IO_SCHEDULER_SD", ioScheds.get(position)).commit();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    }));
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

}
