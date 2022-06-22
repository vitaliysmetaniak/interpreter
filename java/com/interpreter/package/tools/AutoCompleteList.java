package ru.avroraventures.russiansinglanguage.tools;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.ArrayList;
import java.util.List;
import ru.avroraventures.russiansinglanguage.BuildConfig;

public class AutoCompleteList {
    private static final String LIST_ITEM = "item";
    private static final List<String> listForAutocomlete = new ArrayList(10);
    private static SharedPreferences sharedPreferences;
    private boolean firstTime = true;

    public AutoCompleteList(SharedPreferences _sharedPreferences) {
        sharedPreferences = _sharedPreferences;
    }

    private static void loadListFromSharedPreferences() {
        for (int i = 0; i < 10; i++) {
            String listItem = LIST_ITEM + String.valueOf(i);
            if (sharedPreferences.contains(listItem)) {
                String item = sharedPreferences.getString(listItem, BuildConfig.FLAVOR);
                if (!item.isEmpty()) {
                    listForAutocomlete.add(item);
                }
            }
        }
    }

    private void saveListToSharedPreferences() {
        Editor edit = sharedPreferences.edit();
        for (int i = 0; i < 10; i++) {
            String listItem = LIST_ITEM + String.valueOf(i);
            if (i < listForAutocomlete.size()) {
                edit.putString(listItem, (String) listForAutocomlete.get(i));
            } else if (sharedPreferences.contains(listItem)) {
                edit.remove(listItem);
            }
            edit.apply();
        }
    }

    public void addNewItem(String item) {
        for (String savedItem : listForAutocomlete) {
            if (savedItem.equals(item)) {
                listForAutocomlete.remove(item);
                break;
            }
        }
        if (listForAutocomlete.size() >= 10) {
            listForAutocomlete.remove(listForAutocomlete.size() - 1);
        }
        listForAutocomlete.add(0, item);
        saveListToSharedPreferences();
    }

    public List<String> getList() {
        if (this.firstTime) {
            this.firstTime = false;
            loadListFromSharedPreferences();
        }
        return listForAutocomlete;
    }
}
