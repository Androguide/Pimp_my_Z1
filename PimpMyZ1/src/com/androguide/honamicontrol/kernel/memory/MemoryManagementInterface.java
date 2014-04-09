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

package com.androguide.honamicontrol.kernel.memory;

public interface MemoryManagementInterface {
    public static final String KSM_TOGGLE = "/sys/kernel/mm/ksm/run";
    public static final String KSM_PAGES_TO_SCAN = "/sys/kernel/mm/ksm/pages_to_scan";
    public static final String KSM_SLEEP_TIMER = "/sys/kernel/mm/ksm/sleep_millisecs";
    public static final String VFS_CACHE_PRESSURE = "vm.vfs_cache_pressure";
    public static final String SWAPPINESS = "vm.swappiness";
    public static final String DIRTY_RATIO = "vm.dirty_ratio";
    public static final String DIRTY_BG_RATIO = "vm.dirty_background_ratio";
    public static final String DIRTY_WRITEBACK_CENTISECS = "vm.dirty_writeback_centisecs";
    public static final String DIRTY_EXPIRE_CENTISECS = "vm.dirty_expire_centisecs";
}
