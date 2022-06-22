package ru.avroraventures.russiansinglanguage.games;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import ru.avroraventures.russiansinglanguage.MyApplication;
import ru.avroraventures.russiansinglanguage.R;
import ru.avroraventures.russiansinglanguage.db.Answer;
import ru.avroraventures.russiansinglanguage.db.Item;

public class ChooseLetterOrGestureActivity extends GameActivity {
    private static final String TAG = ChooseLetterOrGestureActivity.class.getSimpleName();
    private final int SIZE_OF_SCENE = 6;
    private ImageView[] backgroundImages;
    Item goalItem;
    ImageView image;
    private boolean isChooseLetter;
    int itemInt;
    private Item[] items;
    private ArrayList<Item> learnedPool;
    private TextView letter;
    private boolean listerningForAnswer = true;
    OnClickListener onLetterClickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.v0 /*2131492905*/:
                    ChooseLetterOrGestureActivity.this.answer(0);
                    return;
                case R.id.v1 /*2131492906*/:
                    ChooseLetterOrGestureActivity.this.answer(1);
                    return;
                case R.id.v2 /*2131492907*/:
                    ChooseLetterOrGestureActivity.this.answer(2);
                    return;
                case R.id.v3 /*2131492908*/:
                    ChooseLetterOrGestureActivity.this.answer(3);
                    return;
                case R.id.v4 /*2131492909*/:
                    ChooseLetterOrGestureActivity.this.answer(4);
                    return;
                case R.id.v5 /*2131492910*/:
                    ChooseLetterOrGestureActivity.this.answer(5);
                    return;
                default:
                    return;
            }
        }
    };
    private final Random random = new Random();
    private ArrayList<Item> scenePool;
    private ArrayList<Item> tempPool;
    private ArrayList<Item> trainPool;
    final View[] variants = new View[6];

    public void onCreate(Bundle savedInstanceState) {
        int i;
        int i2;
        int i3 = 1;
        this.isChooseLetter = getIntent().getBooleanExtra("isChooseLetter", true);
        setMyTitle(getString(this.isChooseLetter ? R.string.chooseLetterTitle : R.string.chooseGestureTitle));
        setContentView(this.isChooseLetter ? R.layout.game_choose_letter : R.layout.game_choose_gesture);
        this.stepsOfBall = 1;
        if (this.isChooseLetter) {
            i = 0;
        } else {
            i = 1;
        }
        this.game = i;
        super.onCreate(savedInstanceState);
        this.items = MyApplication.getItems(MyApplication.indexOfGroup);
        if (this.items.length != this.items.length) {
            Log.e(TAG, "items.length!=items.length");
        }
        this.variants[0] = findViewById(R.id.v0);
        this.variants[1] = findViewById(R.id.v1);
        this.variants[2] = findViewById(R.id.v2);
        this.variants[3] = findViewById(R.id.v3);
        this.variants[4] = findViewById(R.id.v4);
        this.variants[5] = findViewById(R.id.v5);
        if (this.isChooseLetter) {
            this.image = (ImageView) findViewById(R.id.image);
            this.backgroundImages = new ImageView[6];
            this.backgroundImages[0] = (ImageView) findViewById(R.id.l1);
            this.backgroundImages[1] = (ImageView) findViewById(R.id.l2);
            this.backgroundImages[2] = (ImageView) findViewById(R.id.l3);
            this.backgroundImages[3] = (ImageView) findViewById(R.id.l4);
            this.backgroundImages[4] = (ImageView) findViewById(R.id.l5);
            this.backgroundImages[5] = (ImageView) findViewById(R.id.l6);
            for (int i4 = 0; i4 < 6; i4++) {
                setBackgroundToVariant(i4);
            }
        } else {
            this.letter = (TextView) findViewById(R.id.letter);
        }
        for (View v : this.variants) {
            v.setOnClickListener(this.onLetterClickListener);
        }
        Answers instance = Answers.getInstance();
        ContentViewEvent contentViewEvent = (ContentViewEvent) new ContentViewEvent().putContentId(this.isChooseLetter ? "ChooseLetter" : "ChooseGesture").putCustomAttribute("group", Integer.valueOf(MyApplication.indexOfGroup));
        String str = "isSoundOn";
        if (MyApplication.getIsSoundOn()) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        contentViewEvent = (ContentViewEvent) ((ContentViewEvent) contentViewEvent.putCustomAttribute(str, Integer.valueOf(i2))).putCustomAttribute("voice", Integer.valueOf(MyApplication.getCurrentVoices_menu_item()));
        str = "isEffectsOn";
        if (MyApplication.getIsEffectsOn()) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        contentViewEvent = (ContentViewEvent) contentViewEvent.putCustomAttribute(str, Integer.valueOf(i2));
        String str2 = "isVibrationOn";
        if (!MyApplication.getIsVibrationOn()) {
            i3 = 0;
        }
        instance.logContentView((ContentViewEvent) contentViewEvent.putCustomAttribute(str2, Integer.valueOf(i3)));
    }

    private void answer(final int i) {
        Log.i(TAG, "### prompt, answer=" + i + ", listerningForAnswer=" + this.listerningForAnswer);
        long endTime = System.currentTimeMillis();
        Log.i(TAG, "### game endTime=" + endTime);
        if (this.listerningForAnswer) {
            this.listerningForAnswer = false;
            Item item = (Item) this.scenePool.get(i);
            boolean isRightAnswer = item.equals(this.goalItem);
            Log.i(TAG, "### prompt, isRightAnswer=" + isRightAnswer);
            if (isRightAnswer) {
                setBackgroundRightToVariant(i);
                registerRightAnswer(10, new Answer(endTime, item.letter, item.group, this.game, endTime - this.startTime, 1, 0));
                this.trainPool.remove(this.scenePool.get(i));
                this.learnedPool.add(this.scenePool.get(i));
                ((Item) this.scenePool.get(i)).registerRightAnswer(-1.0f);
                for (int j = 0; j < this.scenePool.size(); j++) {
                    if (((Item) this.scenePool.get(j)).equals(this.goalItem)) {
                        setBackgroundRightToVariant(j);
                    }
                }
            } else {
                if (!this.trainPool.contains(this.scenePool.get(i))) {
                    this.trainPool.add(this.scenePool.get(i));
                }
                this.learnedPool.remove(this.scenePool.get(i));
                setBackgroundWrongToVariant(i);
                this.variants[i].startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake));
                registerWrongAnswer(new Answer(endTime, item.letter, item.group, this.game, endTime - this.startTime, 0, 0));
                ((Item) this.scenePool.get(i)).registerWrongAnswer();
            }
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (ChooseLetterOrGestureActivity.this != null) {
                        ChooseLetterOrGestureActivity.this.setBackgroundToVariant(i);
                        for (int j = 0; j < ChooseLetterOrGestureActivity.this.scenePool.size(); j++) {
                            if (((Item) ChooseLetterOrGestureActivity.this.scenePool.get(j)).equals(ChooseLetterOrGestureActivity.this.goalItem)) {
                                ChooseLetterOrGestureActivity.this.setBackgroundToVariant(j);
                            }
                        }
                        ChooseLetterOrGestureActivity.this.time0 = System.currentTimeMillis();
                        ChooseLetterOrGestureActivity.this.generateScene();
                        ChooseLetterOrGestureActivity.this.time1 = System.currentTimeMillis();
                        ChooseLetterOrGestureActivity.this.showScene();
                        ChooseLetterOrGestureActivity.this.registerNewScene();
                        ChooseLetterOrGestureActivity.this.listerningForAnswer = true;
                    }
                }
            }, isRightAnswer ? 0 : 0);
        }
    }

    private void setBackgroundRightToVariant(int i) {
        if (this.isChooseLetter) {
            MyApplication.setImage("l" + String.valueOf(i + 1) + "2.png", this.backgroundImages[i]);
        } else {
            setBackgroundToVariant(i, R.drawable.background_green);
        }
    }

    private void setBackgroundWrongToVariant(int i) {
        if (this.isChooseLetter) {
            MyApplication.setImage("l" + String.valueOf(i + 1) + "3.png", this.backgroundImages[i]);
        } else {
            setBackgroundToVariant(i, R.drawable.background_red);
        }
    }

    private void setBackgroundToVariant(int i) {
        if (this.isChooseLetter) {
            MyApplication.setImage("l" + String.valueOf(i + 1) + ".png", this.backgroundImages[i]);
        } else {
            setBackgroundToVariant(i, R.drawable.background);
        }
    }

    @TargetApi(16)
    private void setBackgroundToVariant(int i, int backgroundId) {
        if (VERSION.SDK_INT < 16) {
            this.variants[i].setBackgroundDrawable(getResources().getDrawable(backgroundId));
        } else {
            this.variants[i].setBackground(getResources().getDrawable(backgroundId));
        }
    }

    public void onResume() {
        Log.i(TAG, "### scene onResume");
        super.onResume();
        this.trainPool = new ArrayList(this.items.length);
        this.learnedPool = new ArrayList();
        this.scenePool = new ArrayList();
        this.tempPool = new ArrayList();
        for (Object add : this.items) {
            this.trainPool.add(add);
        }
        this.time0 = System.currentTimeMillis();
        generateScene();
        this.time1 = System.currentTimeMillis();
        showScene();
        registerNewScene();
    }

    private void generateScene() {
        this.scenePool.clear();
        this.tempPool.clear();
        Collections.shuffle(this.trainPool, this.random);
        int i = 0;
        while (i < this.trainPool.size() && i < 6) {
            this.scenePool.add(this.trainPool.get(i));
            i++;
        }
        int j = 0;
        if (i < 6) {
            Collections.shuffle(this.learnedPool, this.random);
            j = 0;
            while (j < 6 - i && j < this.learnedPool.size()) {
                this.scenePool.add(this.learnedPool.get(j));
                j++;
            }
        }
        Collections.shuffle(this.scenePool, this.random);
        if (i + j < 6) {
            for (int k = 0; k < (6 - i) - j; k++) {
                this.scenePool.add(this.scenePool.get(k));
            }
        }
        this.tempPool.clear();
        for (int index = 0; index < this.scenePool.size(); index++) {
            Item itemScene = (Item) this.scenePool.get(index);
            if (!itemScene.equals(this.goalItem)) {
                this.tempPool.add(itemScene);
            }
        }
        if (this.tempPool.size() == 0) {
            if (this.scenePool.size() == 1) {
                this.goalItem = getItemFromPool(0);
            } else {
                this.goalItem = (Item) this.scenePool.get(new Random().nextInt(this.scenePool.size() - 1));
            }
        } else if (this.tempPool.size() == 1) {
            this.goalItem = (Item) this.tempPool.get(0);
        } else {
            this.goalItem = (Item) this.tempPool.get(new Random().nextInt(this.tempPool.size() - 1));
        }
        Collections.shuffle(this.scenePool, new Random(System.nanoTime()));
        this.itemInt = this.scenePool.indexOf(this.goalItem);
    }

    private Item getItemFromPool(int i) {
        if (i == 0) {
            Log.i(TAG, "### scene i==0");
            return (Item) this.scenePool.get(0);
        }
        Log.i(TAG, "### scene trainPool.size() != 1");
        return (Item) this.scenePool.get(this.random.nextInt(i));
    }

    private void showScene() {
        this.listerningForAnswer = true;
        int i;
        long time;
        if (this.isChooseLetter) {
            for (i = 0; i < 6; i++) {
                ((TextView) this.variants[i]).setText(((Item) this.scenePool.get(i)).content.toUpperCase(new Locale("ru", "RU")));
            }
            time = System.currentTimeMillis();
            MyApplication.setImage((Item) this.scenePool.get(this.itemInt), this.image);
            Log.i(TAG, "### optimization setImage " + (System.currentTimeMillis() - time));
        } else {
            for (i = 0; i < 6; i++) {
                time = System.currentTimeMillis();
                MyApplication.setImage((Item) this.scenePool.get(i), (ImageView) this.variants[i]);
                Log.i(TAG, "### optimization setImage " + (System.currentTimeMillis() - time));
            }
            this.letter.setText(((Item) this.scenePool.get(this.itemInt)).content.toUpperCase(new Locale("ru", "RU")));
        }
        showGameInfo();
    }
}
