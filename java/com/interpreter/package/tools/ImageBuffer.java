package ru.avroraventures.russiansinglanguage.tools;

import android.graphics.Bitmap;
import android.util.Log;
import java.util.HashMap;

public class ImageBuffer {
    private static final int BUFFER_SIZE = 6;
    private static final String TAG = ImageBuffer.class.getSimpleName();
    private static int used = 0;
    private HashMap<Integer, Bitmap> map;

    public void putBitmap(int indexOffLetter, Bitmap bitmap) {
        Log.i(TAG, "### image putBitmap indexOffLetter=" + indexOffLetter + ", size=" + bitmap.getByteCount());
        if (this.map == null) {
            this.map = new HashMap(BUFFER_SIZE);
            used = 0;
        }
        if (this.map.containsKey(Integer.valueOf(indexOffLetter))) {
            Log.i(TAG, "### image putBitmap indexOffLetter=" + indexOffLetter + ", size=" + bitmap.getByteCount() + "already! pass");
        } else if (used < BUFFER_SIZE) {
            Log.i(TAG, "### image putBitmap indexOffLetter=" + indexOffLetter + ", size=" + bitmap.getByteCount() + " - add");
            used++;
            this.map.put(Integer.valueOf(indexOffLetter), bitmap);
        } else {
            this.map.remove(this.map.keySet().toArray()[0]);
            this.map.put(Integer.valueOf(indexOffLetter), bitmap);
            Log.i(TAG, "### image putBitmap indexOffLetter=" + indexOffLetter + ", size=" + bitmap.getByteCount() + " - replace first");
        }
    }

    public Bitmap getBitmap(int indexOffLetter) {
        Log.i(TAG, "### image getBitmap indexOffLetter=" + indexOffLetter);
        if (this.map.containsKey(Integer.valueOf(indexOffLetter))) {
            return (Bitmap) this.map.get(Integer.valueOf(indexOffLetter));
        }
        return null;
    }

    public void clear() {
        Log.i(TAG, "### image clear");
        if (this.map != null) {
            for (Integer key : this.map.keySet()) {
                ((Bitmap) this.map.get(key)).recycle();
            }
            this.map = null;
            System.gc();
        }
    }
}
