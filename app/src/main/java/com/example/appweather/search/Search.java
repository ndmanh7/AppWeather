package com.example.appweather.search;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appweather.Adapter.AdapterStringList;
import com.example.appweather.R;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    SearchView svSearch;
    ListView lv;
    ImageView imgBackSearch, imgDeleteAll;
    AdapterStringList adapterStringList;
    List<SearchHistoryList> showList;

    SQLHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        svSearch = findViewById(R.id.svSearch);
        lv = findViewById(R.id.lvSearch);
        imgBackSearch = findViewById(R.id.imgBackSearch);
        imgDeleteAll = findViewById(R.id.imgDeleteAll);
        helper = new SQLHelper(this);
        showList = new ArrayList<>();


        svSearch.setFocusable(true);
        svSearch.setIconified(false);
        svSearch.requestFocusFromTouch();

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                helper.onAddList(query);
                Intent intent1 = new Intent();
                intent1.putExtra("cityname",query);
                setResult(RESULT_OK,intent1);
                finish();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        List<SearchHistoryList> searchHistoryLists = helper.onGetList();
        for(int i = 0; i<searchHistoryLists.size(); i++){
            SearchHistoryList searchHistoryList = new SearchHistoryList(searchHistoryLists.get(i).getString());
            showList.add(searchHistoryList);
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent();
                String cityname = searchHistoryLists.get(position).getString();
                intent1.putExtra("cityname",cityname);
                setResult(RESULT_OK,intent1);
                finish();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog alertDialog = new AlertDialog.Builder(Search.this).setTitle("Thông báo")
                        .setMessage("Xóa lịch sử tìm kiếm ?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                helper.onDelete(searchHistoryLists.get(position).getString());
                                showList.remove(position);
                                adapterStringList.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapterStringList.notifyDataSetChanged();
                            }
                        })
                       .show();

                return true;
            }
        });


        imgBackSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(Search.this).setTitle("Thông báo")
                        .setMessage("Xóa tất cả lịch sử tìm kiếm ?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                helper.deleteAll();
                                showList.clear();
                                adapterStringList.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapterStringList.notifyDataSetChanged();
                            }
                        })
                        .show();
            }
        });


        adapterStringList = new AdapterStringList(showList);
        lv.setAdapter(adapterStringList);
        adapterStringList.notifyDataSetChanged();

    }




}