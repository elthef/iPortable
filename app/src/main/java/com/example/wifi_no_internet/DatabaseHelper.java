package com.example.wifi_no_internet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase database;

    public  static final String TABLE_NAME = "PRODUCTS";

    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String WEIGHT = "weight";
    public static final String DATE = "date";

    static final String DB_NAME = "groceryStock.DB";

    static final int DB_VERSION = 1;

    private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+NAME+" TEXT NOT NULL, "+WEIGHT+" TEXT NOT NULL, "+DATE+" TEXT NOT NULL);";

    public DatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public void open() throws SQLException{
        database = this.getWritableDatabase();
    }

    public void close(){
        database.close();
    }

    public void add(Patient patient){
        ContentValues contentVal = new ContentValues();
        contentVal.put(NAME,patient.getName());
        contentVal.put(WEIGHT,patient.getWeight());
        contentVal.put(DATE,patient.getDateLastWeight());

        database.insert(TABLE_NAME,null,contentVal);
    }

    public Cursor getAllprod(){
        String[] projection = {_ID,NAME,WEIGHT,DATE};
        Cursor cursor = database.query(TABLE_NAME,projection,null,null,null,null,null,null);
        return cursor;
    }

    public int update(Patient patient){
        Long _id = patient.getDbID();

        ContentValues contentVal = new ContentValues();
        contentVal.put(NAME,patient.getName());
        contentVal.put(WEIGHT,patient.getWeight());
        contentVal.put(DATE,patient.getDateLastWeight());

        int count = database.update(TABLE_NAME, contentVal, this._ID + " = " + _id, null);
        return count;
    }

    public void delete(long _id){
        database.delete(TABLE_NAME,_ID+"="+_id,null);
    }
}


