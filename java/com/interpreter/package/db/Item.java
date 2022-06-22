package ru.avroraventures.russiansinglanguage.db;

import android.util.Log;

public class Item {
    private static final String TAG = Item.class.getSimpleName();
    public String content;
    public String description;
    public int group = -1;
    public String imageFileName;
    private int isLearned;
    public int isWord;
    public int letter;
    private int rightAnswers = 0;
    public String soundFileName;
    public float speedOfAnswer;

    public Item(int letter, String content, int group, String imageFileName, String soundFileName, String description, int isWord, int isLearned, int rightAnswers, float speedOfAnswer) {
        this.letter = letter;
        this.content = content;
        this.group = group;
        this.imageFileName = imageFileName;
        this.soundFileName = soundFileName;
        this.description = description;
        this.isWord = isWord;
        this.isLearned = isLearned;
        this.speedOfAnswer = speedOfAnswer;
        this.rightAnswers = rightAnswers;
    }

    public String toString() {
        return "'" + this.content + "'";
    }

    public String toLongString() {
        return "Item{letter=" + this.letter + ", content='" + this.content + '\'' + ", group='" + this.group + '\'' + ", imageFileName='" + this.imageFileName + '\'' + ", soundFileName='" + this.soundFileName + '\'' + ", description='" + this.description + '\'' + ", isWord=" + this.isWord + ", isLearned=" + this.isLearned + ", rightAnswers=" + this.rightAnswers + ", speedOfAnswer=" + this.speedOfAnswer + '}';
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r6) {
        /*
        r5 = this;
        r1 = 1;
        r2 = 0;
        if (r5 != r6) goto L_0x0006;
    L_0x0004:
        r2 = r1;
    L_0x0005:
        return r2;
    L_0x0006:
        r3 = r6 instanceof ru.avroraventures.russiansinglanguage.db.Item;
        if (r3 == 0) goto L_0x0005;
    L_0x000a:
        r0 = r6;
        r0 = (ru.avroraventures.russiansinglanguage.db.Item) r0;
        r3 = r5.letter;
        r4 = r0.letter;
        if (r3 != r4) goto L_0x0005;
    L_0x0013:
        r3 = r5.group;
        r4 = r0.group;
        if (r3 != r4) goto L_0x0005;
    L_0x0019:
        r3 = r5.isWord;
        r4 = r0.isWord;
        if (r3 != r4) goto L_0x0005;
    L_0x001f:
        r3 = r5.content;
        if (r3 == 0) goto L_0x005a;
    L_0x0023:
        r3 = r5.content;
        r4 = r0.content;
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x0005;
    L_0x002d:
        r3 = r5.imageFileName;
        if (r3 == 0) goto L_0x005f;
    L_0x0031:
        r3 = r5.imageFileName;
        r4 = r0.imageFileName;
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x0005;
    L_0x003b:
        r3 = r5.soundFileName;
        if (r3 == 0) goto L_0x0064;
    L_0x003f:
        r3 = r5.soundFileName;
        r4 = r0.soundFileName;
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x0005;
    L_0x0049:
        r3 = r5.description;
        if (r3 == 0) goto L_0x0069;
    L_0x004d:
        r3 = r5.description;
        r4 = r0.description;
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x0058;
    L_0x0057:
        r1 = r2;
    L_0x0058:
        r2 = r1;
        goto L_0x0005;
    L_0x005a:
        r3 = r0.content;
        if (r3 == 0) goto L_0x002d;
    L_0x005e:
        goto L_0x0005;
    L_0x005f:
        r3 = r0.imageFileName;
        if (r3 == 0) goto L_0x003b;
    L_0x0063:
        goto L_0x0005;
    L_0x0064:
        r3 = r0.soundFileName;
        if (r3 == 0) goto L_0x0049;
    L_0x0068:
        goto L_0x0005;
    L_0x0069:
        r3 = r0.description;
        if (r3 != 0) goto L_0x0057;
    L_0x006d:
        goto L_0x0058;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.avroraventures.russiansinglanguage.db.Item.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        int result;
        int hashCode;
        int i = 0;
        if (this.content != null) {
            result = this.content.hashCode();
        } else {
            result = 0;
        }
        int i2 = ((((result * 31) + this.letter) * 31) + this.group) * 31;
        if (this.imageFileName != null) {
            hashCode = this.imageFileName.hashCode();
        } else {
            hashCode = 0;
        }
        i2 = (i2 + hashCode) * 31;
        if (this.soundFileName != null) {
            hashCode = this.soundFileName.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (i2 + hashCode) * 31;
        if (this.description != null) {
            i = this.description.hashCode();
        }
        return ((hashCode + i) * 31) + this.isWord;
    }

    public void registerRightAnswer(float time) {
        this.rightAnswers++;
        if (this.rightAnswers >= 20) {
            this.isLearned = 1;
        }
        Log.i(TAG, "### registerRightAnswer for " + this + ", rightAnswers=" + this.rightAnswers + ", isLearned=" + this.isLearned);
    }

    public void registerWrongAnswer() {
        Log.i(TAG, "### registerWrongAnswer for " + this);
        if (this.rightAnswers > 0) {
            this.rightAnswers--;
        }
    }

    public int getRightAnswers() {
        return this.rightAnswers;
    }

    public int getIsLearned() {
        return this.isLearned;
    }
}
