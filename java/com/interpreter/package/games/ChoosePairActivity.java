package ru.avroraventures.russiansinglanguage.games;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import ru.avroraventures.russiansinglanguage.Const;
import ru.avroraventures.russiansinglanguage.MyApplication;
import ru.avroraventures.russiansinglanguage.R;
import ru.avroraventures.russiansinglanguage.db.Answer;
import ru.avroraventures.russiansinglanguage.db.Item;
import ru.avroraventures.russiansinglanguage.db.PairVariant;
import ru.avroraventures.russiansinglanguage.tools.AutoResizeTextView;

public class ChoosePairActivity extends GameActivity {
    private static final String TAG = ChoosePairActivity.class.getSimpleName();
    private final int PROMPT = -1;
    final int SIZE_OF_ITEMS = 5;
    private final int SIZE_OF_SCENE = 9;
    private ArrayList<Integer> allPlaces = new ArrayList(9);
    private ArrayList<Item> allPool;
    private LinearLayout answer_layout;
    private LinearLayout[] answer_layout_rows = new LinearLayout[3];
    private Item[] items;
    private int itemsOnScene;
    private ArrayList<Item> learnedPool;
    private boolean listerningForAnswer = true;
    private Item no_goalItem;
    private int no_itemInt;
    OnClickListener onLetterClickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.prompt /*2131492903*/:
                    ChoosePairActivity.this.answer(-1);
                    return;
                default:
                    ChoosePairActivity.this.answer(((Integer) v.getTag()).intValue());
                    return;
            }
        }
    };
    private HashMap<Integer, PairVariant> placesMap = new HashMap(9);
    private ArrayList<Item> scenePool;
    private PairVariant selectedItem = null;
    private int selectedItems;
    private HashMap<Item, Boolean> tempItemsForPairs = new HashMap(9);
    private ArrayList<Item> tempPool;
    private ArrayList<Item> trainPool;
    final FrameLayout[] variants = new FrameLayout[9];

    protected void onCreate(Bundle savedInstanceState) {
        int i;
        int i2 = 1;
        setMyTitle(getString(R.string.choosePairTitle));
        setContentView(R.layout.game_choose_pair);
        super.onCreate(savedInstanceState);
        this.game = 2;
        this.stepsOfBall = 3;
        this.items = MyApplication.getItems(MyApplication.indexOfGroup);
        this.answer_layout = (LinearLayout) findViewById(R.id.answer_layout);
        for (int i3 = 0; i3 < this.answer_layout.getChildCount(); i3++) {
            if (i3 < this.answer_layout_rows.length) {
                this.answer_layout_rows[i3] = (LinearLayout) this.answer_layout.getChildAt(i3);
            }
        }
        int k = 0;
        for (LinearLayout childAt : this.answer_layout_rows) {
            for (int j = 0; j < 3; j++) {
                this.variants[k] = (FrameLayout) childAt.getChildAt(j);
                k++;
            }
        }
        Answers instance = Answers.getInstance();
        ContentViewEvent contentViewEvent = (ContentViewEvent) ((ContentViewEvent) ((ContentViewEvent) new ContentViewEvent().putContentId("ChoosePair").putCustomAttribute("group", Integer.valueOf(MyApplication.indexOfGroup))).putCustomAttribute("isSoundOn", Integer.valueOf(MyApplication.getIsSoundOn() ? 1 : 0))).putCustomAttribute("voice", Integer.valueOf(MyApplication.getCurrentVoices_menu_item()));
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

    private void answer(int i) {
        Log.i(TAG, "### prompt, answer=" + i + ", listerningForAnswer=" + this.listerningForAnswer);
        MyApplication.tryVibrate(true);
        PairVariant pairVariant = (PairVariant) this.placesMap.get(new Integer(i));
        Log.i(TAG, "### pair pairVariant.status=" + pairVariant.status);
        if (pairVariant.status == -1 || pairVariant.status == 1) {
            if (pairVariant.status == -1) {
                this.variants[pairVariant.placeOnScene].startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_little));
            }
        } else if (this.selectedItems == 0) {
            selectItem(pairVariant);
            setBackgroundToVariant(i, R.drawable.background_green);
            setColorToVariant(pairVariant, R.color.white);
            this.listerningForAnswer = true;
        } else if (pairVariant.isSelected) {
            unSelectItem(pairVariant);
            setBackgroundToVariant(i, R.drawable.background);
            setColorToVariant(pairVariant, R.color.font_color);
            this.listerningForAnswer = true;
        } else if (pairVariant.isLetter == this.selectedItem.isLetter) {
            unSelectItem(this.selectedItem);
            setBackgroundToVariant(this.selectedItem.placeOnScene, R.drawable.background);
            setColorToVariant(this.selectedItem, R.color.font_color);
            selectItem(pairVariant);
            setBackgroundToVariant(pairVariant.placeOnScene, R.drawable.background_green);
            setColorToVariant(pairVariant, R.color.white);
        } else if (pairVariant.item == this.selectedItem.item) {
            rightAnswer(pairVariant);
        } else {
            wrongAnswer(pairVariant);
            this.listerningForAnswer = true;
        }
    }

    private void wrongAnswer(PairVariant pairVariant) {
        Log.i(TAG, "### pair Wrong answer");
        pairVariant.status = -1;
        pairVariant.item.registerWrongAnswer();
        this.selectedItem.status = -1;
        this.selectedItem.item.registerWrongAnswer();
        long endTime = System.currentTimeMillis();
        Log.i(TAG, "### game endTime=" + endTime);
        registerWrongAnswer(new Answer(endTime, pairVariant.item.letter, pairVariant.item.group, this.game, endTime - this.startTime, 0, 0));
        Animation shake1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        Animation shake2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        this.variants[this.selectedItem.placeOnScene].startAnimation(shake1);
        setBackgroundToVariant(this.selectedItem.placeOnScene, R.drawable.background_red);
        setColorToVariant(this.selectedItem, R.color.white);
        this.variants[pairVariant.placeOnScene].startAnimation(shake2);
        setBackgroundToVariant(pairVariant.placeOnScene, R.drawable.background_red);
        setColorToVariant(pairVariant, R.color.white);
        if (!this.trainPool.contains(this.selectedItem.item)) {
            this.trainPool.add(this.selectedItem.item);
        }
        this.learnedPool.remove(this.selectedItem.item);
        if (!this.trainPool.contains(pairVariant.item)) {
            this.trainPool.add(pairVariant.item);
        }
        this.learnedPool.remove(pairVariant.item);
        this.selectedItems = 0;
        this.selectedItem = null;
        this.itemsOnScene -= 4;
        refreshSceneIfNeeded();
    }

    private void rightAnswer(PairVariant pairVariant) {
        Log.i(TAG, "### pair Right answer");
        Item item = pairVariant.item;
        long endTime = System.currentTimeMillis();
        Log.i(TAG, "### game endTime=" + endTime);
        registerRightAnswer(10, new Answer(endTime, item.letter, item.group, this.game, endTime - this.startTime, 1, 0));
        pairVariant.status = 1;
        this.selectedItem.status = 1;
        pairVariant.item.registerRightAnswer(-1.0f);
        Animation fade_and_zoom_out1 = AnimationUtils.loadAnimation(this, R.anim.fade_and_zoom_out1);
        fade_and_zoom_out1.setFillAfter(true);
        this.variants[this.selectedItem.placeOnScene].startAnimation(fade_and_zoom_out1);
        selectItem(pairVariant);
        setBackgroundToVariant(pairVariant.placeOnScene, R.drawable.background_green);
        setColorToVariant(pairVariant, R.color.white);
        Animation fade_and_zoom_out2 = AnimationUtils.loadAnimation(this, R.anim.fade_and_zoom_out1);
        fade_and_zoom_out2.setFillAfter(true);
        this.variants[pairVariant.placeOnScene].startAnimation(fade_and_zoom_out2);
        this.selectedItems = 0;
        this.selectedItem = null;
        this.itemsOnScene -= 2;
        this.trainPool.remove(pairVariant.item);
        if (!this.learnedPool.contains(pairVariant.item)) {
            this.learnedPool.add(pairVariant.item);
        }
        showGameInfo();
        Log.i(TAG, "### pair trainPool.size()=" + this.trainPool.size());
        refreshSceneIfNeeded();
        this.listerningForAnswer = true;
    }

    private void refreshSceneIfNeeded() {
        Log.i(TAG, "### pair refreshSceneIfNeeded itemsOnScene=" + this.itemsOnScene + ", isPairsExisted()=" + isPairsExisted());
        this.startTime = System.currentTimeMillis();
        if (!isPairsExisted()) {
            if (isMoreThanOneCardOnScene()) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        for (int i = 0; i < 9; i++) {
                            PairVariant pairVariant = (PairVariant) ChoosePairActivity.this.placesMap.get(Integer.valueOf(i));
                            if (pairVariant.status == 0) {
                                ChoosePairActivity.this.setBackgroundToVariant(pairVariant.placeOnScene, R.drawable.background_red);
                                ChoosePairActivity.this.setColorToVariant(pairVariant, R.color.white);
                            }
                        }
                    }
                }, 0);
            }
            if (this.trainPool.size() == 0) {
                initPools();
            }
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    ChoosePairActivity.this.time0 = System.currentTimeMillis();
                    ChoosePairActivity.this.generateScene();
                    ChoosePairActivity.this.time1 = System.currentTimeMillis();
                    ChoosePairActivity.this.showScene();
                    ChoosePairActivity.this.registerNewScene();
                    ChoosePairActivity.this.listerningForAnswer = true;
                }
            }, 0);
        }
    }

    private boolean isMoreThanOneCardOnScene() {
        int showed = 0;
        for (int i = 0; i < 9; i++) {
            if (((PairVariant) this.placesMap.get(Integer.valueOf(i))).status == 0) {
                showed++;
                if (showed > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPairsExisted() {
        for (int i = 0; i < 9; i++) {
            PairVariant firstPairVariant = (PairVariant) this.placesMap.get(Integer.valueOf(i));
            if (firstPairVariant.status == 0) {
                for (int j = i + 1; j < 9; j++) {
                    PairVariant secondPairVariant = (PairVariant) this.placesMap.get(Integer.valueOf(j));
                    if (secondPairVariant.status == 0 && secondPairVariant.isLetter != firstPairVariant.isLetter && secondPairVariant.item.content.equals(firstPairVariant.item.content)) {
                        return true;
                    }
                }
                continue;
            }
        }
        return false;
    }

    private void selectItem(PairVariant pairVariant) {
        pairVariant.isSelected = true;
        this.selectedItem = pairVariant;
        this.selectedItems++;
    }

    private void unSelectItem(PairVariant pairVariant) {
        pairVariant.isSelected = false;
        this.selectedItems--;
    }

    private void setColorToVariant(PairVariant pairVariant, int colorId) {
        if (pairVariant.isLetter) {
            ((TextView) this.variants[pairVariant.placeOnScene].getChildAt(0)).setTextColor(getResources().getColor(colorId));
        }
    }

    @TargetApi(16)
    private void setBackgroundToVariant(int i, int backgroundId) {
        if (this != null) {
            if (VERSION.SDK_INT < 16) {
                this.variants[i].setBackgroundDrawable(getResources().getDrawable(backgroundId));
            } else {
                this.variants[i].setBackground(getResources().getDrawable(backgroundId));
            }
        }
    }

    public void onResume() {
        Log.i(TAG, "### scene onResume");
        super.onResume();
        initPools();
        this.time0 = System.currentTimeMillis();
        generateScene();
        this.time1 = System.currentTimeMillis();
        showScene();
        registerNewScene();
    }

    private void initPools() {
        this.allPool = new ArrayList(this.items.length);
        this.trainPool = new ArrayList(this.items.length);
        this.learnedPool = new ArrayList();
        this.scenePool = new ArrayList();
        this.tempPool = new ArrayList();
        for (int i = 0; i < this.items.length; i++) {
            this.allPool.add(this.items[i]);
            this.trainPool.add(this.items[i]);
        }
    }

    private void generateScene() {
        int i;
        Log.i(TAG, "### pair generateScene");
        Random random = new Random(System.nanoTime());
        this.selectedItems = 0;
        this.scenePool.clear();
        this.tempPool.clear();
        int learningPoolIndex = 0;
        Collections.shuffle(this.trainPool, random);
        for (i = 0; i < this.trainPool.size(); i++) {
            this.tempPool.add(this.trainPool.get(i));
        }
        Log.i(TAG, "### scene tempPool=" + this.tempPool.toString());
        for (i = 0; i < 5; i++) {
            if (this.tempPool.size() > 0) {
                Log.i(TAG, "### scene tempPool.size() > 0");
                if (this.tempPool.size() == 1) {
                    this.scenePool.add(this.tempPool.get(0));
                    this.tempPool.remove(0);
                } else {
                    int randomIndex = random.nextInt(this.tempPool.size() - 1);
                    this.scenePool.add(this.tempPool.get(randomIndex));
                    this.tempPool.remove(randomIndex);
                }
                Log.i(TAG, "### scene tempPool=" + this.tempPool.toString());
            } else if (this.learnedPool.size() > 0) {
                Log.i(TAG, "### scene learnedPool.size() > 0");
                if (this.learnedPool.size() == 1) {
                    this.scenePool.add(this.learnedPool.get(0));
                } else {
                    this.scenePool.add(this.learnedPool.get(learningPoolIndex));
                    if (learningPoolIndex < this.learnedPool.size() - 1) {
                        learningPoolIndex++;
                    }
                }
            } else if (this.scenePool.size() == 1) {
                this.scenePool.add(this.scenePool.get(0));
            } else {
                this.scenePool.add(this.scenePool.get(random.nextInt(this.scenePool.size() - 1)));
            }
        }
        Log.i(TAG, "### scene scenePool=" + this.scenePool);
        Log.i(TAG, "### scene learnedPool=" + this.learnedPool);
        Log.i(TAG, "### scene trainPool=" + this.trainPool);
        if (this.tempPool.size() == 0) {
            this.no_goalItem = (Item) this.scenePool.get(random.nextInt(4) + 1);
        } else {
            this.no_goalItem = (Item) this.scenePool.get(random.nextInt(5));
        }
        this.no_itemInt = this.scenePool.indexOf(this.no_goalItem);
        Log.i(TAG, "### scene no_itemInt=" + this.no_itemInt);
    }

    private void showScene() {
        int i;
        Random random = new Random();
        this.itemsOnScene = 9;
        this.tempItemsForPairs.clear();
        int k = 0;
        for (i = 0; i < this.answer_layout_rows.length; i++) {
            for (int j = 0; j < 3; j++) {
                ((FrameLayout) this.answer_layout_rows[i].getChildAt(j)).removeAllViews();
                ((FrameLayout) this.answer_layout_rows[i].getChildAt(j)).clearAnimation();
                setBackgroundToVariant(k, R.drawable.background);
                k++;
            }
        }
        for (i = 0; i < 9; i++) {
            this.allPlaces.add(new Integer(i));
        }
        for (int indexScene = 0; indexScene < 9; indexScene++) {
            PairVariant pairVariant;
            Item item = (Item) this.scenePool.get(indexScene % 5);
            Log.i(TAG, "### pair showScene, item=" + item);
            int index = this.allPlaces.size() == 1 ? 0 : random.nextInt(this.allPlaces.size() - 1);
            Integer placeIndex = (Integer) this.allPlaces.get(index);
            this.allPlaces.remove(index);
            if (this.tempItemsForPairs.containsKey(item)) {
                pairVariant = new PairVariant(item, !((Boolean) this.tempItemsForPairs.get(item)).booleanValue(), placeIndex.intValue());
                this.tempItemsForPairs.remove(item);
            } else {
                boolean isLetter = random.nextBoolean();
                pairVariant = new PairVariant(item, isLetter, placeIndex.intValue());
                this.tempItemsForPairs.put(item, Boolean.valueOf(isLetter));
            }
            this.placesMap.put(placeIndex, pairVariant);
        }
        showGameInfo();
        Log.i(TAG, "### pair temp scenePool=" + this.scenePool);
        Log.i(TAG, "### pair temp scenePool=" + this.placesMap);
        for (i = 0; i < 9; i++) {
            showItem(i, (PairVariant) this.placesMap.get(new Integer(i)));
        }
    }

    private void showItem(int place, PairVariant pairVariant) {
        Log.i(TAG, "### pair showItem place=" + place + ", pairVariant=" + pairVariant);
        pairVariant.status = 0;
        LayoutInflater inflater = (LayoutInflater) getSystemService("layout_inflater");
        if (pairVariant.isLetter) {
            AutoResizeTextView letter = (AutoResizeTextView) inflater.inflate(R.layout.one_letter_for_pair, null);
            letter.setText(pairVariant.item.content.toUpperCase(Const.RUS_LOCALE));
            this.variants[place].addView(letter);
        } else {
            ImageView imageView = (ImageView) inflater.inflate(R.layout.one_gesture_for_pair1, null);
            MyApplication.setImage(pairVariant.item, imageView);
            this.variants[place].addView(imageView);
        }
        this.variants[place].setTag(Integer.valueOf(place));
        this.variants[place].setOnClickListener(this.onLetterClickListener);
    }

    public void onPause() {
        super.onPause();
    }
}
