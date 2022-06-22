package ru.avroraventures.russiansinglanguage.games;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import ru.avroraventures.russiansinglanguage.MyActivity;
import ru.avroraventures.russiansinglanguage.MyApplication;
import ru.avroraventures.russiansinglanguage.R;
import ru.avroraventures.russiansinglanguage.tools.AutoResizeTextView;

public class AchievementsActivity extends MyActivity {
    private static final String TAG = AchievementsActivity.class.getSimpleName();
    private OnGlobalLayoutListener listener;
    private TextView myAchievements;
    private ViewTreeObserver observer;
    private TextView scoreTextView;
    private AutoResizeTextView toClose;
    private AutoResizeTextView toGameAgain;

    protected void onCreate(Bundle savedInstanceState) {
        int i = 0;
        super.onCreate(savedInstanceState);
        setMyTitle(getString(R.string.achievementsTitle));
        setContentView(R.layout.achievements);
        this.toGameAgain = (AutoResizeTextView) findViewById(R.id.toGameAgain);
        this.toClose = (AutoResizeTextView) findViewById(R.id.toClose);
        this.toClose.setEtalonText(getString(R.string.play_again));
        this.scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        final Intent intent = getIntent();
        boolean isBestInGroup = intent.getBooleanExtra("isBestInGroup", true);
        boolean isBestInGame = intent.getBooleanExtra("isBestInGame", true);
        boolean isAllAnswersRight = intent.getBooleanExtra("isAllAnswersRight", true);
        int score = intent.getIntExtra("score", 0);
        int rightAnswersInt = intent.getIntExtra("rightAnswersInt", 0);
        int allAnswersInt = intent.getIntExtra("allAnswersInt", 0);
        int game = intent.getIntExtra("game", -1);
        this.scoreTextView.setText(String.format(getString(R.string.scoreText), new Object[]{String.valueOf(score)}));
        new Handler().postDelayed(new Runnable() {
            public void run() {
                AchievementsActivity.this.findViewById(R.id.yes).setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        AchievementsActivity.this.setResult(-1, intent);
                        AchievementsActivity.this.finish();
                    }
                });
                AchievementsActivity.this.findViewById(R.id.no).setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        AchievementsActivity.this.setResult(0, intent);
                        AchievementsActivity.this.finish();
                    }
                });
            }
        }, 1000);
        View findViewById = findViewById(R.id.myAchievements);
        int i2 = (isBestInGroup || isBestInGame || isAllAnswersRight) ? 0 : 4;
        findViewById.setVisibility(i2);
        findViewById = findViewById(R.id.best_in_group);
        if (isBestInGroup) {
            i2 = 0;
        } else {
            i2 = 8;
        }
        findViewById.setVisibility(i2);
        findViewById = findViewById(R.id.best_in_game);
        if (isBestInGame) {
            i2 = 0;
        } else {
            i2 = 8;
        }
        findViewById.setVisibility(i2);
        View findViewById2 = findViewById(R.id.best_all_right);
        if (!isAllAnswersRight) {
            i = 8;
        }
        findViewById2.setVisibility(i);
        Answers.getInstance().logCustom((CustomEvent) ((CustomEvent) ((CustomEvent) ((CustomEvent) ((CustomEvent) new CustomEvent("Results").putCustomAttribute("game", Integer.valueOf(game))).putCustomAttribute("group", Integer.valueOf(MyApplication.indexOfGroup))).putCustomAttribute("rightAnswers", Integer.valueOf(rightAnswersInt))).putCustomAttribute("allAnswers", Integer.valueOf(allAnswersInt))).putCustomAttribute("score", Integer.valueOf(score)));
    }
}
