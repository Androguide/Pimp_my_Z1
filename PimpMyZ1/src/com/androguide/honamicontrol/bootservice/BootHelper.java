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

package com.androguide.honamicontrol.bootservice;

import android.content.SharedPreferences;

import com.androguide.honamicontrol.helpers.CMDProcessor.CMDProcessor;
import com.androguide.honamicontrol.helpers.Helpers;
import com.androguide.honamicontrol.kernel.cpucontrol.CPUInterface;
import com.androguide.honamicontrol.kernel.gpucontrol.GPUInterface;
import com.androguide.honamicontrol.kernel.iotweaks.IOTweaksInterface;
import com.androguide.honamicontrol.kernel.memory.MemoryManagementInterface;
import com.androguide.honamicontrol.kernel.misc.MiscInterface;
import com.androguide.honamicontrol.kernel.powermanagement.PowerManagementInterface;
import com.androguide.honamicontrol.soundcontrol.SoundControlInterface;
import com.androguide.honamicontrol.touchscreen.TouchScreenInterface;

public class BootHelper {
    public static void generateScriptFromPrefs(SharedPreferences prefs) {
        int CPU_MAX_FREQ = Integer.valueOf(prefs.getString("CPU_MAX_FREQ", "2150400"));
        int CPU_MIN_FREQ = Integer.valueOf(prefs.getString("CPU_MIN_FREQ", "300000"));
        int GPU_MAX_FREQ = Integer.valueOf(prefs.getString("GPU_MAX_FREQ", "450000000"));
        int GPU_MIN_FREQ = Integer.valueOf(prefs.getString("GPU_MIN_FREQ", "200000000"));
        int SCHED_MC_LEVEL = prefs.getInt("SCHED_MC_LEVEL", 0);
        int KSM_PAGES_TO_SCAN = prefs.getInt(MemoryManagementInterface.KSM_PAGES_TO_SCAN.replaceAll("/", "_"), 100);
        int KSM_SLEEP_TIMER = prefs.getInt(MemoryManagementInterface.KSM_SLEEP_TIMER.replaceAll("/", "_"), 500);
        int FASTCHARGE_MODE = prefs.getInt("FASTCHARGE_MODE", 0);
        int HOTPLUG_DRIVER = prefs.getInt("HOTPLUG_DRIVER", 0);
        int ALUCARD_CORES = prefs.getInt("ALUCARD_CORES", 4);
        int INTELLI_CORES = prefs.getInt("INTELLI_PLUG_ECO_CORES", 2);
        int VFS_CACHE_PRESSURE = prefs.getInt(MemoryManagementInterface.VFS_CACHE_PRESSURE, 100);
        int SWAPPINESS = prefs.getInt(MemoryManagementInterface.SWAPPINESS, 60);
        int DIRTY_RATIO = prefs.getInt(MemoryManagementInterface.DIRTY_RATIO, 20);
        int DIRTY_BG_RATIO = prefs.getInt(MemoryManagementInterface.DIRTY_BG_RATIO, 5);
        int DIRTY_WRITEBACK = prefs.getInt(MemoryManagementInterface.DIRTY_WRITEBACK_CENTISECS, 500);
        int DIRTY_EXPIRE = prefs.getInt(MemoryManagementInterface.DIRTY_EXPIRE_CENTISECS, 200);
        Boolean DYNAMIC_FSYNC = prefs.getBoolean("DYNAMIC_FSYNC", false);
        Boolean INTELLI_PLUG_ECO = prefs.getBoolean("INTELLI_PLUG_ECO", false);
        Boolean POWER_SUSPEND = prefs.getBoolean("POWER_SUSPEND", false);
        Boolean PEN_MODE = prefs.getBoolean("PEN_MODE", false);
        Boolean GLOVE_MODE = prefs.getBoolean("GLOVE_MODE", false);
        Boolean DT2WAKE = prefs.getBoolean("DT2WAKE", false);
        Boolean KSM_ENABLED = prefs.getBoolean("KSM_ENABLED", false);
        Boolean SNAKE_CHARMER = prefs.getBoolean("SNAKE_CHARMER", true);
        Boolean EMMC_ENTROPY = prefs.getBoolean("EMMC_ENTROPY_CONTRIB", true);
        Boolean SD_ENTROPY = prefs.getBoolean("SD_ENTROPY_CONTRIB", true);
        Boolean MSM_THERMAL = prefs.getBoolean("MSM_THERMAL", false);
        String CORE0_GOV = prefs.getString("CORE0_GOVERNOR", "intellidemand");
        String CORE1_GOV = prefs.getString("CORE1_GOVERNOR", "intellidemand");
        String CORE2_GOV = prefs.getString("CORE2_GOVERNOR", "intellidemand");
        String CORE3_GOV = prefs.getString("CORE3_GOVERNOR", "intellidemand");
        String GPU_GOV = prefs.getString("GPU_GOVERNOR", "msm-adreno-tz");
        String IO_SCHED_EMMC = prefs.getString("IO_SCHEDULER", "row");
        String IO_SCHED_SD = prefs.getString("IO_SCHEDULER_SD", "row");
        String TCP_ALGORITHM = prefs.getString("TCP_ALGORITHM", "cubic");
        String EMMC_READAHEAD = prefs.getString("EMMC_READAHEAD", "1024");
        String SD_READAHEAD = prefs.getString("SD_READAHEAD", "1024");
        String SC_MIC = prefs.getString("SC_MIC", "0 0 255");
        String SC_CAM_MIC = prefs.getString("SC_CAM_MIC", "0 0 255");
        String SC_HEADPHONE_PA = prefs.getString("HEADPHONE_PA", "38 38 179");
        String SC_HEADPHONE = prefs.getString("HEADPHONE", "0 0 255");
        String SC_SPEAKER = prefs.getString("SPEAKER", "0 0 255");
        String FASTCHARGE_LEVEL = prefs.getString("FASTCHARGE_LEVEL", "500");

        String applyMaxCpuFreq = "busybox echo " + CPU_MAX_FREQ + " > " + CPUInterface.MAX_FREQ;
        String applyMsmThermal = "";
        if (SNAKE_CHARMER) applyMaxCpuFreq += "\nbusybox echo " + CPU_MAX_FREQ + " > " + CPUInterface.SNAKE_CHARMER_MAX_FREQ;
        if (MSM_THERMAL)
            applyMsmThermal = "busybox echo Y > " + CPUInterface.MSM_THERMAL;
        else
           applyMsmThermal = "busybox echo N > " + CPUInterface.MSM_THERMAL;

        String applyMinCpuFreq = "busybox echo " + CPU_MIN_FREQ + " > " + CPUInterface.MIN_FREQ;
        String applyMaxGpuFreq = "busybox echo " + GPU_MAX_FREQ + " > " + GPUInterface.maxFreq;
        String applyMinGpuFreq = "busybox echo " + GPU_MIN_FREQ + " > " + GPUInterface.minFreq;
        String applyCore0Governor = "busybox echo " + CORE0_GOV + " > " + CPUInterface.GOVERNOR;
        String applyCore1Governor = "busybox echo " + CORE1_GOV + " > " + CPUInterface.GOVERNOR2;
        String applyCore2Governor = "busybox echo " + CORE2_GOV + " > " + CPUInterface.GOVERNOR3;
        String applyCore3Governor = "busybox echo " + CORE3_GOV + " > " + CPUInterface.GOVERNOR4;
        String applyGpuGovernor = "busybox echo " + GPU_GOV + " > " + GPUInterface.currGovernor;
        String applyIOScheduler = "busybox echo " + IO_SCHED_EMMC + " > " + IOTweaksInterface.IO_SCHEDULER;
        String applyIOSchedulerSD = "busybox echo " + IO_SCHED_SD + " > " + IOTweaksInterface.IO_SCHEDULER_SD;
        String applyTcpAlgorithm = "busybox echo " + TCP_ALGORITHM + " > " + CPUInterface.CURR_TCP_ALGORITHM + "\n" + CPUInterface.SYSCTL_TCP_ALGORITHM + TCP_ALGORITHM;
        String applySchedMcLevel = "busybox echo " + SCHED_MC_LEVEL + " > " + PowerManagementInterface.SCHED_MC_POWER_SAVINGS;
        String applyDynamicFsync = "busybox echo " + getIntFromBoolean(DYNAMIC_FSYNC) + " > " + IOTweaksInterface.DYNAMIC_FSYNC_TOGGLE;
        String applyIntelliPlugEco = "busybox echo " + getIntFromBoolean(INTELLI_PLUG_ECO) + " > " + PowerManagementInterface.INTELLI_PLUG_ECO_MODE;
        String applyPowerSuspend = "busybox echo " + getIntFromBoolean(POWER_SUSPEND) + " > " + PowerManagementInterface.POWER_SUSPEND_TOGGLE;
        String applyPenMode = "chown system:system " + TouchScreenInterface.PEN_MODE + "\nbusybox echo " + getIntFromBoolean(PEN_MODE) + " > " + TouchScreenInterface.PEN_MODE;
        String applyGloveMode = "chown system:system " + TouchScreenInterface.PEN_MODE + "\nbusybox echo " + getIntFromBoolean(GLOVE_MODE) + " > " + TouchScreenInterface.GLOVE_MODE;
        String applyDt2Wake = "busybox echo " + getIntFromBoolean(DT2WAKE) + " > " + TouchScreenInterface.DT2WAKE;
        String applyScHeadphone = "busybox echo " + SC_HEADPHONE + " > " + SoundControlInterface.FAUX_SC_HEADPHONE;
        String applyScHeadphonePa = "busybox echo " + SC_HEADPHONE_PA + " > " + SoundControlInterface.FAUX_SC_HEADPHONE_POWERAMP;
        String applyScSpeaker = "busybox echo " + SC_SPEAKER + " > " + SoundControlInterface.FAUX_SC_SPEAKER;
        String applyScMic = "busybox echo " + SC_MIC + " > " + SoundControlInterface.FAUX_SC_MIC;
        String applyScCamMic = "busybox echo " + SC_CAM_MIC + " > " + SoundControlInterface.FAUX_SC_CAM_MIC;
        String applyKSM = "busybox echo " + getIntFromBoolean(KSM_ENABLED) + " > " + MemoryManagementInterface.KSM_TOGGLE;
        String applyKSMPages = "busybox echo " + KSM_PAGES_TO_SCAN + " > " + MemoryManagementInterface.KSM_PAGES_TO_SCAN;
        String applyKSMTimer = "busybox echo " + KSM_SLEEP_TIMER + " > " + MemoryManagementInterface.KSM_SLEEP_TIMER;
        String applyEmmcReadahead = "busybox echo " + EMMC_READAHEAD + " > " + IOTweaksInterface.EMMC_READAHEAD;
        String applySDReadahead = "busybox echo " + SD_READAHEAD + " > " + IOTweaksInterface.SD_READAHEAD;
        String applyEmmcEntropy = "busybox echo " + EMMC_ENTROPY + " > " + IOTweaksInterface.EMMC_ENTROPY_CONTRIB;
        String applySDEntropy = "busybox echo " + SD_ENTROPY + " > " + IOTweaksInterface.SD_ENTROPY_CONTRIB;
        String applyFastChargeMode = "busybox echo " + FASTCHARGE_MODE + " > " + MiscInterface.FORCE_FAST_CHARGE;
        String applyFastChargeLevel = "busybox echo " + FASTCHARGE_LEVEL + " > " + MiscInterface.FAST_CHARGE_LEVEL;

        Helpers.CMDProcessorWrapper.runSuCommand(
                applyMaxCpuFreq + "\n" +
                        applyMinCpuFreq + "\n" +
                        applyMaxGpuFreq + "\n" +
                        applyMinGpuFreq + "\n" +
                        applyCore0Governor + "\n" +
                        applyCore1Governor + "\n" +
                        applyCore2Governor + "\n" +
                        applyCore3Governor + "\n" +
                        applyIOScheduler + "\n" +
                        applyIOSchedulerSD + "\n" +
                        applyEmmcReadahead + "\n" +
                        applySDReadahead + "\n" +
                        applyEmmcEntropy + "\n" +
                        applySDEntropy + "\n" +
                        applyTcpAlgorithm + "\n" +
                        applyGpuGovernor + "\n" +
                        "echo 0 > " + SoundControlInterface.FAUX_SC_LOCKED + "\n" +
                        applyScHeadphone + "\n" +
                        applyScHeadphonePa + "\n" +
                        applyScSpeaker + "\n" +
                        applyScMic + "\n" +
                        applyScCamMic + "\n" +
                        "echo 1 > " + SoundControlInterface.FAUX_SC_LOCKED + "\n" +
                        applySchedMcLevel + "\n" +
                        applyDynamicFsync + "\n" +
                        applyPowerSuspend + "\n" +
                        applyMsmThermal + "\n" +
                        applyKSM + "\n" +
                        applyKSMPages + "\n" +
                        applyKSMTimer + "\n" +
                        applyFastChargeMode + "\n" +
                        applyFastChargeLevel + "\n" +
                        applyPenMode + "\n" +
                        applyGloveMode + "\n" +
                        applyDt2Wake
        );

        switch (HOTPLUG_DRIVER) {
            case 0:
                CMDProcessor.runSuCommand("echo 0 > " + PowerManagementInterface.INTELLI_PLUG_TOGGLE);
                CMDProcessor.runSuCommand("echo 0 > " + PowerManagementInterface.ALUCARD_HOTPLUG_TOGGLE);
                CMDProcessor.runSuCommand("start mpdecision");
                break;
            case 1:
                CMDProcessor.runSuCommand("echo 0 > " + PowerManagementInterface.ALUCARD_HOTPLUG_TOGGLE);
                CMDProcessor.runSuCommand("stop mpdecision");
                CMDProcessor.runSuCommand("echo 1 > " + PowerManagementInterface.INTELLI_PLUG_TOGGLE);
                CMDProcessor.runSuCommand("echo " + INTELLI_CORES + " > " + PowerManagementInterface.INTELLI_PLUG_ECO_CORES);
                break;
            case 2:
                CMDProcessor.runSuCommand("echo 0 > " + PowerManagementInterface.INTELLI_PLUG_TOGGLE);
                CMDProcessor.runSuCommand("stop mpdecision");
                CMDProcessor.runSuCommand("echo 1 > " + PowerManagementInterface.ALUCARD_HOTPLUG_TOGGLE);
                CMDProcessor.runSuCommand("echo " + ALUCARD_CORES + " > " + PowerManagementInterface.ALUCARD_HOTPLUG_CORES);
                break;
            default:
                break;
        }

        CMDProcessor.runSuCommand(applyIntelliPlugEco);

        Helpers.applySysctlValue(MemoryManagementInterface.VFS_CACHE_PRESSURE, VFS_CACHE_PRESSURE + "");
        Helpers.applySysctlValue(MemoryManagementInterface.SWAPPINESS, SWAPPINESS + "");
        Helpers.applySysctlValue(MemoryManagementInterface.DIRTY_RATIO, DIRTY_RATIO + "");
        Helpers.applySysctlValue(MemoryManagementInterface.DIRTY_BG_RATIO, DIRTY_BG_RATIO + "");
        Helpers.applySysctlValue(MemoryManagementInterface.DIRTY_WRITEBACK_CENTISECS, DIRTY_WRITEBACK + "");
        Helpers.applySysctlValue(MemoryManagementInterface.DIRTY_EXPIRE_CENTISECS, DIRTY_EXPIRE + "");
    }

    private static int getIntFromBoolean(Boolean bool) {
        if (bool)
            return 1;
        else
            return 0;
    }
}
