package com.example.myapplication00;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherFragment extends Fragment {
    private static final String TAG = "WeatherFragmentDebug";
    private ImageView weatherImage;
    private TextView temperatureText;
    private String apiKey = "0bef8b4291032f6f96d8d784459cbdf8\n";
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        weatherImage = rootView.findViewById(R.id.weatherIcon);
        temperatureText = rootView.findViewById(R.id.temperature_text);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        fetchCityFromDatabase();

        return rootView;
    }

    private void fetchCityFromDatabase() {
        String userId = sharedPreferences.getString("userId", null);
        Log.d(TAG, "Fetched userId: " + userId);

        if (userId != null) {
            databaseReference.child(userId).child("city").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String city = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "Retrieved city: " + city);
                        fetchWeatherData(city);
                    } else {
                        Log.w(TAG, "City not found in the database");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Failed to retrieve city information", databaseError.toException());
                }
            });
        } else {
            Log.e(TAG, "User not logged in");
        }
    }



    private void fetchWeatherData(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";

        JsonObjectRequest weatherRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject main = response.getJSONObject("main");
                            double temperature = main.getDouble("temp");

                            // Extract the weather description
                            JSONObject weather = response.getJSONArray("weather").getJSONObject(0);
                            String description = weather.getString("description");

                            updateWeatherData(temperature + "°C", description); // Pass description here
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error parsing weather data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getContext(), "Error fetching weather data", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the request queue
        Volley.newRequestQueue(getContext()).add(weatherRequest);
    }

    public void updateWeatherData(String temperature, String description) {
        // Round the temperature value to a whole number
        double tempValue = Double.parseDouble(temperature.replace("°C", "").trim());
        int roundedTemp = (int) Math.round(tempValue); // Round to the nearest integer

        // Set the rounded temperature to the TextView
        temperatureText.setText(roundedTemp + "°C");

        // Get the corresponding weather icon based on the weather description
        int weatherIcon = getWeatherIcon(description);

        // Set the weather icon to the ImageView
        weatherImage.setImageResource(weatherIcon);
    }

    // Method to map OpenWeather description to your local resource
    private int getWeatherIcon(String description) {
        switch (description.toLowerCase()) {
            case "snow":
                return R.drawable.snowflake;
            case "thunderstorm":
                return R.drawable.lighting;
            case "shower rain":
            case "rain":
                return R.drawable.rainy;
            case "scattered clouds":
            case "broken clouds":
                return R.drawable.cloud;
            case "clear sky":
                return R.drawable.sunny;
            case "few clouds":
                return R.drawable.sunnyc;
            case "mist":
                return R.drawable.mist;
            default:
                return R.drawable.cloud;
        }
    }
}
