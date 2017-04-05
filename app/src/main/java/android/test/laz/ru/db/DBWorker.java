package android.test.laz.ru.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Dmitry Lazarev on 04.04.2017.
 */

public class DBWorker extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TranslateDB.db";

    private static final String SQL_CREATE_HISTORY =
            "CREATE TABLE " + DBContract.HistoryEntry.TABLE_NAME + " (" +
                    DBContract.HistoryEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.HistoryEntry.TO_TEXT + " TEXT," +
                    DBContract.HistoryEntry.DATE + " TEXT)";

    private static final String SQL_CREATE_FAVORITES =
            "CREATE TABLE " + DBContract.FavoritesEntry.TABLE_NAME + " (" +
                    DBContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.FavoritesEntry.FROM_TEXT + " TEXT," +
                    DBContract.FavoritesEntry.DATE + " TEXT)";

    private static final String SQL_DELETE_HISTORY =
            "DROP TABLE IF EXISTS " + DBContract.HistoryEntry.TABLE_NAME;

    private static final String SQL_DELETE_FAVORITES =
            "DROP TABLE IF EXISTS " + DBContract.HistoryEntry.TABLE_NAME;

    public DBWorker(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
