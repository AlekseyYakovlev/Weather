package ru.spb.yakovlev.weathersimple;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Aleksey on 04.03.2018.
 */

public class CityPreference {
    private static final String KEY = "city";
    private static final String MOSCOW = "Moscow";
    private SharedPreferences userPreferences;

    CityPreference(Activity activity) {
        userPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getCity() {
        return userPreferences.getString(KEY, MOSCOW);
    }

    void setCity (String city){
        userPreferences.edit().putString(KEY,city).apply();
    }
}