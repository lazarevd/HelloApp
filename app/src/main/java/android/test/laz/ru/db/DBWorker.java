package android.test.laz.ru.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.test.laz.ru.translateapp.Prefs;
import android.util.Log;

/**
 * Created by Dmitry Lazarev on 04.04.2017.
 */

public class DBWorker extends SQLiteOpenHelper {

    //Синглтон для работы с SQLite БД
    private static DBWorker dbWorkerInstance;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TranslateDB.db";

    private DBWorker(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
                    DBContract.HistoryEntry.TO_TEXT + " TEXT," +
                    DBContract.HistoryEntry.DATE + " TEXT)";

    private final String SQL_CREATE_FAVORITES =
            "CREATE TABLE " + DBContract.FavoritesEntry.TABLE_NAME + " (" +
                    DBContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.FavoritesEntry.FROM_TEXT + " TEXT," +
                    DBContract.FavoritesEntry.DATE + " TEXT)";

    private final String SQL_DELETE_HISTORY =
            "DELETE FROM " + DBContract.HistoryEntry.TABLE_NAME;

    private final String SQL_DELETE_FAVORITES =
            "DELETE FROM " + DBContract.HistoryEntry.TABLE_NAME;




    private class AddFavoriteTask extends AsyncTask<String, Void, Void> {//запись в БД делаем в отдельном потоке
        protected Void doInBackground(String... putValues) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBContract.FavoritesEntry.FROM_TEXT, putValues[0]);
            values.put(DBContract.FavoritesEntry.DATE, Prefs.getCurrentDateString());
            db.insert(DBContract.FavoritesEntry.TABLE_NAME, null, values);
            db.close();
            return null;
        }
    }

    public void addFavorite(String inputString) {
        new AddFavoriteTask().execute(inputString);//запускаем поток записи в БД
    }


    public Cursor getFavoriteItemsCursor() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor ret = db.rawQuery("SELECT * FROM favorites ORDER BY _id", null);
        return ret;
    }



    private class AddHistoryTask extends AsyncTask<String, Void, Void> {//запись в БД делаем в отдельном потоке
        protected Void doInBackground(String... putValues) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBContract.HistoryEntry.FROM_TEXT, putValues[0]);
            values.put(DBContract.HistoryEntry.TO_TEXT, putValues[1]);
            values.put(DBContract.HistoryEntry.DATE, Prefs.getCurrentDateString());
            db.insert(DBContract.HistoryEntry.TABLE_NAME, null, values);
            db.close();
            return null;
        }
    }

    public void addHistory(String inputString, String toText) {
        new AddHistoryTask().execute(inputString, toText);//запускаем поток записи в БД
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
