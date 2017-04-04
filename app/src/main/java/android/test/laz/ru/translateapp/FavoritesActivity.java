package android.test.laz.ru.translateapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.test.laz.ru.db.DBContract;
import android.test.laz.ru.db.DBWorker;
import android.view.View;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_layout);
        DBWorker dbWorker = new DBWorker(this);
        SQLiteDatabase db = dbWorker.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.HistoryEntry.COLUMN_NAME_FROM_TEXT, "go");
        values.put(DBContract.HistoryEntry.COLUMN_NAME_TO_TEXT, "идти");
        values.put(DBContract.HistoryEntry.COLUMN_NAME_DATE, "2017-04-05 0:21");

        long newRowId = db.insert(DBContract.HistoryEntry.TABLE_NAME, null, values);


        SQLiteDatabase dbread = db.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                DBContract.HistoryEntry._ID,
                DBContract.HistoryEntry.COLUMN_NAME_FROM_TEXT,
                DBContract.HistoryEntry.COLUMN_NAME_TO_TEXT
        };
    }

    public void startTranslate(View view) {
        Intent historyIntent = new Intent(this, TranslateActivity.class);
        startActivity(historyIntent);
    }

    public  void startHistory(View view) {
        Intent historyIntent = new Intent(this, HistoryActivity.class);
        startActivity(historyIntent);
    }
}
