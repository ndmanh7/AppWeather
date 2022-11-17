package com.example.appweather.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appweather.R;
import com.example.appweather.main.HourlyForecast;

import java.util.List;

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {
    List<HourlyForecast> hourlyForecastList;

    public AdapterRecyclerView(List<HourlyForecast> hourlyForecastList) {
        this.hourlyForecastList = hourlyForecastList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_recyclerview,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRecyclerView.ViewHolder holder, int position) {
        HourlyForecast hourlyForecast = hourlyForecastList.get(position);
        holder.tvHour.setText(hourlyForecast.getDate());
        holder.tvHourlyTemp.setText(""+hourlyForecast.getTemp()+"°C");
        switch (hourlyForecast.getIcon()){
            case "01d":
                holder.img.setImageResource(R.drawable.day_clear);
                break;
            case "02d":
                holder.img.setImageResource(R.drawable.few_clouds_day);
                break;
            case "03d":
                holder.img.setImageResource(R.drawable.cloud);
                break;
            case "04d":
                holder.img.setImageResource(R.drawable.broken_cloud);
                break;
            case "09d":
                holder.img.setImageResource(R.drawable.rain);
                break;
            case "10d":
                holder.img.setImageResource(R.drawable.day_rain);
                break;
            case "11d":
                holder.img.setImageResource(R.drawable.storm);
                break;
            case "13d":
                holder.img.setImageResource(R.drawable.snowflake);
                break;
            case "50d":
                holder.img.setImageResource(R.drawable.mist);
                break;
            case "01n":
                holder.img.setImageResource(R.drawable.night_clear);
                break;
            case "02n":
                holder.img.setImageResource(R.drawable.few_clouds_night);
                break;
            case "03n":
                holder.img.setImageResource(R.drawable.cloud);
                break;
            case "04n":
                holder.img.setImageResource(R.drawable.broken_cloud);
                break;
            case "09n":
                holder.img.setImageResource(R.drawable.rain);
                break;
            case "10n":
                holder.img.setImageResource(R.drawable.night_rain);
                break;
            case "11n":
                holder.img.setImageResource(R.drawable.storm);
                break;
            case "13n":
                holder.img.setImageResource(R.drawable.snowflake);
                break;
            case "50n":
                holder.img.setImageResource(R.drawable.mist);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return hourlyForecastList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHour, tvHourlyTemp;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvHour = itemView.findViewById(R.id.tvHour);
            tvHourlyTemp = itemView.findViewById(R.id.tvHourlyTemp);
            img = itemView.findViewById(R.id.imgHour);
        }
    }
}
