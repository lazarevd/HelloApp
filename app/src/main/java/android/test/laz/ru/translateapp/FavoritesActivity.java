package android.test.laz.ru.translateapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.test.laz.ru.db.DBWorker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    private DBWorker dbWorker;

    private class FavoritesItem {
        public FavoritesItem (int id, String fromText, String date) {
            this.id = id;
            this.fromText = fromText;
            this.date = date;
        }
        public int id;
        public String fromText = "";
        public String date = "";
    }


    public class FavoritesCursorAdapter extends CursorAdapter {

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


private ArrayList<FavoritesItem> getFavoriteItemsList() {
    ArrayList<FavoritesItem> ret = new ArrayList<FavoritesItem>();
    SQLiteDatabase db = dbWorker.getReadableDatabase();

    Cursor cc = db.rawQuery("SELECT * FROM favorites ORDER BY _id", null);

    if (cc.moveToFirst()) {
        while ( !cc.isAfterLast() ) {
            ret.add(new FavoritesItem(cc.getInt(0), cc.getString(1), cc.getString(2)));
            Log.i("TABLE", cc.getString(0));
            cc.moveToNext();
        }
    }
    db.close();
    return ret;
}




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_layout);
        dbWorker = DBWorker.getInstance(this);

        ListView favListView = (ListView) findViewById(R.id.favoritesList);//Список избранного
        Cursor favCursor = dbWorker.getFavoriteItemsCursor();
        FavoritesCursorAdapter favCursorAdapter = new FavoritesCursorAdapter(this, favCursor);
        favListView.setAdapter(favCursorAdapter);//Сразу выводим то, что в БД
        favCursorAdapter.getCursor().close();
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
