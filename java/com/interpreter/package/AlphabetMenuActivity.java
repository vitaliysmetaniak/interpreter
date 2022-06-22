package ru.avroraventures.russiansinglanguage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import ru.avroraventures.russiansinglanguage.db.DatabaseHandler;
import ru.avroraventures.russiansinglanguage.tools.AnimatingProgressBar;

public class AlphabetMenuActivity extends MyActivity {
    private static final String TAG = AlphabetMenuActivity.class.getSimpleName();
    private static AnimatingProgressBar[] animatingProgressBars;
    private static Context context;
    private static final OnClickListener onClickListener = new OnClickListener() {
        public void onClick(View v) {
            Log.i(AlphabetMenuActivity.TAG, "### onClickListener");
            switch (v.getId()) {
                case R.id.g0 /*2131492886*/:
                    MyApplication.indexOfGroup = 0;
                    break;
                case R.id.g1 /*2131492888*/:
                    MyApplication.indexOfGroup = 1;
                    break;
                case R.id.g2 /*2131492890*/:
                    MyApplication.indexOfGroup = 2;
                    break;
                case R.id.g3 /*2131492892*/:
                    MyApplication.indexOfGroup = 3;
                    break;
                case R.id.g4 /*2131492894*/:
                    MyApplication.indexOfGroup = 4;
                    break;
                case R.id.g5 /*2131492896*/:
                    MyApplication.indexOfGroup = 5;
                    break;
                case R.id.g6 /*2131492898*/:
                    MyApplication.indexOfGroup = 6;
                    break;
                case R.id.g7 /*2131492900*/:
                    MyApplication.indexOfGroup = 7;
                    break;
            }
            Log.i(AlphabetMenuActivity.TAG, "### onClickListener, indexOfGroup=" + MyApplication.indexOfGroup);
            AlphabetMenuActivity.context.startActivity(new Intent(AlphabetMenuActivity.context, MainMenuActivity.class));
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMyTitle(getString(R.string.alphabetMenuTitle));
        setContentView(R.layout.alphabet_menu);
        context = this;
        animatingProgressBars = new AnimatingProgressBar[8];
        animatingProgressBars[0] = (AnimatingProgressBar) findViewById(R.id.p0);
        animatingProgressBars[1] = (AnimatingProgressBar) findViewById(R.id.p1);
        animatingProgressBars[2] = (AnimatingProgressBar) findViewById(R.id.p2);
        animatingProgressBars[3] = (AnimatingProgressBar) findViewById(R.id.p3);
        animatingProgressBars[4] = (AnimatingProgressBar) findViewById(R.id.p4);
        animatingProgressBars[5] = (AnimatingProgressBar) findViewById(R.id.p5);
        animatingProgressBars[6] = (AnimatingProgressBar) findViewById(R.id.p6);
        animatingProgressBars[7] = (AnimatingProgressBar) findViewById(R.id.p7);
        findViewById(R.id.g0).setOnClickListener(onClickListener);
        findViewById(R.id.g1).setOnClickListener(onClickListener);
        findViewById(R.id.g2).setOnClickListener(onClickListener);
        findViewById(R.id.g3).setOnClickListener(onClickListener);
        findViewById(R.id.g4).setOnClickListener(onClickListener);
        findViewById(R.id.g5).setOnClickListener(onClickListener);
        findViewById(R.id.g6).setOnClickListener(onClickListener);
        findViewById(R.id.g7).setOnClickListener(onClickListener);
        Answers.getInstance().logContentView(new ContentViewEvent().putContentId("AlphabetMenu"));
    }

    protected void onResume() {
        super.onResume();
        showProgress();
    }

    private void showProgress() {
        Log.i(TAG, "### progress showProgress");
        DatabaseHandler db = new DatabaseHandler(context);
        db.getAllAnswers();
        float[] progressByGroup = db.getProgressInAllGroups();
        for (int i = 0; i < 8; i++) {
            animatingProgressBars[i].setProgress((int) (progressByGroup[i] * 100.0f));
            Log.i(TAG, "### progress progress[" + i + "]=" + (progressByGroup[i] * 100.0f));
        }
    }
}
