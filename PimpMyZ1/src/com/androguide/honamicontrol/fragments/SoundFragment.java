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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.cards.CardDoubleSeekBar;
import com.androguide.honamicontrol.cards.CardDoubleSeekBarPA;
import com.androguide.honamicontrol.cards.CardSeekBarSC;
import com.androguide.honamicontrol.helpers.CPUHelper;
import com.androguide.honamicontrol.helpers.Helpers;
import com.androguide.honamicontrol.soundcontrol.SoundControlInterface;
import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardTextStripe;
import com.fima.cardsui.views.CardUI;

import java.util.ArrayList;

public class SoundFragment extends Fragment implements SoundControlInterface {

    private static ActionBarActivity fa;
    private static ArrayList<Card> mCards = new ArrayList<Card>();
    private static CardUI mCardUI;
    private static SharedPreferences bootPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.cardsui, container, false);
        fa = (ActionBarActivity) super.getActivity();
        fa.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fa.getSupportActionBar().setHomeButtonEnabled(true);
        bootPrefs = fa.getSharedPreferences("BOOT_PREFS", 0);

        assert ll != null;
        mCardUI = (CardUI) (ll.findViewById(R.id.cardsui));
        createCards();
        return ll;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sound_control, menu);
        SharedPreferences p = fa.getSharedPreferences("SOUND_CONTROL", 0);
        if (p.getBoolean("LINKED", true))
            menu.getItem(0).setChecked(true);
        else
            menu.getItem(0).setChecked(false);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.link_seekbars:
                SharedPreferences soundPrefs = fa.getSharedPreferences("SOUND_CONTROL", 0);
                if (item.isChecked()) {
                    item.setChecked(false);
                    soundPrefs.edit().putBoolean("LINKED", false).commit();
                } else {
                    item.setChecked(true);
                    soundPrefs.edit().putBoolean("LINKED", true).commit();
                }
                break;

            case R.id.refresh:
                mCardUI.clearCards();
                createCards();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public static void recreateCards() {
        mCardUI.clearCards();
        createCards();
    }

    private static void createCards() {
        String sectionColor = fa.getString(R.string.sound_control_color);
        mCardUI.addStack(new CardStack(""));

        if (!Helpers.doesFileExist(FAUX_SC_VERSION)) {
            mCardUI.addCard(new CardTextStripe(
                            fa.getString(R.string.unsupported),
                            fa.getString(R.string.sound_control_unsupported),
                            "#C74B46",
                            "#C74B46",
                            false)
            );

        } else {

            String[] headphoneGains = CPUHelper.readOneLineNotRoot(FAUX_SC_HEADPHONE).split(" ");
            int headphoneGainLeft = Integer.valueOf(headphoneGains[0]);
            if (headphoneGainLeft > 100)
                headphoneGainLeft -= 256;

            int headphoneGainRight = Integer.valueOf(headphoneGains[1]);
            if (headphoneGainRight > 100)
                headphoneGainRight -= 256;

            final CardDoubleSeekBar headphoneCard = new CardDoubleSeekBar(
                    fa.getString(R.string.sc_headphone_digital_gain),
                    fa.getString(R.string.sc_headphone_digital_gain_desc),
                    sectionColor,
                    "",
                    FAUX_SC_HEADPHONE,
                    40,
                    headphoneGainLeft + 30,
                    headphoneGainRight + 30,
                    fa,
                    null
            );
            mCards.add(headphoneCard);
            mCardUI.addCard(headphoneCard);

            String[] headphonePAGains = CPUHelper.readOneLineNotRoot(FAUX_SC_HEADPHONE_POWERAMP).split(" ");
            int headphonePAGainLeft = Integer.valueOf(headphonePAGains[0]);
            int headphonePAGainRight = Integer.valueOf(headphonePAGains[1]);
            CardDoubleSeekBarPA headphonePaCard = new CardDoubleSeekBarPA(
                    fa.getString(R.string.sc_headphone_analog_gain),
                    fa.getString(R.string.sc_headphone_analog_gain_desc),
                    sectionColor,
                    "",
                    FAUX_SC_HEADPHONE_POWERAMP,
                    12,
                    headphonePAGainLeft ,
                    headphonePAGainRight,
                    fa,
                    null
            );
            mCards.add(headphonePaCard);
            mCardUI.addCard(headphonePaCard);

            String[] speakerGains = CPUHelper.readOneLineNotRoot(FAUX_SC_SPEAKER).split(" ");
            int speakerGainLeft = Integer.valueOf(speakerGains[0]);
            if (speakerGainLeft > 100)
                speakerGainLeft -= 256;

            int speakerGainRight = Integer.valueOf(speakerGains[1]);
            if (speakerGainRight > 100)
                speakerGainRight -= 256;

            CardDoubleSeekBar speakerCard = new CardDoubleSeekBar(
                    fa.getString(R.string.sc_speaker_gain),
                    fa.getString(R.string.sc_speaker_gain_desc),
                    fa.getString(R.string.sound_control_color),
                    "",
                    FAUX_SC_SPEAKER,
                    40,
                    speakerGainLeft + 30,
                    speakerGainRight + 30,
                    fa,
                    null
            );

            mCards.add(speakerCard);
            mCardUI.addCard(speakerCard);

            int micGain = Integer.valueOf(CPUHelper.readOneLine(FAUX_SC_MIC));
            if (micGain > 100)
                micGain -= 256;

            CardSeekBarSC micCard = new CardSeekBarSC(
                    fa.getString(R.string.sc_mic_gain),
                    fa.getString(R.string.sc_mic_gain_desc),
                    fa.getString(R.string.sound_control_color),
                    "",
                    FAUX_SC_MIC,
                    40,
                    micGain + 30,
                    fa,
                    new ActionMode.Callback() {
                        private Boolean isApplied = false;

                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                            MenuInflater inflater = actionMode.getMenuInflater();
                            inflater.inflate(R.menu.contextual_menu, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            return false;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.cancel:
                                    actionMode.finish();
                                    break;
                                case R.id.apply:
                                    isApplied = true;
                                    SharedPreferences prefs = fa.getSharedPreferences("syskernelsound_control_3gpl_mic_gain", 0);
                                    int toApply = prefs.getInt("VALUE", 0);
                                    Helpers.CMDProcessorWrapper.runSuCommand(
                                            "busybox echo 0 > " + FAUX_SC_LOCKED + "\n" +
                                                    "busybox echo " + toApply + " " + Helpers.getSoundCountrolBitRepresentation(toApply, 0) + " > " + FAUX_SC_MIC + "\n" +
                                                    "busybox echo 1 > " + FAUX_SC_LOCKED);
                                    bootPrefs.edit().putString("SC_MIC", toApply + " " + Helpers.getSoundCountrolBitRepresentation(toApply, 0)).commit();
                                    actionMode.finish();
                            }
                            return false;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            if (!isApplied) {
                                mCardUI.clearCards();
                                createCards();
                            }
                        }
                    }
            );
            mCards.add(micCard);
            mCardUI.addCard(micCard);

            int camMicGain = Integer.valueOf(CPUHelper.readOneLine(FAUX_SC_CAM_MIC));
            if (camMicGain > 100)
                camMicGain -= 256;

            CardSeekBarSC camCard = new CardSeekBarSC(
                    fa.getString(R.string.sc_cam_mic_gain),
                    fa.getString(R.string.sc_cam_mic_gain_desc),
                    fa.getString(R.string.sound_control_color),
                    "",
                    FAUX_SC_CAM_MIC,
                    40,
                    camMicGain + 30,
                    fa,
                    new ActionMode.Callback() {
                        private Boolean isApplied = false;

                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                            MenuInflater inflater = actionMode.getMenuInflater();
                            inflater.inflate(R.menu.contextual_menu, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            return false;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.cancel:
                                    actionMode.finish();
                                    break;
                                case R.id.apply:
                                    isApplied = true;
                                    SharedPreferences prefs = fa.getSharedPreferences("syskernelsound_control_3gpl_cam_mic_gain", 0);
                                    int toApply = prefs.getInt("VALUE", 0);
                                    Helpers.CMDProcessorWrapper.runSuCommand(
                                            "busybox echo 0 > " + FAUX_SC_LOCKED + "\n" +
                                                    "busybox echo " + toApply + " " + Helpers.getSoundCountrolBitRepresentation(toApply, 0) + " > " + FAUX_SC_CAM_MIC + "\n" +
                                                    "busybox echo 1 > " + FAUX_SC_LOCKED);
                                    bootPrefs.edit().putString("SC_CAM_MIC", toApply + " " + Helpers.getSoundCountrolBitRepresentation(toApply, 0)).commit();
                                    actionMode.finish();
                                    break;
                            }
                            return false;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            if (!isApplied) {
                                mCardUI.clearCards();
                                createCards();
                            }
                        }
                    }
            );
            mCards.add(camCard);
            mCardUI.addCard(camCard);
        }
        mCardUI.refresh();
    }
}