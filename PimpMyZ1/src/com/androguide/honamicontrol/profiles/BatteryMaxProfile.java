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

package com.androguide.honamicontrol.profiles;

import com.androguide.honamicontrol.helpers.CPUHelper;
import com.androguide.honamicontrol.kernel.cpucontrol.CPUInterface;
import com.androguide.honamicontrol.kernel.gpucontrol.GPUInterface;
import com.androguide.honamicontrol.kernel.iotweaks.IOTweaksInterface;
import com.androguide.honamicontrol.kernel.misc.MiscInterface;
import com.androguide.honamicontrol.kernel.powermanagement.PowerManagementInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BatteryMaxProfile extends Profile {

    @Override
    public String getCpuMaxFreq() {
        String[] freqs = CPUHelper.getAvailableCPUFreqs();
        prefs.edit().putString("CPU_MAX_FREQ", freqs[freqs.length - 4]).commit();
        return toShell(freqs[freqs.length - 4], CPUInterface.MAX_FREQ) + "\n" + toShell(freqs[freqs.length - 4], CPUInterface.SNAKE_CHARMER_MAX_FREQ);
    }

    @Override
    public String getGpuMaxFreq() {
        String[] freqs = CPUHelper.getAvailableGPUFreqs();
        List<String> frequencies = Arrays.asList(freqs);
        if (frequencies.contains("320000000")) {
            prefs.edit().putString("GPU_MAX_FREQ", "320000000").commit();
            return toShell("320000000", GPUInterface.maxFreq);
        } else if (frequencies.size() >= 3) {
            prefs.edit().putString("GPU_MAX_FREQ", frequencies.get(3)).commit();
            return frequencies.get(3);
        } else return "";
    }

    @Override
    public String getCPUGovernor() {
        String[] govs = CPUHelper.readOneLineNotRoot(CPUInterface.GOVERNORS_LIST).split(" ");
        List<String> governors = Arrays.asList(govs);
        if (governors.contains("powersave")) {
            prefs.edit().putString("CORE0_GOVERNOR", "powersave").commit();
            return toShell("powersave", CPUInterface.GOVERNOR);
        } else if (governors.contains("conservative")) {
            prefs.edit().putString("CORE0_GOVERNOR", "conservative").commit();
            return toShell("conservative", CPUInterface.GOVERNOR);
        } else if (governors.contains("intellidemand")) {
            prefs.edit().putString("CORE0_GOVERNOR", "intellidemand").commit();
            return toShell("intellidemand", CPUInterface.GOVERNOR);
        } else
            return "";
    }

    @Override
    public String getGPUGovernor() {
        String[] govs = CPUHelper.readOneLineNotRoot(GPUInterface.availableGovernors).split(" ");
        List<String> governors = Arrays.asList(govs);
        if (governors.contains("powersave")) {
            prefs.edit().putString("GPU_GOVERNOR", "powersave").commit();
            return toShell("powersave", GPUInterface.currGovernor);
        } else if (governors.contains("simple_ondemand")) {
            prefs.edit().putString("GPU_GOVERNOR", "simple_ondemand").commit();
            return toShell("simple_ondemand", GPUInterface.currGovernor);
        } else if (governors.contains("msm_cpufreq")) {
            prefs.edit().putString("GPU_GOVERNOR", "msm_cpufreq").commit();
            return toShell("msm_cpufreq", GPUInterface.currGovernor);
        } else {
            prefs.edit().putString("GPU_GOVERNOR", "msm-adreno-tz").commit();
            return toShell("msm-adreno-tz", GPUInterface.currGovernor);
        }
    }

    @Override
    public String getIOScheduler() {
        ArrayList<String> scheds = CPUHelper.getAvailableIOSchedulers();
        if (scheds.contains("noop")) {
            prefs.edit().putString("IO_SCHEDULER", "noop").commit();
            prefs.edit().putString("IO_SCHEDULER_SD", "noop").commit();
            return toShell("noop", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("noop", IOTweaksInterface.IO_SCHEDULER_SD);
        } else if (scheds.contains("sio")) {
            prefs.edit().putString("IO_SCHEDULER", "sio").commit();
            prefs.edit().putString("IO_SCHEDULER_SD", "sio").commit();
            return toShell("sio", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("sio", IOTweaksInterface.IO_SCHEDULER_SD);
        } else if (scheds.contains("bfq")) {
            prefs.edit().putString("IO_SCHEDULER", "bfq").commit();
            prefs.edit().putString("IO_SCHEDULER_SD", "bfq").commit();
            return toShell("bfq", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("bfq", IOTweaksInterface.IO_SCHEDULER_SD);
        } else if (scheds.contains("deadline")) {
            prefs.edit().putString("IO_SCHEDULER", "deadline").commit();
            prefs.edit().putString("IO_SCHEDULER_SD", "deadline").commit();
            return toShell("deadline", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("deadline", IOTweaksInterface.IO_SCHEDULER_SD);
        } else if (scheds.contains("row")) {
            prefs.edit().putString("IO_SCHEDULER", "row").commit();
            prefs.edit().putString("IO_SCHEDULER_SD", "row").commit();
            return toShell("row", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("row", IOTweaksInterface.IO_SCHEDULER_SD);
        } else
            return "";
    }

    @Override
    public String isIntelliplugEnabled() {
        prefs.edit().putBoolean("INTELLI_PLUG", true).commit();
        return "stop mpdecision\n" + "busybox echo 0 > " + PowerManagementInterface.MSM_MPDECISION_TOGGLE + "\n"
                + toShell("1", PowerManagementInterface.INTELLI_PLUG_TOGGLE);
    }

    @Override
    public String isEcoModeEnabled() {
        prefs.edit().putBoolean("INTELLI_PLUG_ECO", true).commit();
        return toShell("1", PowerManagementInterface.INTELLI_PLUG_ECO_MODE);
    }

    @Override
    public String isPowerSuspendEnabled() {
        prefs.edit().putBoolean("POWER_SUSPEND", true).commit();
        return toShell("1", PowerManagementInterface.POWER_SUSPEND_TOGGLE);
    }

    @Override
    public String isKSMEnabled() {
        prefs.edit().putBoolean("KSM_ENABLED", false).commit();
        return toShell("0", MiscInterface.KSM_TOGGLE);
    }

    @Override
    public String isDynFsyncEnabled() {
        prefs.edit().putBoolean("DYNAMIC_FSYNC", false).commit();
        return toShell("0", IOTweaksInterface.DYNAMIC_FSYNC_TOGGLE);
    }

    @Override
    public String isEntropyContributionEnabled() {
        prefs.edit().putBoolean("EMMC_ENTROPY_CONTRIB", false).commit();
        prefs.edit().putBoolean("SD_ENTROPY_CONTRIB", false).commit();
        return toShell("0", IOTweaksInterface.EMMC_ENTROPY_CONTRIB) + "\n" + toShell("0", IOTweaksInterface.SD_ENTROPY_CONTRIB);
    }

    @Override
    public String schedMCLevel() {
        prefs.edit().putInt("SCHED_MC_LEVEL", 2).commit();
        return toShell("2", PowerManagementInterface.SCHED_MC_POWER_SAVINGS);
    }

    @Override
    public String readahead() {
        prefs.edit().putString("EMMC_READAHEAD", "512").commit();
        prefs.edit().putString("SD_READAHEAD", "512").commit();
        return toShell("512", IOTweaksInterface.EMMC_READAHEAD) + "\n" + toShell("512", IOTweaksInterface.SD_READAHEAD);
    }

    @Override
    public String KSMpagesToScan() {
        prefs.edit().putString("KSM_PAGES_TO_SCAN", "0").commit();
        return toShell("0", MiscInterface.KSM_PAGES_TO_SCAN);
    }

    @Override
    public String KSMTimer() {
        prefs.edit().putString("KSM_SLEEP_TIMER", "0").commit();
        return toShell("0", MiscInterface.KSM_SLEEP_TIMER);
    }
}