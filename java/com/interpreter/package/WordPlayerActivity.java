package ru.avroraventures.russiansinglanguage;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import java.util.Locale;
import ru.avroraventures.russiansinglanguage.db.Item;
import ru.avroraventures.russiansinglanguage.tools.AutoCompleteTextViewWithButtonClear;
import ru.avroraventures.russiansinglanguage.tools.AutoResizeTextView;
import ru.avroraventures.russiansinglanguage.tools.OnSwipeTouchListener;

public class WordPlayerActivity extends MyActivity {
    private static final String TAG = WordPlayerActivity.class.getSimpleName();
    private static Context context;
    private static Handler handlerPlaying;
    static ImageView imageView;
    private static int indexPlaying;
    private static boolean isPlaying = false;
    private static Item[] items;
    static TextView left;
    static MediaPlayer m;
    private static TextView no_image_text;
    private static Runnable playOneLetterRunnable;
    static TextView right;
    private static String savedWord;
    private static FrameLayout start;
    private static AutoResizeTextView startTextView;
    private static FrameLayout stop;
    private static AutoResizeTextView stopTextView;
    private static AutoCompleteTextViewWithButtonClear word;
    private boolean isFirstTime = true;
    private LinearLayout mainLayout;
    private TextView speedTextView;

    static /* synthetic */ int access$808() {
        int i = indexPlaying;
        indexPlaying = i + 1;
        return i;
    }

