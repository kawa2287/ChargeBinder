package kawa.chargebinder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper
{

    public static final String DATABASE_NAME = "cicuBeds2.db";
    public static final String TABLE_NAME = "Beds";

    //  Table properties
    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_BED = "bed";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_DOB = "DOB";
    public static final String KEY_ECPR = "ECPR";
    public static final String KEY_CODE = "CODE";
    public static final String KEY_CAP = "CAP";
    public static final String KEY_RT_FLOAT_APPR = "resourceTeamAppropriate";
    public static final String KEY_ISO = "ISO";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "( " +
                KEY_ROW_ID + " INTEGER, " +
                KEY_BED + " INTEGER, " +
                KEY_FIRST_NAME + " TEXT, " +
                KEY_LAST_NAME + " TEXT, " +
                KEY_DOB + " TEXT, " +
                KEY_ECPR + " TEXT, " +
                KEY_CODE + " TEXT," +
                KEY_CAP + " TEXT," +
                KEY_RT_FLOAT_APPR + " TEXT," +
                KEY_ISO + " TEXT" +
                ");";
        db.execSQL(createTable);    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop if database is exists" + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getBedData(int bedNumber, SQLiteDatabase sqLiteDatabase) {
        return sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_BED + " = " + bedNumber, null);

    }


}