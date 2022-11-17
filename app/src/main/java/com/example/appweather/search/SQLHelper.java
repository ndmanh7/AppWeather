package com.example.appweather.search;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class SQLHelper extends SQLiteOpenHelper {
    static private final String DB_NAME = "HistoryList.db";

    private static final String DV_STRING= "string";

    private static final String TB_HISTORYLIST = "historylist";

    public SQLHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE historylist("
                +"string TEXT NOT NULL PRIMARY KEY"
                +")";
        db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion){
            String strQuery = "DROP TABLE IF EXISTS "+DB_NAME;
            db.execSQL(strQuery);
            onCreate(db);
        }
    }
    public void onAddList(String string){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("string",string);

        sqLiteDatabase.insert(TB_HISTORYLIST,null,contentValues);
        sqLiteDatabase.close();
        contentValues.clear();

    }

    public List<SearchHistoryList> onGetList(){
        String queryString = "SELECT * FROM " + TB_HISTORYLIST;
        List<SearchHistoryList> searchHistoryLists = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(queryString, null);
        try{
            if(cursor.moveToFirst()){
                do{
                    String string = cursor.getString(cursor.getColumnIndex(DV_STRING));
                    SearchHistoryList searchHistoryList= new SearchHistoryList(string);
                    searchHistoryLists.add(searchHistoryList);
                }
            while (cursor.moveToNext());
        }
        }
            finally{
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            sqLiteDatabase.close();
            }


        return  searchHistoryLists;
    }

    public void onDelete(String string){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TB_HISTORYLIST,"string=?",new String[]{string});

    }

    public void deleteAll(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TB_HISTORYLIST,null,null);
        sqLiteDatabase.execSQL("delete from "+TB_HISTORYLIST);
        sqLiteDatabase.close();
    }

}
