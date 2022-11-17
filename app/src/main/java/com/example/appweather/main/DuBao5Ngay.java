package com.example.appweather.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.appweather.Adapter.AdapterListView;
import com.example.appweather.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DuBao5Ngay extends AppCompatActivity {

    ListView lvWeatherForecast5Day;
    List<WeatherForecast5Days> weatherForecast5DaysList;
    AdapterListView adapterListView;

    TextView tv;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_du_bao5_ngay);

        //hien thi du lieu
        Intent intent = getIntent();
        String city = intent.getStringExtra("city");
        lvWeatherForecast5Day = findViewById(R.id.lvWeatherForecast5Day);
        Get5DayWeather(city);




        //nut quay lai
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




    }


    //lay du lieu thoi tiet 5 ngay tiep theo
    private void Get5DayWeather(String city) {
        String newurl = "http://api.openweathermap.org/data/2.5/forecast?q="+city+"&units=metric&cnt=40&appid=0c96e8a8c0262bd497ceecb5cb81684b";
        RequestQueue requestQueue = Volley.newRequestQueue(DuBao5Ngay.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, newurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayList = jsonObject.getJSONArray("list");
                    weatherForecast5DaysList = new ArrayList<>();





                    for (int i = 0; i < jsonArrayList.length(); i += 8){
                        JSONObject jsonObjectList = jsonArrayList.getJSONObject(i);



                        //thu trong tuan
                        String ngay = jsonObjectList.getString("dt");
                        long l = Long.valueOf(ngay);
                        Date date = new Date(l*1000L);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
                        String thu = simpleDateFormat.format(date);

                        //hinh anh
                        JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                        String icon = jsonObjectWeather.getString("icon");


                        //nhiet do max min
                        JSONObject jsonObjectMain = jsonObjectList.getJSONObject("main");
                        int max = jsonObjectMain.getInt("temp_max");
                        int min = jsonObjectMain.getInt("temp_min");

                        WeatherForecast5Days weatherForecast5Days = new WeatherForecast5Days(thu,icon,max,min);
                        weatherForecast5DaysList.add(weatherForecast5Days);

                    }


                    adapterListView = new AdapterListView(weatherForecast5DaysList);
                    lvWeatherForecast5Day.setAdapter(adapterListView);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                ;


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }


}