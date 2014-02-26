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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.fragments.SoundFragment;
import com.androguide.honamicontrol.helpers.Helpers;
import com.androguide.honamicontrol.soundcontrol.SoundControlInterface;
import com.fima.cardsui.objects.Card;

public class CardDoubleSeekBarPA extends Card implements SoundControlInterface {

    private SharedPreferences prefs, soundPrefs;
    private Boolean isLinked = true;
    private String location;

    public CardDoubleSeekBarPA(String title, String desc, String color, String unit, String prop, int seekBarMax, int seekBarProgress, int seekBarProgress2, ActionBarActivity fa, ActionMode.Callback callback) {
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

        seekBarLeft.setMax(12);
        seekBarRight.setMax(12);
//        Toast.makeText(fa, "raw values: " + seekBarProgress + " / " + seekBarProgress2, Toast.LENGTH_LONG).show();
        seekBarLeft.setProgress(getSeekbarValue(seekBarProgress));
        seekBarRight.setProgress(getSeekbarValue(seekBarProgress2));

        valueLeft.setText(getSeekbarLabel(seekBarProgress) + unit);
        valueRight.setText(getSeekbarLabel(seekBarProgress2) + unit);

        prefs = fa.getSharedPreferences(prop.replaceAll("/", ""), 0);
        soundPrefs = fa.getSharedPreferences("SOUND_CONTROL", 0);
        isLinked = soundPrefs.getBoolean("LINKED", true);

        seekBarLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarProgress = i;
                valueLeft.setText(i - 6 + unit);
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
                                int toApplyLeft = getSysfsValue(seekBarProgress);
                                int toApplyRight = getSysfsValue(seekBarProgress2);
                                Toast.makeText(fa, "Left: " + toApplyLeft + " / Right: " + toApplyRight, Toast.LENGTH_LONG).show();
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
                        if (!isApplied)
                            SoundFragment.recreateCards();
                    }
                });
                prefs.edit().putInt("VALUE_LEFT", getSysfsValue(seekBarProgress)).commit();
            }
        });

        seekBarRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarProgress2 = i;
                valueRight.setText(i - 6 + unit);
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
                                int toApplyLeft = getSysfsValue(seekBarProgress);
                                int toApplyRight = getSysfsValue(seekBarProgress2);
                                Toast.makeText(fa, "Left: " + toApplyLeft + " / Right: " + toApplyRight, Toast.LENGTH_LONG).show();
                                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + FAUX_SC_LOCKED + " && "
                                        + "busybox echo " + toApplyLeft + " " + toApplyRight + " "
                                        + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight) + " > " + location + " && "
                                        + "busybox echo 1 > " + FAUX_SC_LOCKED
                                );
                                SharedPreferences bootPrefs = fa.getSharedPreferences("BOOT_PREFS", 0);
                                bootPrefs.edit().putString(
                                        "SC_HEADPHONE_PA", toApplyLeft + " " + toApplyRight + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight)
                                ).commit();
                                actionMode.finish();
                                break;
                        }
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {

                    }
                });

                prefs.edit().putInt("VALUE_RIGHT", getSysfsValue(seekBarProgress2)).commit();
            }
        });

        return v;
    }

    public int getCardContentId() {
        return R.layout.card_double_seekbar;
    }

    private int getSysfsValue(int progress) {
        switch (progress) {
            case 0:
                return 44;
            case 1:
                return 43;
            case 2:
                return 42;
            case 3:
                return 41;
            case 4:
                return 40;
            case 5:
                return 39;
            case 6:
                return 38;
            case 7:
                return 37;
            case 8:
                return 36;
            case 9:
                return 35;
            case 10:
                return 34;
            case 11:
                return 33;
            case 12:
                return 32;
            case 13:
                return 31;
            default:
                return 38;
        }
    }

    private int getSeekbarValue(int progress) {
        switch (progress) {
            case 44:
                return 0;
            case 43:
                return 1;
            case 42:
                return 2;
            case 41:
                return 3;
            case 40:
                return 4;
            case 39:
                return 5;
            case 38:
                return 6;
            case 37:
                return 7;
            case 36:
                return 8;
            case 35:
                return 9;
            case 34:
                return 10;
            case 33:
                return 11;
            case 32:
                return 12;
            case 31:
                return 13;
            default:
                return 6;
        }
    }

    private int getSeekbarLabel(int progress) {
        switch (progress) {
            case 44:
                return -6;
            case 43:
                return -5;
            case 42:
                return -4;
            case 41:
                return -3;
            case 40:
                return -2;
            case 39:
                return -1;
            case 38:
                return 0;
            case 37:
                return 1;
            case 36:
                return 2;
            case 35:
                return 2;
            case 34:
                return 3;
            case 33:
                return 4;
            case 32:
                return 5;
            case 31:
                return 6;
            default:
                return 0;
        }
    }

    @Override
    public boolean convert(View convertCardView) {
        return true;
    }
}
