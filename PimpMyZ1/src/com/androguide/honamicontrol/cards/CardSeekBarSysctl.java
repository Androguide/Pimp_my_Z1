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

package com.androguide.honamicontrol.cards;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androguide.honamicontrol.helpers.Helpers;
import com.fima.cardsui.R;
import com.fima.cardsui.objects.Card;

public class CardSeekBarSysctl extends Card  {

    public CardSeekBarSysctl(String title, String desc, String color, String unit, String prop, int seekBarMax, int seekBarProgress, ActionBarActivity fa, SeekBar.OnSeekBarChangeListener listener) {
        super(title, desc, color, unit, prop, seekBarMax, seekBarProgress, fa, listener);
    }

    @Override
    public View getCardContent(Context context) {
        final View v = LayoutInflater.from(context).inflate(R.layout.card_seekbar, null);
        assert v != null;
        final TextView mValue = (TextView) v.findViewById(R.id.unit);
        final SeekBar seekBar = (SeekBar) v.findViewById(R.id.seek);
        TextView titleTextView = (TextView) v.findViewById(R.id.title);
        titleTextView.setText(title);
        titleTextView.setTextColor(Color.parseColor(color));
        ((TextView) v.findViewById(R.id.desc)).setText(desc);
        mValue.setText(seekBarProgress + unit);
        seekBar.setMax(seekBarMax);
        seekBar.setProgress(seekBarProgress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mValue.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                fa.getSharedPreferences("BOOT_PREFS", 0).edit().putInt(prop, progress).commit();
                try {
                    Helpers.applySysctlValue(prop, progress + "");
                } catch (Exception e) {
                    Log.e("CardSeekBarGeneric", e.getMessage());
                }
            }
        });

        return v;
    }

    public int getCardContentId() {
        return R.layout.card_seekbar;
    }

    @Override
    public boolean convert(View convertCardView) {
        return true;
    }

}
