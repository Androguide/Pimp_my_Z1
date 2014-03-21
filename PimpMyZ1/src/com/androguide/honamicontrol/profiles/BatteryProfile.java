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

public class BatteryProfile extends Profile {

    @Override
    public String getCpuMaxFreq() {
        String[] freqs = CPUHelper.getAvailableCPUFreqs();
        return toShell(freqs[freqs.length - 3], CPUInterface.MAX_FREQ) + "\n" + toShell(freqs[freqs.length - 3], CPUInterface.SNAKE_CHARMER_MAX_FREQ);
    }

    @Override
    public String getGpuMaxFreq() {
        String[] freqs = CPUHelper.getAvailableGPUFreqs();
        List<String> frequencies = Arrays.asList(freqs);
        if (frequencies.contains("389000000"))
            return toShell("389000000", GPUInterface.maxFreq);
        else if (frequencies.contains("320000000"))
            return toShell("320000000", GPUInterface.maxFreq);
        else return toShell("450000000", GPUInterface.maxFreq);
    }

    @Override
    public String getCPUGovernor() {
        String[] govs = CPUHelper.readOneLineNotRoot(CPUInterface.GOVERNORS_LIST).split(" ");
        List<String> governors = Arrays.asList(govs);
        if (governors.contains("intellidemand"))
            return toShell("intellidemand", CPUInterface.GOVERNOR) + "\n" + toShell("100", "/sys/devices/system/cpu/cpufreq/intellidemand/powersave_bias");
        else if (governors.contains("lionheart"))
            return toShell("lionheart", CPUInterface.GOVERNOR);
        else if (governors.contains("Lionheart"))
            return toShell("Lionheart", CPUInterface.GOVERNOR);
        else if (governors.contains("lagfree"))
            return toShell("lagfree", CPUInterface.GOVERNOR);
        else if (governors.contains("conservative"))
            return toShell("conservative", CPUInterface.GOVERNOR);
        else
            return "";
    }

    @Override
    public String getGPUGovernor() {
        String[] govs = CPUHelper.readOneLineNotRoot(GPUInterface.availableGovernors).split(" ");
        List<String> governors = Arrays.asList(govs);
        if (governors.contains("powersave"))
            return toShell("powersave", GPUInterface.currGovernor);
        else if (governors.contains("simple_ondemand"))
            return toShell("simple_ondemand", GPUInterface.currGovernor);
        else if (governors.contains("msm_cpufreq"))
            return toShell("msm_cpufreq", GPUInterface.currGovernor);
        else
            return toShell("msm-adreno-tz", GPUInterface.currGovernor);
    }

    @Override
    public String getIOScheduler() {
        ArrayList<String> scheds = CPUHelper.getAvailableIOSchedulers();
        if (scheds.contains("sio"))
            return toShell("sio", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("sio", IOTweaksInterface.IO_SCHEDULER_SD);
        else if (scheds.contains("bfq"))
            return toShell("bfq", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("bfq", IOTweaksInterface.IO_SCHEDULER_SD);
        else if (scheds.contains("deadline"))
            return toShell("deadline", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("deadline", IOTweaksInterface.IO_SCHEDULER_SD);
        else if (scheds.contains("row"))
            return toShell("row", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("row", IOTweaksInterface.IO_SCHEDULER_SD);
        else
            return "";
    }

    @Override
    public String isIntelliplugEnabled() {
        return "stop mpdecision\n" + "busybox echo 0 > " + PowerManagementInterface.MSM_MPDECISION_TOGGLE + "\n"
                + toShell("1", PowerManagementInterface.INTELLI_PLUG_TOGGLE);
    }

    @Override
    public String isEcoModeEnabled() {
        return toShell("1", PowerManagementInterface.INTELLI_PLUG_ECO_MODE);
    }

    @Override
    public String isPowerSuspendEnabled() {
        return toShell("1", PowerManagementInterface.POWER_SUSPEND_TOGGLE);
    }

    @Override
    public String isKSMEnabled() {
        return toShell("0", MiscInterface.KSM_TOGGLE);
    }

    @Override
    public String isDynFsyncEnabled() {
        return toShell("0", IOTweaksInterface.DYNAMIC_FSYNC_TOGGLE);
    }

    @Override
    public String isEntropyContributionEnabled() {
        return toShell("0", IOTweaksInterface.EMMC_ENTROPY_CONTRIB) + "\n" + toShell("0", IOTweaksInterface.SD_ENTROPY_CONTRIB);
    }

    @Override
    public String schedMCLevel() {
        return toShell("2", PowerManagementInterface.SCHED_MC_POWER_SAVINGS);
    }

    @Override
    public String readahead() {
        return toShell("512", IOTweaksInterface.EMMC_READAHEAD) + "\n" + toShell("512", IOTweaksInterface.SD_READAHEAD);
    }

    @Override
    public String KSMpagesToScan() {
        return "";
    }

    @Override
    public String KSMTimer() {
        return "";
    }
}

