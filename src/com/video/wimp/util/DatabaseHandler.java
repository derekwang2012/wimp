package com.video.wimp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "wimp";

    // Contacts table name
    private static final String TABLE_CONTACTS = "youtube";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "title";
    private static final String KEY_YID = "yid";
    private static final String KEY_CREATE_DATE = "createDate";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_YID + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_CREATE_DATE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    // Drop the table
    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new youtube
    public void addYoutube(Youtube youtube) {
        SQLiteDatabase db = this.getWritableDatabase();
        String yid = youtube.getYid();
        if(!isExist(yid)) {
            ContentValues values = new ContentValues();
            values.put(KEY_YID, yid);
            values.put(KEY_NAME, youtube.getTitle());
            values.put(KEY_CREATE_DATE, sdf.format(new Date()));

            // Inserting Row
            db.insert(TABLE_CONTACTS, null, values);
            db.close(); // Closing database connection
        }
    }

    public boolean isExist(String yid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM "+TABLE_CONTACTS+" WHERE yid = ?", new String[] {yid});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // Getting single youtube
    public Youtube getYoutube(String yid) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                KEY_YID, KEY_NAME, KEY_CREATE_DATE }, KEY_YID + "=?",
                new String[] { yid }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Youtube youtube = new Youtube(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));

        return youtube;
    }

    // Getting All youtubes
    public List<Youtube> getAllYoutubes() {
        List<Youtube> youtubeList = new ArrayList<Youtube>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Youtube youtube = new Youtube();
                youtube.setId(Integer.parseInt(cursor.getString(0)));
                youtube.setYid(cursor.getString(1));
                youtube.setTitle(cursor.getString(2));
                youtube.setCreateDate(cursor.getString(3));

                youtubeList.add(youtube);
            } while (cursor.moveToNext());
        }

        return youtubeList;
    }

    // Getting All youtubes
    public List<Youtube> getAllYoutubesOrderByCreateDate() {
        List<Youtube> youtubeList = new ArrayList<Youtube>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " ORDER BY " + KEY_CREATE_DATE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Youtube youtube = new Youtube();
                youtube.setId(Integer.parseInt(cursor.getString(0)));
                youtube.setYid(cursor.getString(1));
                youtube.setTitle(cursor.getString(2));
                youtube.setCreateDate(cursor.getString(3));

                youtubeList.add(youtube);
            } while (cursor.moveToNext());
        }

        return youtubeList;
    }

    // Updating single youtube
    public int updateYoutube(Youtube youtube) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_YID, youtube.getYid());
        values.put(KEY_NAME, youtube.getTitle());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_YID + " = ?",
                new String[] { youtube.getYid() });
    }

    // Deleting single youtube
    public void deleteYoutube(Youtube youtube) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_YID + " = ?",
                new String[] { youtube.getYid() });
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, null, null);
        db.close();
    }


    // Getting youtubes Count
    public int getYoutubesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
