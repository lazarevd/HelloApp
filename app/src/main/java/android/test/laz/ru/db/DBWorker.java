package android.test.laz.ru.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.test.laz.ru.translateapp.Prefs;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Dmitry Lazarev on 04.04.2017.
 */

public class DBWorker extends SQLiteOpenHelper {

    //Синглтон для работы с SQLite БД
    private static DBWorker dbWorkerInstance;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TranslateDB.db";
    public Context context;

    private DBWorker(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static DBWorker getInstance(Context context) {
        if (dbWorkerInstance == null) {
            dbWorkerInstance = new DBWorker(context);
            return dbWorkerInstance;
        } else {
            return dbWorkerInstance;
        }
    }

    public static void setContext(Context context) {
        dbWorkerInstance.setContext(context);
    }



    private final String SQL_CREATE_HISTORY =
            "CREATE TABLE " + DBContract.HistoryEntry.TABLE_NAME + " (" +
                    DBContract.HistoryEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.HistoryEntry.FROM_TEXT + " TEXT," +
                    DBContract.HistoryEntry.TO_TEXT + " TEXT," +
                    DBContract.HistoryEntry.DATE + " TEXT," +
                    DBContract.HistoryEntry.DIR + " TEXT)";

    private final String SQL_CREATE_FAVORITES =
            "CREATE TABLE " + DBContract.FavoritesEntry.TABLE_NAME + " (" +
                    DBContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.FavoritesEntry.FROM_TEXT + " TEXT," +
                    DBContract.FavoritesEntry.TO_TEXT + " TEXT," +
                    DBContract.HistoryEntry.DATE + " TEXT," +
                    DBContract.HistoryEntry.DIR + " TEXT)";

    private final String SQL_DELETE_HISTORY =
            "DELETE FROM " + DBContract.HistoryEntry.TABLE_NAME;

    private final String SQL_SELECTALL_HISTORY =
            "SELECT * FROM " + DBContract.HistoryEntry.TABLE_NAME;


    private final String SQL_DELETE_FAVORITES =
            "DELETE FROM " + DBContract.FavoritesEntry.TABLE_NAME;


    public void addFavorite(String inputString) {
        new AddFavoriteTask().execute(inputString);//запускаем поток записи в БД
    }

    private class AddFavoriteTask extends AsyncTask<String, Void, Void> {//запись в БД делаем в отдельном потоке
        protected Void doInBackground(String... putValues) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBContract.FavoritesEntry.FROM_TEXT, putValues[0]);
            values.put(DBContract.FavoritesEntry.TO_TEXT, putValues[1]);
            values.put(DBContract.FavoritesEntry.DATE, Prefs.getCurrentDateString());
            db.insert(DBContract.FavoritesEntry.TABLE_NAME, null, values);
            db.close();
            return null;
        }
    }

    public void addFavorite(String inputString, String toText) {
        System.out.println("EXEC addFavorite" + inputString + " " + toText);
        new AddFavoriteTask().execute(inputString, toText);//запускаем поток записи в БД
    }




    public Cursor getFavoriteItemsCursor() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor ret = db.rawQuery("SELECT * FROM " + DBContract.FavoritesEntry.TABLE_NAME + " ORDER BY _id DESC", null);
        return ret;
    }

    public Cursor getHistoryItemsCursor() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor ret = db.rawQuery("SELECT * FROM " + DBContract.HistoryEntry.TABLE_NAME + " ORDER BY _id DESC", null);
        return ret;
    }



    private class AddHistoryTask extends AsyncTask<String, Void, Void> {//запись в БД делаем в отдельном потоке
        protected Void doInBackground(String... putValues) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBContract.HistoryEntry.FROM_TEXT, putValues[0]);
            if (putValues[1].length() >= 50) {//Пишем сокращение перевода, все равно потом запросим
                values.put(DBContract.HistoryEntry.TO_TEXT, putValues[1].substring(0, 10));
            } else {
                values.put(DBContract.HistoryEntry.TO_TEXT, putValues[1]);}
            values.put(DBContract.HistoryEntry.DATE, Prefs.getCurrentDateString());
            db.insert(DBContract.HistoryEntry.TABLE_NAME, null, values);
            db.close();
            return null;
        }
    }

    public void addHistory(String inputString, String toText) {
        System.out.println("EXEC addHistory" + inputString + " " + toText);
        new AddHistoryTask().execute(inputString, toText);//запускаем поток записи в БД
    }

    public String getHistoryFromTextById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBContract.HistoryEntry.TABLE_NAME + " WHERE " + DBContract.HistoryEntry._ID + "=" + id, null);
        cursor.moveToFirst();
       return cursor.getString(cursor.getColumnIndexOrThrow(DBContract.HistoryEntry.FROM_TEXT));
    }

    public String[] getFavoritesFromTextById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBContract.FavoritesEntry.TABLE_NAME + " WHERE " + DBContract.FavoritesEntry._ID + "=" + id, null);
        cursor.moveToFirst();
        String [] ret = new String[2];
        ret[0] = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.FavoritesEntry.FROM_TEXT));
        ret[1] = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.FavoritesEntry.TO_TEXT));
        return ret;
    }

    public boolean isInHistory(String stringFromText) {
        ArrayList<String> toTextStrings = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECTALL_HISTORY,null);
        //cursor.moveToFirst();
        while (cursor.moveToNext()) {
            toTextStrings.add(cursor.getString(cursor.getColumnIndex(DBContract.HistoryEntry.FROM_TEXT)));
        }
        cursor.close();
        db.close();
        System.out.println(toTextStrings.size());
        for (String str : toTextStrings) {
            System.out.println("str " + str + " " + stringFromText);
            if(str.equals(stringFromText)) {
                System.out.println("true");
                return true;
            }
        }
        System.out.println("false");
        return false;
    };

    public void deleteHistoryById(long id) {
        SQLiteDatabase db = getWritableDatabase();
        System.out.println("DELETING " + id);
        db.execSQL("DELETE FROM " + DBContract.HistoryEntry.TABLE_NAME + " WHERE " + DBContract.HistoryEntry._ID + "=" + id);
        db.close();
    }

    public void deleteFavoriteById(long id) {
        SQLiteDatabase db = getWritableDatabase();
        System.out.println("DELETING " + id);
        db.execSQL("DELETE FROM " + DBContract.FavoritesEntry.TABLE_NAME + " WHERE " + DBContract.FavoritesEntry._ID + "=" + id);
        db.close();
    }


    public void delAllHistory() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_DELETE_HISTORY);
        db.close();
        //new DeleteAllHistoryTask().execute();//запускаем поток записи в БД
    }


    public void delAllFavorites() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_DELETE_FAVORITES);
        db.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("SQL_CREATE_HISTORY", SQL_CREATE_HISTORY);
        Log.i("SQL_CREATE_FAVORITES", SQL_CREATE_FAVORITES);
        db.execSQL(SQL_CREATE_HISTORY);
        db.execSQL(SQL_CREATE_FAVORITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    //Тут ничего пока не делаем. Иожет, позже, когда прийдет известность и мировое признание. Или будет хотя бы больше времени.
    }
}
