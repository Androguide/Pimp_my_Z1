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

package com.androguide.honamicontrol.kernel.iotweaks;

public interface IOTweaksInterface {
    public static final String DYNAMIC_FSYNC_VERSION = "/sys/kernel/dyn_fsync/Dyn_fsync_version";
    public static final String DYNAMIC_FSYNC_TOGGLE = "/sys/kernel/dyn_fsync/Dyn_fsync_active";
    public static final String IO_SCHEDULER = "/sys/block/mmcblk0/queue/scheduler";
    public static final String IO_SCHEDULER_SD = "/sys/block/mmcblk1/queue/scheduler";
    public static final String EMMC_ENTROPY_CONTRIB = "/sys/block/mmcblk0/queue/add_random";
    public static final String SD_ENTROPY_CONTRIB = "/sys/block/mmcblk1/queue/add_random";
    public static final String EMMC_READAHEAD = "/sys/block/mmcblk0/queue/read_ahead_kb";
    public static final String SD_READAHEAD = "/sys/block/mmcblk1/queue/read_ahead_kb";
}
