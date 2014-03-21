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

abstract class Profile {
    public static final String echo = "busybox echo ";
    public static String toShell(String value, String path) {
        return echo + value + " > " + path;
    }

    abstract String getCpuMaxFreq();
    abstract String getGpuMaxFreq();
    abstract String getCPUGovernor();
    abstract String getGPUGovernor();
    abstract String getIOScheduler();
    abstract String isIntelliplugEnabled();
    abstract String isEcoModeEnabled();
    abstract String isPowerSuspendEnabled();
    abstract String isKSMEnabled();
    abstract String isDynFsyncEnabled();
    abstract String isEntropyContributionEnabled();
    abstract String schedMCLevel();
    abstract String readahead();
    abstract String KSMpagesToScan();
    abstract String KSMTimer();
}
