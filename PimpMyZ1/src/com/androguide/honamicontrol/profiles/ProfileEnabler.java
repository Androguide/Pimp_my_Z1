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

import com.androguide.honamicontrol.helpers.Helpers;


public class ProfileEnabler {
    public static void enableProfile(Profile profile) {
        Helpers.CMDProcessorWrapper.runSuCommand(
                profile.getCpuMaxFreq() + "\n"
                        + profile.getCPUGovernor() + "\n"
                        + profile.getGpuMaxFreq() + "\n"
                        + profile.getGPUGovernor() + "\n"
                        + profile.getIOScheduler() + "\n"
                        + profile.isIntelliplugEnabled() + "\n"
                        + profile.isEcoModeEnabled() + "\n"
                        + profile.isPowerSuspendEnabled() + "\n"
                        + profile.isKSMEnabled() + "\n"
                        + profile.KSMpagesToScan() + "\n"
                        + profile.KSMTimer() + "\n"
                        + profile.isDynFsyncEnabled() + "\n"
                        + profile.isEntropyContributionEnabled() + "\n"
                        + profile.readahead() + "\n"
                        + profile.schedMCLevel()
        );
    }
}
