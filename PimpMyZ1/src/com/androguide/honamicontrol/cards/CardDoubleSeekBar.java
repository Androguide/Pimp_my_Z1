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

package com.androguide.honamicontrol.cards;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.fragments.SoundFragment;
import com.androguide.honamicontrol.helpers.Helpers;
import com.androguide.honamicontrol.soundcontrol.SoundControlInterface;
import com.fima.cardsui.objects.Card;

public class CardDoubleSeekBar extends Card implements SoundControlInterface {

    private SharedPreferences prefs, soundPrefs;
    private Boolean isLinked = true;
    private String location;

    public CardDoubleSeekBar(String title, String desc, String color, String unit, String prop, int seekBarMax, int seekBarProgress, int seekBarProgress2, ActionBarActivity fa, ActionMode.Callback callback) {
        super(title, desc, color, unit, prop, seekBarMax, seekBarProgress, seekBarProgress2, fa, callback);
        location = prop;
    }

    @Override
    public View getCardContent(Context context) {
        final View v = LayoutInflater.from(context).inflate(R.layout.card_double_seekbar, null);

        assert v != null;
        final TextView valueLeft = (TextView) v.findViewById(R.id.unit_left);
        final TextView valueRight = (TextView) v.findViewById(R.id.unit_right);
        final TextView titleView = (TextView) v.findViewById(R.id.title);
        titleView.setText(title);
        titleView.setTextColor(Color.parseColor(color));
        ((TextView) v.findViewById(R.id.desc)).setText(desc);

        final SeekBar seekBarLeft = (SeekBar) v.findViewById(R.id.seek_left);
        final SeekBar seekBarRight = (SeekBar) v.findViewById(R.id.seek_right);

        seekBarLeft.setMax(40);
        seekBarRight.setMax(40);
        seekBarLeft.setProgress(seekBarProgress);
        seekBarRight.setProgress(seekBarProgress2);

        valueLeft.setText(seekBarProgress - 30 + unit);
        valueRight.setText(seekBarProgress2 - 30 + unit);

        prefs = fa.getSharedPreferences(prop.replaceAll("/", ""), 0);
        soundPrefs = fa.getSharedPreferences("SOUND_CONTROL", 0);
        isLinked = soundPrefs.getBoolean("LINKED", true);

        seekBarLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarProgress = i;
                valueLeft.setText(i - 30 + unit);
                if (soundPrefs.getBoolean("LINKED", true)) {
                    seekBarProgress2 = i;
                    seekBarRight.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                fa.startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        MenuInflater inflater = actionMode.getMenuInflater();
                        inflater.inflate(R.menu.contextual_menu, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.apply:
                                int toApplyLeft = getSCInt(seekBarProgress);
                                int toApplyRight = getSCInt(seekBarProgress2);
                                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + FAUX_SC_LOCKED + " && "
                                        + "busybox echo " + toApplyLeft + " " + toApplyRight + " "
                                        + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight) + " > " + location + " && "
                                        + "busybox echo 1 > " + FAUX_SC_LOCKED
                                );
                                actionMode.finish();
                                break;
                        }
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {

                    }
                });
                int scProgress = seekBar.getProgress() - 30;
                if (scProgress < 0)
                    scProgress += 256;
                prefs.edit().putInt("VALUE_LEFT", scProgress).commit();
            }
        });

        seekBarRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarProgress2 = i;
                valueRight.setText(i - 30 + unit);
                if (soundPrefs.getBoolean("LINKED", true)) {
                    seekBarProgress = i;
                    seekBarLeft.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                fa.startSupportActionMode(new ActionMode.Callback() {
                    private Boolean isApplied = false;

                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        MenuInflater inflater = actionMode.getMenuInflater();
                        inflater.inflate(R.menu.contextual_menu, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.apply:
                                isApplied = true;
                                int toApplyLeft = getSCInt(seekBarProgress);
                                int toApplyRight = getSCInt(seekBarProgress2);
                                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + FAUX_SC_LOCKED + " && "
                                        + "busybox echo " + toApplyLeft + " " + toApplyRight + " "
                                        + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight) + " > " + location + " && "
                                        + "busybox echo 1 > " + FAUX_SC_LOCKED
                                );
                                SharedPreferences bootPrefs = fa.getSharedPreferences("BOOT_PREFS", 0);
                                bootPrefs.edit().putString(
                                        prop.replaceAll("/", "_"), toApplyLeft + " " + toApplyRight + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight)
                                ).commit();
                                actionMode.finish();
                                break;
                        }
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {
                        if (!isApplied)
                            SoundFragment.recreateCards();
                    }
                });
                int scProgress = seekBar.getProgress() - 30;
                if (scProgress < 0)
                    scProgress += 256;
                prefs.edit().putInt("VALUE_RIGHT", scProgress).commit();
            }
        });

        return v;
    }

    public int getCardContentId() {
        return R.layout.card_double_seekbar;
    }

    public int getSCInt(int seekbarInt) {
        seekbarInt -= 30;
        if (seekbarInt < 0)
            seekbarInt += 256;
        return seekbarInt;
    }

    @Override
    public boolean convert(View convertCardView) {
        return true;
    }
}
