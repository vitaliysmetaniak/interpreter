package ru.avroraventures.russiansinglanguage.db;

public class PairVariant {
    public static final int RIGHT = 1;
    public static final int SHOWED = 0;
    public static final int WRONG = -1;
    public boolean isLetter = false;
    public boolean isSelected = false;
    public Item item;
    public int placeOnScene;
    public int status = -2;

    public PairVariant(Item item, boolean isLetter, int placeOnScene) {
        this.item = item;
        this.isLetter = isLetter;
        this.placeOnScene = placeOnScene;
    }

    public String toString() {
        return "PairVariant{item=" + this.item + ", isLetter=" + this.isLetter + ", isSelected=" + this.isSelected + ", placeOnScene=" + this.placeOnScene + '}';
    }
}
