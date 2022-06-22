package ru.avroraventures.russiansinglanguage.db;

import ru.avroraventures.russiansinglanguage.MyApplication;

public class Answer {
    int exercise;
    int group;
    int isRight;
    int isTakingIntoAccount;
    int letter;
    long time;
    long timeOfAnswer;

    public Answer(long time, int letter, int group, int exercise, long timeOfAnswer, int isRight, int isTakingIntoAccount) {
        this.time = time;
        this.letter = letter;
        this.group = group;
        this.exercise = exercise;
        this.timeOfAnswer = timeOfAnswer;
        this.isRight = isRight;
        this.isTakingIntoAccount = isTakingIntoAccount;
    }

    public String toString() {
        return "Answer{time=" + this.time + ", letter=" + MyApplication.all_letters[this.letter] + "(" + this.letter + ")" + ", group=" + this.group + ", exercise=" + this.exercise + ", timeOfAnswer=" + this.timeOfAnswer + ", isRight=" + this.isRight + ", isTakingIntoAccount=" + this.isTakingIntoAccount + '}';
    }
}