    static /* synthetic */ int access$810() {
        int i = indexPlaying;
        indexPlaying = i - 1;
        return i;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMyTitle(getString(R.string.wordPlayerTitle));
        setContentView(R.layout.player_of_words);
        items = MyApplication.getItems(7);
        context = this;
        stop = (FrameLayout) findViewById(R.id.stop);
        start = (FrameLayout) findViewById(R.id.start);
        stopTextView = (AutoResizeTextView) findViewById(R.id.stopTextView);
        startTextView = (AutoResizeTextView) findViewById(R.id.startTextView);
        stopTextView.setEtalonText(startTextView.getText());
        imageView = (ImageView) findViewById(R.id.image);
        this.speedTextView = (TextView) findViewById(R.id.speedTextView);
        this.speedTextView.setText(MyApplication.getSpeedText());
        word = (AutoCompleteTextViewWithButtonClear) findViewById(R.id.word);
        this.mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        stop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.i(WordPlayerActivity.TAG, "### test  stop.setOnClickListener");
                WordPlayerActivity.this.endOfEditingWord();
                if (WordPlayerActivity.isPlaying) {
                    WordPlayerActivity.stopPlayer();
                }
            }
        });
        start.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.i(WordPlayerActivity.TAG, "### test start.setOnClickListener");
                WordPlayerActivity.this.endOfEditingWord();
                if (!WordPlayerActivity.isPlaying) {
                    WordPlayerActivity.this.startPlayer();
                }
            }
        });
        findViewById(R.id.play_speed).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MyApplication.toggleSpeed();
                WordPlayerActivity.this.speedTextView.setText(MyApplication.getSpeedText());
            }
        });
        word.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i(WordPlayerActivity.TAG, "### player onItemClick");
                WordPlayerActivity.this.endOfEditingWord();
            }
        });
        word.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i(WordPlayerActivity.TAG, "### player onFocusChange, hasFocus=" + hasFocus);
                String newWord = WordPlayerActivity.word.getText().toString();
                if (hasFocus) {
                    WordPlayerActivity.savedWord = newWord;
                    WordPlayerActivity.clearLettersMark();
                    if (WordPlayerActivity.isPlaying) {
                        WordPlayerActivity.stopPlayer();
                    }
                } else if (!newWord.equals(WordPlayerActivity.savedWord)) {
                    WordPlayerActivity.savedWord = newWord;
                    if (!newWord.isEmpty()) {
                        WordPlayerActivity.this.startPlayer();
                    }
                }
            }
        });
        word.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (WordPlayerActivity.word.getText().toString().length() > 0) {
                    WordPlayerActivity.this.findViewById(R.id.gesture_area).setVisibility(0);
                } else {
                    WordPlayerActivity.this.findViewById(R.id.gesture_area).setVisibility(4);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        findViewById(R.id.mainLayout).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.i(WordPlayerActivity.TAG, "### play onClick mainLayout");
                WordPlayerActivity.this.endOfEditingWord();
            }
        });
        word.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId != 3 && actionId != 6 && (event.getAction() != 0 || event.getKeyCode() != 66)) || (event != null && event.isShiftPressed())) {
                    return false;
                }
                Log.i(WordPlayerActivity.TAG, "### player - done writing the Word");
                WordPlayerActivity.this.endOfEditingWord();
                return true;
            }
        });
        setAutoCompleteAdapter();
        no_image_text = (TextView) findViewById(R.id.no_image_text);
        left = (TextView) findViewById(R.id.left);
        left.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.i(WordPlayerActivity.TAG, "### left");
                if (WordPlayerActivity.isPlaying) {
                    WordPlayerActivity.stopPlayer();
                }
                if (WordPlayerActivity.indexPlaying > 0) {
                    WordPlayerActivity.access$810();
                    WordPlayerActivity.this.setLetterAndImage(true);
                }
            }
        });
        right = (TextView) findViewById(R.id.right);
        right.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (WordPlayerActivity.isPlaying) {
                    WordPlayerActivity.stopPlayer();
                }
                if (WordPlayerActivity.indexPlaying < WordPlayerActivity.word.getText().toString().length() - 1) {
                    WordPlayerActivity.access$808();
                    WordPlayerActivity.this.setLetterAndImage(true);
                }
            }
        });
        findViewById(R.id.gesture_area).setVisibility(4);
        findViewById(R.id.gesture_area).setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeRight() {
                if (WordPlayerActivity.indexPlaying > 0) {
                    WordPlayerActivity.access$810();
                    WordPlayerActivity.this.setLetterAndImage(true);
                }
            }

            public void onSwipeLeft() {
                if (WordPlayerActivity.indexPlaying < WordPlayerActivity.word.getText().toString().length() - 1) {
                    WordPlayerActivity.access$808();
                    WordPlayerActivity.this.setLetterAndImage(true);
                }
            }
        });
        Answers.getInstance().logContentView(new ContentViewEvent().putContentId("Player"));
    }

    private void setAutoCompleteAdapter() {
        word.setAdapter(new ArrayAdapter(this, 17367050, MyApplication.autoCompleteList.getList()));
    }

    private static void setStartStopButtonsTextColor() {
        if (isPlaying) {
            startTextView.setTextColor(context.getResources().getColor(R.color.text_OFF));
            stopTextView.setTextColor(context.getResources().getColor(R.color.text_ON));
            return;
        }
        startTextView.setTextColor(context.getResources().getColor(R.color.text_ON));
        stopTextView.setTextColor(context.getResources().getColor(R.color.text_OFF));
    }

    private void endOfEditingWord() {
        Log.i(TAG, "### player endOfEditingWord, savedWord=" + savedWord + ", word=" + word.getText().toString());
        word.dismissDropDown();
        this.mainLayout.requestFocus();
        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(word.getWindowToken(), 0);
        if (savedWord == null || savedWord.equals(word.getText().toString())) {
            Log.i(TAG, "### player endOfEditingWord, don't play");
            return;
        }
        savedWord = word.getText().toString();
        startPlayer();
    }

    private static void markLetter() {
        SpannableStringBuilder sb = new SpannableStringBuilder(word.getText().toString());
        ForegroundColorSpan fcs = new ForegroundColorSpan(-1);
        StyleSpan bss = new StyleSpan(1);
        BackgroundColorSpan bgc = new BackgroundColorSpan(context.getResources().getColor(R.color.marked_letter));
        try {
            sb.setSpan(fcs, indexPlaying, indexPlaying + 1, 18);
            sb.setSpan(bss, indexPlaying, indexPlaying + 1, 18);
            sb.setSpan(bgc, indexPlaying, indexPlaying + 1, 18);
            word.setText(sb);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "IndexOutOfBoundsException, indexPlaying=" + indexPlaying);
        }
    }

    private synchronized void startPlayer() {
        int i = 1;
        synchronized (this) {
            Log.i(TAG, "### palyer play, isPlaying=" + isPlaying);
            if (!isPlaying) {
                final String TEXT = word.getText().toString();
                Log.i(TAG, "### player play, text='" + TEXT + "'");
                if (TEXT.length() != 0) {
                    Answers instance = Answers.getInstance();
                    CustomEvent customEvent = (CustomEvent) new CustomEvent("startPlayer").putCustomAttribute("word", TEXT);
                    String str = "isSoundOn";
                    if (!MyApplication.getIsSoundOn()) {
                        i = 0;
                    }
                    instance.logCustom((CustomEvent) ((CustomEvent) ((CustomEvent) customEvent.putCustomAttribute(str, Integer.valueOf(i))).putCustomAttribute("voice", Integer.valueOf(MyApplication.getCurrentVoices_menu_item()))).putCustomAttribute("speed", Integer.valueOf(MyApplication.getSpeedIndex())));
                    MyApplication.autoCompleteList.addNewItem(TEXT);
                    setAutoCompleteAdapter();
                    System.gc();
                    isPlaying = true;
                    setStartStopButtonsTextColor();
                    indexPlaying = 0;
                    if (!(handlerPlaying == null || playOneLetterRunnable == null)) {
                        handlerPlaying.removeCallbacks(playOneLetterRunnable);
                    }
                    playOneLetterRunnable = new Runnable() {
                        public void run() {
                            Log.i(WordPlayerActivity.TAG, "### player run, indexPlaying=" + WordPlayerActivity.indexPlaying + ", TEXT.length=" + TEXT.length());
                            if (WordPlayerActivity.indexPlaying < TEXT.length()) {
                                int duration = WordPlayerActivity.this.setLetterAndImage(true);
                                Log.i(WordPlayerActivity.TAG, "### duration = " + duration);
                                WordPlayerActivity.access$808();
                                if (WordPlayerActivity.handlerPlaying == null || WordPlayerActivity.indexPlaying >= TEXT.length() + 1) {
                                    Log.i(WordPlayerActivity.TAG, "### player run, indexPlaying>= charArray.length, stopPlayer()");
                                    WordPlayerActivity.stopPlayer();
                                    return;
                                }
                                WordPlayerActivity.handlerPlaying.postDelayed(this, (long) (MyApplication.getSpeedInt() + duration));
                                return;
                            }
                            WordPlayerActivity.stopPlayer();
                        }
                    };
                    handlerPlaying = new Handler();
                    handlerPlaying.post(playOneLetterRunnable);
                }
            }
        }
    }

    private int setLetterAndImage(boolean isPlaySound) {
        String TEXT = word.getText().toString();
        if (TEXT.length() <= 0) {
            return 0;
        }
        if (indexPlaying < 0) {
            indexPlaying = 0;
        } else if (indexPlaying > TEXT.length() - 1) {
            indexPlaying = TEXT.length() - 1;
        }
        markLetter();
        if (indexPlaying == 0) {
            left.setVisibility(4);
        } else if (indexPlaying == TEXT.length() - 1) {
            right.setVisibility(4);
        } else {
            left.setVisibility(0);
            right.setVisibility(0);
        }
        String LETTER = String.valueOf(TEXT.charAt(indexPlaying)).toUpperCase(Const.RUS_LOCALE);
        setIllustration(imageView, LETTER);
        if (isPlaySound && MyApplication.getIsSoundOn()) {
            return playSound(LETTER);
        }
        return 0;
    }

    private static void setIllustration(ImageView imageView, String letter) {
        Item item = getItemByContent(letter);
        Log.i(TAG, "### temp '" + letter + "'");
        if (item == null) {
            imageView.setImageDrawable(null);
        } else {
            MyApplication.setImage(item, imageView);
        }
    }

    private int playSound(String letter) {
        Item item = getItemByContent(letter);
        if (item == null) {
            return 300;
        }
        String soundFileName = item.soundFileName;
        if (MyApplication.currentVoices_menu_item == 1) {
            soundFileName = soundFileName + "_1";
        }
        soundFileName = soundFileName + ".mp3";
        if (soundFileName == null || soundFileName.isEmpty()) {
            return 0;
        }
        try {
            if (m != null) {
                if (m.isPlaying()) {
                    m.stop();
                }
                m.release();
            }
            m = new MediaPlayer();
            m.setAudioStreamType(3);
            AssetFileDescriptor descriptor = context.getResources().getAssets().openFd(soundFileName);
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            m.prepare();
            m.setLooping(false);
            m.start();
            return m.getDuration() + (MyApplication.longLetters.contains(letter) ? 300 : 0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void onResume() {
        super.onResume();
        MyApplication.mHapticFeedbackController.start();
        System.gc();
        if (this.isFirstTime) {
            this.isFirstTime = false;
        } else if (indexPlaying != -1) {
            setLetterAndImage(false);
        }
    }

    protected static void stopPlayer() {
        Log.i(TAG, "### player stopPlayer");
        System.gc();
        if (!(handlerPlaying == null || playOneLetterRunnable == null)) {
            handlerPlaying.removeCallbacks(playOneLetterRunnable);
            playOneLetterRunnable = null;
            handlerPlaying = null;
        }
        if (m != null) {
            m.release();
            m = null;
        }
        isPlaying = false;
        setStartStopButtonsTextColor();
        if (indexPlaying > 0) {
            indexPlaying--;
        }
    }

    private static void clearLettersMark() {
        word.setText(word.getText().toString());
        Log.i(TAG, "### player clearLettersMark");
    }

    private static Item getItemByContent(String letter) {
        for (int i = 0; i < items.length; i++) {
            if (letter.toUpperCase(new Locale("ru", "RU")).equals(items[i].content.toUpperCase(new Locale("ru", "RU")))) {
                return items[i];
            }
        }
        return null;
    }

    public void onPause() {
        super.onPause();
        if (isPlaying) {
            stopPlayer();
        }
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("word", word.getText().toString());
        savedInstanceState.putInt("indexPlaying", indexPlaying);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String TEXT = savedInstanceState.getString("word");
        word.setText(TEXT);
        indexPlaying = savedInstanceState.getInt("indexPlaying");
        if (indexPlaying < 0) {
            indexPlaying = 0;
        } else if (indexPlaying > TEXT.length() - 1) {
            indexPlaying = TEXT.length() - 1;
        }
    }
}
