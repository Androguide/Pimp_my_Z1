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

package com.androguide.honamicontrol.kernel.powermanagement;

public interface PowerManagementInterface {
    public static final String SCHED_MC_POWER_SAVINGS = "/sys/devices/system/cpu/sched_mc_power_savings";
    public static final String INTELLI_PLUG_TOGGLE = "/sys/module/intelli_plug/parameters/intelli_plug_active";
    public static final String INTELLI_PLUG_ECO_MODE = "/sys/module/intelli_plug/parameters/eco_mode_active";
    public static final String MSM_MPDECISION_TOGGLE = "/sys/kernel/msm_mpdecision/conf/enabled";
    public static final String POWER_SUSPEND_TOGGLE = "/sys/kernel/power_suspend/power_suspend_mode";
}

