package ru.avroraventures.russiansinglanguage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;
import ru.avroraventures.russiansinglanguage.MyApplication.TypeOfSound;
import ru.avroraventures.russiansinglanguage.db.Item;
import ru.avroraventures.russiansinglanguage.games.ChooseLetterOrGestureActivity;
import ru.avroraventures.russiansinglanguage.tools.AutoResizeTextView;
import ru.avroraventures.russiansinglanguage.tools.OnSwipeTouchListener;

public class AlphabetActivity extends MyActivity {
    private static final String TAG = AlphabetActivity.class.getSimpleName();
    private Button exit;
    private HorizontalScrollView horizontal_letters_view;
    ImageView imageView;
    int index = 0;
    private boolean isFirstTime = true;
    Item[] items;
    private LinearLayout layout_with_letters;
    TextView left;
    private AutoResizeTextView letter;
    OnClickListener onClickListener = new OnClickListener() {
        public void onClick(View v) {
            AlphabetActivity alphabetActivity;
            switch (v.getId()) {
                case R.id.left /*2131492877*/:
                    if (AlphabetActivity.this.index > 0) {
                        alphabetActivity = AlphabetActivity.this;
                        alphabetActivity.index--;
                        AlphabetActivity.this.setLetterAndImage(AlphabetActivity.this.index);
                        return;
                    }
                    return;
                case R.id.right /*2131492879*/:
                    if (AlphabetActivity.this.index < AlphabetActivity.this.items.length - 1) {
                        alphabetActivity = AlphabetActivity.this;
                        alphabetActivity.index++;
                        AlphabetActivity.this.setLetterAndImage(AlphabetActivity.this.index);
                        return;
                    }
                    return;
                case R.id.exit /*2131492882*/:
                    break;
                case R.id.train /*2131492884*/:
                    Intent intent = new Intent(AlphabetActivity.this, ChooseLetterOrGestureActivity.class);
                    intent.putExtra("isChooseLetter", true);
                    AlphabetActivity.this.startActivity(intent);
                    break;
                default:
                    return;
            }
            AlphabetActivity.this.finish();
        }
    };
    OnClickListener onLetterClickListener = new OnClickListener() {
        public void onClick(View v) {
            MyApplication.tryVibrate(true);
            v.getId();
            int i = 0;
            while (i < AlphabetActivity.this.layout_with_letters.getChildCount()) {
                if (AlphabetActivity.this.layout_with_letters.getChildAt(i) != v || AlphabetActivity.this.index == i || AlphabetActivity.this.index >= AlphabetActivity.this.items.length) {
                    i++;
                } else {
                    AlphabetActivity.this.index = i;
                    AlphabetActivity.this.setLetterAndImage(AlphabetActivity.this.index);
                    return;
                }
            }
        }
    };
    TextView right;
    private Button train;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMyTitle(getString(R.string.alphabetTitle));
        setContentView(R.layout.alphabet);
        this.items = MyApplication.getItems(MyApplication.indexOfGroup);
        this.imageView = (ImageView) findViewById(R.id.image);
        this.horizontal_letters_view = (HorizontalScrollView) findViewById(R.id.horizontal_letters_view);
        findViewById(R.id.exit).setOnClickListener(this.onClickListener);
        findViewById(R.id.train).setOnClickListener(this.onClickListener);
        ((AutoResizeTextView) findViewById(R.id.close)).setEtalonText(getString(R.string.training));
        this.layout_with_letters = (LinearLayout) findViewById(R.id.layout_with_letters);
        for (int i = 0; i < this.items.length; i++) {
            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.one_letter, null);
            textView.setText(this.items[i].content.toUpperCase(new Locale("ru", "RU")));
            this.layout_with_letters.addView(textView);
            textView.setOnClickListener(this.onLetterClickListener);
            Log.i(TAG, "### add " + this.items[i].content);
        }
        Log.i(TAG, "### add size of items = " + this.items.length);
        this.left = (TextView) findViewById(R.id.left);
        this.left.setOnClickListener(this.onClickListener);
        this.right = (TextView) findViewById(R.id.right);
        this.right.setOnClickListener(this.onClickListener);
        this.letter = (AutoResizeTextView) findViewById(R.id.letter);
        findViewById(R.id.gesture_area).setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeRight() {
                if (AlphabetActivity.this.index > 0) {
                    AlphabetActivity alphabetActivity = AlphabetActivity.this;
                    alphabetActivity.index--;
                    AlphabetActivity.this.setLetterAndImage(AlphabetActivity.this.index);
                }
            }

            public void onSwipeLeft() {
                if (AlphabetActivity.this.index < AlphabetActivity.this.items.length - 1) {
                    AlphabetActivity alphabetActivity = AlphabetActivity.this;
                    alphabetActivity.index++;
                    AlphabetActivity.this.setLetterAndImage(AlphabetActivity.this.index);
                }
            }
        });
    }

    private void setLetterAndImage(int index) {
        if (this.layout_with_letters != null && this != null) {
            int color = getResources().getColor(R.color.marked_letter);
            for (int i = 0; i < this.layout_with_letters.getChildCount(); i++) {
                View view = this.layout_with_letters.getChildAt(i);
                if (index != i) {
                    view.setBackgroundColor(0);
                } else {
                    view.setBackgroundColor(color);
                    int center = this.horizontal_letters_view.getScrollX() + (this.horizontal_letters_view.getWidth() / 2);
                    int viewLeft = view.getLeft();
                    int viewWidth = view.getWidth();
                    Log.d(TAG, "### viewLeft=" + viewLeft + ", viewWidth=" + viewWidth + ", center=" + center);
                    Log.d(TAG, "### CENTER THIS : " + (((viewWidth / 2) + viewLeft) - center));
                    this.horizontal_letters_view.smoothScrollBy(((viewWidth / 2) + viewLeft) - center, 0);
                }
            }
            if (index == 0) {
                this.left.setVisibility(4);
            } else if (index == this.items.length - 1) {
                this.right.setVisibility(4);
            } else {
                this.left.setVisibility(0);
                this.right.setVisibility(0);
            }
            MyApplication.setImage(this.items[index], this.imageView);
            try {
                this.letter.setText(this.items[index].content.toUpperCase(new Locale("ru", "RU")));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            MyApplication.playSound(this.items[index].soundFileName, TypeOfSound.Voice);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.isFirstTime) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    AlphabetActivity.this.setLetterAndImage(AlphabetActivity.this.index);
                }
            }, 500);
            return;
        }
        setLetterAndImage(this.index);
        this.isFirstTime = false;
    }
}
