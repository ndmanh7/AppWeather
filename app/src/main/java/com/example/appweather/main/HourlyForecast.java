package com.example.appweather.main;

public class HourlyForecast {
    String date;
    String icon;
    int temp;

    public HourlyForecast(String date, String icon, int temp) {
        this.date = date;
        this.icon = icon;
        this.temp = temp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}
