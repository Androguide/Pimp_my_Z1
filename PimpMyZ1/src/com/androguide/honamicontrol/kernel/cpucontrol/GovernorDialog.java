package com.androguide.honamicontrol.kernel.cpucontrol;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androguide.honamicontrol.R;
import com.androguide.honamicontrol.helpers.CMDProcessor.CMDProcessor;
import com.androguide.honamicontrol.helpers.CPUHelper;
import com.androguide.honamicontrol.helpers.Helpers;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardEdit;
import com.fima.cardsui.views.CardUI;


public class GovernorDialog extends DialogFragment implements CPUInterface {

    ActionBarActivity fa;

    public static GovernorDialog newInstance() {
        return new GovernorDialog();
    }

    public GovernorDialog() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Governor Customization");

        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.cardsui, container, false);
        fa = (ActionBarActivity) super.getActivity();

        assert ll != null;
        CardUI cardsUI = (CardUI) ll.findViewById(R.id.cardsui);
        final String currGovernor = CPUHelper.readOneLineNotRoot(GOVERNOR_ALL_CORES);
        cardsUI.addStack(new CardStack(""));
        cardsUI.addStack(new CardStack(currGovernor.toUpperCase()));

        if (Helpers.doesFileExist(GOV_CUSTOMIZATION + "/" + currGovernor)) {
            String[] paramsList = CMDProcessor.runShellCommand("ls " + GOV_CUSTOMIZATION + "/" + currGovernor)
                    .getStdout().split("\n");

            final SharedPreferences govPrefs = fa.getSharedPreferences("GOVERNOR_CUSTOMIZATION", 0);
            govPrefs.edit().putString("TARGET_GOV", currGovernor).commit();

            for (final String param : paramsList) {
                final String[] userInput = {""};
                cardsUI.addCard(new CardEdit(
                        param,
                        Helpers.readOneLine(GOV_CUSTOMIZATION + "/" + currGovernor + "/" + param),
                        "#1abc9c",
                        new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                userInput[0] = s.toString();
                            }
                        },

                        new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus) {
                                    String toApply = "busybox echo \'" + userInput[0] + "\' > " + GOV_CUSTOMIZATION + "/" + currGovernor + "/" + param;
                                    CMDProcessor.runSuCommand(toApply);
                                    govPrefs.edit().putString(param, toApply).commit();
                                }
                            }
                        }
                ));
            }
            cardsUI.refresh();

        } else {
            Toast.makeText(super.getActivity(), "Error!", Toast.LENGTH_LONG).show();
        }

        return ll;
    }
}
