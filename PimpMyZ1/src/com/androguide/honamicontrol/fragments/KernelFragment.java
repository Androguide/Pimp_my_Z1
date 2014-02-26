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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.cards.CardImageLocal;
import com.androguide.honamicontrol.kernel.cpucontrol.CPUActivity;
import com.androguide.honamicontrol.kernel.gpucontrol.GPUActivity;
import com.androguide.honamicontrol.kernel.iotweaks.IOTweaksActivity;
import com.androguide.honamicontrol.kernel.powermanagement.PowerManagementActivity;
import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

import java.util.ArrayList;

public class KernelFragment extends Fragment {

    private static ArrayList<Card> mCards;
    private static CardUI mCardUI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.cardsui, container, false);
        final ActionBarActivity fa = (ActionBarActivity) super.getActivity();
        fa.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fa.getSupportActionBar().setHomeButtonEnabled(true);

        assert ll != null;
        mCardUI = (CardUI) (ll.findViewById(R.id.cardsui));
        mCardUI.addStack(new CardStack(""), true);
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

        mCardUI.addCard(cpuControl);
        mCardUI.addCard(gpuControl);
        mCardUI.addCard(powerManagement);
        mCardUI.addCard(ioTweaks);
        mCardUI.refresh();

        return ll;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
