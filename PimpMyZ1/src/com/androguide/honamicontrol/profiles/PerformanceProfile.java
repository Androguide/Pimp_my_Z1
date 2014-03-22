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

public class PerformanceProfile extends Profile {

    @Override
    public String getCpuMaxFreq() {
        String[] freqs = CPUHelper.getAvailableCPUFreqs();
        prefs.edit().putString("CPU_MAX_FREQ", freqs[freqs.length - 1]).commit();
        return toShell(freqs[freqs.length - 1], CPUInterface.MAX_FREQ) + "\n" + toShell(freqs[freqs.length - 1], CPUInterface.SNAKE_CHARMER_MAX_FREQ);
    }

    @Override
    public String getGpuMaxFreq() {
        String[] freqs = CPUHelper.getAvailableGPUFreqs();
        prefs.edit().putString("GPU_MAX_FREQ", freqs[freqs.length - 1]).commit();
        return toShell(freqs[freqs.length - 1], GPUInterface.maxFreq);
    }

    @Override
    public String getCPUGovernor() {
        String[] govs = CPUHelper.readOneLineNotRoot(CPUInterface.GOVERNORS_LIST).split(" ");
        List<String> governors = Arrays.asList(govs);
        if (governors.contains("intelliactive")) {
            prefs.edit().putString("CORE0_GOVERNOR", "intelliactive").commit();
            return toShell("intelliactive", CPUInterface.GOVERNOR);
        } else if (governors.contains("wheatley")) {
            prefs.edit().putString("CORE0_GOVERNOR", "wheatley").commit();
            return toShell("wheatley", CPUInterface.GOVERNOR);
        } else if (governors.contains("smartassV2")) {
            prefs.edit().putString("CORE0_GOVERNOR", "smartassV2").commit();
            return toShell("smartassV2", CPUInterface.GOVERNOR);
        } else if (governors.contains("interactivex2")) {
            prefs.edit().putString("CORE0_GOVERNOR", "interactivex2").commit();
            return toShell("interactivex2", CPUInterface.GOVERNOR);
        } else if (governors.contains("badass")) {
            prefs.edit().putString("CORE0_GOVERNOR", "badass").commit();
            return toShell("badass", CPUInterface.GOVERNOR);
        } else if (governors.contains("")) {
            prefs.edit().putString("CORE0_GOVERNOR", "interactive").commit();
            return toShell("interactive", CPUInterface.GOVERNOR);
        } else
            return "";
    }

    @Override
    public String getGPUGovernor() {
        prefs.edit().putString("GPU_GOVERNOR", "msm-adreno-tz").commit();
        return toShell("msm-adreno-tz", GPUInterface.currGovernor);
    }

    @Override
    public String getIOScheduler() {
        ArrayList<String> scheds = CPUHelper.getAvailableIOSchedulers();
        if (scheds.contains("fiops")) {
            prefs.edit().putString("IO_SCHEDULER", "fiops").commit();
            prefs.edit().putString("IO_SCHEDULER_SD", "fiops").commit();
            return toShell("fiops", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("sio", IOTweaksInterface.IO_SCHEDULER_SD);
        } else if (scheds.contains("bfq")) {
            prefs.edit().putString("IO_SCHEDULER", "bfq").commit();
            prefs.edit().putString("IO_SCHEDULER_SD", "bfq").commit();
            return toShell("bfq", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("bfq", IOTweaksInterface.IO_SCHEDULER_SD);
        } else if (scheds.contains("cfq")) {
            prefs.edit().putString("IO_SCHEDULER", "cfq").commit();
            prefs.edit().putString("IO_SCHEDULER_SD", "cfq").commit();
            return toShell("cfq", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("deadline", IOTweaksInterface.IO_SCHEDULER_SD);
        } else if (scheds.contains("row")) {
            prefs.edit().putString("IO_SCHEDULER", "row").commit();
            prefs.edit().putString("IO_SCHEDULER_SD", "row").commit();
            return toShell("row", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("row", IOTweaksInterface.IO_SCHEDULER_SD);
        } else
            return "";
    }

    @Override
    public String isIntelliplugEnabled() {
        prefs.edit().putBoolean("INTELLI_PLUG", false).commit();
        return "start mpdecision\n" + "busybox echo 1 > " + PowerManagementInterface.MSM_MPDECISION_TOGGLE + "\n"
                + toShell("0", PowerManagementInterface.INTELLI_PLUG_TOGGLE);
    }

    @Override
    public String isEcoModeEnabled() {
        prefs.edit().putBoolean("INTELLI_PLUG_ECO", false).commit();
        return toShell("0", PowerManagementInterface.INTELLI_PLUG_ECO_MODE);
    }

    @Override
    public String isPowerSuspendEnabled() {
        prefs.edit().putBoolean("POWER_SUSPEND", true).commit();
        return toShell("0", PowerManagementInterface.POWER_SUSPEND_TOGGLE);
    }

    @Override
    public String isKSMEnabled() {
        prefs.edit().putBoolean("KSM_ENABLED", true).commit();
        return toShell("1", MiscInterface.KSM_TOGGLE);
    }

    @Override
    public String isDynFsyncEnabled() {
        prefs.edit().putBoolean("DYNAMIC_FSYNC", true).commit();
        return toShell("1", IOTweaksInterface.DYNAMIC_FSYNC_TOGGLE);
    }

    @Override
    public String isEntropyContributionEnabled() {
        prefs.edit().putBoolean("EMMC_ENTROPY_CONTRIB", false).commit();
        prefs.edit().putBoolean("SD_ENTROPY_CONTRIB", false).commit();
        return toShell("0", IOTweaksInterface.EMMC_ENTROPY_CONTRIB) + "\n" + toShell("0", IOTweaksInterface.SD_ENTROPY_CONTRIB);
    }

    @Override
    public String schedMCLevel() {
        prefs.edit().putInt("SCHED_MC_LEVEL", 0).commit();
        return toShell("0", PowerManagementInterface.SCHED_MC_POWER_SAVINGS);
    }

    @Override
    public String readahead() {
        prefs.edit().putString("EMMC_READAHEAD", "1024").commit();
        prefs.edit().putString("SD_READAHEAD", "1024").commit();
        return toShell("1024", IOTweaksInterface.EMMC_READAHEAD) + "\n" + toShell("1024", IOTweaksInterface.SD_READAHEAD);
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