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

package com.androguide.honamicontrol.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.helpers.CPUHelper;
import com.androguide.honamicontrol.helpers.Helpers;
import com.androguide.honamicontrol.soundcontrol.SoundControlInterface;
import com.echo.holographlibrary.Line;
import com.fima.cardsui.objects.Card;

import java.util.ArrayList;

public class SoundControlFragment extends Fragment implements SoundControlInterface {

    private static ActionBarActivity fa;
    private static SharedPreferences bootPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ScrollView ll = (ScrollView) inflater.inflate(R.layout.sound_control, container, false);
        fa = (ActionBarActivity) super.getActivity();
        fa.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fa.getSupportActionBar().setHomeButtonEnabled(true);
        bootPrefs = fa.getSharedPreferences("BOOT_PREFS", 0);
        final Boolean isLinked = fa.getSharedPreferences("SOUND_CONTROL", 0).getBoolean("LINKED", true);

        assert ll != null;
        final SeekBar headphoneLeft = (SeekBar) ll.findViewById(R.id.headphone_seekbar_left);
        final SeekBar headphoneRight = (SeekBar) ll.findViewById(R.id.headphone_seekbar_right);
        final SeekBar headphonePALeft = (SeekBar) ll.findViewById(R.id.headphone_pa_seekbar_left);
        final SeekBar headphonePARight = (SeekBar) ll.findViewById(R.id.headphone_pa_seekbar_right);
        final SeekBar speakerLeft = (SeekBar) ll.findViewById(R.id.speaker_seekbar_left);
        final SeekBar speakerRight = (SeekBar) ll.findViewById(R.id.speaker_seekbar_right);
        final SeekBar micGain = (SeekBar) ll.findViewById(R.id.mic_seekbar);
        final SeekBar camMicGain = (SeekBar) ll.findViewById(R.id.cam_mic_seekbar);

        final TextView headUnitLeft = (TextView) ll.findViewById(R.id.headphone_unit_left);
        final TextView headUnitRight = (TextView) ll.findViewById(R.id.headphone_unit_right);
        final TextView headPAUnitLeft = (TextView) ll.findViewById(R.id.headphone_pa_unit_left);
        final TextView headPAUnitRight = (TextView) ll.findViewById(R.id.headphone_pa_unit_right);
        final TextView speakerUnitLeft = (TextView) ll.findViewById(R.id.speaker_unit_left);
        final TextView speakerUnitRight = (TextView) ll.findViewById(R.id.speaker_unit_right);
        final TextView micUnit = (TextView) ll.findViewById(R.id.mic_unit);
        final TextView camMicUnit = (TextView) ll.findViewById(R.id.cam_mic_unit);

