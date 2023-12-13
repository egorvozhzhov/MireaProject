package ru.mirea.vozhzhovea.mireaproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "my_database";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "user_location";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static double latitude;
    public static double longitude;


    // Конструктор
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Создание таблицы
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LOGIN + " TEXT, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL)";
        db.execSQL(createTableQuery);
    }

    // Обновление базы данных, если версия изменилась
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addLocationData(String login, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, login);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    public double[] getLocationDataByLogin(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_LATITUDE + ", " + COLUMN_LONGITUDE +
                " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_LOGIN + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{login});


        if (cursor.moveToFirst()) {
            latitude = cursor.getDouble(0);
            longitude = cursor.getDouble(1);

        }
        double[] values = new double[2];
        values[0] = latitude;
        values[1] = longitude;
        cursor.close();
        db.close();
        return values;
    }
}








