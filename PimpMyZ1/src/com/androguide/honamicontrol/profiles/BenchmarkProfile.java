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
import com.androguide.honamicontrol.kernel.memory.MemoryManagementInterface;
import com.androguide.honamicontrol.kernel.misc.MiscInterface;
import com.androguide.honamicontrol.kernel.powermanagement.PowerManagementInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BenchmarkProfile extends Profile {
    @Override
    public String getCpuMaxFreq() {
        String[] freqs = CPUHelper.getAvailableCPUFreqs();
        return toShell(freqs[freqs.length - 1], CPUInterface.MAX_FREQ) + "\n" + toShell(freqs[freqs.length - 1], CPUInterface.SNAKE_CHARMER_MAX_FREQ);
    }

    @Override
    public String getGpuMaxFreq() {
        String[] freqs = CPUHelper.getAvailableGPUFreqs();
        return toShell(freqs[freqs.length - 1], GPUInterface.maxFreq);
    }

    @Override
    public String getCPUGovernor() {
        String[] govs = CPUHelper.readOneLineNotRoot(CPUInterface.GOVERNORS_LIST).split(" ");
        List<String> governors = Arrays.asList(govs);
        if (governors.contains("performance"))
            return toShell("performance", CPUInterface.GOVERNOR);
        else if (governors.contains("intelliactive"))
            return toShell("intelliactive", CPUInterface.GOVERNOR);
        else if (governors.contains("wheatley"))
            return toShell("wheatley", CPUInterface.GOVERNOR);
        else if (governors.contains("smartassV2"))
            return toShell("wheatley", CPUInterface.GOVERNOR);
        else if (governors.contains("Lionheart"))
            return toShell("Lionheart", CPUInterface.GOVERNOR);
        else if (governors.contains("lionheart"))
            return toShell("lionheart", CPUInterface.GOVERNOR);
        else if (governors.contains("ondemand"))
            return toShell("ondemand", CPUInterface.GOVERNOR);
        else
            return "";
    }

    @Override
    public String getGPUGovernor() {
        String[] govs = CPUHelper.readOneLineNotRoot(GPUInterface.availableGovernors).split(" ");
        List<String> governors = Arrays.asList(govs);
        if (governors.contains("performance"))
            return toShell("performance", GPUInterface.currGovernor);
        else if (governors.contains("msm-adreno-tz"))
            return toShell("msm-adreno-tz", GPUInterface.currGovernor);
        else
            return "";
    }

    @Override
    public String getIOScheduler() {
        ArrayList<String> scheds = CPUHelper.getAvailableIOSchedulers();
        if (scheds.contains("fiops"))
            return toShell("fiops", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("sio", IOTweaksInterface.IO_SCHEDULER_SD);
        else if (scheds.contains("bfq"))
            return toShell("bfq", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("bfq", IOTweaksInterface.IO_SCHEDULER_SD);
        else if (scheds.contains("cfq"))
            return toShell("cfq", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("deadline", IOTweaksInterface.IO_SCHEDULER_SD);
        else if (scheds.contains("row"))
            return toShell("row", IOTweaksInterface.IO_SCHEDULER) + "\n" + toShell("row", IOTweaksInterface.IO_SCHEDULER_SD);
        else
            return "";
    }

    @Override
    public String isIntelliplugEnabled() {
        return "start mpdecision\n" + "busybox echo 1 > " + PowerManagementInterface.MSM_MPDECISION_TOGGLE + "\n"
                + toShell("0", PowerManagementInterface.INTELLI_PLUG_TOGGLE);
    }

    @Override
    public String isEcoModeEnabled() {
        return toShell("0", PowerManagementInterface.INTELLI_PLUG_ECO_MODE);
    }

    @Override
    public String isPowerSuspendEnabled() {
        return toShell("0", PowerManagementInterface.POWER_SUSPEND_TOGGLE);
    }

    @Override
    public String isKSMEnabled() {
        return toShell("1", MemoryManagementInterface.KSM_TOGGLE);
    }

    @Override
    public String isDynFsyncEnabled() {
        return toShell("1", IOTweaksInterface.DYNAMIC_FSYNC_TOGGLE);
    }

    @Override
    public String isEntropyContributionEnabled() {
        return toShell("0", IOTweaksInterface.EMMC_ENTROPY_CONTRIB) + "\n" + toShell("0", IOTweaksInterface.SD_ENTROPY_CONTRIB);
    }

    @Override
    public String schedMCLevel() {
        return toShell("0", PowerManagementInterface.SCHED_MC_POWER_SAVINGS);
    }

    @Override
    public String readahead() {
        return toShell("1280", IOTweaksInterface.EMMC_READAHEAD) + "\n" + toShell("1280", IOTweaksInterface.SD_READAHEAD);
    }

    @Override
    public String KSMpagesToScan() {
        return toShell("512", MemoryManagementInterface.KSM_PAGES_TO_SCAN);
    }

    @Override
    public String KSMTimer() {
        return toShell("500", MemoryManagementInterface.KSM_SLEEP_TIMER);
    }
}
