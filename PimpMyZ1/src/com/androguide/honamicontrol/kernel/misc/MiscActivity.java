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
import com.androguide.honamicontrol.cards.CardSeekBarVibrator;
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

        cardsUI.addCard(new CardSeekBarVibrator(getString(R.string.vibrator_intensity), getString(R.string.vibrator_intensity_text), "#1abc9c", this));

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
