package ru.avroraventures.russiansinglanguage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import ru.avroraventures.russiansinglanguage.db.PairVariant;
import ru.avroraventures.russiansinglanguage.games.ChooseLetterOrGestureActivity;
import ru.avroraventures.russiansinglanguage.games.ChoosePairActivity;
import ru.avroraventures.russiansinglanguage.games.YesNoActivity;

public class MainMenuActivity extends MyActivity {
    private static final String TAG = MainMenuActivity.class.getSimpleName();
    private int itemHeight;
    private int itemWidth;
    private ImageView menu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MyApplication.groupsTitles.length <= MyApplication.indexOfGroup) {
            setMyTitle(getString(R.string.all_alphabet_title));
        } else {
            setMyTitle(MyApplication.groupsTitles[MyApplication.indexOfGroup]);
        }
        setContentView(R.layout.menu);
        this.menu = (ImageView) findViewById(R.id.menu);
        this.menu.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & 255) {
                    case PairVariant.RIGHT /*1*/:
                        MainMenuActivity.this.itemWidth = MainMenuActivity.this.menu.getWidth() / 2;
                        MainMenuActivity.this.itemHeight = MainMenuActivity.this.menu.getHeight() / 4;
                        int index = ((((int) event.getY()) / MainMenuActivity.this.itemHeight) * 2) + (((int) event.getX()) / MainMenuActivity.this.itemWidth);
                        Log.i(MainMenuActivity.TAG, "### menu onTouch, index=" + index + ", (" + event.getX() + ", " + event.getY() + ") size=" + MainMenuActivity.this.menu.getWidth() + ", " + MainMenuActivity.this.menu.getHeight());
                        MyApplication.tryVibrate(true);
                        System.gc();
                        Intent intent;
                        switch (index) {
                            case PairVariant.SHOWED /*0*/:
                                MainMenuActivity.this.startActivity(new Intent(MainMenuActivity.this, AlphabetActivity.class));
                                break;
                            case PairVariant.RIGHT /*1*/:
                                intent = new Intent(MainMenuActivity.this, ChooseLetterOrGestureActivity.class);
                                intent.putExtra("isChooseLetter", false);
                                MainMenuActivity.this.startActivity(intent);
                                break;
                            case 2:
                                MainMenuActivity.this.startActivity(new Intent(MainMenuActivity.this, ChoosePairActivity.class));
                                break;
                            case 3:
                                intent = new Intent(MainMenuActivity.this, ChooseLetterOrGestureActivity.class);
                                intent.putExtra("isChooseLetter", true);
                                MainMenuActivity.this.startActivity(intent);
                                break;
                            case 4:
                                MainMenuActivity.this.startActivity(new Intent(MainMenuActivity.this, YesNoActivity.class));
                                break;
                            case Const.MAX_BALL_MULTIPLICATION /*5*/:
                                MainMenuActivity.this.startActivity(new Intent(MainMenuActivity.this, UnderconstractionActivity.class));
                                break;
                            case 6:
                                MainMenuActivity.this.startActivity(new Intent(MainMenuActivity.this, UnderconstractionActivity.class));
                                break;
                            case 7:
                                MainMenuActivity.this.startActivity(new Intent(MainMenuActivity.this, WordPlayerActivity.class));
                                break;
                            default:
                                break;
                        }
                }
                return true;
            }
        });
    }
}
