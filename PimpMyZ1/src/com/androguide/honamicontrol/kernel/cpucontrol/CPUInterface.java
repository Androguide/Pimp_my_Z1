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

public interface CPUInterface {
    public static final String CURRENT_CPU = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
    public static final String MAX_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
    public static final String SNAKE_CHARMER_MAX_FREQ = "/sys/kernel/msm_cpufreq_limit/cpufreq_limit";
    public static final String SNAKE_CHARMER_VERSION = "/sys/kernel/msm_cpufreq_limit/msm_cpufreq_limit_version";
    public static final String TEGRA_MAX_FREQ = "/sys/module/cpu_tegra/parameters/cpu_user_cap";
    public static final String MIN_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
    public static final String STEPS = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    public static final String ONLINE_CPUS = "/sys/devices/system/cpu/online";
    public static final String OFFLINE_CPUS = "/sys/devices/system/cpu/offline";
    public static final String CPU0_ONLINE = "/sys/devices/system/cpu/cpu0/online";
    public static final String CPU1_ONLINE = "/sys/devices/system/cpu/cpu1/online";
    public static final String CPU2_ONLINE = "/sys/devices/system/cpu/cpu2/online";
    public static final String CPU3_ONLINE = "/sys/devices/system/cpu/cpu3/online";
    public static final String GOVERNORS_LIST = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
    public static final String GOVERNORS_LIST2 = "/sys/devices/system/cpu/cpu1/cpufreq/scaling_available_governors";
    public static final String GOVERNORS_LIST3 = "/sys/devices/system/cpu/cpu2/cpufreq/scaling_available_governors";
    public static final String GOVERNORS_LIST4 = "/sys/devices/system/cpu/cpu3/cpufreq/scaling_available_governors";
    public static final String FIRST_PART_CPU = "/sys/devices/system/cpu/cpu";
    public static final String SECOND_PART_GOV = "/cpufreq/scaling_governor";
    public static final String SECOND_PART_ONLINE = "/online";
    public static final String GOVERNOR = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
    public static final String GOVERNOR2 = "/sys/devices/system/cpu/cpu1/cpufreq/scaling_governor";
    public static final String GOVERNOR3 = "/sys/devices/system/cpu/cpu2/cpufreq/scaling_governor";
    public static final String GOVERNOR4 = "/sys/devices/system/cpu/cpu3/cpufreq/scaling_governor";
    public static final String IO_SCHEDULER = "/sys/block/mmcblk0/queue/scheduler";
    public static final String NUM_OF_CPUS = "/sys/devices/system/cpu/present";
    public static final String AVAILABLE_TCP_ALGORITHMS = "/proc/sys/net/ipv4/tcp_available_congestion_control";
    public static final String CURR_TCP_ALGORITHM = "/proc/sys/net/ipv4/tcp_congestion_control";
    public static final String SYSCTL_TCP_ALGORITHM = "sysctl -w net.ipv4.tcp_congestion_control=";
    public static final String MSM_THERMAL = "/sys/module/msm_thermal/parameters/enabled";
}
