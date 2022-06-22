package ru.avroraventures.russiansinglanguage.tools;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import ru.avroraventures.russiansinglanguage.R;

public class Banner extends View {
    private final Drawable logo;

    public Banner(Context context) {
        super(context);
        this.logo = context.getResources().getDrawable(R.drawable.action_bar);
        setBackgroundDrawable(this.logo);
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.logo = context.getResources().getDrawable(R.drawable.action_bar);
        setBackgroundDrawable(this.logo);
    }

    public Banner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.logo = context.getResources().getDrawable(R.drawable.action_bar);
        setBackgroundDrawable(this.logo);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension((this.logo.getIntrinsicWidth() * height) / this.logo.getIntrinsicHeight(), height);
    }
}
