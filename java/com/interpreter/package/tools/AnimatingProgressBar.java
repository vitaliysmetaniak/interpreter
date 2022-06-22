package ru.avroraventures.russiansinglanguage.tools;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;

public class AnimatingProgressBar extends ProgressBar {
    private static final Interpolator DEFAULT_INTERPOLATER = new AccelerateDecelerateInterpolator();
    private static final String TAG = AnimatingProgressBar.class.getSimpleName();
    private boolean animate = true;
    private ValueAnimator animator;
    private ValueAnimator animatorSecondary;

    public AnimatingProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AnimatingProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatingProgressBar(Context context) {
        super(context);
    }

    public boolean isAnimate() {
        return this.animate;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public synchronized void setProgress(int progress) {
        if (this.animate) {
            if (this.animator != null) {
                this.animator.cancel();
            }
            if (this.animator == null) {
                this.animator = ValueAnimator.ofInt(new int[]{getProgress(), progress});
                this.animator.setInterpolator(DEFAULT_INTERPOLATER);
                this.animator.addUpdateListener(new AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        super.setProgress(((Integer) animation.getAnimatedValue()).intValue());
                    }
                });
            } else {
                this.animator.setIntValues(new int[]{getProgress(), progress});
            }
            this.animator.start();
        } else {
            super.setProgress(progress);
        }
    }

    public synchronized void setSecondaryProgress(int secondaryProgress) {
        if (this.animate) {
            if (this.animatorSecondary != null) {
                this.animatorSecondary.cancel();
            }
            if (this.animatorSecondary == null) {
                this.animatorSecondary = ValueAnimator.ofInt(new int[]{getProgress(), secondaryProgress});
                this.animatorSecondary.setInterpolator(DEFAULT_INTERPOLATER);
                this.animatorSecondary.addUpdateListener(new AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        super.setSecondaryProgress(((Integer) animation.getAnimatedValue()).intValue());
                    }
                });
            } else {
                this.animatorSecondary.setIntValues(new int[]{getProgress(), secondaryProgress});
            }
            this.animatorSecondary.start();
        } else {
            super.setSecondaryProgress(secondaryProgress);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.animator != null) {
            this.animator.cancel();
        }
        if (this.animatorSecondary != null) {
            this.animatorSecondary.cancel();
        }
    }
}
