package ru.spb.yakovlev.weathersimple;


import android.app.Activity;
import android.graphics.Typeface;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    private static final String LOG_TAG = WeatherFragment.class.getSimpleName();

    private final Handler handler = new Handler();

    private Typeface weatherFont;
    private TextView cityTextView;
    private TextView updatedTextView;
    private TextView detailsTextView;
    private TextView currentTemperatureTextView;
    private TextView weatherIcon;


    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        weatherFont = ResourcesCompat.getFont(activity, R.font.weather);
        updateWeatherData(new CityPreference(activity).getCity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        cityTextView = view.findViewById(R.id.tv_city);
        updatedTextView = view.findViewById(R.id.tv_updated);
        detailsTextView = view.findViewById(R.id.tv_details);
        currentTemperatureTextView = view.findViewById(R.id.tv_temperature);
        weatherIcon = view.findViewById(R.id.tv_weather_icon);
        weatherIcon.setTypeface(weatherFont);
        return view;
    }

    private void updateWeatherData(String city) {

        Activity activity = getActivity();
        new Thread(() -> {
            final JSONObject json = WeatherDataLoader.getJSONData(activity, city);
            if (json == null) {
                handler.post(() -> {
                    Toast
                            .makeText(activity, activity.getString(R.string.place_not_found), Toast.LENGTH_LONG)
                            .show();
                });
            } else {
                handler.post(() -> renderWeather(json));
            }
//           
        }).start();
    }

    private void renderWeather(JSONObject json) {
        Log.d(LOG_TAG, "json" + json.toString());
        try {
            cityTextView.setText(String.format("%s, %s"
                    , json.getString("name").toUpperCase(Locale.US)
                    , json.getJSONObject("sys").getString("country")));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsTextView.setText(String.format("%s\nHumidity: %s%%\nPressure: %shPa"
                    , details.getString("description").toUpperCase(Locale.US)
                    , main.getString("humidity")
                    , main.getString("pressure")));
            currentTemperatureTextView.setText(String.format(Locale.US, "%.2f °C", main.getDouble("temp")));

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt") * 1000));
            updatedTextView.setText(String.format("Last update: %s", updatedOn));
            setWeatherIcon(details.getInt("id")
                    , json.getJSONObject("sys").getLong("sunrise") * 1000
                    , json.getJSONObject("sys").getLong("sunset") * 1000);


        } catch (Exception e) {
            Log.d(LOG_TAG, "One or more fields not found in JSON data");
        }

    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        weatherIcon.setText("");
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime <= sunset) {
                setIcon(R.string.weather_clear_day);
            } else {
                setIcon(R.string.weather_clear_night);
            }
        } else {
            Log.d(LOG_TAG, "id " + id);
            switch (id) {
                case 2:
                    setIcon(R.string.weather_thunder);
                    break;
                case 3:
                    setIcon(R.string.weather_drizzle);
                    break;
//                case 4:
//                    setIcon(R.string.weather_rainy);
//                    break;
                case 5:
                    setIcon(R.string.weather_rainy);
                    break;
                case 6:
                    setIcon(R.string.weather_snowy);
                    break;
                case 7:
                    setIcon(R.string.weather_foggy);
                    break;
                case 8:
                    setIcon(R.string.weather_cloudy);
                    break;
                default:
                    break;
            }
        }
    }

    private void setIcon(int resId) {
        weatherIcon.setText(getActivity().getString(resId));
    }

    public void changeCity(String city) {
        updateWeatherData(city);
    }
}
