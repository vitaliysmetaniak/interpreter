package ru.avroraventures.russiansinglanguage;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import ru.avroraventures.russiansinglanguage.db.DatabaseHandler;
import ru.avroraventures.russiansinglanguage.db.Item;
import ru.avroraventures.russiansinglanguage.db.PairVariant;
import ru.avroraventures.russiansinglanguage.tools.AutoCompleteList;
import ru.avroraventures.russiansinglanguage.tools.HapticFeedbackController;

public final class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    public static MenuItem action_about;
    public static MenuItem action_settings;
    public static HashMap<String, Item> allAlphabetHashMap = new HashMap(33);
    static String[] all_files;
    public static String[] all_letters;
    private static Context appContext;
    private static int appVolume;
    public static AutoCompleteList autoCompleteList;
    public static Context context;
    private static int currentStatisticsSpinnerItem;
    public static int currentVoices_menu_item;
    public static String[] gamesNames;
    public static String[][] group;
    public static String[] group_descriptions;
    public static String[] groupsTitles;
    public static int indexOfGroup = 0;
    private static boolean isEffectsOn;
    private static boolean isSoundOn;
    private static boolean isVibrationOn;
    public static String longLetters;
    private static MediaPlayer m;
    public static HapticFeedbackController mHapticFeedbackController;
    private static SharedPreferences sharedPreferences;
    private static int speedIndex;
    private static String[] speeds;
    private static int[] speedsInt;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent intent) {
            Log.i(MyApplication.TAG, "### volume onReceive");
            int volume = ((AudioManager) MyApplication.this.getSystemService("audio")).getStreamVolume(3);
            Log.i(MyApplication.TAG, "### volume onReceive, volume =" + volume);
            MyApplication.setAppVolume(volume, true);
        }
    };

    public enum TypeOfSound {
        Voice,
        Effect,
        SwitchOnOffSoundEffect
    }

    public static boolean getIsEffectsOn() {
        return isEffectsOn;
    }

    public static boolean getIsVibrationOn() {
        return isVibrationOn;
    }

    public static int getCurrentVoices_menu_item() {
        return currentVoices_menu_item;
    }

    public static int getCurrentStatisticsSpinnerItem() {
        return currentStatisticsSpinnerItem;
    }

    public static int getAppVolume() {
        return appVolume;
    }

    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Kit[]{new Crashlytics()});
        Log.i(TAG, "on app create,  BuildConfig.DEBUG=false");
        context = getApplicationContext();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(this.receiver, filter);
        appContext = getApplicationContext();
        sharedPreferences = getSharedPreferences("AppPref", 0);
        autoCompleteList = new AutoCompleteList(sharedPreferences);
        m = new MediaPlayer();
        m.setAudioStreamType(3);
        longLetters = getResources().getString(R.string.longLetters);
        isSoundOn = sharedPreferences.getBoolean("isSoundOn", true);
        isEffectsOn = sharedPreferences.getBoolean("isEffectsOn", true);
        isVibrationOn = sharedPreferences.getBoolean("isVibrationOn", true);
        currentVoices_menu_item = sharedPreferences.getInt("currentVoices_menu_item", 0);
        speedIndex = sharedPreferences.getInt("speedIndex", 1);
        currentStatisticsSpinnerItem = sharedPreferences.getInt("currentStatisticsSpinnerItem", 0);
        speeds = getResources().getStringArray(R.array.speeds);
        speedsInt = getResources().getIntArray(R.array.speedsInt);
        gamesNames = getResources().getStringArray(R.array.gamesNames);
        if (speeds.length != speedsInt.length) {
            Log.wtf(TAG, "### speeds.length!=speedsInt.length !!!");
        }
        loadDataToDataBase();
        mHapticFeedbackController = new HapticFeedbackController(this);
        AudioManager audioManager = (AudioManager) appContext.getSystemService("audio");
        appVolume = sharedPreferences.getInt("appVolume", audioManager.getStreamVolume(3));
        audioManager.setStreamVolume(3, appVolume, 0);
        Log.i(TAG, "### volume get from shared appVolume=" + appVolume);
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        db.getAllAnswers();
        Log.i(TAG, "### test " + db.getStatistics(0));
    }

    private void loadDataToDataBase() {
        all_letters = getResources().getStringArray(R.array.all_letters);
        all_files = getResources().getStringArray(R.array.all_letters_files);
        if (all_letters.length != all_files.length) {
            Log.wtf(TAG, "all_letters.length!=all_files.length ???");
        }
        for (int i = 0; i < all_letters.length; i++) {
            allAlphabetHashMap.put(all_letters[i].toUpperCase(new Locale("ru", "RU")), new Item(Integer.valueOf(all_files[i]).intValue() - 1, all_letters[i], -1, all_files[i], all_files[i], BuildConfig.FLAVOR, 0, 0, 0, 0.0f));
        }
        groupsTitles = getResources().getStringArray(R.array.groups);
        group_descriptions = getResources().getStringArray(R.array.group_descriptions);
        group = new String[7][];
        group[0] = getResources().getStringArray(R.array.group1);
        group[1] = getResources().getStringArray(R.array.group2);
        group[2] = getResources().getStringArray(R.array.group3);
        group[3] = getResources().getStringArray(R.array.group4);
        group[4] = getResources().getStringArray(R.array.group5);
        group[5] = getResources().getStringArray(R.array.group6);
        group[6] = getResources().getStringArray(R.array.group7);
        setGroupIndexes();
    }

    private void setGroupIndexes() {
        for (int i = 0; i < group.length; i++) {
            for (int j = 0; j < group[i].length; j++) {
                Item item = (Item) allAlphabetHashMap.get(group[i][j].toUpperCase(new Locale("ru", "RU")));
                if (item != null) {
                    item.group = i;
                } else {
                    Log.e(TAG, "### setGroupIndexes - can find letter " + group[i][j] + " for group " + i);
                }
            }
        }
    }

    public static Item[] getItems(int indexOfGroup) {
        Item[] items;
        int i;
        if (indexOfGroup >= group.length) {
            items = new Item[33];
            for (i = 0; i < 33; i++) {
                items[i] = (Item) allAlphabetHashMap.get(all_letters[i].toUpperCase(new Locale("ru", "RU")));
            }
        } else {
            items = new Item[group[indexOfGroup].length];
            for (i = 0; i < group[indexOfGroup].length; i++) {
                items[i] = (Item) allAlphabetHashMap.get(group[indexOfGroup][i].toUpperCase(new Locale("ru", "RU")));
            }
        }
        return items;
    }

    public static void setImage(Item item, ImageView imageView) {
        if (item != null) {
            String fileName = item.imageFileName + ".png";
            Log.i(TAG, "### setImage, fileName=" + fileName);
            setImage(fileName, imageView);
        }
    }

    public static void setImage(String fileName, ImageView imageView) {
        InputStream ims = null;
        try {
            ims = appContext.getAssets().open(fileName);
            imageView.setImageDrawable(Drawable.createFromStream(ims, null));
            if (ims != null) {
                try {
                    ims.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            try {
                ims = appContext.getAssets().open("no_image.png");
                imageView.setImageDrawable(Drawable.createFromStream(ims, null));
            } catch (IOException e2) {
                Log.e(TAG, "file =no_image.png, error:" + ex.toString());
            } catch (Throwable th) {
                if (ims != null) {
                    try {
                        ims.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            }
            if (ims != null) {
                try {
                    ims.close();
                } catch (IOException e32) {
                    e32.printStackTrace();
                }
            }
        }
    }

    public static void playSample() {
        playSound("1", TypeOfSound.Voice);
    }

    public static void playSwitchSoundOnOff() {
        if (isEffectsOn) {
            if (isSoundOn) {
                playSound("switch_on", TypeOfSound.SwitchOnOffSoundEffect);
            } else {
                playSound("switch_off", TypeOfSound.SwitchOnOffSoundEffect);
            }
            if (SettingsActivity.switchSound != null) {
                SettingsActivity.switchSound.setChecked(isSoundOn);
            }
        }
    }

    public static synchronized void playSound(String file, TypeOfSound typeOfSound) {
        synchronized (MyApplication.class) {
            if (isSoundOn) {
                if (typeOfSound != TypeOfSound.SwitchOnOffSoundEffect) {
                    if (isSoundOn) {
                        if (typeOfSound == TypeOfSound.Voice) {
                            switch (currentVoices_menu_item) {
                                case PairVariant.SHOWED /*0*/:
                                    break;
                                case PairVariant.RIGHT /*1*/:
                                    file = file + "_1";
                                    break;
                                case 2:
                                    break;
                                default:
                                    break;
                            }
                        } else if (typeOfSound == TypeOfSound.Effect && !isEffectsOn) {
                        }
                    }
                }
                try {
                    if (m != null) {
                        if (m.isPlaying()) {
                            m.stop();
                        }
                        m.release();
                    }
                    m = new MediaPlayer();
                    AssetFileDescriptor descriptor = appContext.getResources().getAssets().openFd(file + ".mp3");
                    m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                    descriptor.close();
                    m.prepare();
                    m.setLooping(false);
                    m.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean getIsSoundOn() {
        return isSoundOn;
    }

    public static int getSpeedIndex() {
        return speedIndex;
    }

    public static void toggleSpeed() {
        if (speedIndex == speeds.length - 1) {
            setSpeedIndex(0);
        } else {
            setSpeedIndex(speedIndex + 1);
        }
    }

    public static String getSpeedText() {
        return speeds[speedIndex];
    }

    public static void setSpeedIndex(int _speedIndex) {
        speedIndex = _speedIndex;
        Editor edit = sharedPreferences.edit();
        edit.putInt("speedIndex", speedIndex);
        edit.apply();
    }

    public static int getSpeedInt() {
        return speedsInt[speedIndex];
    }

    public static void setIsSoundOn(boolean _isSoundOn) {
        isSoundOn = _isSoundOn;
        Editor edit = sharedPreferences.edit();
        edit.putBoolean("isSoundOn", _isSoundOn);
        edit.apply();
    }

    public static void setIsEffectsOn(boolean _isEffectsOn) {
        isEffectsOn = _isEffectsOn;
        Editor edit = sharedPreferences.edit();
        edit.putBoolean("isEffectsOn", _isEffectsOn);
        edit.apply();
    }

    public static void setIsVibrationOn(boolean _isVibrationOn) {
        isVibrationOn = _isVibrationOn;
        Editor edit = sharedPreferences.edit();
        edit.putBoolean("isVibrationOn", _isVibrationOn);
        edit.apply();
    }

    public static void setCurrentStatisticsSpinnerItem(int _currentStatisticsSpinnerItem) {
        if (currentStatisticsSpinnerItem != _currentStatisticsSpinnerItem) {
            currentStatisticsSpinnerItem = _currentStatisticsSpinnerItem;
            sharedPreferences.edit().putInt("currentStatisticsSpinnerItem", currentStatisticsSpinnerItem).apply();
        }
    }

    public static void setCurrentVoices_menu_item(int _currentVoices_menu_item) {
        if (currentVoices_menu_item != _currentVoices_menu_item) {
            currentVoices_menu_item = _currentVoices_menu_item;
            sharedPreferences.edit().putInt("currentVoices_menu_item", _currentVoices_menu_item).apply();
        }
    }

    public static void setAppVolume(int streamVolume, boolean isHardButton) {
        Log.i(TAG, "### volume setAppVolume: " + streamVolume);
        if (appVolume != streamVolume) {
            AudioManager audioManager = (AudioManager) context.getSystemService("audio");
            if (!isHardButton) {
                audioManager.setStreamVolume(3, streamVolume, 0);
            }
            if (isHardButton && SettingsActivity.volumeSeekBar != null) {
                SettingsActivity.volumeSeekBar.setProgress(streamVolume);
            }
            appVolume = streamVolume;
            sharedPreferences.edit().putInt("appVolume", streamVolume).apply();
        }
    }

    public static void tryVibrate(boolean isTrue) {
        if (isVibrationOn) {
            mHapticFeedbackController.tryVibrate(isTrue);
        }
    }

    public static boolean saveScoreForGame(int game, int score) {
        if (score <= sharedPreferences.getInt("scoreForGame" + String.valueOf(game), 0)) {
            return false;
        }
        sharedPreferences.edit().putInt("scoreForGame" + String.valueOf(game), score).apply();
        return true;
    }

    public static boolean saveScoreForGroup(int groupIndex, int score) {
        if (score <= sharedPreferences.getInt("scoreForGroup" + String.valueOf(groupIndex), 0)) {
            return false;
        }
        sharedPreferences.edit().putInt("scoreForGroup" + String.valueOf(groupIndex), score).apply();
        return true;
    }
}
