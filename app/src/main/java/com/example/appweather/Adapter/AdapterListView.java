    package com.example.appweather.Adapter;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.BaseAdapter;
    import android.widget.ImageView;
    import android.widget.TextView;

    import com.example.appweather.R;
    import com.example.appweather.main.WeatherForecast5Days;

    import java.util.List;

    public class AdapterListView extends BaseAdapter {
        List<WeatherForecast5Days> weatherForecast5DaysList;

        public AdapterListView(List<WeatherForecast5Days> weatherForecast5DaysList) {
            this.weatherForecast5DaysList = weatherForecast5DaysList;
        }

        @Override
        public int getCount() {
            return weatherForecast5DaysList.size();
        }

        @Override
        public Object getItem(int position) {
            return weatherForecast5DaysList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item_listview,parent,false);

            TextView tvDate,tvMaxMinTemp;
            ImageView imgForecastIcon;

            tvDate = view.findViewById(R.id.tvDate);
            tvMaxMinTemp = view.findViewById(R.id.tvMaxMinTemp);
            imgForecastIcon = view.findViewById(R.id.imgForecastIcon);
            WeatherForecast5Days weatherForecast5Days = weatherForecast5DaysList.get(position);

            tvDate.setText(weatherForecast5Days.getDay());
            switch (weatherForecast5Days.getIcon()){
                case "01d":
                    imgForecastIcon.setImageResource(R.drawable.day_clear);
                    break;
                case "02d":
                    imgForecastIcon.setImageResource(R.drawable.few_clouds_day);
                    break;
                case "03d":
                    imgForecastIcon.setImageResource(R.drawable.cloud);
                    break;
                case "04d":
                    imgForecastIcon.setImageResource(R.drawable.broken_cloud);
                    break;
                case "09d":
                    imgForecastIcon.setImageResource(R.drawable.rain);
                    break;
                case "10d":
                    imgForecastIcon.setImageResource(R.drawable.day_rain);
                    break;
                case "11d":
                    imgForecastIcon.setImageResource(R.drawable.storm);
                    break;
                case "13d":
                    imgForecastIcon.setImageResource(R.drawable.snowflake);
                    break;
                case "50d":
                    imgForecastIcon.setImageResource(R.drawable.mist);
                    break;
                case "01n":
                    imgForecastIcon.setImageResource(R.drawable.night_clear);
                    break;
                case "02n":
                    imgForecastIcon.setImageResource(R.drawable.few_clouds_night);
                    break;
                case "03n":
                    imgForecastIcon.setImageResource(R.drawable.cloud);
                    break;
                case "04n":
                    imgForecastIcon.setImageResource(R.drawable.broken_cloud);
                    break;
                case "09n":
                    imgForecastIcon.setImageResource(R.drawable.rain);
                    break;
                case "10n":
                    imgForecastIcon.setImageResource(R.drawable.night_rain);
                    break;
                case "11n":
                    imgForecastIcon.setImageResource(R.drawable.storm);
                    break;
                case "13n":
                    imgForecastIcon.setImageResource(R.drawable.snowflake);
                    break;
                case "50n":
                    imgForecastIcon.setImageResource(R.drawable.mist);
                    break;
            }
            int max = weatherForecast5Days.getMaxTemp();
            int min = weatherForecast5Days.getMinTemp();
            tvMaxMinTemp.setText(""+min+"°C - "+max+"°C");

            return view;
        }
    }
