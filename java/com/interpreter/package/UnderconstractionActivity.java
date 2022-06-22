package ru.avroraventures.russiansinglanguage;

import android.os.Bundle;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

public class UnderconstractionActivity extends MyActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMyTitle(getString(R.string.underconstraction));
        setContentView(R.layout.underconstraction);
        Answers.getInstance().logContentView(new ContentViewEvent().putContentId("Underconstraction"));
    }
}
