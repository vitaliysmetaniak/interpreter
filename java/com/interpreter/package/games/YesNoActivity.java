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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import ru.avroraventures.russiansinglanguage.Const;
import ru.avroraventures.russiansinglanguage.MyApplication;
import ru.avroraventures.russiansinglanguage.R;
import ru.avroraventures.russiansinglanguage.db.Answer;
import ru.avroraventures.russiansinglanguage.db.Item;
import ru.avroraventures.russiansinglanguage.tools.AutoResizeTextView;

public class YesNoActivity extends GameActivity {
    private static final String TAG = YesNoActivity.class.getSimpleName();
    private ArrayList<Item> allPool;
    private LinearLayout answer_layout;
    private long endTime = 0;
    private ImageView image;
    private final String[] imageBackgrounds = new String[]{"l2.png", "l5.png", "l22.png", "l23.png", "l52.png", "l53.png"};
    private boolean isYes = false;
    private Item[] items;
    private int lastImageBackgroundsIndex = -1;
    private Item lastSceneItem = null;
    private ArrayList<Item> learnedPool;
    private TextView letter;
    private ImageView letter_background;
    private Random random = new Random(System.nanoTime());
    private int sceneIndex = 0;
    private ArrayList<Item> scenePool;
    private ArrayList<Item> tempPool;
    private ArrayList<Item> trainPool;

