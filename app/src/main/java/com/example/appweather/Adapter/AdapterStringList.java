package com.example.appweather.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.appweather.R;
import com.example.appweather.search.SearchHistoryList;

import java.util.List;

public class AdapterStringList extends BaseAdapter {
    List<SearchHistoryList> searchHistoryLists;

    public AdapterStringList(List<SearchHistoryList> searchHistoryLists) {
        this.searchHistoryLists = searchHistoryLists;
    }
    @Override
    public int getCount() {
        return searchHistoryLists.size();
    }

    @Override
    public Object getItem(int position) {
        return searchHistoryLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.iem_string_list,parent,false);

        TextView tvString;
        tvString = view.findViewById(R.id.tvString);
        tvString.setText(searchHistoryLists.get(position).getString());



        return view;
    }
}
