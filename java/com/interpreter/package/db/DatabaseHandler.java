package ru.avroraventures.russiansinglanguage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import ru.avroraventures.russiansinglanguage.BuildConfig;
import ru.avroraventures.russiansinglanguage.Const;
import ru.avroraventures.russiansinglanguage.MyApplication;
import ru.avroraventures.russiansinglanguage.tools.AutoResizeTextView;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "app_base.db";
    private static final int DATABASE_VERSION = 3;
    private static final String KEY_ANSWER_TIME = "answer_time";
    private static final String KEY_EXERCISE = "exercise";
    private static final String KEY_GROUP = "groupOfLetter";
    private static final String KEY_IS_RIGHT_ANSWER = "isRight";
    private static final String KEY_IS_TAKING_INTO_ACCOUNT = "isTakingIntoAccount";
    private static final String KEY_LETTER = "letter";
    private static final String KEY_TIME = "time";
    public static final String TABLE = "statistics";
    public static final String TAG = DatabaseHandler.class.getSimpleName();
    private final Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE statistics(_id  INTEGER PRIMARY KEY AUTOINCREMENT,time INTEGER,letter INTEGER,groupOfLetter INTEGER,exercise INTEGER,answer_time INTEGER,isRight INTEGER,isTakingIntoAccount INTEGER)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS statistics");
        onCreate(db);
    }

    public long insertAnswer(Answer answer) {
        Log.i(TAG, "### db - insertAnswer " + answer);
        if (answer == null) {
            return -1;
        }
        SQLiteDatabase db = getWritableDatabase();
        try {
            long id = db.insert(TABLE, null, getContentValues(answer));
            Log.i(TAG, "### db insertAnswer updateIsTakingIntoAccount " + updateIsTakingIntoAccount(db, answer.letter));
            db.close();
            return id;
        } catch (Exception e) {
            Log.e(TAG, "### db An error occurred while inserting the row: " + e.toString(), e);
            db.close();
            return -1;
        }
    }

    public void updateIsTakingIntoAccount() {
        Log.i(TAG, "### db - updateIsTakingIntoAccount, RIGHT_ANSWERS_TO_LEARN=20");
        SQLiteDatabase db = getWritableDatabase();
        boolean isSuccess = true;
        try {
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT DISTINCT letter FROM statistics", null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    Answer answer = getAnswerFromCursor(cursor);
                    int letter = cursor.getInt(cursor.getColumnIndex(KEY_LETTER));
                    if (!updateIsTakingIntoAccount(db, letter)) {
                        Log.e(TAG, "### db updateIsTakingIntoAccount, isSuccess = false for indexOfLetter=" + letter);
                        isSuccess = false;
                        break;
                    }
                } while (cursor.moveToNext());
                if (isSuccess) {
                    db.setTransactionSuccessful();
                }
                db.endTransaction();
            }
        } catch (Exception e) {
            Log.e(TAG, "### db updateIsTakingIntoAccount error=" + e.getMessage());
        }
        db.close();
    }

    private boolean updateIsTakingIntoAccount(SQLiteDatabase db, int letter) {
        Log.i(TAG, "### db - updateIsTakingIntoAccount for letter " + letter);
        if (!db.isOpen() || db.isReadOnly()) {
            Log.wtf(TAG, "### db db is NOT open or not writadable");
        } else {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(KEY_IS_TAKING_INTO_ACCOUNT, Integer.valueOf(0));
                db.update(TABLE, contentValues, "letter = " + String.valueOf(letter) + " AND " + KEY_IS_TAKING_INTO_ACCOUNT + " = 1", null);
                Cursor cursor = db.rawQuery("SELECT MIN(time)  FROM (SELECT time FROM statistics WHERE letter = " + String.valueOf(letter) + " ORDER BY " + KEY_TIME + " DESC LIMIT " + String.valueOf(20) + ")", null);
                Log.i(TAG, "### db updateIsTakingIntoAccount, cursor=" + DatabaseUtils.dumpCursorToString(cursor));
                if (cursor == null || !cursor.moveToFirst()) {
                    return true;
                }
                long maxTime = cursor.getLong(0);
                Log.i(TAG, "### db updateIsTakingIntoAccount, maxTime=" + maxTime);
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put(KEY_IS_TAKING_INTO_ACCOUNT, Integer.valueOf(1));
                db.update(TABLE, contentValues2, "letter = " + String.valueOf(letter) + " AND " + KEY_TIME + " >= " + String.valueOf(maxTime), null);
                return true;
            } catch (Exception e) {
                Log.e(TAG, "### db updateIsTakingIntoAccount for letter=" + letter + " error:" + e.getMessage());
            }
        }
        return false;
    }

    private ContentValues getContentValues(Answer answer) {
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, Long.valueOf(answer.time));
        values.put(KEY_LETTER, Integer.valueOf(answer.letter));
        values.put(KEY_GROUP, Integer.valueOf(answer.group));
        values.put(KEY_EXERCISE, Integer.valueOf(answer.exercise));
        values.put(KEY_ANSWER_TIME, Long.valueOf(answer.timeOfAnswer));
        values.put(KEY_IS_RIGHT_ANSWER, Integer.valueOf(answer.isRight));
        values.put(KEY_IS_TAKING_INTO_ACCOUNT, Integer.valueOf(answer.isTakingIntoAccount));
        return values;
    }

    public void deleteStatisticsForLetter(int letter) {
        Log.i(TAG, "### db - deleteStaticticsForLetter " + MyApplication.all_letters[letter - 1] + "(" + letter + ")");
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE, "letter = " + String.valueOf(letter), null);
        } catch (Exception e) {
            Log.e(TAG, "### db An error occurred while deleting statictics for the letter:" + MyApplication.all_letters[letter - 1] + "(" + letter + ")" + "\n" + e.toString(), e);
        }
        db.close();
    }

    public void deleteAnswer(Answer answer) {
        Log.i(TAG, "### db - deleteAnswer " + answer);
        if (answer != null) {
            SQLiteDatabase db = getWritableDatabase();
            try {
                db.delete(TABLE, "time = " + answer.time, null);
            } catch (Exception e) {
                Log.e(TAG, "### db An error occurred while deleting the answer:" + answer + "\n" + e.toString(), e);
            }
            db.close();
        }
    }

    public void deleteAllItems() {
        Log.i(TAG, "### db deleteAllItems");
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(TABLE, null, null);
        } catch (Exception e) {
            Log.e(TAG, "### db An error occurred while deleting all Items: " + e.toString(), e);
        }
        db.close();
    }

    public int getQuantityRightAnsweredLetters() {
        return getQuantityRightAnsweredLettersForGroup(-1);
    }

    public int getQuantityRightAnsweredLettersForGroup(int group) {
        int quantity;
        Log.i(TAG, "### db getQuantityRightAnsweredLettersForGroup'" + group + "' (-1 means for all");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        if (group == -1) {
            try {
                cursor = db.rawQuery("SELECT count(DISTINCT letter) AS myQuantity FROM statistics WHERE isRight=1", null);
            } catch (Exception e) {
                Log.e(TAG, "### db getQuantityRightAnsweredLettersForGroup An error " + e.toString(), e);
                quantity = 0;
                if (!(cursor == null || cursor.isClosed())) {
                    cursor.close();
                }
            } catch (Throwable th) {
                if (!(cursor == null || cursor.isClosed())) {
                    cursor.close();
                }
            }
        } else {
            cursor = db.rawQuery("SELECT count(DISTINCT letter) AS myQuantity FROM statistics WHERE groupOfLetter=" + String.valueOf(group) + " AND " + KEY_IS_RIGHT_ANSWER + "=1", null);
        }
        if (cursor == null || !cursor.moveToFirst()) {
            quantity = 0;
        } else {
            quantity = cursor.getInt(cursor.getColumnIndex("myQuantity"));
        }
        if (!(cursor == null || cursor.isClosed())) {
            cursor.close();
        }
        db.close();
        return quantity;
    }

    public int getAverageTimeOfAnswerForLetter(int letter) {
        int average;
        Log.i(TAG, "### db getAverageTimeOfAnswerForLetter'" + letter + "'");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT AVG(answer_time) AS myAverage FROM statistics WHERE letter=" + String.valueOf(letter) + " AND " + KEY_IS_RIGHT_ANSWER + "=1", null);
            if (cursor == null || !cursor.moveToFirst()) {
                average = -1;
            } else {
                average = cursor.getInt(cursor.getColumnIndex("myAverage"));
            }
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "### db An error occurred while searching for Item with letter=" + letter + ": " + e.toString(), e);
            average = -1;
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        }
        db.close();
        return average;
    }

    public int getAverageTimeOfAnswerForGroup(int group) {
        int average;
        Log.i(TAG, "### db getAverageTimeOfAnswerForGroup'" + group + "'");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT AVG(answer_time) AS myAverage FROM statistics WHERE groupOfLetter=" + String.valueOf(group) + " AND " + KEY_IS_RIGHT_ANSWER + "=1", null);
            if (cursor == null || !cursor.moveToFirst()) {
                average = -1;
            } else {
                average = cursor.getInt(cursor.getColumnIndex("myAverage"));
            }
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "### db An error occurred while searching for Answer with group=" + group + ": " + e.toString(), e);
            average = -1;
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        }
        db.close();
        return average;
    }

    public ArrayList<Answer> getAllAnswers() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Answer> answerList = new ArrayList();
        String selectQuery = "SELECT  * FROM statistics";
        Cursor cursor = null;
        Log.i(TAG, "### db logAllBase ------------------------------- ");
        try {
            cursor = db.rawQuery("SELECT  * FROM statistics", null);
            if (cursor == null || cursor.getCount() <= 0 || !cursor.moveToFirst()) {
                if (!(cursor == null || cursor.isClosed())) {
                    cursor.close();
                }
                db.close();
                return answerList;
            }
            do {
                Answer answer = getAnswerFromCursor(cursor);
                answerList.add(answer);
                Log.i(TAG, answer.toString());
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
            return answerList;
        } catch (Exception e) {
            Log.e(TAG, "### db An error occurred while getAllAnswers", e);
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        }
    }

    public int getQuantityOfRightAnswersForLetter(int letter) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM statistics WHERE letter=" + String.valueOf(letter) + " AND " + KEY_IS_RIGHT_ANSWER + "=1" + " AND " + KEY_ANSWER_TIME + "<" + String.valueOf(Const.RIGHT_ANSWER_MAX_TIME_TO_LEARN);
        int result = 0;
        Cursor cursor = null;
        Log.i(TAG, "### db logAllBase ------------------------------- ");
        try {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor != null) {
                result = cursor.getCount();
            }
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "### db An error occurred while getQuantityOfRightAnswersForLetter", e);
            result = 0;
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        }
        db.close();
        return result;
    }

    public float[] getProgressInAllGroups() {
        Log.i(TAG, "### db getProgressInAllGroups");
        SQLiteDatabase db = getReadableDatabase();
        float[] progress = new float[(MyApplication.group.length + 1)];
        for (int i = 0; i < progress.length; i++) {
            progress[i] = 0.0f;
        }
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT groupOfLetter, SUM(isRight)  FROM statistics WHERE isTakingIntoAccount=1 AND answer_time < " + String.valueOf(Const.RIGHT_ANSWER_MAX_TIME_TO_LEARN) + " GROUP BY " + KEY_GROUP, null);
            if (cursor == null || !cursor.moveToFirst()) {
                Log.e(TAG, "### db getProgressInAllGroups cursor null ???");
            } else {
                Log.i(TAG, "### db getProgressInAllGroups, cursor=" + DatabaseUtils.dumpCursorToString(cursor));
                int rightAnswers = 0;
                do {
                    int indexOfGroup = Integer.valueOf(cursor.getString(cursor.getColumnIndex(KEY_GROUP))).intValue();
                    int count = cursor.getInt(1);
                    progress[indexOfGroup] = ((float) count) / ((float) (MyApplication.group[indexOfGroup].length * 20));
                    rightAnswers += count;
                    Log.i(TAG, "### db getProgressInAllGroups " + indexOfGroup + ", " + count);
                } while (cursor.moveToNext());
                progress[MyApplication.group.length] = ((float) rightAnswers) / 660.0f;
            }
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "### db getProgressInAllGroups, An error occurred while getQuantityOfRightAnswersForLetter:" + e.getMessage());
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        }
        db.close();
        return progress;
    }

    private Answer getAnswerFromCursor(Cursor cursor) {
        return new Answer(cursor.getLong(cursor.getColumnIndex(KEY_TIME)), cursor.getInt(cursor.getColumnIndex(KEY_LETTER)), cursor.getInt(cursor.getColumnIndex(KEY_GROUP)), cursor.getInt(cursor.getColumnIndex(KEY_EXERCISE)), cursor.getLong(cursor.getColumnIndex(KEY_ANSWER_TIME)), cursor.getInt(cursor.getColumnIndex(KEY_IS_RIGHT_ANSWER)), cursor.getInt(cursor.getColumnIndex(KEY_IS_TAKING_INTO_ACCOUNT)));
    }

    public ArrayList<StatisticsItem> getStatistics(int index) {
        Log.i(TAG, "### statistics getStatistics index=" + index);
        String getRightAnswersQuery = null;
        String getAllAnswersQuery = null;
        String getProgressQuery = null;
        int listLength = 0;
        if (index == 0) {
            listLength = MyApplication.group.length;
            getRightAnswersQuery = "SELECT groupOfLetter, SUM(isRight) FROM statistics WHERE answer_time < " + String.valueOf(Const.RIGHT_ANSWER_MAX_TIME_TO_LEARN) + " GROUP BY " + KEY_GROUP;
            getAllAnswersQuery = "SELECT groupOfLetter, COUNT(*), AVG(answer_time) FROM statistics GROUP BY groupOfLetter";
            getProgressQuery = "SELECT groupOfLetter, SUM(isRight)  FROM statistics WHERE isTakingIntoAccount=1 AND answer_time < " + String.valueOf(Const.RIGHT_ANSWER_MAX_TIME_TO_LEARN) + " GROUP BY " + KEY_GROUP;
        } else if (index == 1) {
            listLength = MyApplication.all_letters.length;
            getRightAnswersQuery = "SELECT letter, SUM(isRight) FROM statistics WHERE answer_time < " + String.valueOf(Const.RIGHT_ANSWER_MAX_TIME_TO_LEARN) + " GROUP BY " + KEY_LETTER;
            getAllAnswersQuery = "SELECT letter, COUNT(*), AVG(answer_time) FROM statistics GROUP BY letter";
            getProgressQuery = "SELECT letter, SUM(isRight)  FROM statistics WHERE isTakingIntoAccount=1 AND answer_time < " + String.valueOf(Const.RIGHT_ANSWER_MAX_TIME_TO_LEARN) + " GROUP BY " + KEY_LETTER;
        } else if (index == 2) {
            listLength = MyApplication.gamesNames.length;
            getRightAnswersQuery = "SELECT exercise, SUM(isRight) FROM statistics WHERE answer_time < " + String.valueOf(Const.RIGHT_ANSWER_MAX_TIME_TO_LEARN) + " GROUP BY " + KEY_EXERCISE;
            getAllAnswersQuery = "SELECT exercise, COUNT(*), AVG(answer_time) FROM statistics GROUP BY exercise";
            getProgressQuery = "SELECT exercise, SUM(isRight)  FROM statistics WHERE isTakingIntoAccount=1 AND answer_time < " + String.valueOf(Const.RIGHT_ANSWER_MAX_TIME_TO_LEARN) + " GROUP BY " + KEY_EXERCISE;
        }
        return getStatistics(listLength, index, getRightAnswersQuery, getAllAnswersQuery, getProgressQuery);
    }

    private ArrayList<StatisticsItem> getStatistics(int listLength, int index, String getRightAnswersQuery, String getAllAnswersQuery, String getProgressQuery) {
        String by = BuildConfig.FLAVOR;
        switch (index) {
            case PairVariant.SHOWED /*0*/:
                by = "by group";
                break;
            case PairVariant.RIGHT /*1*/:
                by = "by letter";
                break;
            case 2:
                by = "by game";
                break;
        }
        Log.i(TAG, "&&& " + by + " getRightAnswersQuery=" + getRightAnswersQuery);
        Log.i(TAG, "&&& " + by + "getAllAnswersQuery=" + getAllAnswersQuery);
        Log.i(TAG, "&&& " + by + "getProgressQuery=" + getProgressQuery);
        Log.i(TAG, "### statistics getStatistics, listLength=" + listLength);
        ArrayList<StatisticsItem> list = new ArrayList(listLength + 1);
        if (getRightAnswersQuery == null || getAllAnswersQuery == null || getProgressQuery == null) {
            return list;
        }
        int rightAnswers;
        SQLiteDatabase db = getReadableDatabase();
        for (int i = 0; i < listLength; i++) {
            list.add(new StatisticsItem(i));
        }
        list.add(new StatisticsItem(-1));
        Log.i(TAG, "### test first " + list);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(getRightAnswersQuery, null);
            if (cursor == null || !cursor.moveToFirst()) {
                list = null;
            } else {
                Log.i(TAG, "### db getStatistics, cursor=" + DatabaseUtils.dumpCursorToString(cursor));
                rightAnswers = 0;
                do {
                    int indexOfGroupOrLetterOrGame = cursor.getInt(0);
                    int count = cursor.getInt(1);
                    ((StatisticsItem) list.get(indexOfGroupOrLetterOrGame)).rightAnswers = count;
                    rightAnswers += count;
                    Log.i(TAG, "### db getStatistics rightAnswers:" + indexOfGroupOrLetterOrGame + ", " + count);
                } while (cursor.moveToNext());
                ((StatisticsItem) list.get(listLength)).rightAnswers = rightAnswers;
            }
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "### db getStatistics error:" + e.getMessage());
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        }
        if (list != null) {
            int indexOfGroup;
            cursor = null;
            try {
                cursor = db.rawQuery(getAllAnswersQuery, null);
                if (cursor != null && cursor.moveToFirst()) {
                    Log.i(TAG, "### db getStatistics, cursor=" + DatabaseUtils.dumpCursorToString(cursor));
                    int answers = 0;
                    float avgSum = 0.0f;
                    do {
                        indexOfGroup = cursor.getInt(0);
                        count = cursor.getInt(1);
                        long avg = cursor.getLong(2);
                        ((StatisticsItem) list.get(indexOfGroup)).allAnswers = count;
                        float avgTimeInSec = ((float) avg) / 1000.0f;
                        ((StatisticsItem) list.get(indexOfGroup)).averageTimeOfAnswers = avgTimeInSec;
                        avgSum += ((float) count) * avgTimeInSec;
                        answers += count;
                        Log.i(TAG, "### db getStatistics allAnswers:" + indexOfGroup + ", " + count + ", avgTimeInSec=" + avgTimeInSec);
                    } while (cursor.moveToNext());
                    ((StatisticsItem) list.get(listLength)).allAnswers = answers;
                    ((StatisticsItem) list.get(listLength)).averageTimeOfAnswers = avgSum / ((float) answers);
                }
                if (!(cursor == null || cursor.isClosed())) {
                    cursor.close();
                }
            } catch (Exception e2) {
                Log.e(TAG, "### db getStatistics error:" + e2.getMessage());
                if (!(cursor == null || cursor.isClosed())) {
                    cursor.close();
                }
            } catch (Throwable th2) {
                if (!(cursor == null || cursor.isClosed())) {
                    cursor.close();
                }
            }
            cursor = null;
            cursor = db.rawQuery(getProgressQuery, null);
            if (cursor != null && cursor.moveToFirst()) {
                Log.i(TAG, "### db getStatistics, cursor=" + DatabaseUtils.dumpCursorToString(cursor));
                rightAnswers = 0;
                do {
                    indexOfGroup = cursor.getInt(0);
                    count = cursor.getInt(1);
                    if (index == 0) {
                        ((StatisticsItem) list.get(indexOfGroup)).percentage = (100.0f * ((float) count)) / ((float) (MyApplication.group[indexOfGroup].length * 20));
                    } else if (index == 1) {
                        try {
                            ((StatisticsItem) list.get(indexOfGroup)).percentage = (100.0f * ((float) count)) / AutoResizeTextView.MIN_TEXT_SIZE;
                        } catch (Exception e22) {
                            Log.e(TAG, "### statistics getStatistics error:" + e22.getMessage());
                            if (!(cursor == null || cursor.isClosed())) {
                                cursor.close();
                            }
                        } catch (Throwable th3) {
                            if (!(cursor == null || cursor.isClosed())) {
                                cursor.close();
                            }
                        }
                    } else {
                        ((StatisticsItem) list.get(indexOfGroup)).percentage = 0.0f;
                    }
                    rightAnswers += count;
                    Log.i(TAG, "### db getStatistics " + indexOfGroup + ", " + count);
                } while (cursor.moveToNext());
                ((StatisticsItem) list.get(listLength)).percentage = (100.0f * ((float) rightAnswers)) / 660.0f;
                Log.i(TAG, "### statistics rightAnswers=" + rightAnswers + ", percentage=" + ((100.0f * ((float) rightAnswers)) / 660.0f));
            }
            if (!(cursor == null || cursor.isClosed())) {
                cursor.close();
            }
        }
        db.close();
        return list;
    }

    public boolean isDatabaseNotEmpty() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM statistics LIMIT 1", null);
        if (cursor == null || !cursor.moveToFirst()) {
            return false;
        }
        return true;
    }
}
