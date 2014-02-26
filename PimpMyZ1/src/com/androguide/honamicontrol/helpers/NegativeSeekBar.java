package com.androguide.honamicontrol.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class NegativeSeekBar extends SeekBar {

    protected int minimumValue = 0;
    protected int maximumValue = 0;
    protected OnSeekBarChangeListener listener;

    public NegativeSeekBar(Context context){
        super(context);
        setUpInternalListener();
    }

    public NegativeSeekBar(Context context, AttributeSet attrs){
        super(context, attrs);
        setUpInternalListener();
    }

    public NegativeSeekBar(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        setUpInternalListener();
    }

    public void setMin(int min){
        this.minimumValue = min;
        super.setMax(maximumValue - minimumValue);
    }

    public void setMax(int max){
        this.maximumValue = max;
        super.setMax(maximumValue - minimumValue);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener){
        this.listener = listener;
    }

    private void setUpInternalListener(){
        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(listener != null){
                    listener.onProgressChanged(seekBar, minimumValue + i, b);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(listener != null)
                    listener.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(listener != null)
                    listener.onStopTrackingTouch(seekBar);
            }
        });
    }
}