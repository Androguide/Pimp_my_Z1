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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androguide.honamicontrol.helpers.CMDProcessor.CMDProcessor;
import com.androguide.honamicontrol.helpers.CPUHelper;
import com.androguide.honamicontrol.helpers.Helpers;
import com.androguide.honamicontrol.kernel.misc.MiscInterface;
import com.fima.cardsui.R;
import com.fima.cardsui.objects.Card;

public class CardSeekBarVibrator extends Card {

    public CardSeekBarVibrator(String title, String desc, String color, ActionBarActivity fa) {
        super(title, desc, color, fa);
    }

    private static int WARNING_THRESHOLD = 80;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getCardContent(Context context) {
        final View v = LayoutInflater.from(context).inflate(R.layout.card_seekbar, null);
        assert v != null;
        final TextView mValue = (TextView) v.findViewById(R.id.unit);
        final SeekBar mSeekBar = (SeekBar) v.findViewById(R.id.seek);
        TextView titleTextView = (TextView) v.findViewById(R.id.title);
        titleTextView.setText(title);
        titleTextView.setTextColor(Color.parseColor("#1abc9c"));
        TextView mWarning = (TextView) v.findViewById(R.id.desc);
        mWarning.setText(desc);

        Drawable progressDrawable = mSeekBar.getProgressDrawable();
        LayerDrawable ld = (LayerDrawable) progressDrawable;
        final Drawable mProgressDrawable = ld != null ? ld.findDrawableByLayerId(android.R.id.progress) : null;
        final Drawable mProgressThumb = mSeekBar.getThumb();
        final LightingColorFilter mRedFilter = new LightingColorFilter(Color.BLACK,
                fa.getResources().getColor(R.color.play_red));

        final SharedPreferences prefs = fa.getSharedPreferences("VIBRATOR", 0);

        if (Helpers.doesFileExist(MiscInterface.VIBRATOR_SYSFS)) {
            String mOriginalValue = prefs.getString("INTENSITY", "50");
            mSeekBar.setProgress(Integer.valueOf(mOriginalValue));
            mValue.setText(mOriginalValue + "%");

            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                private int prog;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    prog = progress;
                    boolean shouldWarn = progress >= WARNING_THRESHOLD;
                    if (mProgressDrawable != null) {
                        mProgressDrawable.setColorFilter(shouldWarn ? mRedFilter : null);
                    }
                    if (mProgressThumb != null) {
                        mProgressThumb.setColorFilter(shouldWarn ? mRedFilter : null);
                    }
                    mValue.setText(String.format("%d%%", progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // Do nothing
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    CMDProcessor.runSuCommand("busybox echo " + prog + " > " + MiscInterface.VIBRATOR_SYSFS);
                    prefs.edit().putString("INTENSITY", prog + "").commit();
                    Vibrator vib = (Vibrator) fa.getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(75);
                }
            });

        } else {
            mValue.setText("Unsupported");
            if (mProgressDrawable != null) mProgressDrawable.setColorFilter(mRedFilter);
        }
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
