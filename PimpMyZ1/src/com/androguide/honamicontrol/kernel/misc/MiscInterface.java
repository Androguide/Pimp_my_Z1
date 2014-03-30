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

package com.androguide.honamicontrol.kernel.misc;

public interface MiscInterface {
    public static final String VIBRATOR_SYSFS = "/sys/devices/virtual/timed_output/vibrator/vtg_level";
    public static final String FORCE_FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";
    public static final String FAST_CHARGE_LEVEL = "/sys/kernel/fast_charge/fast_charge_level";
    public static final String AVAILABLE_FAST_CHARGE_LEVELS = "/sys/kernel/fast_charge/available_charge_levels";
    public static final String FAST_CHARGE_VERSION = "/sys/kernel/fast_charge/version";
}
