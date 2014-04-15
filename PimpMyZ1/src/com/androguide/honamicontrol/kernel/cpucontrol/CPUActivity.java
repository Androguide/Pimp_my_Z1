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
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
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

    private static SharedPreferences bootPrefs;

    public static final String TAG = "CPUSettings";


    private static LineGraph graph;
    private static Line line;
    private static int currX = 0;
    private static int counter = 0;
    private static TextView mCurFreq;
    private SeekBar mMaxSlider;
    private SeekBar mMinSlider;
    private Spinner mGeneralGovernor;
    private static Spinner mGovernor;
    private static Spinner mGovernor2;
    private static Spinner mGovernor3;
    private static Spinner mGovernor4;
    private Switch perCoreGovernor;
    private TextView mMaxSpeedText;
    private TextView mMinSpeedText;
    private TextView mCoresOnline;
    private String[] availableFrequencies;
    private String mMaxFreqSetting;
    private String mMinFreqSetting;
    private CurCPUThread mCurCPUThread;
    private Boolean mIsTegra3 = false, snakeCharmerEnabled = true;
    private int mNumOfCpus = 0;
    private int govCounterGeneral = 0, govCounter = 0, govCounter2 = 0, govCounter3 = 0, govCounter4 = 0, tcpCounter = 0;

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
        String availableFrequenciesLine;

        mCoresOnline = (TextView) findViewById(R.id.cores_online);
        mGeneralGovernor = (Spinner) findViewById(R.id.general_governor);
        mGovernor = (Spinner) findViewById(R.id.governor);
        mGovernor2 = (Spinner) findViewById(R.id.governor2);
        mGovernor3 = (Spinner) findViewById(R.id.governor3);
        mGovernor4 = (Spinner) findViewById(R.id.governor4);

        if (Helpers.doesFileExist(STEPS)) {
            availableFrequenciesLine = CPUHelper.readOneLineNotRoot(STEPS);

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

        Switch snakeCharmer = (Switch) findViewById(R.id.snake_charmer_switch);
        if (!Helpers.doesFileExist(SNAKE_CHARMER_MAX_FREQ)) {
            LinearLayout cardSnakeCharmer = (LinearLayout) findViewById(R.id.card_snake_charmer);
            cardSnakeCharmer.setVisibility(View.GONE);
        } else {
            if (Helpers.doesFileExist(SNAKE_CHARMER_VERSION)) {
                TextView snakeTitle = (TextView) findViewById(R.id.snake_charmer);
                String snakeVersion = CPUHelper.readOneLineNotRoot(SNAKE_CHARMER_VERSION);
                snakeVersion = snakeVersion.replaceAll("version: ", "v");
                snakeTitle.setText(snakeTitle.getText() + " " + snakeVersion);
                if (snakeVersion.equals("v1.2")) {
                    TextView snakeDesc = (TextView) findViewById(R.id.snake_charmer_text);
                    snakeDesc.setText(snakeDesc.getText() + "\n" + "Your version (v1.2) is used as a built-in kernel feature and cannot be disabled");
                    snakeCharmer.setEnabled(false);
                }
            }

            if (bootPrefs.getBoolean("SNAKE_CHARMER", true)) {
                snakeCharmer.setChecked(true);
                snakeCharmerEnabled = true;
            }

            snakeCharmer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                    snakeCharmerEnabled = isOn;
                }
            });
        }

        int frequenciesNum = availableFrequencies.length - 1;

        Switch thermalControl = (Switch) findViewById(R.id.msm_thermal_switch);
        if (Helpers.doesFileExist(MSM_THERMAL)) {
            String thermal = CPUHelper.readOneLineNotRoot(MSM_THERMAL);
            if (thermal.equals("Y"))
                thermalControl.setChecked(true);
            else
                thermalControl.setChecked(false);

            thermalControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked)
                        Helpers.CMDProcessorWrapper.runSuCommand("echo Y > " + MSM_THERMAL);
                    else
                        Helpers.CMDProcessorWrapper.runSuCommand("echo N > " + MSM_THERMAL);

                    bootPrefs.edit().putBoolean("MSM_THERMAL", isChecked).commit();
                }
            });
        }
        thermalControl.setChecked(bootPrefs.getBoolean("MSM_THERMAL", true));

        perCoreGovernor = (Switch) findViewById(R.id.per_core_governors_switch);
        perCoreGovernor.setChecked(bootPrefs.getBoolean("PER_CORE_GOV", true));

        if (perCoreGovernor.isChecked()) {
            findViewById(R.id.card_general_governor).setVisibility(View.GONE);
            findViewById(R.id.card_per_core_governors).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.card_per_core_governors).setVisibility(View.GONE);
            findViewById(R.id.card_general_governor).setVisibility(View.VISIBLE);
        }

        perCoreGovernor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    findViewById(R.id.card_general_governor).setVisibility(View.GONE);
                    findViewById(R.id.card_per_core_governors).setVisibility(View.VISIBLE);
                    handleGovernors();
                } else {
                    findViewById(R.id.card_per_core_governors).setVisibility(View.GONE);
                    findViewById(R.id.card_general_governor).setVisibility(View.VISIBLE);
                    handleGovernors();
                }

                bootPrefs.edit().putBoolean("PER_CORE_GOV", isChecked).commit();
            }
        });


        String currentIo = "";
        if (Helpers.doesFileExist(IO_SCHEDULER))
            currentIo = CPUHelper.getIOScheduler();

        String currentTcp = "";
        if (Helpers.doesFileExist(CURR_TCP_ALGORITHM))
            currentTcp = Helpers.getCurrentTcpAlgorithm();

        String curMaxSpeed = "NaN";
        if (Helpers.doesFileExist(MAX_FREQ_ALL_CORES))
            curMaxSpeed = CPUHelper.readOneLineNotRoot(MAX_FREQ_ALL_CORES);
        else if (Helpers.doesFileExist(MAX_FREQ))
            curMaxSpeed = CPUHelper.readOneLineNotRoot(MAX_FREQ);

        String curMinSpeed = "NaN";
        if (Helpers.doesFileExist(MIN_FREQ_ALL_CORES))
            curMinSpeed = CPUHelper.readOneLineNotRoot(MIN_FREQ_ALL_CORES);
        else if (Helpers.doesFileExist(MIN_FREQ))
            curMinSpeed = CPUHelper.readOneLineNotRoot(MIN_FREQ);

        if (mIsTegra3) {
            String curTegraMaxSpeed = "NaN";
            if (Helpers.doesFileExist(TEGRA_MAX_FREQ)) {
                curTegraMaxSpeed = CPUHelper.readOneLineNotRoot(TEGRA_MAX_FREQ);
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
        }

        if (Helpers.doesFileExist(NUM_OF_CPUS))
            mNumOfCpus = Helpers.getNumOfCpus();

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
                startSupportActionMode(new ActionMode.Callback() {
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
                                if (mMaxFreqSetting != null && !mMaxFreqSetting.isEmpty()) {
                                    bootPrefs.edit().putString("CPU_MAX_FREQ", mMaxFreqSetting).commit();
                                    for (int i = 0; i < mNumOfCpus; i++)
                                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + mMaxFreqSetting + " > "
                                                + MAX_FREQ.replace("cpu0", "cpu" + i));

                                    if (snakeCharmerEnabled)
                                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + mMaxFreqSetting + " > " + SNAKE_CHARMER_MAX_FREQ);
                                }


                                if (mIsTegra3) {
                                    if (mMaxFreqSetting != null && !mMaxFreqSetting.isEmpty())
                                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + mMaxFreqSetting + " > "
                                                + TEGRA_MAX_FREQ);

                                    if (snakeCharmerEnabled)
                                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + mMaxFreqSetting + " > " + SNAKE_CHARMER_MAX_FREQ);
                                }
                                actionMode.finish();
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {

                    }
                });
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
                startSupportActionMode(new ActionMode.Callback() {
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
                                if (mMinFreqSetting != null && !mMinFreqSetting.isEmpty()) {
                                    bootPrefs.edit().putString("CPU_MIN_FREQ", mMinFreqSetting).commit();
                                    for (int i = 0; i < mNumOfCpus; i++)
                                        Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + mMinFreqSetting + " > "
                                                + MIN_FREQ.replace("cpu0", "cpu" + i));
                                }
                                actionMode.finish();
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {

                    }
                });
            }
        });

        handleGovernors();

        /** TCP Congestion Spinner */
        Spinner mTcp = (Spinner) findViewById(R.id.tcp);
        ArrayAdapter<CharSequence> tcpAdapter = new ArrayAdapter<CharSequence>(
                this, R.layout.spinner_row);
        tcpAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayList<String> algorithms = Helpers.getTcpAlgorithms();
        for (String algorithm : algorithms) tcpAdapter.add(algorithm);
        mTcp.setAdapter(tcpAdapter);
        mTcp.setSelection(algorithms.indexOf(currentTcp));
        mTcp.setOnItemSelectedListener(new TCPListener());

        onlineCoresPolling();
    }

    private void handleGovernors() {
        String currentGovernor = "";
        if (Helpers.doesFileExist(GOVERNOR))
            currentGovernor = CPUHelper.readOneLineNotRoot(GOVERNOR);

        String currentGovernor2 = "";
        if (Helpers.doesFileExist(GOVERNOR2))
            currentGovernor2 = CPUHelper.readOneLineNotRoot(GOVERNOR2);
        else
            currentGovernor2 = bootPrefs.getString("CORE1_GOVERNOR", "intelliactive");

        String currentGovernor3 = "";
        if (Helpers.doesFileExist(GOVERNOR3))
            currentGovernor3 = CPUHelper.readOneLineNotRoot(GOVERNOR3);
        else
            currentGovernor3 = bootPrefs.getString("CORE2_GOVERNOR", "intelliactive");

        String currentGovernor4 = "";
        if (Helpers.doesFileExist(GOVERNOR4))
            currentGovernor4 = CPUHelper.readOneLineNotRoot(GOVERNOR4);
        else
            currentGovernor4 = bootPrefs.getString("CORE3_GOVERNOR", "intelliactive");


        /** CPU Governor for all cores */
        String[] availableGovernorsGeneral = CPUHelper.readOneLineNotRoot(GOVERNORS_LIST).split(" ");
        ArrayAdapter<CharSequence> governorAdapterGeneral = new ArrayAdapter<CharSequence>(
                this, R.layout.spinner_row);
        governorAdapterGeneral
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (String availableGovernor : availableGovernorsGeneral) {
            governorAdapterGeneral.add(availableGovernor);
        }
        mGeneralGovernor.setAdapter(governorAdapterGeneral);
        mGeneralGovernor.setSelection(Arrays.asList(availableGovernorsGeneral).indexOf(
                currentGovernor));
        mGeneralGovernor.setOnItemSelectedListener(new GeneralGovListener());

        /** CPU Governor for core 0 */
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
        ArrayAdapter<CharSequence> governorAdapter2 = new ArrayAdapter<CharSequence>(
                this, R.layout.spinner_row);
        governorAdapter2
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (String anAvailableGovernors2 : availableGovernors) {
            governorAdapter2.add(anAvailableGovernors2);
        }
        mGovernor2.setAdapter(governorAdapter2);
        mGovernor2.setSelection(Arrays.asList(availableGovernors).indexOf(
                currentGovernor2));
        mGovernor2.setOnItemSelectedListener(new GovListener2());

        /** CPU Governor for core 2 */
        ArrayAdapter<CharSequence> governorAdapter3 = new ArrayAdapter<CharSequence>(
                this, R.layout.spinner_row);
        governorAdapter3
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (String anAvailableGovernors3 : availableGovernors) {
            governorAdapter3.add(anAvailableGovernors3);
        }
        mGovernor3.setAdapter(governorAdapter3);
        mGovernor3.setSelection(Arrays.asList(availableGovernors).indexOf(
                currentGovernor3));
        mGovernor3.setOnItemSelectedListener(new GovListener3());

        /** CPU Governor for core 3 */
        ArrayAdapter<CharSequence> governorAdapter4 = new ArrayAdapter<CharSequence>(
                this, R.layout.spinner_row);
        governorAdapter4
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (String anAvailableGovernors4 : availableGovernors) {
            governorAdapter4.add(anAvailableGovernors4);
        }
        mGovernor4.setAdapter(governorAdapter4);
        mGovernor4.setSelection(Arrays.asList(availableGovernors).indexOf(
                currentGovernor4));
        mGovernor4.setOnItemSelectedListener(new GovListener4());


        /** CPU Boost */
        if (Helpers.doesFileExist(KRAIT_BOOST)) {
            Switch cpuBoostSwitch = (Switch) findViewById(R.id.krait_boost_switch);
            String cpuBoostState = CPUHelper.readOneLineNotRoot(KRAIT_BOOST);
            if (cpuBoostState.equals("Y"))
                cpuBoostSwitch.setChecked(true);
            else
                cpuBoostSwitch.setChecked(false);

            cpuBoostSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   if (isChecked)
                       Helpers.CMDProcessorWrapper.runSuCommand("busybox echo Y > " + KRAIT_BOOST);
                    else
                       Helpers.CMDProcessorWrapper.runSuCommand("busybox echo N > " + KRAIT_BOOST);
                }
            });

        } else {
            findViewById(R.id.card_krait_boost).setVisibility(View.GONE);
        }

        /** CPU Informations */
        String pvs = "NaN";
        TextView pvsBinning = (TextView) findViewById(R.id.pvs_bin);
        if (Helpers.doesFileExist(PVS_BINNING)) {
            String rawTableName = CPUHelper.readOneLineNotRoot(PVS_BINNING);
            rawTableName = rawTableName.replaceAll("[\\D]", "");
            String[] params = rawTableName.split("");
            if (rawTableName.length() == 3)
                pvs = params[2];
        }

        pvsBinning.setText(getString(R.string.pvs_binning) + " " + pvs);
    }

    public class GeneralGovListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
            if (govCounterGeneral > 0 && !perCoreGovernor.isChecked()) {
                @SuppressWarnings("ConstantConditions")
                String selected = parent.getItemAtPosition(pos).toString();
                // do this on all cpu's since MSM can have different governors on
                // each cpu
                // and it doesn't hurt other devices to do it

                if (Helpers.doesFileExist(GOVERNOR_ALL_CORES)) {
                    Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + selected + " > " + GOVERNOR_ALL_CORES);

                } else {
                    for (int i = 0; i < mNumOfCpus; i++) {
                        Helpers.CMDProcessorWrapper.runSuCommand(
                                "busybox echo " + selected + " > " + FIRST_PART_CPU + i + SECOND_PART_GOV
                        );
                        bootPrefs.edit().putString("CORE" + i + "_GOVERNOR", selected).commit();
                    }
                }

            } else {
                govCounterGeneral++;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public class GovListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
            if (govCounter > 0) {
                @SuppressWarnings("ConstantConditions")
                String selected = parent.getItemAtPosition(pos).toString();
                if (!selected.equals("Core Offline")) {
                    // do this on all cpu's since MSM can have different governors on
                    // each cpu
                    // and it doesn't hurt other devices to do it
                    Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + selected + " > " + GOVERNOR);
                    bootPrefs.edit().putString("CORE0_GOVERNOR", selected).commit();
                }
            } else {
                govCounter++;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public class GovListener2 implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
            if (govCounter2 > 0 && perCoreGovernor.isChecked()) {
                @SuppressWarnings("ConstantConditions")
                String selected = parent.getItemAtPosition(pos).toString();

                // do this on all cpu's since MSM can have different governors on
                // each cpu
                // and it doesn't hurt other devices to do it
                Helpers.CMDProcessorWrapper.runSuCommand(
                        "buysbox echo 1 > " + CPUInterface.CPU1_ONLINE +
                                "\nbusybox echo " + selected + " > " + GOVERNOR2
                );
            } else {
                govCounter2++;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public class GovListener3 implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
            if (govCounter3 > 0 && perCoreGovernor.isChecked()) {
                @SuppressWarnings("ConstantConditions")
                String selected = parent.getItemAtPosition(pos).toString();

                // do this on all cpu's since MSM can have different governors on
                // each cpu
                // and it doesn't hurt other devices to do it
                Helpers.CMDProcessorWrapper.runSuCommand(
                        "buysbox echo 1 > " + CPUInterface.CPU2_ONLINE +
                                "\nbusybox echo " + selected + " > " + GOVERNOR3
                );
                bootPrefs.edit().putString("CORE2_GOVERNOR", selected).commit();
            } else {
                govCounter3++;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public class GovListener4 implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
            if (govCounter4 > 0 && perCoreGovernor.isChecked()) {
                @SuppressWarnings("ConstantConditions")
                String selected = parent.getItemAtPosition(pos).toString();

                // do this on all cpu's since MSM can have different governors on
                // each cpu
                // and it doesn't hurt other devices to do it
                Helpers.CMDProcessorWrapper.runSuCommand(
                        "buysbox echo 1 > " + CPUInterface.CPU3_ONLINE +
                                "\nbusybox echo " + selected + " > " + GOVERNOR4
                );
                bootPrefs.edit().putString("CORE3_GOVERNOR", selected).commit();
            } else {
                govCounter4++;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

//    public class IOListener implements OnItemSelectedListener {
//        public void onItemSelected(AdapterView<?> parent, View view, int pos,
//                                   long id) {
//            if (schedCounter > 0) {
//                String selected = CPUHelper.getAvailableIOSchedulers()[pos];
//                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + selected + " > " + IO_SCHEDULER);
//                bootPrefs.edit().putString("IO_SCHEDULER", selected).commit();
//            } else {
//                schedCounter++;
//            }
//        }
//
//        public void onNothingSelected(AdapterView<?> parent) {
//        }
//    }

    public class TCPListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                   long id) {
            if (tcpCounter > 0) {
                String selected = Helpers.getTcpAlgorithms().get(pos);
                Helpers.CMDProcessorWrapper.runSuCommand("busybox echo " + selected + " > " + CURR_TCP_ALGORITHM + " && "
                        + SYSCTL_TCP_ALGORITHM + selected);
                bootPrefs.edit().putString("TCP_ALGORITHM", selected).commit();
            } else {
                tcpCounter++;
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
            } catch (InterruptedException ignored) {
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
    }

    // Convert raw collected values to formatted MhZ
    private static String toMHz(String mhzString) {
        try {
            if (Integer.valueOf(mhzString) != null) {
                int val = 0;
                try {
                    val = Integer.valueOf(mhzString);
                } catch (NumberFormatException e) {
                    Log.e("ToMHZ", e.getMessage());
                }
                return String.valueOf(val / 1000) + " MHz";
            } else
                return "NaN";
        } catch (NumberFormatException e) {
            Log.e("CPU-toMHz", e.getMessage());
            return "NaN";
        }
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
                    String curFreq = "";
                    if (Helpers.doesFileExist(CURRENT_CPU))
                        curFreq = CPUHelper.readOneLineNotRoot(CURRENT_CPU);

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
            try {
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
                // HoloGraph card
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
            } catch (NumberFormatException e) {
                Log.e("CPUHandler", e.getMessage());
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

    public void onlineCoresPolling() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String cores = CPUHelper.readOneLineNotRoot(OFFLINE_CPUS);
                String displayed;
                if (cores.equals("1-3"))
                    displayed = "1/4";
                else if (cores.equals("2-3"))
                    displayed = "2/4";
                else if (cores.equals("3-3"))
                    displayed = "3/4";
                else
                    displayed = "4/4";
                mCoresOnline.setText("Cores Online: " + displayed);
                onlineCoresPolling();
            }

        }, 125);
    }
}
