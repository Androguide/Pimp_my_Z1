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

package com.androguide.honamicontrol.kernel.cpucontrol;

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

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.helpers.CPUHelper;
import com.androguide.honamicontrol.helpers.Helpers;
import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class CPUActivity extends ActionBarActivity implements CPUInterface {

    private static SharedPreferences preferences, bootPrefs;

    public static final String TAG = "CPUSettings";


    private static LineGraph graph;
    private static Line line;
    private static int currX = 0;
    private static int counter = 0;
    private static TextView mCurFreq;

    private SeekBar mMaxSlider;
    private SeekBar mMinSlider;
    private Spinner mGovernor, mGovernor2, mGovernor3, mGovernor4;
    private Spinner mIo;
    private TextView mMaxSpeedText;
    private TextView mMinSpeedText;
    private String[] availableFrequencies;
    private String mMaxFreqSetting;
    private String mMinFreqSetting;
    private CurCPUThread mCurCPUThread;
    private boolean mIsTegra3 = false;
    private int mNumOfCpu = 1;
    private int spinnerCounter = 0, spinnerCounter2 = 0, spinnerCounter3 = 0, spinnerCounter4 = 0, schedCounter = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_tools_cpu_control));
        setContentView(R.layout.card_cpu_control);
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
        String availableFrequenciesLine = CPUHelper.readOneLineNotRoot(STEPS);
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

        int frequenciesNum = availableFrequencies.length - 1;

        String currentGovernor = CPUHelper.readOneLineNotRoot(GOVERNOR);
        String currentGovernor2 = CPUHelper.readOneLineNotRoot(GOVERNOR2);
        String currentGovernor3 = CPUHelper.readOneLineNotRoot(GOVERNOR3);
        String currentGovernor4 = CPUHelper.readOneLineNotRoot(GOVERNOR4);

        String currentIo = CPUHelper.readOneLineNotRoot(CPUInterface.IO_SCHEDULER);
        String curMaxSpeed = CPUHelper.readOneLineNotRoot(MAX_FREQ);
        String curMinSpeed = CPUHelper.readOneLineNotRoot(MIN_FREQ);

        if (mIsTegra3) {
            String curTegraMaxSpeed = CPUHelper.readOneLineNotRoot(TEGRA_MAX_FREQ);
            int curTegraMax = 0;
            try {
                curTegraMax = Integer.parseInt(curTegraMaxSpeed);
                if (curTegraMax > 0) {
                    curMaxSpeed = Integer.toString(curTegraMax);
                }
            } catch (NumberFormatException ex) {
                curTegraMax = 0;
            }
        }

        String numOfCpus = CPUHelper.readOneLineNotRoot(NUM_OF_CPUS);
        String[] cpuCount = numOfCpus.split("-");
        if (cpuCount.length > 1) {
            try {
                int cpuStart = Integer.parseInt(cpuCount[0]);
                int cpuEnd = Integer.parseInt(cpuCount[1]);

                mNumOfCpu = cpuEnd - cpuStart + 1;

                if (mNumOfCpu < 0)
                    mNumOfCpu = 1;
            } catch (NumberFormatException ex) {
                mNumOfCpu = 1;
            }
        }

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
                    bootPrefs.edit().putString("CPU_MAX_FREQ", mMaxFreqSetting).commit();
                    for (int i = 0; i < mNumOfCpu; i++)
                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + mMaxFreqSetting + " > "
                                + MAX_FREQ.replace("cpu0", "cpu" + i));
                }


                if (mIsTegra3) {
                    if (mMaxFreqSetting != null && !mMaxFreqSetting.isEmpty())
                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + mMaxFreqSetting + " > "
                                + TEGRA_MAX_FREQ);
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
                    bootPrefs.edit().putString("CPU_MIN_FREQ", mMinFreqSetting).commit();
                    for (int i = 0; i < mNumOfCpu; i++)
                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + mMinFreqSetting + " > "
                                + MIN_FREQ.replace("cpu0", "cpu" + i));
                }
            }
        });

        /** CPU Governor for core 0 */
        mGovernor = (Spinner) findViewById(R.id.governor);
        String[] availableGovernors = CPUHelper.readOneLineNotRoot(GOVERNORS_LIST).split(" ");
        ArrayAdapter<CharSequence> governorAdapter = new ArrayAdapter<CharSequence>(
                this, R.layout.spinner_row);
        governorAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (String availableGovernor : availableGovernors) {
            governorAdapter.add(availableGovernor);
        }
        mGovernor.setAdapter(governorAdapter);
        mGovernor.setSelection(Arrays.asList(availableGovernors).indexOf(
                currentGovernor));
        mGovernor.setOnItemSelectedListener(new GovListener());

        /** CPU Governor for core 1 */
        mGovernor2 = (Spinner) findViewById(R.id.governor2);
        String[] availableGovernors2;
        try {
            availableGovernors2 = CPUHelper.readOneLineNotRoot(GOVERNORS_LIST2)
                    .split(" ");
        } catch (NullPointerException e) {
            availableGovernors2 = new String[]{"Core Offline"};
        }
        ArrayAdapter<CharSequence> governorAdapter2 = new ArrayAdapter<CharSequence>(
                this, R.layout.spinner_row);
        governorAdapter2
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (String anAvailableGovernors2 : availableGovernors2) {
            governorAdapter2.add(anAvailableGovernors2);
        }
        mGovernor2.setAdapter(governorAdapter2);
        mGovernor2.setSelection(Arrays.asList(availableGovernors2).indexOf(
                currentGovernor2));
        mGovernor2.setOnItemSelectedListener(new GovListener2());

        /** CPU Governor for core 2 */
        mGovernor3 = (Spinner) findViewById(R.id.governor3);
        String[] availableGovernors3 = {};
        try {
            availableGovernors3 = CPUHelper.readOneLineNotRoot(GOVERNORS_LIST3)
                    .split(" ");
        } catch (NullPointerException e) {
            availableGovernors3 = new String[]{"Core Offline"};
        }
        ArrayAdapter<CharSequence> governorAdapter3 = new ArrayAdapter<CharSequence>(
                this, R.layout.spinner_row);
        governorAdapter3
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (String anAvailableGovernors3 : availableGovernors3) {
            governorAdapter3.add(anAvailableGovernors3);
        }
        mGovernor3.setAdapter(governorAdapter3);
        mGovernor3.setSelection(Arrays.asList(availableGovernors3).indexOf(
                currentGovernor3));
        mGovernor3.setOnItemSelectedListener(new GovListener3());

        /** CPU Governor for core 3 */
        mGovernor4 = (Spinner) findViewById(R.id.governor4);
        String[] availableGovernors4;
        try {
            availableGovernors4 = CPUHelper.readOneLineNotRoot(GOVERNORS_LIST4)
                    .split(" ");
        } catch (NullPointerException e) {
            availableGovernors4 = new String[]{"Core Offline"};
        }
        ArrayAdapter<CharSequence> governorAdapter4 = new ArrayAdapter<CharSequence>(
                this, R.layout.spinner_row);
        governorAdapter4
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (String anAvailableGovernors4 : availableGovernors4) {
            governorAdapter4.add(anAvailableGovernors4);
        }
        mGovernor4.setAdapter(governorAdapter4);
        mGovernor4.setSelection(Arrays.asList(availableGovernors4).indexOf(
                currentGovernor4));
        mGovernor4.setOnItemSelectedListener(new GovListener4());

        /** I/O Scheduler Spinner */
        mIo = (Spinner) findViewById(R.id.io);
        String[] availableIo = CPUHelper.getAvailableIOSchedulers();
        ArrayAdapter<CharSequence> ioAdapter = new ArrayAdapter<CharSequence>(
                this, R.layout.spinner_row);
        ioAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (int i = 0; i < availableIo.length; i++) {
            ioAdapter.add(availableIo[i]);
        }
        mIo.setAdapter(ioAdapter);
        mIo.setSelection(Arrays.asList(availableIo).indexOf(currentIo));
        mIo.setOnItemSelectedListener(new IOListener());
    }

    public class GovListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
            if (spinnerCounter > 0) {
                @SuppressWarnings("ConstantConditions")
                String selected = parent.getItemAtPosition(pos).toString();
                if (!selected.equals("Core Offline")) {
                    // do this on all cpu's since MSM can have different governors on
                    // each cpu
                    // and it doesn't hurt other devices to do it
                    Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + selected + " > " + GOVERNOR);
                    bootPrefs.edit().putString("CORE0_GOVERNOR", selected).commit();

                    final SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(GOV_PREF, selected);
                    editor.commit();
                }
            } else {
                spinnerCounter++;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public class GovListener2 implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
            if (spinnerCounter2 > 0) {
                @SuppressWarnings("ConstantConditions")
                String selected = parent.getItemAtPosition(pos).toString();

                // do this on all cpu's since MSM can have different governors on
                // each cpu
                // and it doesn't hurt other devices to do it
                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + selected + " > " + GOVERNOR2);
                bootPrefs.edit().putString("CORE1_GOVERNOR", selected).commit();

                final SharedPreferences.Editor editor = preferences.edit();
                editor.putString("GOVERNOR_2", selected);
                editor.commit();
            } else {
                spinnerCounter2++;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public class GovListener3 implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
            if (spinnerCounter3 > 0) {
                @SuppressWarnings("ConstantConditions")
                String selected = parent.getItemAtPosition(pos).toString();

                // do this on all cpu's since MSM can have different governors on
                // each cpu
                // and it doesn't hurt other devices to do it
                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + selected + " > " + GOVERNOR3);
                bootPrefs.edit().putString("CORE2_GOVERNOR", selected).commit();

                final SharedPreferences.Editor editor = preferences.edit();
                editor.putString("GOVERNOR_3", selected);
                editor.commit();
            } else {
                spinnerCounter3++;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public class GovListener4 implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
            if (spinnerCounter4 > 0) {
                @SuppressWarnings("ConstantConditions")
                String selected = parent.getItemAtPosition(pos).toString();

                // do this on all cpu's since MSM can have different governors on
                // each cpu
                // and it doesn't hurt other devices to do it
                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + selected + " > " + GOVERNOR4);
                bootPrefs.edit().putString("CORE3_GOVERNOR", selected).commit();

                final SharedPreferences.Editor editor = preferences.edit();
                editor.putString("GOVERNOR_4", selected);
                editor.commit();
            } else {
                spinnerCounter4++;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public class IOListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
            if (schedCounter > 0) {
                String selected = parent.getItemAtPosition(pos).toString();
                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + selected + " > " + IO_SCHEDULER);
                bootPrefs.edit().putString("IO_SCHEDULER", selected).commit();
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putString(IO_PREF, selected);
                editor.commit();
            } else {
                schedCounter++;
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
        if (progress <= minSliderProgress) {
            mMinSlider.setProgress(progress);
            mMinSpeedText.setText(toMHz(current));
            mMinFreqSetting = current;
        }
        mMaxSpeedText.setText(toMHz(current));
        mMaxFreqSetting = current;
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MAX_CPU, current);
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
        editor.putString(MIN_CPU, current);
        editor.commit();
    }

    // Convert raw collected values to formatted MhZ
    private static String toMHz(String mhzString) {
        if (Integer.valueOf(mhzString) != null)
            return String.valueOf(Integer.valueOf(mhzString) / 1000) + " MHz";
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
                    sleep(750);
                    final String curFreq = CPUHelper.readOneLineNotRoot(CURRENT_CPU);
                    mCurCPUHandler.sendMessage(mCurCPUHandler.obtainMessage(0,
                            curFreq));
                }
            } catch (InterruptedException e) {
                Log.e("CPU Thread", e.getMessage());
            }
        }
    }


    // Update real-time current frequency & stats in a separate thread
    protected static Handler mCurCPUHandler = new Handler() {
        public void handleMessage(Message msg) {
            mCurFreq.setText(toMHz((String) msg.obj));
            currX += 1;
            final int p = Integer.parseInt((String) msg.obj);
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
