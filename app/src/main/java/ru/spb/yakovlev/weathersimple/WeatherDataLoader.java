package ru.spb.yakovlev.weathersimple;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Aleksey on 04.03.2018.
 */

class WeatherDataLoader {

    private static final String LOG_TAG = WeatherDataLoader.class.getSimpleName();

    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";
    private static final String KEY = "x-api-key";
    private static final String RESPONSE = "cod";
    private static final String NEW_LINE = "\n";
    private static final int RESPONSE_OK = 200;

    //Единственный метод класса, который делает запрос на сервер и получает от него данные
    //Возвращает объект JSON или null
    static JSONObject getJSONData(Context context, String city) {
        try {
            //Используем API (Application programming interface) openweathermap
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.addRequestProperty("id", cityId);
            connection.addRequestProperty(KEY, context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder rawData = new StringBuilder(1024);
            String tempVariable;
            while ((tempVariable = reader.readLine()) != null) {
                rawData.append(tempVariable).append(NEW_LINE);
            }
            reader.close();

            JSONObject jsonObject = new JSONObject(rawData.toString());
            // API openweathermap
            if (jsonObject.getInt(RESPONSE) != RESPONSE_OK) {
                Log.d(LOG_TAG, "RESPONSE not ok");
                return null;
            }
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Фрагмент уведомления об ошибке
            return null;
        }
    }
}
