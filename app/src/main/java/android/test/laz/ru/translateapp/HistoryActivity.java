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
            TextView fromText = (TextView) findViewById(R.id.histFromText);
                System.out.println("MOVING " + cursor.getCount() + " " + cursor.getPosition());
                try {
                    String fromTxt = "f" + cursor.getString(1);
                    //if (fromTxt.length() > 11) {
                   //     fromText.setText(fromTxt.substring(0, 10));
                   // } else {
                    if (fromText !=null) {
                        fromText.setText(fromTxt);
                    }
                   // }
                } catch (NullPointerException npe) {

                    Log.e("fromText", npe.toString());
                    npe.printStackTrace();
                } catch (Exception e) {
                    Log.e("No in db", e.toString());
                }


                TextView toText = (TextView) findViewById(R.id.histToText);
                try {
                    String toTxt = "t" + cursor.getString(2);
                    //if (toTxt.length() > 11) {
                   //     toText.setText(toTxt.substring(0, 10));
                   // } else {
                    if(toText != null) {
                        toText.setText(toTxt);
                    }
                  //  }
                } catch (NullPointerException npe) {
                    Log.e("toText", npe.toString());
                    npe.printStackTrace();
                } catch (Exception e) {
                    Log.e("No in db", e.toString());
                }

                TextView dateText = (TextView) findViewById(R.id.histDate);
                try {
                    if (dateText != null) {
                        dateText.setText(cursor.getString(3));
                    }
                } catch (NullPointerException npe) {
                    Log.e("dateText", npe.toString());
                    npe.printStackTrace();
                } catch (Exception e) {
                    Log.e("No in db", e.toString());
                }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshListView();
    }



    public void refreshListView() {
        histCursorAdapter.notifyDataSetChanged();
    }

    public void assignAdapter() {
        dbWorker = DBWorker.getInstance(this);
        histListView = (ListView) findViewById(R.id.historyList);//Список избранного
        Cursor histCursor = dbWorker.getHistoryItemsCursor();
        histCursorAdapter = new HistoryCursorAdapter(this, histCursor);
        histListView.setAdapter(histCursorAdapter);//Сразу выводим то, что в БД
        histCursorAdapter.changeCursor(histCursor);
        //histCursorAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        assignAdapter();



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
