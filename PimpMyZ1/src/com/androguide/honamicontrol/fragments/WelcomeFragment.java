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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.androguide.honamicontrol.MainActivity;
import com.androguide.honamicontrol.R;
import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardCategory;
import com.fima.cardsui.views.CardUI;

import java.util.ArrayList;

public class WelcomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.cardsui, container, false);
        final ActionBarActivity fa = (ActionBarActivity) super.getActivity();
        fa.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fa.getSupportActionBar().setHomeButtonEnabled(true);

        assert ll != null;
        CardUI mCardUI = (CardUI) (ll.findViewById(R.id.cardsui));
        ArrayList<Card> mCards = new ArrayList<Card>();

        mCardUI.addStack(new CardStack(""));

        mCards.add(new CardCategory(
                        fa.getString(R.string.kernel_control),
                        fa.getString(R.string.kernel_control_desc),
                        "#16a085",
                        "",
                        false)
        );

        mCards.add(new CardCategory(
                        fa.getString(R.string.sound_control),
                        fa.getString(R.string.sound_control_desc),
                        fa.getString(R.string.sound_control_color),
                        "",
                        false)
        );

        mCards.add(new CardCategory(
                        fa.getString(R.string.touch_screen),
                        fa.getString(R.string.touch_screen_desc),
                        fa.getString(R.string.touch_screen_color),
                        "",
                        false)
        );

        mCards.add(new CardCategory(
                        fa.getString(R.string.help_center),
                        fa.getString(R.string.help_center_introduction),
                        fa.getString(R.string.help_center_color),
                        "",
                        false)
        );

        for (int i = 0; i < mCards.size(); i++) {
            final int curr = i + 1;
            Card card = mCards.get(i);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.getViewPager().setCurrentItem(curr, true);
                }
            });
            mCardUI.addCard(card);
        }

        mCardUI.addStack(new CardStack());
        mCardUI.refresh();
        return ll;
    }
}
