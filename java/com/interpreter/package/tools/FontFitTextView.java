package ru.avroraventures.russiansinglanguage.tools;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.TextView;

public class FontFitTextView extends TextView {
    private Paint mTestPaint;

    public FontFitTextView(Context context) {
        super(context);
        initialize();
    }

    public FontFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        this.mTestPaint = new Paint();
        this.mTestPaint.set(getPaint());
    }

    private void refitText(String text, int textWidth, int textHeight) {
        if (textWidth > 0) {
            int targetWidth = (textWidth - getPaddingLeft()) - getPaddingRight();
            int targetHeight = (textHeight - getPaddingTop()) - getPaddingBottom();
            float hi = (float) Math.min(targetHeight, 100);
            float lo = 2.0f;
            Rect bounds = new Rect();
            this.mTestPaint.set(getPaint());
            while (hi - lo > 0.5f) {
                float size = (hi + lo) / 2.0f;
                this.mTestPaint.setTextSize(size);
                this.mTestPaint.getTextBounds(text, 0, text.length(), bounds);
                if (this.mTestPaint.measureText(text) >= ((float) targetWidth) || 1.0f + (((((float) bounds.top) + size) * 2.0f) - ((float) bounds.bottom)) >= ((float) targetHeight)) {
                    hi = size;
                } else {
                    lo = size;
                }
            }
            setTextSize(0, lo);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int height = getMeasuredHeight();
        refitText(getText().toString(), parentWidth, height);
        setMeasuredDimension(parentWidth, height);
    }

    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        refitText(text.toString(), getWidth(), getHeight());
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            refitText(getText().toString(), w, h);
        }
    }
}
