/**
 * @author Louis Teboul (Androguide)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.androguide.honamicontrol.cards;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.androguide.honamicontrol.R;
import com.fima.cardsui.objects.Card;

import java.util.ArrayList;

public class CardSpinnerVoltage extends Card {

    public CardSpinnerVoltage(String title, String desc, String color, String prop, int currentItem, ArrayList<String> spinnerEntries, ActionBarActivity fa, AdapterView.OnItemSelectedListener onItemSelectedListener) {
        super(title, desc, color, prop, currentItem, spinnerEntries, fa, onItemSelectedListener);
    }

    @Override
    public View getCardContent(Context context) {
        final View v = LayoutInflater.from(context).inflate(R.layout.card_spinner_voltage, null);

        assert v != null;
        ((TextView) v.findViewById(R.id.desc)).setText(desc);

        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(fa, R.layout.spinner_row, spinnerEntries);
        spinner.setAdapter(adapter);

        spinner.setSelection(currentItem);

        spinner.setOnItemSelectedListener(onItemSelectedListener);
        return v;
    }

    public int getCardContentId() {
        return R.layout.card_spinner_voltage;
    }

    @Override
    public boolean convert(View convertCardView) {
        return true;
    }

}
