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

package com.androguide.honamicontrol.kernel.voltagecontrol;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.cards.CardSpinnerVoltage;
import com.androguide.honamicontrol.helpers.CMDProcessor.CMDProcessor;
import com.androguide.honamicontrol.helpers.CPUHelper;
import com.androguide.honamicontrol.helpers.Helpers;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardTextStripe;
import com.fima.cardsui.views.CardUI;

import java.util.ArrayList;

public class VoltageActivity extends ActionBarActivity implements VoltageInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_tools_voltage_control));
        setContentView(R.layout.cardsui);

        final SharedPreferences bootPrefs = getSharedPreferences("BOOT_PREFS", 0);
        CardUI cardsUI = (CardUI) findViewById(R.id.cardsui);
        cardsUI.addStack(new CardStack(""));
        cardsUI.addStack(new CardStack(getString(R.string.voltage_control).toUpperCase()));

        if (Helpers.doesFileExist(UV_MV_TABLE)) {
            cardsUI.addCard(
                    new CardTextStripe(
                            getString(R.string.warning),
                            getString(R.string.voltage_warning_text),
                            getString(R.string.play_orange),
                            getString(R.string.play_orange),
                            false
                    )
            );


            final ArrayList<Integer> applicableVoltages = new ArrayList<Integer>();
            for (int i = 500; i < 1100; i += 5) {
                applicableVoltages.add(i);
            }

            String rawUvTable = CPUHelper.readFileViaShell(UV_MV_TABLE, false);
            String[] splitTable = rawUvTable.split("\n");
            final ArrayList<Integer> currentApplicableTable = new ArrayList<Integer>();

            // Counters to avoid applying voltage when launching the activity
            final int[] spinnerCounters = new int[splitTable.length];
            for (int i = 0; i < splitTable.length; i++)
                spinnerCounters[i] = 0;

            Boolean areDefaultsSaved = false;
            String[] defaultTable = new String[splitTable.length];

            if (!bootPrefs.getString("DEFAULT_VOLTAGE_TABLE", "null").equals("null")) {
                areDefaultsSaved = true;
                defaultTable = bootPrefs.getString("DEFAULT_VOLTAGE_TABLE", "null").split(" ");
                Log.e("Default Table", "Def Table Size: " + defaultTable.length + " // Default Table: " + bootPrefs.getString("DEFAULT_VOLTAGE_TABLE", "null"));
            }

            // for each frequency scaling step we add a card
            for (int i = 0; i < splitTable.length; i++) {
                // format each line into a frequency in MHz & a voltage in mV
                String[] separateFreqFromVolts = splitTable[i].split(":");
                String freqLabel = separateFreqFromVolts[0].replace("mhz", " MHz:");
                String intVoltage = separateFreqFromVolts[1].replaceAll("mV", "");
                intVoltage = intVoltage.replaceAll(" ", "");

                int currStepVoltage = Integer.valueOf(intVoltage);
                currentApplicableTable.add(currStepVoltage);
                ArrayList<String> possibleVoltages = new ArrayList<String>();

                if (areDefaultsSaved) {
                    for (int k = 500; k < 1100; k += 5) {
                        if (k == Integer.valueOf(defaultTable[i]))
                            possibleVoltages.add(k + " mV (default)");
                        else
                            possibleVoltages.add(k + " mV");
                    }

                } else {
                    for (int k = 500; k < 1100; k += 5) {
                        if (k == currStepVoltage)
                            possibleVoltages.add(k + " mV (default)");
                        else
                            possibleVoltages.add(k + " mV");
                    }
                }

                int currIndex = possibleVoltages.indexOf(currStepVoltage + " mV (default)");
                if (currIndex == -1)
                    currIndex = possibleVoltages.indexOf(currStepVoltage + " mV");

                final int currStep = i;

                cardsUI.addCard(new CardSpinnerVoltage(
                        "",
                        freqLabel,
                        "#1abc9c",
                        "",
                        currIndex,
                        possibleVoltages,
                        this,
                        new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                                if (spinnerCounters[currStep] > 0) {
                                    String toApply = "busybox echo \"";
                                    String defaultTable = "";
                                    currentApplicableTable.set(currStep, applicableVoltages.get(pos));

                                    for (int j = 0; j < currentApplicableTable.size(); j++) {
                                        if (j == 0) {
                                            toApply += currentApplicableTable.get(j);
                                            defaultTable += currentApplicableTable.get(j);
                                        } else {
                                            toApply += " " + currentApplicableTable.get(j);
                                            defaultTable += " " + currentApplicableTable.get(j);
                                        }
                                    }

                                    toApply += "\" > " + UV_MV_TABLE;
                                    CMDProcessor.runSuCommand(toApply);
                                    bootPrefs.edit().putString("CURRENT_VOLTAGE_TABLE", defaultTable).commit();
                                    Toast.makeText(VoltageActivity.this, toApply, Toast.LENGTH_LONG).show();
                                    Log.e("toApply", toApply);
                                } else spinnerCounters[currStep]++;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        }
                ));
            }

            if (bootPrefs.getString("DEFAULT_VOLTAGE_TABLE", "null").equals("null")) {
                String table = "";
                for (int j = 0; j < currentApplicableTable.size(); j++) {
                    if (j == 0)
                        table += currentApplicableTable.get(j);
                    else
                        table += " " + currentApplicableTable.get(j);
                }
                bootPrefs.edit().putString("DEFAULT_VOLTAGE_TABLE", table).commit();
            }
        }

        cardsUI.refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.voltage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.reset_default:
                String defaultTable = getSharedPreferences("BOOT_PREFS", 0).getString("DEFAULT_VOLTAGE_TABLE", "null");
                if (defaultTable.equals("null"))
                    CMDProcessor.runSuCommand("busybox echo \"" + defaultTable + "\" > " + VoltageInterface.UV_MV_TABLE);
                else
                    Toast.makeText(this, "Default not found!", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
