package ru.avroraventures.russiansinglanguage.db;

public class StatisticsItem {
    public int allAnswers;
    public float averageTimeOfAnswers;
    public int indexOfGroupOrLetter;
    public float percentage;
    public int rightAnswers;

    public StatisticsItem(int indexOfGroupOrLetter, int rightAnswers, int allAnswers, float averageTimeOfAnswers, float percentage) {
        this.indexOfGroupOrLetter = indexOfGroupOrLetter;
        this.rightAnswers = rightAnswers;
        this.allAnswers = allAnswers;
        this.averageTimeOfAnswers = averageTimeOfAnswers;
        this.percentage = percentage;
    }

    public String getAverageTimeOfAnswers() {
        return String.format("%.02f", new Object[]{Float.valueOf(this.averageTimeOfAnswers)});
    }

    public String getPercentage() {
        if (this.percentage == 0.0f) {
            return "-";
        }
        return String.format("%.01f", new Object[]{Float.valueOf(this.percentage)}) + "%";
    }

    public StatisticsItem(int indexOfGroupOrLetter) {
        this.indexOfGroupOrLetter = indexOfGroupOrLetter;
        this.rightAnswers = 0;
        this.allAnswers = 0;
        this.averageTimeOfAnswers = 0.0f;
        this.percentage = 0.0f;
    }

    public String toString() {
        return "StatisticsItem{i=" + this.indexOfGroupOrLetter + ", rightAnswers=" + this.rightAnswers + ", allAnswers=" + this.allAnswers + ", averageTime=" + this.averageTimeOfAnswers + ", " + getPercentage() + " %" + '}';
    }
}
