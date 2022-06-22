package ru.avroraventures.russiansinglanguage.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class VoiceSpinner extends Spinner {
    public VoiceSpinner(Context context) {
        super(context);
    }

    public VoiceSpinner(Context context, int mode) {
        super(context, mode);
    }

    public VoiceSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VoiceSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VoiceSpinner(Context context, AttributeSet attrs, int defStyle, int mode) {
        super(context, attrs, defStyle, mode);
    }

    public void setSelection(int position) {
        boolean sameSelected = position == getSelectedItemPosition();
        Log.i("VoiceSpinner", "### voice setSelection, position=" + position + ", sameSelected=" + sameSelected);
        super.setSelection(position);
        if (sameSelected) {
            Log.i("VoiceSpinner", "### voice setSelection sameSelected");
            OnItemSelectedListener onItemSelectedListener = getOnItemSelectedListener();
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(this, getSelectedView(), position, getSelectedItemId());
            }
        }
    }
}
