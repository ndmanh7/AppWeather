package com.example.appweather.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.appweather.Adapter.AdapterRecyclerView;
import com.example.appweather.R;
import com.example.appweather.databinding.ActivityMainBinding;
import com.example.appweather.search.Search;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    List<HourlyForecast> hourlyForecastList;
    RecyclerView recyclerView;
    AdapterRecyclerView adapterRecyclerView;

    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                MainActivity.this
        );

        GetCurrentWeather("Hà Nội");

        binding.tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation();
                }else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
                }
            }
        });

        binding.imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Search.class);
                startActivityForResult(intent,1);
            }
        });

        binding.btn5Day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = binding.tvCityName.getText().toString();
                Intent intent = new Intent(MainActivity.this, DuBao5Ngay.class);
                intent.putExtra("city", city);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            switch (resultCode){
                case RESULT_OK:
                    GetCurrentWeather(data.getStringExtra("cityname"));
                    break;

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        }else {
            Toast.makeText(getApplicationContext(),"denided",Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull  Task<Location> task) {
                    Location location = task.getResult();
                    if(location != null){
                        GetCurrentWeatherByLocation(location.getLatitude(),location.getLongitude());
                    }else{
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                GetCurrentWeatherByLocation(location1.getLatitude(),location1.getLongitude());
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                                locationCallback, Looper.myLooper());
                    }
                }
            });
        }else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public void GetCurrentWeather(String city) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String urlJson = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=0c96e8a8c0262bd497ceecb5cb81684b&lang=vi";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlJson, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String cityName = jsonObject.getString("name");
                    String day = jsonObject.getString("dt");
                    double vision = jsonObject.getDouble("visibility");

                    //lay toa do thanh pho
                    JSONObject jsonObjectCoord = jsonObject.getJSONObject("coord");
                    String lat = jsonObjectCoord.getString("lat");
                    String lon = jsonObjectCoord.getString("lon");
                    Double latitude = Double.valueOf(lat);
                    Double longtitude = Double.valueOf(lon);


                    GetHourlyForecast(latitude, longtitude);
                    GetDailyForecast(latitude, longtitude);


                    JSONObject jsonObjectBackground = jsonObject.getJSONObject("sys");
                    long srise = jsonObjectBackground.getLong("sunrise");
                    long sset = jsonObjectBackground.getLong("sunset");
                    Date sunrise = new Date(srise * 1000);
                    Date sunset = new Date(sset * 1000);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    String timeSunrise = simpleDateFormat.format(sunrise);
                    String timeSunset = simpleDateFormat.format(sunset);


                    binding.tvCityName.setText(cityName);

                    JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                    String description = jsonObjectWeather.getString("main");
                    String icon = jsonObjectWeather.getString("icon");
                    binding.tvDescription.setText(description);


                    JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                    int feelsLike = jsonObjectMain.getInt("feels_like");
                    String temperature = jsonObjectMain.getString("temp");
                    String humidity = jsonObjectMain.getString("humidity");
                    int pressure = jsonObjectMain.getInt("pressure");
                    Double temp = Double.valueOf(temperature);
                    String nhietdo = String.valueOf(temp.intValue());
                    Double humi = Double.valueOf(humidity);
                    String doam = String.valueOf(humi.intValue());


                    JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                    String speed = jsonObjectWind.getString("speed");
                    Double sp = Double.valueOf(speed);
                    String gio = String.valueOf(sp.intValue());


                    JSONObject jsonObjectCloud = jsonObject.getJSONObject("clouds");
                    String cloud = jsonObjectCloud.getString("all");
                    Double cloudiness = Double.valueOf(cloud);
                    String may = String.valueOf(cloudiness.intValue());

                    //set text
                    binding.tvTemperature.setText(nhietdo + "°C");
                    binding.tvHumidity.setText(doam + "%");
                    binding.tvWind.setText(gio + "m/s");
                    binding.tvCloudiness.setText(may + "%");
                    binding.tvSunrise.setText(timeSunrise);
                    binding.tvSunset.setText(timeSunset);
                    binding.tvFeellike.setText(feelsLike + "°C");
                    binding.tvPressure.setText("" + pressure + "hPa");
                    binding.tvVision.setText("" + vision / 1000 + "km");

                    //set icon
                    switch (icon){
                        case "01d":
                            binding.imgIcon.setImageResource(R.drawable.day_clear);
                            break;
                        case "02d":
                            binding.imgIcon.setImageResource(R.drawable.few_clouds_day);
                            break;
                        case "03d":
                            binding.imgIcon.setImageResource(R.drawable.cloud);
                            break;
                        case "04d":
                            binding.imgIcon.setImageResource(R.drawable.broken_cloud);
                            break;
                        case "09d":
                            binding.imgIcon.setImageResource(R.drawable.rain);
                            break;
                        case "10d":
                            binding.imgIcon.setImageResource(R.drawable.day_rain);
                            break;
                        case "11d":
                            binding.imgIcon.setImageResource(R.drawable.storm);
                            break;
                        case "13d":
                            binding.imgIcon.setImageResource(R.drawable.snowflake);
                            break;
                        case "50d":
                            binding.imgIcon.setImageResource(R.drawable.mist);
                            break;
                        case "01n":
                            binding.imgIcon.setImageResource(R.drawable.night_clear);
                            break;
                        case "02n":
                            binding.imgIcon.setImageResource(R.drawable.few_clouds_night);
                            break;
                        case "03n":
                            binding.imgIcon.setImageResource(R.drawable.cloud);
                            break;
                        case "04n":
                            binding.imgIcon.setImageResource(R.drawable.broken_cloud);
                            break;
                        case "09n":
                            binding.imgIcon.setImageResource(R.drawable.rain);
                            break;
                        case "10n":
                            binding.imgIcon.setImageResource(R.drawable.night_rain);
                            break;
                        case "11n":
                            binding.imgIcon.setImageResource(R.drawable.storm);
                            break;
                        case "13n":
                            binding.imgIcon.setImageResource(R.drawable.snowflake);
                            break;
                        case "50n":
                            binding.imgIcon.setImageResource(R.drawable.mist);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Không tìm thấy thành phố",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void GetHourlyForecast(double lat, double lon) {

        recyclerView = findViewById(R.id.recyclerview);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String coordURL = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&exclude=daily&units=metric&appid=0c96e8a8c0262bd497ceecb5cb81684b";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, coordURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    hourlyForecastList = new ArrayList<>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayHourly = jsonObject.getJSONArray("hourly");

                    JSONObject jsonObjectTest = jsonObject.getJSONObject("current");
                    long dt = jsonObjectTest.getLong("dt");
                    long sunrise = jsonObjectTest.getLong("sunrise");
                    long sunset = jsonObjectTest.getLong("sunset");
                    double uvi = jsonObjectTest.getDouble("uvi");
                    double lat = jsonObject.getDouble("lat");
                    double lon = jsonObject.getDouble("lon");

                    for(int i = 0 ; i<12; i++){
                        JSONObject jsonObjectHourly = jsonArrayHourly.getJSONObject(i);

                        //get date
                        String hour = jsonObjectHourly.getString("dt");
                        long l = Long.valueOf(hour);
                        Date date = new Date(l*1000);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        String date_format = simpleDateFormat.format(date);

                        //get temp
                        int temp = jsonObjectHourly.getInt("temp");

                        //get icon
                        JSONArray jsonArrayWeather = jsonObjectHourly.getJSONArray("weather");
                        JSONObject jsonObjectweather = jsonArrayWeather.getJSONObject(0);
                        String icon = jsonObjectweather.getString("icon");


                        HourlyForecast hourlyForecast = new HourlyForecast(date_format,icon,temp);
                        hourlyForecastList.add(hourlyForecast);



                    }


                    //set text
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false);
                    AdapterRecyclerView adapterRecyclerView = new AdapterRecyclerView(hourlyForecastList);

                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapterRecyclerView);



                    binding.tvUV.setText("" + uvi);




                    //set background
                    if (dt >= sunrise && dt < sunset) {
                        binding.constraintBackground.setBackgroundResource(R.drawable.day);
                    } else {
                        binding.constraintBackground.setBackgroundResource(R.drawable.night);
                    }

                    //set icon




                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getMessage();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void GetDailyForecast(double latitude, double longtitude) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String newURL = "https://api.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" + longtitude + "&exclude=hourly&units=metric&appid=0c96e8a8c0262bd497ceecb5cb81684b";

        StringRequest s = new StringRequest(Request.Method.GET, newURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayDaily = jsonObject.getJSONArray("daily");


                    JSONObject jsonObject1 = jsonArrayDaily.getJSONObject(0);
                    JSONObject jsonObject2 = jsonArrayDaily.getJSONObject(1);
                    JSONObject jsonObject3 = jsonArrayDaily.getJSONObject(2);

                    //temp
                    JSONObject jsonObjectTemp1 = jsonObject1.getJSONObject("temp");
                    JSONObject jsonObjectTemp2 = jsonObject2.getJSONObject("temp");
                    JSONObject jsonObjectTemp3 = jsonObject3.getJSONObject("temp");

                    //weather
                    JSONArray jsonArrayWeather1 = jsonObject1.getJSONArray("weather");
                    JSONArray jsonArrayWeather2 = jsonObject2.getJSONArray("weather");
                    JSONArray jsonArrayWeather3 = jsonObject3.getJSONArray("weather");

                    JSONObject jsonObjectWeather1 = jsonArrayWeather1.getJSONObject(0);
                    JSONObject jsonObjectWeather2 = jsonArrayWeather2.getJSONObject(0);
                    JSONObject jsonObjectWeather3 = jsonArrayWeather3.getJSONObject(0);

                    //lay du lieu
                    String d = jsonObject3.getString("dt");
                    long l = Long.valueOf(d);
                    Date date = new Date(l * 1000);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
                    String dt = simpleDateFormat.format(date);


                    int min1 = jsonObjectTemp1.getInt("min");
                    int min2 = jsonObjectTemp2.getInt("min");
                    int min3 = jsonObjectTemp3.getInt("min");

                    int max1 = jsonObjectTemp1.getInt("max");
                    int max2 = jsonObjectTemp2.getInt("max");
                    int max3 = jsonObjectTemp3.getInt("max");

                    String icon1 = jsonObjectWeather1.getString("icon");
                    String icon2 = jsonObjectWeather2.getString("icon");
                    String icon3 = jsonObjectWeather3.getString("icon");


                    //set text

                    binding.tvTodayMaxMin.setText("" + max1 + "°C / " + min1 + "°C");
                    binding.tvTomorrowMaxMin.setText("" + max2 + "°C / " + min2 + "°C");
                    binding.tv3rdDayMaxMin.setText("" + max3 + "°C / " + min3 + "°C");
                    binding.tv3rdDay.setText(dt);

                    //set icon
                    switch (icon1){
                        case "01d":
                            binding.imgIconToday.setImageResource(R.drawable.day_clear);
                            break;
                        case "02d":
                            binding.imgIconToday.setImageResource(R.drawable.few_clouds_day);
                            break;
                        case "03d":
                            binding.imgIconToday.setImageResource(R.drawable.cloud);
                            break;
                        case "04d":
                            binding.imgIconToday.setImageResource(R.drawable.broken_cloud);
                            break;
                        case "09d":
                            binding.imgIconToday.setImageResource(R.drawable.rain);
                            break;
                        case "10d":
                            binding.imgIconToday.setImageResource(R.drawable.day_rain);
                            break;
                        case "11d":
                            binding.imgIconToday.setImageResource(R.drawable.storm);
                            break;
                        case "13d":
                            binding.imgIconToday.setImageResource(R.drawable.snowflake);
                            break;
                        case "50d":
                            binding.imgIconToday.setImageResource(R.drawable.mist);
                            break;
                        case "01n":
                            binding.imgIconToday.setImageResource(R.drawable.night_clear);
                            break;
                        case "02n":
                            binding.imgIconToday.setImageResource(R.drawable.few_clouds_night);
                            break;
                        case "03n":
                            binding.imgIconToday.setImageResource(R.drawable.cloud);
                            break;
                        case "04n":
                            binding.imgIconToday.setImageResource(R.drawable.broken_cloud);
                            break;
                        case "09n":
                            binding.imgIconToday.setImageResource(R.drawable.rain);
                            break;
                        case "10n":
                            binding.imgIconToday.setImageResource(R.drawable.night_rain);
                            break;
                        case "11n":
                            binding.imgIconToday.setImageResource(R.drawable.storm);
                            break;
                        case "13n":
                            binding.imgIconToday.setImageResource(R.drawable.snowflake);
                            break;
                        case "50n":
                            binding.imgIconToday.setImageResource(R.drawable.mist);
                            break;
                    };
                    switch (icon2){
                        case "01d":
                            binding.imgIconTomorrow.setImageResource(R.drawable.day_clear);
                            break;
                        case "02d":
                            binding.imgIconTomorrow.setImageResource(R.drawable.few_clouds_day);
                            break;
                        case "03d":
                            binding.imgIconTomorrow.setImageResource(R.drawable.cloud);
                            break;
                        case "04d":
                            binding.imgIconTomorrow.setImageResource(R.drawable.broken_cloud);
                            break;
                        case "09d":
                            binding.imgIconTomorrow.setImageResource(R.drawable.rain);
                            break;
                        case "10d":
                            binding.imgIconTomorrow.setImageResource(R.drawable.day_rain);
                            break;
                        case "11d":
                            binding.imgIconTomorrow.setImageResource(R.drawable.storm);
                            break;
                        case "13d":
                            binding.imgIconTomorrow.setImageResource(R.drawable.snowflake);
                            break;
                        case "50d":
                            binding.imgIconTomorrow.setImageResource(R.drawable.mist);
                            break;
                        case "01n":
                            binding.imgIconTomorrow.setImageResource(R.drawable.night_clear);
                            break;
                        case "02n":
                            binding.imgIconTomorrow.setImageResource(R.drawable.few_clouds_night);
                            break;
                        case "03n":
                            binding.imgIconTomorrow.setImageResource(R.drawable.cloud);
                            break;
                        case "04n":
                            binding.imgIconTomorrow.setImageResource(R.drawable.broken_cloud);
                            break;
                        case "09n":
                            binding.imgIconTomorrow.setImageResource(R.drawable.rain);
                            break;
                        case "10n":
                            binding.imgIconTomorrow.setImageResource(R.drawable.night_rain);
                            break;
                        case "11n":
                            binding.imgIconTomorrow.setImageResource(R.drawable.storm);
                            break;
                        case "13n":
                            binding.imgIconTomorrow.setImageResource(R.drawable.snowflake);
                            break;
                        case "50n":
                            binding.imgIconTomorrow.setImageResource(R.drawable.mist);
                            break;
                    };
                    switch (icon3){
                        case "01d":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.day_clear);
                            break;
                        case "02d":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.few_clouds_day);
                            break;
                        case "03d":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.cloud);
                            break;
                        case "04d":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.broken_cloud);
                            break;
                        case "09d":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.rain);
                            break;
                        case "10d":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.day_rain);
                            break;
                        case "11d":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.storm);
                            break;
                        case "13d":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.snowflake);
                            break;
                        case "50d":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.mist);
                            break;
                        case "01n":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.night_clear);
                            break;
                        case "02n":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.few_clouds_night);
                            break;
                        case "03n":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.cloud);
                            break;
                        case "04n":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.broken_cloud);
                            break;
                        case "09n":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.rain);
                            break;
                        case "10n":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.night_rain);
                            break;
                        case "11n":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.storm);
                            break;
                        case "13n":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.snowflake);
                            break;
                        case "50n":
                            binding.imgIcon3rdDay.setImageResource(R.drawable.mist);
                            break;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getMessage();

            }
        });
        requestQueue.add(s);
    }

    public void GetCurrentWeatherByLocation(double latitude, double longtitude){
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String urlJson = "http://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longtitude+"&appid=0c96e8a8c0262bd497ceecb5cb81684b";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlJson, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String cityName = jsonObject.getString("name");
                    GetCurrentWeather(cityName);
                } catch (Exception e) {
                    e.printStackTrace();
                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getMessage();
            }
        });
        requestQueue.add(stringRequest);
    }
}