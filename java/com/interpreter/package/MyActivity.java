package ru.avroraventures.russiansinglanguage;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class MyActivity extends Activity {
    private static final String TAG = MyActivity.class.getSimpleName();
    public static MenuItem action_about;
    public static MenuItem action_settings;
    public static MenuItem action_sound;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setMyTitle(String title) {
        ActionBar actionBar = getActionBar();
        Log.i(TAG, "### test actionbar");
        if (actionBar != null) {
            Log.i(TAG, "### test actionbar, title = title");
            actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>" + title + "</font>"));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        action_sound = menu.getItem(0);
        action_settings = menu.getItem(1);
        action_about = menu.getItem(2);
        if (this instanceof SettingsActivity) {
            action_settings.setIcon(MyApplication.context.getResources().getDrawable(R.drawable.settings_on));
        } else {
            action_settings.setIcon(MyApplication.context.getResources().getDrawable(R.drawable.settings_off));
        }
        if (this instanceof AboutActivity) {
            action_about.setIcon(MyApplication.context.getResources().getDrawable(R.drawable.info_on));
        } else {
            action_about.setIcon(MyApplication.context.getResources().getDrawable(R.drawable.info_off));
        }
        if (MyApplication.getIsSoundOn()) {
            action_sound.setIcon(MyApplication.context.getResources().getDrawable(R.drawable.sound_on_));
        } else {
            action_sound.setIcon(MyApplication.context.getResources().getDrawable(R.drawable.sound_off_));
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        MyApplication.tryVibrate(true);
        switch (item.getItemId()) {
            case 16908332:
                Log.i(TAG, "onOptionsItemSelected - home");
                return true;
            case R.id.action_sound /*2131492951*/:
                Log.i(TAG, "### menu onOptionsItemSelected - action_sound");
                MyApplication.setIsSoundOn(!MyApplication.getIsSoundOn());
                setSoundIcon();
                MyApplication.playSwitchSoundOnOff();
                return true;
            case R.id.action_settings /*2131492952*/:
                Log.i(TAG, "### menu onOptionsItemSelected - action_settings");
                if (this instanceof SettingsActivity) {
                    return true;
                }
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_about /*2131492953*/:
                Log.i(TAG, "### menu onOptionsItemSelected - action_about");
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setSoundIcon() {
        if (MyApplication.getIsSoundOn()) {
            action_sound.setIcon(MyApplication.context.getResources().getDrawable(R.drawable.sound_on_));
        } else {
            action_sound.setIcon(MyApplication.context.getResources().getDrawable(R.drawable.sound_off_));
        }
        invalidateOptionsMenu();
    }

    public void onPause() {
        super.onPause();
        MyApplication.mHapticFeedbackController.stop();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i(TAG, "### volume dispatchKeyEvent, event=" + event.toString());
        AudioManager audioManager = (AudioManager) getApplication().getBaseContext().getSystemService("audio");
        switch (event.getKeyCode()) {
            case BuildConfig.VERSION_CODE /*24*/:
                Log.i(TAG, "### volume KEYCODE_VOLUME_UP");
                if (event.getAction() != 1) {
                    return true;
                }
                audioManager.adjustStreamVolume(3, 1, 1);
                MyApplication.setAppVolume(audioManager.getStreamVolume(3), true);
                return true;
            case 25:
                Log.i(TAG, "### volume KEYCODE_VOLUME_DOWN");
                if (event.getAction() != 1) {
                    return true;
                }
                audioManager.adjustStreamVolume(3, -1, 1);
                MyApplication.setAppVolume(audioManager.getStreamVolume(3), true);
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}