    protected void onCreate(Bundle savedInstanceState) {
        int i;
        int i2 = 1;
        setMyTitle(getString(R.string.ChooseYesNoTitle));
        setContentView(R.layout.game_choose_yes_no);
        super.onCreate(savedInstanceState);
        this.game = 3;
        this.stepsOfBall = 1;
        this.items = MyApplication.getItems(MyApplication.indexOfGroup);
        this.answer_layout = (LinearLayout) findViewById(R.id.answer_layout);
        this.image = (ImageView) findViewById(R.id.image);
        this.letter = (TextView) findViewById(R.id.letter);
        this.letter_background = (ImageView) findViewById(R.id.letter_background);
        ((AutoResizeTextView) findViewById(R.id.yesText)).setEtalonText(getString(R.string.noText));
        findViewById(R.id.yes).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                YesNoActivity.this.endTime = System.currentTimeMillis();
                Log.i(YesNoActivity.TAG, "### game endTime=" + YesNoActivity.this.endTime);
                if (YesNoActivity.this.isYes) {
                    YesNoActivity.this.rightAnswer();
                } else {
                    YesNoActivity.this.wrongAnswer();
                }
            }
        });
        findViewById(R.id.no).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                YesNoActivity.this.endTime = System.currentTimeMillis();
                Log.i(YesNoActivity.TAG, "### game endTime=" + YesNoActivity.this.endTime);
                if (YesNoActivity.this.isYes) {
                    YesNoActivity.this.wrongAnswer();
                } else {
                    YesNoActivity.this.rightAnswer();
                }
            }
        });
        Answers instance = Answers.getInstance();
        ContentViewEvent contentViewEvent = (ContentViewEvent) ((ContentViewEvent) ((ContentViewEvent) new ContentViewEvent().putContentId("YesNo").putCustomAttribute("group", Integer.valueOf(MyApplication.indexOfGroup))).putCustomAttribute("isSoundOn", Integer.valueOf(MyApplication.getIsSoundOn() ? 1 : 0))).putCustomAttribute("voice", Integer.valueOf(MyApplication.getCurrentVoices_menu_item()));
        String str = "isEffectsOn";
        if (MyApplication.getIsEffectsOn()) {
            i = 1;
        } else {
            i = 0;
        }
        contentViewEvent = (ContentViewEvent) contentViewEvent.putCustomAttribute(str, Integer.valueOf(i));
        String str2 = "isVibrationOn";
        if (!MyApplication.getIsVibrationOn()) {
            i2 = 0;
        }
        instance.logContentView((ContentViewEvent) contentViewEvent.putCustomAttribute(str2, Integer.valueOf(i2)));
    }

    private void wrongAnswer() {
        Log.i(TAG, "### pair Wrong answer");
        Item item = (Item) this.scenePool.get(0);
        registerWrongAnswer(new Answer(this.endTime, item.letter, item.group, this.game, this.endTime - this.startTime, 0, 0));
        ((Item) this.scenePool.get(0)).registerWrongAnswer();
        if (!this.trainPool.contains(this.scenePool.get(0))) {
            this.trainPool.add(this.scenePool.get(0));
        }
        this.learnedPool.remove(this.scenePool.get(0));
        if (!this.isYes) {
            ((Item) this.scenePool.get(1)).registerWrongAnswer();
            if (!this.trainPool.contains(this.scenePool.get(1))) {
                this.trainPool.add(this.scenePool.get(1));
            }
            this.learnedPool.remove(this.scenePool.get(1));
        }
        this.answer_layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_little));
        setBackgroundToVariant(0, R.drawable.background_red);
        setBackgroundToVariant(1, R.drawable.background_red);
        refreshScene();
    }

    private void rightAnswer() {
        Log.i(TAG, "### pair Right answer");
        Item item = (Item) this.scenePool.get(0);
        registerRightAnswer(5, new Answer(this.endTime, item.letter, item.group, this.game, this.endTime - this.startTime, 1, 0));
        this.trainPool.remove(this.scenePool.get(0));
        if (!this.learnedPool.contains(this.scenePool.get(0))) {
            this.learnedPool.add(this.scenePool.get(0));
        }
        Log.i(TAG, "### pair trainPool.size()=" + this.trainPool.size());
        refreshScene();
    }

    private void refreshScene() {
        Log.i(TAG, "### pair refreshScene");
        new Handler().post(new Runnable() {
            public void run() {
                YesNoActivity.this.time0 = System.currentTimeMillis();
                YesNoActivity.this.generateScene();
                YesNoActivity.this.time1 = System.currentTimeMillis();
                YesNoActivity.this.showScene();
                YesNoActivity.this.registerNewScene();
            }
        });
    }

    @TargetApi(16)
    private void setBackgroundToVariant(int i, int backgroundId) {
        if (this != null && i == 0) {
            if (VERSION.SDK_INT < 16) {
                this.image.setBackgroundDrawable(getResources().getDrawable(backgroundId));
            } else {
                this.image.setBackground(getResources().getDrawable(backgroundId));
            }
        }
    }

    public void onResume() {
        Log.i(TAG, "### game ChooseYesNoActivity onResume");
        super.onResume();
        this.allPool = new ArrayList(this.items.length);
        this.trainPool = new ArrayList(this.items.length);
        this.learnedPool = new ArrayList(this.items.length);
        this.scenePool = new ArrayList();
        this.tempPool = new ArrayList();
        for (int i = 0; i < this.items.length; i++) {
            this.allPool.add(this.items[i]);
            this.trainPool.add(this.items[i]);
        }
        this.time0 = System.currentTimeMillis();
        generateScene();
        this.time1 = System.currentTimeMillis();
        showScene();
        registerNewScene();
    }

    private void generateScene() {
        Log.i(TAG, "### scene generateScene " + this.sceneIndex + "---------------------");
        this.sceneIndex++;
        this.isYes = this.random.nextBoolean();
        Log.i(TAG, "### scene  isYes=" + this.isYes);
        this.scenePool.clear();
        this.tempPool.clear();
        if (this.trainPool.size() > 0) {
            Collections.shuffle(this.trainPool, this.random);
            for (int i = 0; i < this.trainPool.size(); i++) {
                if (((Item) this.trainPool.get(i)).equals(this.lastSceneItem)) {
                    Log.i(TAG, "### test game skip " + this.trainPool.get(i));
                } else {
                    this.tempPool.add(this.trainPool.get(i));
                }
            }
            if (this.tempPool.size() > 1) {
                this.scenePool.add(this.tempPool.get(0));
                this.scenePool.add(this.tempPool.get(1));
            } else if (this.tempPool.size() == 1) {
                this.scenePool.add(this.tempPool.get(0));
                Collections.shuffle(this.learnedPool, this.random);
                this.scenePool.add(this.learnedPool.get(0));
            } else {
                Collections.shuffle(this.learnedPool, this.random);
                this.scenePool.add(this.learnedPool.get(0));
                this.scenePool.add(this.learnedPool.get(1));
            }
        } else if (this.learnedPool.size() > 1) {
            Collections.shuffle(this.learnedPool, this.random);
            if (((Item) this.learnedPool.get(0)).equals(this.lastSceneItem)) {
                this.scenePool.add(this.learnedPool.get(1));
                this.scenePool.add(this.learnedPool.get(2));
            } else {
                this.scenePool.add(this.learnedPool.get(0));
                this.scenePool.add(this.learnedPool.get(1));
            }
        } else {
            Log.e(TAG, "### error learnedPool.size()<=1 ???");
        }
        this.lastSceneItem = (Item) this.scenePool.get(0);
        Log.i(TAG, "### scene trainPool=" + this.trainPool);
        Log.i(TAG, "### scene learnedPool=" + this.learnedPool);
        Log.i(TAG, "### scene scenePool=" + this.scenePool);
    }

    private void showScene() {
        int i;
        showDifferentColorBackground();
        showItem((Item) this.scenePool.get(0), 0, false);
        setBackgroundToVariant(0, R.drawable.background);
        ArrayList arrayList = this.scenePool;
        if (this.isYes) {
            i = 0;
        } else {
            i = 1;
        }
        showItem((Item) arrayList.get(i), 1, true);
        setBackgroundToVariant(1, R.drawable.background);
    }

    private void showDifferentColorBackground() {
        int index = this.random.nextInt(this.imageBackgrounds.length - 1);
        if (this.lastImageBackgroundsIndex == index) {
            index = this.random.nextInt(this.imageBackgrounds.length - 1);
        }
        if (this.lastImageBackgroundsIndex == index) {
            index = this.random.nextInt(this.imageBackgrounds.length - 1);
        }
        MyApplication.setImage(this.imageBackgrounds[index], this.letter_background);
        this.lastImageBackgroundsIndex = index;
    }

    private void showItem(Item item, int place, boolean isLetter) {
        Log.i(TAG, "### pair showItem place=" + place + ", isLetter=" + isLetter);
        if (isLetter) {
            this.letter.setText(item.content.toUpperCase(Const.RUS_LOCALE));
        } else {
            MyApplication.setImage(item, this.image);
        }
    }
}