        // Headphone L/R Gain
        if (Helpers.doesFileExist(FAUX_SC_HEADPHONE)) {
            headphoneLeft.setMax(40);
            headphoneRight.setMax(40);
            String[] headphoneGains = CPUHelper.readOneLineNotRoot(FAUX_SC_HEADPHONE).split(" ");
            int headphoneGainLeft = Integer.valueOf(headphoneGains[0]);
            if (headphoneGainLeft > 100)
                headphoneGainLeft -= 256;

            int headphoneGainRight = Integer.valueOf(headphoneGains[1]);
            if (headphoneGainRight > 100)
                headphoneGainRight -= 256;

            headphoneLeft.setProgress(headphoneGainLeft + 30);
            headphoneRight.setProgress(headphoneGainRight + 30);
            headUnitLeft.setText(headphoneGainLeft + "");
            headUnitRight.setText(headphoneGainRight + "");

            headphoneLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    headUnitLeft.setText(i - 30 + "");
                    if (isLinked) {
                        headUnitRight.setText(i - 30 + "");
                        headphoneRight.setProgress(i);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(final SeekBar seekBar) {
                    int toApplyLeft = getSCInt(seekBar.getProgress());
                    int toApplyRight = getSCInt(headphoneRight.getProgress());
                    Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + FAUX_SC_LOCKED + "\n"
                                    + "busybox echo " + toApplyLeft + " " + toApplyRight + " "
                                    + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight) + " > " + FAUX_SC_HEADPHONE + "\n"
                                    + "busybox echo 1 > " + FAUX_SC_LOCKED
                    );
                    bootPrefs.edit().putString(
                            "HEADPHONE", toApplyLeft + " " + toApplyRight + " " + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight)
                    ).commit();
                }
            });

            headphoneRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    headUnitRight.setText(i - 30 + "");
                    if (isLinked) {
                        headUnitLeft.setText(i - 30 + "");
                        headphoneLeft.setProgress(i);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(final SeekBar seekBar) {
                    int toApplyLeft = getSCInt(headphoneLeft.getProgress());
                    int toApplyRight = getSCInt(seekBar.getProgress());
                    Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + FAUX_SC_LOCKED + "\n"
                                    + "busybox echo " + toApplyLeft + " " + toApplyRight + " "
                                    + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight) + " > " + FAUX_SC_HEADPHONE + "\n"
                                    + "busybox echo 1 > " + FAUX_SC_LOCKED
                    );
                    bootPrefs.edit().putString(
                            "HEADPHONE", toApplyLeft + " " + toApplyRight + " " + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight)
                    ).commit();
                }
            });

        } else {
            headphoneLeft.setMax(40);
            headphoneRight.setMax(40);
            headphoneLeft.setProgress(20);
            headphoneRight.setProgress(20);
            headphoneLeft.setEnabled(false);
            headphoneRight.setEnabled(false);
            headUnitLeft.setText("Unsupported");
            headUnitRight.setText("Unsupported");
        }

        // Headphone analog L/R gain
        if (Helpers.doesFileExist(FAUX_SC_HEADPHONE_POWERAMP)) {
            headphonePALeft.setMax(12);
            headphonePARight.setMax(12);
            String[] headphonePAGains = CPUHelper.readOneLineNotRoot(FAUX_SC_HEADPHONE_POWERAMP).split(" ");
            int headphonePAGainLeft = Integer.valueOf(headphonePAGains[0]);
            int headphonePAGainRight = Integer.valueOf(headphonePAGains[1]);
            headphonePALeft.setProgress(headphonePAGainLeft);
            headphonePARight.setProgress(headphonePAGainRight);
            headPAUnitLeft.setText(headphonePAGainLeft - 26 + "");
            headPAUnitRight.setText(headphonePAGainRight - 26 + "");

            headphonePALeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    headPAUnitLeft.setText(i - 6 + "");
                    if (isLinked) {
                        headphonePARight.setProgress(i);
                        headPAUnitRight.setText(i - 6 + "");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(final SeekBar seekBar) {
                    int toApplyLeft = getSysfsValue(seekBar.getProgress());
                    int toApplyRight = getSysfsValue(headphonePARight.getProgress());
                    Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + FAUX_SC_LOCKED + "\n"
                                    + "busybox echo " + toApplyLeft + " " + toApplyRight + " "
                                    + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight) + " > " + FAUX_SC_HEADPHONE_POWERAMP + "\n"
                                    + "busybox echo 1 > " + FAUX_SC_LOCKED
                    );
                    bootPrefs.edit().putString(
                            "HEADPHONE_PA", toApplyLeft + " " + toApplyRight + " " + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight)
                    ).commit();
                }
            });

            headphonePARight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    headPAUnitRight.setText(i - 6 + "");
                    if (isLinked) {
                        headphonePALeft.setProgress(i);
                        headPAUnitLeft.setText(i - 6 + "");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(final SeekBar seekBar) {
                    int toApplyLeft = getSysfsValue(headphonePALeft.getProgress());
                    int toApplyRight = getSysfsValue(seekBar.getProgress());
                    Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + FAUX_SC_LOCKED + "\n"
                                    + "busybox echo " + toApplyLeft + " " + toApplyRight + " "
                                    + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight) + " > " + FAUX_SC_HEADPHONE_POWERAMP + "\n"
                                    + "busybox echo 1 > " + FAUX_SC_LOCKED
                    );
                    bootPrefs.edit().putString(
                            "HEADPHONE_PA", toApplyLeft + " " + toApplyRight + " " + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight)
                    ).commit();
                }
            });

        } else {
            headphonePALeft.setMax(40);
            headphonePARight.setMax(40);
            headphonePALeft.setProgress(20);
            headphonePARight.setProgress(20);
            headphonePALeft.setEnabled(false);
            headphonePARight.setEnabled(false);
            headPAUnitLeft.setText("Unsupported");
            headPAUnitRight.setText("Unsupported");
        }

        // Speaker L/R Gain
        if (Helpers.doesFileExist(FAUX_SC_SPEAKER)) {
            speakerLeft.setMax(40);
            speakerRight.setMax(40);
            String[] speakerGains = CPUHelper.readOneLineNotRoot(FAUX_SC_SPEAKER).split(" ");
            int speakerGainLeft = Integer.valueOf(speakerGains[0]);
            if (speakerGainLeft > 100)
                speakerGainLeft -= 256;

            int speakerGainRight = Integer.valueOf(speakerGains[1]);
            if (speakerGainRight > 100)
                speakerGainRight -= 256;

            speakerLeft.setProgress(speakerGainLeft + 30);
            speakerRight.setProgress(speakerGainRight + 30);
            speakerUnitLeft.setText(speakerGainLeft + "");
            speakerUnitRight.setText(speakerGainRight + "");

            speakerLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    speakerUnitLeft.setText(i - 30 + "");
                    if (isLinked) {
                        speakerUnitRight.setText(i - 30 + "");
                        speakerRight.setProgress(i);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(final SeekBar seekBar) {
                    int toApplyLeft = getSCInt(seekBar.getProgress());
                    int toApplyRight = getSCInt(speakerRight.getProgress());
                    Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + FAUX_SC_LOCKED + "\n"
                                    + "busybox echo " + toApplyLeft + " " + toApplyRight + " "
                                    + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight) + " > " + FAUX_SC_SPEAKER + "\n"
                                    + "busybox echo 1 > " + FAUX_SC_LOCKED
                    );
                    bootPrefs.edit().putString(
                            "SPEAKER", toApplyLeft + " " + toApplyRight + " " + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight)
                    ).commit();
                }
            });

            speakerRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    speakerUnitRight.setText(i - 30 + "");
                    if (isLinked) {
                        speakerUnitLeft.setText(i - 30 + "");
                        speakerLeft.setProgress(i);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(final SeekBar seekBar) {
                    int toApplyLeft = getSCInt(speakerLeft.getProgress());
                    int toApplyRight = getSCInt(seekBar.getProgress());
                    Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + FAUX_SC_LOCKED + "\n"
                                    + "busybox echo " + toApplyLeft + " " + toApplyRight + " "
                                    + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight) + " > " + FAUX_SC_SPEAKER + "\n"
                                    + "busybox echo 1 > " + FAUX_SC_LOCKED
                    );
                    bootPrefs.edit().putString(
                            "SPEAKER", toApplyLeft + " " + toApplyRight + " " + Helpers.getSoundCountrolBitRepresentation(toApplyLeft, toApplyRight)
                    ).commit();
                }
            });

        } else {
            speakerLeft.setMax(40);
            speakerRight.setMax(40);
            speakerLeft.setProgress(20);
            speakerRight.setProgress(20);
            speakerLeft.setEnabled(false);
            speakerRight.setEnabled(false);
            speakerUnitLeft.setText("Unsupported");
            speakerUnitRight.setText("Unsupported");
        }

        // Microphone Gain
        if (Helpers.doesFileExist(FAUX_SC_MIC)) {
            int micValue = Integer.valueOf(CPUHelper.readOneLine(FAUX_SC_MIC));
            if (micValue > 100)
                micValue -= 256;

            micGain.setMax(40);
            micGain.setProgress(micValue + 30);
            micUnit.setText(micValue + "");
            micGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    micUnit.setText(i - 30 + "");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(final SeekBar seekBar) {
                    int scProgress = seekBar.getProgress() - 30;
                    if (scProgress < 0)
                        scProgress += 256;

                    Helpers.CMDProcessorWrapper.runSuCommand(
                            "busybox echo 0 > " + FAUX_SC_LOCKED + "\n" +
                                    "busybox echo " + scProgress + " " + Helpers.getSoundCountrolBitRepresentation(scProgress, 0) + " > " + FAUX_SC_MIC + "\n" +
                                    "busybox echo 1 > " + FAUX_SC_LOCKED
                    );
                    bootPrefs.edit().putString("SC_MIC", scProgress + " " + Helpers.getSoundCountrolBitRepresentation(scProgress, 0)).commit();
                }
            });

        } else {
            micGain.setMax(40);
            micGain.setProgress(20);
            micUnit.setText("Unsupported");
            micGain.setEnabled(false);
        }

        // Camera Microphone Gain
        if (Helpers.doesFileExist(FAUX_SC_CAM_MIC)) {
            int camMicValue = Integer.valueOf(CPUHelper.readOneLine(FAUX_SC_CAM_MIC));
            if (camMicValue > 100)
                camMicValue -= 256;

            camMicGain.setMax(40);
            camMicGain.setProgress(camMicValue + 30);
            camMicUnit.setText(camMicValue + "");
            camMicGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    camMicUnit.setText(i - 30 + "");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(final SeekBar seekBar) {
                    int scProgress = seekBar.getProgress() - 30;
                    if (scProgress < 0)
                        scProgress += 256;

                    Helpers.CMDProcessorWrapper.runSuCommand(
                            "busybox echo 0 > " + FAUX_SC_LOCKED + "\n" +
                                    "busybox echo " + scProgress + " " + Helpers.getSoundCountrolBitRepresentation(scProgress, 0) + " > " + FAUX_SC_CAM_MIC + "\n" +
                                    "busybox echo 1 > " + FAUX_SC_LOCKED
                    );
                    bootPrefs.edit().putString("SC_MIC", scProgress + " " + Helpers.getSoundCountrolBitRepresentation(scProgress, 0)).commit();
                }
            });

        } else {
            camMicGain.setMax(40);
            camMicGain.setProgress(20);
            camMicUnit.setText("Unsupported");
            camMicGain.setEnabled(false);
        }

        return ll;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sound_control, menu);
        SharedPreferences p = fa.getSharedPreferences("SOUND_CONTROL", 0);
        if (p.getBoolean("LINKED", true))
            menu.getItem(0).setChecked(true);
        else
            menu.getItem(0).setChecked(false);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.link_seekbars:
                SharedPreferences soundPrefs = fa.getSharedPreferences("SOUND_CONTROL", 0);
                if (item.isChecked()) {
                    item.setChecked(false);
                    soundPrefs.edit().putBoolean("LINKED", false).commit();
                } else {
                    item.setChecked(true);
                    soundPrefs.edit().putBoolean("LINKED", true).commit();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getSCInt(int seekbarInt) {
        seekbarInt -= 30;
        if (seekbarInt < 0)
            seekbarInt += 256;
        return seekbarInt;
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
}
