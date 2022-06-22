package ru.avroraventures.russiansinglanguage.tools;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AutoCompleteTextView;
import ru.avroraventures.russiansinglanguage.BuildConfig;
import ru.avroraventures.russiansinglanguage.R;

public class AutoCompleteTextViewWithButtonClear extends AutoCompleteTextView {
    private OnClearListener defaultClearListener = new OnClearListener() {
        public void onClear() {
            AutoCompleteTextViewWithButtonClear.this.setText(BuildConfig.FLAVOR);
        }
    };
    public Drawable imgClearButton = getResources().getDrawable(R.drawable.abc_ic_clear_holo_light);
    boolean justCleared = false;
    private OnClearListener onClearListener = this.defaultClearListener;

    public interface OnClearListener {
        void onClear();
    }

    public Drawable getButton() {
        return this.imgClearButton;
    }

    public AutoCompleteTextViewWithButtonClear(Context context) {
        super(context);
        init();
    }

    public AutoCompleteTextViewWithButtonClear(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AutoCompleteTextViewWithButtonClear(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        setCompoundDrawablesWithIntrinsicBounds(null, null, this.imgClearButton, null);
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                AutoCompleteTextViewWithButtonClear et = AutoCompleteTextViewWithButtonClear.this;
                if (et.getCompoundDrawables()[2] != null && event.getAction() == 1 && event.getX() > ((float) ((et.getWidth() - et.getPaddingRight()) - AutoCompleteTextViewWithButtonClear.this.imgClearButton.getIntrinsicWidth()))) {
                    AutoCompleteTextViewWithButtonClear.this.onClearListener.onClear();
                    AutoCompleteTextViewWithButtonClear.this.justCleared = true;
                }
                return false;
            }
        });
    }

    public void setImgClearButton(Drawable imgClearButton) {
        this.imgClearButton = imgClearButton;
    }

    public void setOnClearListener(OnClearListener clearListener) {
        this.onClearListener = clearListener;
    }

    public void hideClearButton() {
        setCompoundDrawables(null, null, null, null);
    }

    public void showClearButton() {
        setCompoundDrawablesWithIntrinsicBounds(null, null, this.imgClearButton, null);
    }

    public boolean enoughToFilter() {
        return true;
    }

    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused && getAdapter() != null) {
            performFiltering(getText(), 0);
        }
    }
}
