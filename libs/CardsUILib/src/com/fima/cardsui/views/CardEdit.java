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

package com.fima.cardsui.views;

import android.content.Context;
import android.graphics.Color;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fima.cardsui.R;
import com.fima.cardsui.objects.Card;

public class CardEdit extends Card {

    public CardEdit(String title, String desc, String titleColor, TextWatcher textWatcher, View.OnFocusChangeListener focusListener) {
        super(title, desc, titleColor, textWatcher, focusListener);
    }

    @Override
    public View getCardContent(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_edit_text, null);

        assert v != null;
        ((TextView) v.findViewById(R.id.title)).setText(title);
        ((TextView) v.findViewById(R.id.title)).setTextColor(Color
                .parseColor(titleColor));
        final EditText edit = (EditText) v.findViewById(R.id.edit_text);
        edit.setText(desc);
        edit.addTextChangedListener(textWatcher);
        edit.setOnFocusChangeListener(focusListener);
        return v;
    }

    public int getCardContentId() {
        return R.layout.card_text;
    }

    @Override
    public boolean convert(View convertCardView) {
        return true;
    }
}

