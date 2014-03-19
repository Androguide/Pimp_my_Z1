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

package com.androguide.honamicontrol.kernel.misc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.cards.CardSeekBarGeneric;
import com.androguide.honamicontrol.cards.CardSwitchDisabled;
import com.androguide.honamicontrol.cards.CardSwitchPlugin;
import com.androguide.honamicontrol.helpers.CPUHelper;
import com.androguide.honamicontrol.helpers.Helpers;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

public class MiscActivity extends ActionBarActivity implements MiscInterface {

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
        cardsUI.addStack(new CardStack(getString(R.string.ksm_header)));

        if (Helpers.doesFileExist(KSM_TOGGLE)) {
            cardsUI.addCard(new CardSwitchPlugin(
                    getString(R.string.ksm),
                    getString(R.string.ksm_desc),
                    "#1abc9c",
                    KSM_TOGGLE,
                    this,
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                            bootPrefs.edit().putBoolean("KSM_ENABLED", isOn).commit();
                            if (isOn)
                                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 1 > " + KSM_TOGGLE);
                            else
                                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + KSM_TOGGLE);
                        }
                    }
            ));

        } else {
            cardsUI.addCard(new CardSwitchDisabled(
                            getString(R.string.ksm),
                            getString(R.string.ksm_unsupported),
                            "#c74b46",
                            "",
                            this,
                            null)
            );
        }

        if (Helpers.doesFileExist(KSM_PAGES_TO_SCAN)) {
            int currPagesToScan = 100;
            try {
                currPagesToScan = Integer.valueOf(CPUHelper.readOneLineNotRoot(KSM_PAGES_TO_SCAN));
            } catch (Exception e) {
                Log.e("KSM_PAGES_TO_SCAN", e.getMessage());
            }

            final CardSeekBarGeneric cardKSMPages = new CardSeekBarGeneric(
                    getString(R.string.ksm_pages_to_scan),
                    getString(R.string.ksm_pages_to_scan_desc),
                    "#1abc9c", "",
                    KSM_PAGES_TO_SCAN,
                    512,
                    currPagesToScan,
                    this,
                    null
            );
            cardsUI.addCard(cardKSMPages);
        }

        if (Helpers.doesFileExist(KSM_SLEEP_TIMER)) {
            int currTimer = 500;
            try {
                currTimer = Integer.valueOf(CPUHelper.readOneLineNotRoot(KSM_SLEEP_TIMER));
            } catch (Exception e) {
                Log.e("KSM_SLEEP_TIMER", e.getMessage());
            }

            cardsUI.addCard(new CardSeekBarGeneric(
                    getString(R.string.ksm_timer),
                    getString(R.string.ksm_timer_desc),
                    "#1abc9c", "ms",
                    KSM_SLEEP_TIMER,
                    2000,
                    currTimer,
                    this,
                    null
            ));
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
