package android.test.laz.ru.translateapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.test.laz.ru.db.DBContract;
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

    HistoryCursorAdapter histCursorAdapter;
    ListView histListView;

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
            System.out.println("VIEW " + view);
                 TextView fromTextView = (TextView) view.findViewById(R.id.histFromText);//не забываем указать view, а то на первом элементе свалится
            System.out.println("TEXT VIEW " + fromTextView);
            String fromTxt = cursor.getString(1);
                    if (fromTxt.length() > 11) {
                        fromTextView.setText(fromTxt.substring(0, 10));
                    } else {
                            fromTextView.setText(fromTxt);
                        }
                        TextView toTextView = (TextView) view.findViewById(R.id.histToText);
                        String toTxt = cursor.getString(2);
                        if (toTxt.length() > 11) {
                             toTextView.setText(toTxt.substring(0, 10));
                         } else {
                            toTextView.setText(toTxt);
                        TextView dateText = (TextView) view.findViewById(R.id.histDate);
                            dateText.setText(cursor.getString(3));
                    }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshListView();
    }



    public void refreshListView() {
        dbWorker.getHistoryItemsCursor();
        histCursorAdapter.changeCursor(dbWorker.getHistoryItemsCursor());
        histCursorAdapter.notifyDataSetChanged();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        dbWorker = DBWorker.getInstance(this);
        histListView = (ListView) findViewById(R.id.historyList);//Список избранного
        Cursor histCursor = dbWorker.getHistoryItemsCursor();
        histCursorAdapter = new HistoryCursorAdapter(this, histCursor);
        histListView.setAdapter(histCursorAdapter);//Сразу выводим то, что в БД



        Button delHistoryBtn = (Button) findViewById(R.id.delHist);
        delHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICK delete!!");
                DBWorker dbWorker = DBWorker.getInstance(HistoryActivity.this);
                dbWorker.delAllHistory();

            }
        });




        Button printHistoryBtn = (Button) findViewById(R.id.printHist);
        printHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICK print!!");
                dbWorker = DBWorker.getInstance(HistoryActivity.this);
                Cursor histCursor = dbWorker.getHistoryItemsCursor();
                histCursor.moveToFirst();
                while (histCursor.moveToNext()) {
                    String ret = "History ret " + histCursor.getString(histCursor.getColumnIndex(DBContract.HistoryEntry._ID)) + " " + histCursor.getString(histCursor.getColumnIndex(DBContract.HistoryEntry.FROM_TEXT)) + " " + histCursor.getString(histCursor.getColumnIndex(DBContract.HistoryEntry.TO_TEXT)) + " " + histCursor.getString(histCursor.getColumnIndex(DBContract.HistoryEntry.DATE));
                    Log.i("History ret", ret);
                }
                refreshListView();
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
