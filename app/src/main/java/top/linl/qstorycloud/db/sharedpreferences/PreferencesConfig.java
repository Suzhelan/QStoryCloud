package top.linl.qstorycloud.db.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesConfig {
    public static final String FileName = "QStoryCloudAPP";
    private final Context context;

    public PreferencesConfig(Context context) {
        this.context = context;
    }

    public void remove(String key) {
        SharedPreferences share = getContext().getSharedPreferences(FileName, 0);
        SharedPreferences.Editor editor = share.edit();
        editor.remove(key);
        editor.apply();
    }

    public String getString(String key) {
        SharedPreferences share = getContext().getSharedPreferences(FileName, 0);
        return share.getString(key, "");
    }

    public void putString(String key, String value) {
        SharedPreferences share = getContext().getSharedPreferences(FileName, 0);
        SharedPreferences.Editor editor = share.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences share = getContext().getSharedPreferences(FileName, 0);
        SharedPreferences.Editor editor = share.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        SharedPreferences share = getContext().getSharedPreferences(FileName, 0);
        return share.getBoolean(key, false);
    }

    public void putInt(String key, int i) {
        SharedPreferences share = getContext().getSharedPreferences(FileName, 0);
        SharedPreferences.Editor editor = share.edit();
        editor.putInt(key, i);
        editor.apply();
    }

    public int getInt(String key) {
        SharedPreferences share = getContext().getSharedPreferences(FileName, 0);
        return share.getInt(key, 0);
    }

    private Context getContext() {
        return context;
    }
}
