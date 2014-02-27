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

package com.androguide.honamicontrol.kernel.gpucontrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.androguide.honamicontrol.MainActivity;
import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.helpers.CPUHelper;
import com.androguide.honamicontrol.helpers.Helpers;
import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GPUActivity extends ActionBarActivity {

    private static SharedPreferences preferences, bootPrefs;

    private static LineGraph graph;
    private static Line line;
    private static int currX = 0;
    private static int counter = 0;
    private static TextView mCurFreq;
    private SeekBar mMaxSlider;
    private SeekBar mMinSlider;
    private TextView mMaxSpeedText;
    private TextView mMinSpeedText;
    private String[] availableFrequencies;
    private String mMaxFreqSetting;
    private String mMinFreqSetting;
    private CurCPUThread mCurCPUThread;
    private int spinnerCounter = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent back = new Intent(this, MainActivity.class);
                back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(back);

            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_tools_gpu_control));
        setContentView(R.layout.card_gpu_control);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        bootPrefs = getSharedPreferences("BOOT_PREFS", 0);

        graph = (LineGraph) findViewById(R.id.graph);
        line = new Line();
        LinePoint point = new LinePoint();
        point.setX(currX);
        point.setY(1);
        line.addPoint(point);
        line.setColor(Color.parseColor("#FFBB33"));
        graph.addLine(line);
        graph.setLineToFill(0);

        availableFrequencies = new String[0];
        String availableFrequenciesLine = "";
        if (Helpers.doesFileExist(GPUInterface.availableFreqs)) {
            availableFrequenciesLine = CPUHelper.readOneLineNotRoot(GPUInterface.availableFreqs);
            if (availableFrequenciesLine != null) {
                availableFrequencies = availableFrequenciesLine.split(" ");
                Arrays.sort(availableFrequencies, new Comparator<String>() {
                    @Override
                    public int compare(String object1, String object2) {
                        return Integer.valueOf(object1).compareTo(
                                Integer.valueOf(object2));
                    }
                });
            }
        }

        int frequenciesNum = availableFrequencies.length - 1;

        String currentGovernor = "";
        if (Helpers.doesFileExist(GPUInterface.currGovernor))
            currentGovernor = CPUHelper.readOneLineNotRoot(GPUInterface.currGovernor);

        String curMaxSpeed = "";
        if (Helpers.doesFileExist(GPUInterface.maxFreq))
            curMaxSpeed = CPUHelper.readOneLineNotRoot(GPUInterface.maxFreq);

        String curMinSpeed = "";
        if (Helpers.doesFileExist(GPUInterface.minFreq))
            curMinSpeed = CPUHelper.readOneLineNotRoot(GPUInterface.minFreq);

        mCurFreq = (TextView) findViewById(R.id.currspeed);

        mMaxSlider = (SeekBar) findViewById(R.id.max_slider);
        mMaxSlider.setMax(frequenciesNum);
        mMaxSpeedText = (TextView) findViewById(R.id.max_speed_text);
        mMaxSpeedText.setText(toMHz(curMaxSpeed));
        mMaxSlider.setProgress(Arrays.asList(availableFrequencies).indexOf(
                curMaxSpeed));
        mMaxSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    setMaxSpeed(seekBar, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mMaxFreqSetting != null && !mMaxFreqSetting.isEmpty()) {
                    Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + mMaxFreqSetting + " > "
                            + GPUInterface.maxFreq);
                    bootPrefs.edit().putString("GPU_MAX_FREQ", mMaxFreqSetting).commit();
                }
            }
        });

        mMinSlider = (SeekBar) findViewById(R.id.min_slider);
        mMinSlider.setMax(frequenciesNum);
        mMinSpeedText = (TextView) findViewById(R.id.min_speed_text);
        mMinSpeedText.setText(toMHz(curMinSpeed));
        mMinSlider.setProgress(Arrays.asList(availableFrequencies).indexOf(
                curMinSpeed));
        mMinSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    setMinSpeed(seekBar, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mMinFreqSetting != null && !mMinFreqSetting.isEmpty()) {
                    Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + mMinFreqSetting + " > "
                            + GPUInterface.minFreq);
                    bootPrefs.edit().putString("GPU_MIN_FREQ", mMinFreqSetting).commit();
                }
            }
        });

        Spinner mGovernor = (Spinner) findViewById(R.id.governor);
        String[] availableGovernors = new String[]{"No GPU Governors Found!"};
        if (Helpers.doesFileExist(GPUInterface.availableGovernors))
            availableGovernors = CPUHelper.readOneLineNotRoot(GPUInterface.availableGovernors)
                    .split(" ");
        ArrayAdapter<CharSequence> governorAdapter = new ArrayAdapter<CharSequence>(
                this, R.layout.spinner_row);
        governorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (int i = 0; i < availableGovernors.length; i++) {
            governorAdapter.add(availableGovernors[i]);
        }
        mGovernor.setAdapter(governorAdapter);
        mGovernor.setSelection(Arrays.asList(availableGovernors).indexOf(
                currentGovernor));
        mGovernor.setOnItemSelectedListener(new GovListener());
    }

    public class GovListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
            if (spinnerCounter > 0) {
                String selected = parent.getItemAtPosition(pos).toString();

                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + selected + " > "
                        + GPUInterface.currGovernor);

                bootPrefs.edit().putString("GPU_GOVERNOR", selected).commit();

                final SharedPreferences.Editor editor = preferences.edit();
                editor.putString("GPU_GOVERNOR", selected);
                editor.commit();
            } else {
                spinnerCounter++;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCurCPUThread == null) {
            mCurCPUThread = new CurCPUThread();
            mCurCPUThread.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCurCPUThread.isAlive()) {
            mCurCPUThread.interrupt();
            try {
                mCurCPUThread.join();
            } catch (InterruptedException e) {
            }
        }
    }

    public void setMaxSpeed(SeekBar seekBar, int progress) {
        String current;
        current = availableFrequencies[progress];
        int minSliderProgress = mMinSlider.getProgress();
        if (progress <= minSliderProgress && !current.isEmpty()) {
            mMinSlider.setProgress(progress);
            mMinSpeedText.setText(toMHz(current));
            mMinFreqSetting = current;
        }
        mMaxSpeedText.setText(toMHz(current));
        mMaxFreqSetting = current;
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(GPUInterface.maxFreq, current);
        editor.commit();
    }

    public void setMinSpeed(SeekBar seekBar, int progress) {
        String current;
        current = availableFrequencies[progress];
        int maxSliderProgress = mMaxSlider.getProgress();
        if (progress >= maxSliderProgress) {
            mMaxSlider.setProgress(progress);
            mMaxSpeedText.setText(toMHz(current));
            mMaxFreqSetting = current;
        }
        mMinSpeedText.setText(toMHz(current));
        mMinFreqSetting = current;
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(GPUInterface.minFreq, current);
        editor.commit();
    }

    // Convert raw collected values to formatted MhZ
    private static String toMHz(String mhzString) {
        if (Integer.valueOf(mhzString) != null)
            return String.valueOf(Integer.valueOf(mhzString) / 1000000) + " MHz";
        else
            return "NaN";
    }

    // Read current frequency from /sys in a separate thread
    protected class CurCPUThread extends Thread {
        private boolean mInterrupt = false;

        public void interrupt() {
            mInterrupt = true;
        }

        @Override
        public void run() {
            try {
                while (!mInterrupt) {
                    sleep(1000);
                    final String curFreq = CPUHelper.readOneLineNotRoot(GPUInterface.currFreq);
                    mCurCPUHandler.sendMessage(mCurCPUHandler.obtainMessage(0,
                            curFreq));
                }
            } catch (InterruptedException e) {
                Log.e("GPU Thread", e.getMessage());
            }
        }
    }


    // Update real-time current frequency & stats in a separate thread
    protected static Handler mCurCPUHandler = new Handler() {
        public void handleMessage(Message msg) {
            mCurFreq.setText(toMHz((String) msg.obj));
            currX += 1;
            final int p = Integer.parseInt((String) msg.obj);

            new Thread(new Runnable() {
                public void run() {
                    counter++;
                    addStatPoint(currX, p, line, graph);
                    ArrayList<LinePoint> array = line.getPoints();
                    if (line.getSize() > 10)
                        array.remove(0);
                    line.setPoints(array);

                    // Reset the line every 50 updates of the current frequency
                    // to make-up for the lack of garbage collection in the
                    // HoloGraph pluggable
                    if (counter == 50) {
                        graph.removeAllLines();
                        line = new Line();
                        LinePoint point = new LinePoint();
                        point.setX(currX);
                        point.setY(1);
                        line.addPoint(point);
                        line.setColor(Color.parseColor("#FFBB33"));
                        graph.addLine(line);
                        counter = 0;
                    }
                }
            }).start();
        }
    };

    // Static method to add new point to the graph
    public static void addStatPoint(int X, int Y, Line line, LineGraph graph) {
        LinePoint point = new LinePoint();
        point.setX(X);
        point.setY(Y);
        line.addPoint(point);
        graph.addLine(line);
    }
}
