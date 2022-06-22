package ru.avroraventures.russiansinglanguage.games;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import ru.avroraventures.russiansinglanguage.MyActivity;
import ru.avroraventures.russiansinglanguage.MyApplication;
import ru.avroraventures.russiansinglanguage.MyApplication.TypeOfSound;
import ru.avroraventures.russiansinglanguage.R;
import ru.avroraventures.russiansinglanguage.db.Answer;
import ru.avroraventures.russiansinglanguage.db.DatabaseHandler;
import ru.avroraventures.russiansinglanguage.tools.AutoResizeTextView;
import ru.avroraventures.russiansinglanguage.tools.ImageBuffer;

public class GameActivity extends MyActivity {
    private static final String TAG = GameActivity.class.getSimpleName();
    private static Handler handlerTimer;
    private static Runnable oneTickTimerRunnuble;
    private static int timer;
    private int allAnswersInt;
    private int ball;
    protected AutoResizeTextView ballTextView;
    protected ImageBuffer buffer;
    private int currentStepOfBall;
    protected int game;
    private int rightAnswersInt;
    private int score;
    protected AutoResizeTextView scoreTextView;
    protected long startTime = 0;
    protected int stepsOfBall = 1;
    protected long time0;
    protected long time1;
    protected AutoResizeTextView timerTextView;

    static /* synthetic */ int access$110() {
        int i = timer;
        timer = i - 1;
        return i;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "### game GameActivity onCreate");
        this.score = 20;
        this.timerTextView = (AutoResizeTextView) findViewById(R.id.timerTextView);
        this.ballTextView = (AutoResizeTextView) findViewById(R.id.ballTextView);
        this.scoreTextView = (AutoResizeTextView) findViewById(R.id.scoreTextView);
        this.scoreTextView.setEtalonText(getString(R.string.timeTable));
    }

    protected void onResume() {
        super.onResume();
        Log.i(TAG, "### game GameActivity onResume");
        if (this.buffer == null) {
            this.buffer = new ImageBuffer();
        } else {
            this.buffer.clear();
        }
        startTimer();
    }

    public void onPause() {
        super.onPause();
        stopTimer();
        this.buffer.clear();
        this.buffer = null;
    }

    protected void registerRightAnswer(int multiplicator, Answer answer) {
        MyApplication.tryVibrate(true);
        MyApplication.playSound("right", TypeOfSound.Effect);
        this.rightAnswersInt++;
        this.allAnswersInt++;
        this.score += this.ball * multiplicator;
        new DatabaseHandler(this).insertAnswer(answer);
    }

    protected void registerWrongAnswer(Answer answer) {
        this.allAnswersInt++;
        this.score -= 50;
        if (this.score < 0) {
            this.score = 0;
        }
        MyApplication.tryVibrate(false);
        MyApplication.playSound("wrong", TypeOfSound.Effect);
        new DatabaseHandler(this).insertAnswer(answer);
    }

    protected void registerNewScene() {
        this.ball = 5;
        this.startTime = System.currentTimeMillis();
        Log.i(TAG, "### game startTime=" + this.startTime);
        Log.i(TAG, "### optimization " + (this.startTime - this.time1) + " - " + (this.time1 - this.time0) + " = " + (this.startTime - this.time0));
    }

    private void startTimer() {
        Log.i(TAG, "### timer startTimer");
        timer = 45;
        this.score = 0;
        this.ball = 5;
        this.currentStepOfBall = 0;
        this.rightAnswersInt = 0;
        this.allAnswersInt = 0;
        showGameInfo();
        oneTickTimerRunnuble = new Runnable() {
            public void run() {
                Log.i(GameActivity.TAG, "### player run, timer before=" + GameActivity.timer);
                GameActivity.access$110();
                GameActivity.this.currentStepOfBall = GameActivity.this.currentStepOfBall + 1;
                if (GameActivity.this.currentStepOfBall >= GameActivity.this.stepsOfBall) {
                    if (GameActivity.this.ball > 0) {
                        GameActivity.this.ball = GameActivity.this.ball - 1;
                    }
                    GameActivity.this.currentStepOfBall = 0;
                }
                GameActivity.this.showGameInfo();
                if (GameActivity.timer > 0) {
                    GameActivity.handlerTimer.postDelayed(this, 1000);
                    return;
                }
                GameActivity.this.finishing();
                GameActivity.this.stopTimer();
            }
        };
        handlerTimer = new Handler();
        handlerTimer.postDelayed(oneTickTimerRunnuble, 1000);
    }

    public void showGameInfo() {
        Log.i(TAG, "### test timerTextView=" + this.timerTextView + ", timer=" + timer);
        this.timerTextView.setText(getString(R.string.time) + " 0:" + String.format("%02d", new Object[]{Integer.valueOf(timer)}));
        this.ballTextView.setText("x" + this.ball);
        this.scoreTextView.setText(String.format(getString(R.string.score) + "%4d", new Object[]{Integer.valueOf(this.score)}));
    }

    private void stopTimer() {
        Log.i(TAG, "### timer stopTimer");
        if (handlerTimer != null && oneTickTimerRunnuble != null) {
            handlerTimer.removeCallbacks(oneTickTimerRunnuble);
            oneTickTimerRunnuble = null;
            handlerTimer = null;
        }
    }

    protected void finishing() {
        boolean isBestInGame = MyApplication.saveScoreForGame(this.game, this.score);
        boolean isBestInGroup = MyApplication.saveScoreForGroup(MyApplication.indexOfGroup, this.score);
        boolean isAllAnswersRight = this.rightAnswersInt == this.allAnswersInt && this.rightAnswersInt > 0;
        Intent intent = new Intent(this, AchievementsActivity.class);
        intent.putExtra("isBestInGroup", isBestInGroup);
        intent.putExtra("isBestInGame", isBestInGame);
        intent.putExtra("isAllAnswersRight", isAllAnswersRight);
        intent.putExtra("score", this.score);
        intent.putExtra("rightAnswersInt", this.rightAnswersInt);
        intent.putExtra("allAnswersInt", this.allAnswersInt);
        intent.putExtra("game", this.game);
        startActivityForResult(intent, 555);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            Log.i(TAG, "### achievement - game again");
            return;
        }
        Log.i(TAG, "### achievement - game exit");
        finish();
    }
}
