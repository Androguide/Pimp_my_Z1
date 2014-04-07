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

package com.androguide.honamicontrol.kernel.colorcontrol;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.helpers.CMDProcessor.CMDProcessor;
import com.androguide.honamicontrol.helpers.CPUHelper;

public class ColorControlActivity extends ActionBarActivity implements ColorControlInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_tools_color_calibration));
        setContentView(R.layout.card_gamma_control);
        final SharedPreferences bootPrefs = getSharedPreferences("BOOT_PREFS", 0);

        SeekBar redSeek = (SeekBar) findViewById(R.id.red_seekbar);
        SeekBar greenSeek = (SeekBar) findViewById(R.id.green_seekbar);
        final SeekBar blueSeek = (SeekBar) findViewById(R.id.blue_seekbar);

        final TextView redVal = (TextView) findViewById(R.id.red_unit);
        final TextView greenVal = (TextView) findViewById(R.id.green_unit);
        final TextView blueVal = (TextView) findViewById(R.id.blue_unit);

        redSeek.setMax(255);
        greenSeek.setMax(255);
        blueSeek.setMax(255);

        String[] rawCalib = CPUHelper.readOneLineNotRoot(GAMMA_KCAL).split("\\s+");
        final int[] currRed = {255};
        final int[] currGreen = {255};
        final int[] currBlue = {255};

        try {
            currRed[0] = Integer.parseInt(rawCalib[0]);
            currGreen[0] = Integer.parseInt(rawCalib[1]);
            currBlue[0] = Integer.parseInt(rawCalib[2]);
        } catch (Exception e) {
            Log.e("GammaControl", e.getMessage());
        }

        redSeek.setProgress(currRed[0]);
        greenSeek.setProgress(currGreen[0]);
        blueSeek.setProgress(currBlue[0]);

        redVal.setText(getString(R.string.red_val) + " " + currRed[0]);
        greenVal.setText(getString(R.string.green_val) + " " + currGreen[0]);
        blueVal.setText(getString(R.string.blue_val) + " " + currBlue[0]);

        redSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currRed[0] = i;
                redVal.setText(getString(R.string.red_val) + " " + currRed[0]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                CMDProcessor.runSuCommand("busybox echo \'" + currRed[0] + " " + currGreen[0] + " " + currBlue[0] + "\' > " + GAMMA_KCAL + "\nbusybox echo 1 > " + GAMMA_OK);
                bootPrefs.edit().putString("KCAL_CONFIG", currRed[0] + " " + currGreen[0] + " " + currBlue[0]).commit();
            }
        });

        greenSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currGreen[0] = i;
                greenVal.setText(getString(R.string.green_val) + " " + currGreen[0]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                CMDProcessor.runSuCommand("busybox echo \'" + currRed[0] + " " + currGreen[0] + " " + currBlue[0] + "\' > " + GAMMA_KCAL + "\nbusybox echo 1 > " + GAMMA_OK);
                bootPrefs.edit().putString("KCAL_CONFIG", currRed[0] + " " + currGreen[0] + " " + currBlue[0]).commit();
            }
        });

        blueSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currBlue[0] = i;
                blueVal.setText(getString(R.string.blue_val) + " " + currBlue[0]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                CMDProcessor.runSuCommand("busybox echo \'" + currRed[0] + " " + currGreen[0] + " " + currBlue[0] + "\' > " + GAMMA_KCAL + "\nbusybox echo 1 > " + GAMMA_OK);
                bootPrefs.edit().putString("KCAL_CONFIG", currRed[0] + " " + currGreen[0] + " " + currBlue[0]).commit();
            }
        });

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
