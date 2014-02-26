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

package com.androguide.honamicontrol.soundcontrol;

public interface SoundControlInterface {
    public static final String FAUX_SC_LOCKED = "/sys/kernel/sound_control_3/gpl_sound_control_locked";
    public static final String FAUX_SC_VERSION = "/sys/kernel/sound_control_3/gpl_sound_control_version";
    public static final String FAUX_SC_HEADPHONE = "/sys/kernel/sound_control_3/gpl_headphone_gain";
    public static final String FAUX_SC_HEADPHONE_POWERAMP = "/sys/kernel/sound_control_3/gpl_headphone_pa_gain";
    public static final String FAUX_SC_MIC = "/sys/kernel/sound_control_3/gpl_mic_gain";
    public static final String FAUX_SC_CAM_MIC = "/sys/kernel/sound_control_3/gpl_cam_mic_gain";
    public static final String FAUX_SC_SPEAKER = "/sys/kernel/sound_control_3/gpl_speaker_gain";
}
