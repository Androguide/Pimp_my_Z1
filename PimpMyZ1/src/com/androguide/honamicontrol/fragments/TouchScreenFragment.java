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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.cards.CardSeekBar;
import com.androguide.honamicontrol.cards.CardSwitchPlugin;
import com.androguide.honamicontrol.helpers.CMDProcessor.CMDProcessor;
import com.androguide.honamicontrol.helpers.CPUHelper;
import com.androguide.honamicontrol.helpers.Helpers;
import com.androguide.honamicontrol.touchscreen.TouchScreenInterface;
import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

import java.util.ArrayList;

public class TouchScreenFragment extends Fragment implements TouchScreenInterface {

    private static ArrayList<Card> mCards = new ArrayList<Card>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.cardsui, container, false);
        final ActionBarActivity fa = (ActionBarActivity) super.getActivity();
        fa.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fa.getSupportActionBar().setHomeButtonEnabled(true);

        assert ll != null;
        CardUI mCardUI = (CardUI) (ll.findViewById(R.id.cardsui));
        mCardUI.addStack(new CardStack(""));
        mCardUI.addStack(new CardStack(""));

        final SharedPreferences bootPrefs = fa.getSharedPreferences("BOOT_PREFS", 0);
        String sectionColor = fa.getString(R.string.touch_screen_color);

        mCardUI.addCard(new CardSwitchPlugin(
                fa.getString(R.string.pen_mode),
                fa.getString(R.string.pen_mode_desc),
                sectionColor,
                PEN_MODE,
                fa,
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                        bootPrefs.edit().putBoolean("PEN_MODE", isOn).commit();
                        if (isOn)
                            Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 1 > " + PEN_MODE);
                        else
                            Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + PEN_MODE);
                    }
                }
        ));

        mCardUI.addCard(new CardSwitchPlugin(
                fa.getString(R.string.glove_mode),
                fa.getString(R.string.glove_mode_desc),
                sectionColor,
                PEN_MODE,
                fa,
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isOn) {
                        bootPrefs.edit().putBoolean("GLOVE_MODE", isOn).commit();
                        if (isOn)
                            Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 1 > " + PEN_MODE);
                        else
                            Helpers.CMDProcessorWrapper.runSuCommand("busybox echo 0 > " + PEN_MODE);
                    }
                }
        ));

        // bash command to grab the nth line (where NUM is the line number):
        // sed 'NUMq;d' path/to/file
//        mCardUI.addCard(new CardSeekBar("Touch Pressure Scale",
//                "This defines the amount of pressure required for a touch to be detected by the touchscreen. Setting a low value can make hovers be recognized as touch events.",
//                sectionColor,
//                "",
//                CLEARPAD,
//                0,
//                1000,
//                74,
//                fa,
//                new ActionMode.Callback() {
//                    @Override
//                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
//                        return false;
//                    }
//
//                    @Override
//                    public void onDestroyActionMode(ActionMode actionMode) {
//
//                    }
//                }
//                ));

        mCardUI.addStack(new CardStack());
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
