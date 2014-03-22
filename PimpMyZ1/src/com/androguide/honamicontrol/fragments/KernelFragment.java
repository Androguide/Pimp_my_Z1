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

package com.androguide.honamicontrol.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.cards.CardImageLocal;
import com.androguide.honamicontrol.kernel.cpucontrol.CPUActivity;
import com.androguide.honamicontrol.kernel.gpucontrol.GPUActivity;
import com.androguide.honamicontrol.kernel.iotweaks.IOTweaksActivity;
import com.androguide.honamicontrol.kernel.misc.MiscActivity;
import com.androguide.honamicontrol.kernel.powermanagement.PowerManagementActivity;
import com.androguide.honamicontrol.profiles.BalancedProfile;
import com.androguide.honamicontrol.profiles.BatteryMaxProfile;
import com.androguide.honamicontrol.profiles.BatteryProfile;
import com.androguide.honamicontrol.profiles.BenchmarkProfile;
import com.androguide.honamicontrol.profiles.PerformanceProfile;
import com.androguide.honamicontrol.profiles.ProfileEnabler;
import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

import java.util.ArrayList;

public class KernelFragment extends Fragment {

    private static ArrayList<Card> mCards;
    private static CardUI mCardUI;
    private static int profileCounter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.cardsui, container, false);
        final ActionBarActivity fa = (ActionBarActivity) super.getActivity();
        fa.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fa.getSupportActionBar().setHomeButtonEnabled(true);

        assert ll != null;
        mCardUI = (CardUI) (ll.findViewById(R.id.cardsui));
        mCardUI.addStack(new CardStack(""), true);

        String sectionColor = fa.getString(R.string.kernel_color);

        CardImageLocal cpuControl = new CardImageLocal(
                fa.getString(R.string.kernel_cpu_control),
                fa.getString(R.string.kernel_cpu_control_desc),
                sectionColor,
                R.drawable.ic_tools_cpu_control,
                fa
        );

        cpuControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fa.startActivity(new Intent(fa, CPUActivity.class));
            }
        });

        CardImageLocal gpuControl = new CardImageLocal(
                fa.getString(R.string.kernel_gpu_control),
                fa.getString(R.string.kernel_gpu_control_desc),
                sectionColor,
                R.drawable.ic_tools_gpu_control,
                fa
        );

        gpuControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fa.startActivity(new Intent(fa, GPUActivity.class));
            }
        });

        CardImageLocal powerManagement = new CardImageLocal(
                fa.getString(R.string.kernel_power_management),
                fa.getString(R.string.kernel_power_management_desc),
                sectionColor,
                R.drawable.ic_tools_power_management,
                fa
        );

        powerManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fa.startActivity(new Intent(fa, PowerManagementActivity.class));
            }
        });

        CardImageLocal ioTweaks = new CardImageLocal(
                fa.getString(R.string.kernel_io_tweaks),
                fa.getString(R.string.kernel_io_tweaks_desc),
                sectionColor,
                R.drawable.ic_tools_io_tweaks,
                fa
        );

        ioTweaks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fa.startActivity(new Intent(fa, IOTweaksActivity.class));
            }
        });

        CardImageLocal misc = new CardImageLocal(
                fa.getString(R.string.kernel_misc),
                fa.getString(R.string.kernel_misc_desc),
                sectionColor,
                R.drawable.ic_tools_misc,
                fa
        );

        misc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fa.startActivity(new Intent(fa, MiscActivity.class));
            }
        });

        mCardUI.addCard(cpuControl);
        mCardUI.addCard(gpuControl);
        mCardUI.addCard(powerManagement);
        mCardUI.addCard(ioTweaks);
        mCardUI.addCard(misc);
        mCardUI.refresh();

        return ll;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profiles, menu);
        MenuItem item = menu.findItem(R.id.profile_spinner);
        View spinner = item != null ? item.getActionView() : null;
        if (spinner instanceof android.widget.Spinner) {
            final SharedPreferences profilePrefs = super.getActivity().getSharedPreferences("PROFILES", 0);
            final int currProfile = profilePrefs.getInt("CURR_PROFILE", 5);
            Spinner profiles = (Spinner) spinner;
            profiles.setAdapter(ArrayAdapter.createFromResource(super.getActivity(), R.array.profiles_array, R.layout.spinner_action_row));
            profiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (i) {
                        case 0:
                            if (profileCounter > 0 && i != currProfile)
                                ProfileEnabler.enableProfile(new BatteryMaxProfile());
                            profileCounter++;
                            break;
                        case 1:
                            if (profileCounter > 0 && i != currProfile)
                                ProfileEnabler.enableProfile(new BatteryProfile());
                            profileCounter++;
                            break;
                        case 2:
                            if (profileCounter > 0 && i != currProfile)
                                ProfileEnabler.enableProfile(new BalancedProfile());
                            profileCounter++;
                            break;
                        case 3:
                            if (profileCounter > 0 && i != currProfile)
                                ProfileEnabler.enableProfile(new PerformanceProfile());
                            profileCounter++;
                            break;
                        case 4:
                            if (profileCounter > 0 && i != currProfile)
                                ProfileEnabler.enableProfile(new BenchmarkProfile());
                            profileCounter++;
                            break;
                    }
                    profilePrefs.edit().putInt("CURR_PROFILE", i).commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            profiles.setSelection(profilePrefs.getInt("CURR_PROFILE", 5));
        }
    }
}
