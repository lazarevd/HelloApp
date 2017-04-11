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
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FavoritesActivity extends AppCompatActivity {

    private DBWorker dbWorker;



    private class FavoritesCursorAdapter extends CursorAdapter {

        public FavoritesCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.favorites_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView fromText = (TextView) findViewById(R.id.favFromText);
            try {
                fromText.setText(cursor.getString(1));
            } catch (NullPointerException npe) {
                Log.e("No in db", "");
            }
            TextView dateText = (TextView) findViewById(R.id.favDate);
            try {
            dateText.setText(cursor.getString(2));
        } catch (NullPointerException npe) {
            Log.e("No in db", "");
        }

        }
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_layout);

        dbWorker = DBWorker.getInstance(this);
        ListView favListView = (ListView) findViewById(R.id.favoritesList);//Список избранного

        favListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("ITEM CLICK");
            }
        });

        Cursor favCursor = dbWorker.getFavoriteItemsCursor();
        FavoritesCursorAdapter favCursorAdapter = new FavoritesCursorAdapter(this, favCursor);
        favListView.setAdapter(favCursorAdapter);//Сразу выводим то, что в БД
    }

    @Override
    public void onResume() {
        super.onResume();
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
