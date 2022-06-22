package ru.avroraventures.russiansinglanguage;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

public class AboutActivity extends MyActivity {
    private final String EMAIL = "letimgames@gmail.com";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMyTitle(getString(R.string.aboutTitle));
        setContentView(R.layout.activity_about);
        WebView wv = (WebView) findViewById(R.id.wv);
        wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("e-mail", "url=" + url);
                AboutActivity.this.sendEmail();
                return true;
            }
        });
        wv.loadUrl("file:///android_asset/about1.html");
        Answers.getInstance().logContentView(new ContentViewEvent().putContentId("About"));
    }

    private void sendEmail() {
        Intent i = new Intent("android.intent.action.SEND");
        i.setType("message/rfc822");
        i.putExtra("android.intent.extra.EMAIL", new String[]{"letimgames@gmail.com"});
        i.putExtra("android.intent.extra.SUBJECT", getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getAndroidVersion()).append(", ").append("Phone: ").append(getDeviceName()).append("\n");
        i.putExtra("android.intent.extra.TEXT", stringBuffer.toString());
        try {
            startActivity(Intent.createChooser(i, "Choose email client"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No email client installed", 1).show();
        }
    }

    private String getAndroidVersion() {
        return "Android SDK: " + VERSION.SDK_INT + " (" + VERSION.RELEASE + ")";
    }

    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return BuildConfig.FLAVOR;
        }
        char first = s.charAt(0);
        return !Character.isUpperCase(first) ? Character.toUpperCase(first) + s.substring(1) : s;
    }
}
