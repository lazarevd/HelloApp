package android.test.laz.ru.translateapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.test.laz.ru.db.DBWorker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {

    private DBWorker dbWorker;

    private class HistoryCursorAdapter extends CursorAdapter {

        public HistoryCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView fromText = (TextView) findViewById(R.id.histFromText);
            try {
                fromText.setText(cursor.getString(1));
            } catch (NullPointerException npe) {
                Log.e("No in db", "");
            }

            TextView toText = (TextView) findViewById(R.id.histToText);
            try {
                toText.setText(cursor.getString(2));
            } catch (NullPointerException npe) {
                Log.e("No in db", "");
            }

            TextView dateText = (TextView) findViewById(R.id.histDate);
            try {
                dateText.setText(cursor.getString(3));
            } catch (NullPointerException npe) {
                Log.e("No in db", "");
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);

        dbWorker = DBWorker.getInstance(this);
        ListView histListView = (ListView) findViewById(R.id.historyList);//Список избранного
        Cursor histCursor = dbWorker.getHistoryItemsCursor();
        HistoryCursorAdapter histCursorAdapter = new HistoryCursorAdapter(this, histCursor);
        histListView.setAdapter(histCursorAdapter);//Сразу выводим то, что в БД


        Button delHistoryBtn = (Button) findViewById(R.id.delHist);
        delHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICK!!");
                DBWorker dbWorker = DBWorker.getInstance(HistoryActivity.this);
                dbWorker.delAllHistory();
            }
        });
    }

    public void startFavorites(View view) {
        Intent historyIntent = new Intent(this, FavoritesActivity.class);
        startActivity(historyIntent);
    }

    public void startTranslate(View view) {
        Intent historyIntent = new Intent(this, TranslateActivity.class);
        startActivity(historyIntent);
    }
}
