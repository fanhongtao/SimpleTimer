package com.fht.simpletimer.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fht.simpletimer.R;

public class SeekBarPreference extends Preference
        implements SeekBar.OnSeekBarChangeListener {

    private int mProgress;
    private int mMax;

    private SeekBar mSeekBar;
    private TextView mValueView;

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);
        setMax(a.getInt(R.styleable.SeekBarPreference_max, mMax));
        setProgress(a.getInt(R.styleable.SeekBarPreference_progress, mProgress));
        a.recycle();

        setLayoutResource(R.layout.preference_widget_seekbar);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, -300277); //com.android.internal.R.attr.seekBarPreferenceStyle);
    }

    public SeekBarPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        mSeekBar = view.findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setMax(mMax);
        mSeekBar.setProgress(mProgress);
        mSeekBar.setEnabled(isEnabled());

        mValueView = view.findViewById(R.id.seekbar_value);
        mValueView.setEnabled(isEnabled());
        mValueView.setText(String.valueOf(mProgress));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setProgress(restoreValue ? getPersistedInt(mProgress)
                : (Integer)defaultValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    private void setMax(int max) {
        if (max != mMax) {
            mMax = max;
        }
    }

    public void setProgress(int progress) {
        setProgress(progress, true);
    }

    private void setProgress(int progress, boolean notifyChanged) {
        if (progress > mMax) {
            progress = mMax;
        }
        if (progress < 0) {
            progress = 0;
        }
        if (progress != mProgress) {
            mProgress = progress;
            persistInt(progress);
        }
    }

    /**
     * Persist the seekBar's progress value if callChangeListener
     * returns true, otherwise set the seekBar's progress to the stored value
     */
    void syncProgress(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (progress != mProgress) {
            if (callChangeListener(progress)) {
                setProgress(progress, false);
                mValueView.setText(String.valueOf(progress));
            } else {
                seekBar.setProgress(mProgress);
            }
        }
    }

    @Override
    public void onProgressChanged
            (SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            syncProgress(seekBar);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
