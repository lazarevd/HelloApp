package android.test.laz.ru.translateapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.test.laz.ru.db.DBContract;
import android.test.laz.ru.db.DBWorker;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class FavoritesActivity extends AppCompatActivity {

    private DBWorker dbWorker;
    private ListView favListView;
    private FavoritesCursorAdapter favCursorAdapter;




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
            int cutWords = 20;
            TextView fromTextView = (TextView) view.findViewById(R.id.favFromText);
            String fromTxt = cursor.getString(cursor.getColumnIndex(DBContract.FavoritesEntry.FROM_TEXT));
            if (fromTxt.length() > cutWords) {
                fromTextView.setText(fromTxt.substring(0, cutWords-1) + "...(" + fromTxt.length()+")");
            } else {
                fromTextView.setText(fromTxt);
            }
            TextView toTextView = (TextView) view.findViewById(R.id.favToText);
            String toTxt = cursor.getString(cursor.getColumnIndex(DBContract.FavoritesEntry.TO_TEXT));
            if (toTxt.length() > cutWords) {
                toTextView.setText(toTxt.substring(0, cutWords-1) + "...");
            } else {
                toTextView.setText(toTxt);
            }
            TextView dateTextView = (TextView) view.findViewById(R.id.favDate);
            dateTextView.setText(cursor.getString(cursor.getColumnIndex(DBContract.FavoritesEntry.DATE)));

            TextView dirTextView = (TextView) view.findViewById(R.id.favDir);
            dirTextView.setText(cursor.getString(cursor.getColumnIndex(DBContract.FavoritesEntry.DIR)));

        }
    }


    //Создаем меню в экшнбаре
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.favorites_activity));
        setContentView(R.layout.favorites_layout);

        dbWorker = DBWorker.getInstance(this);
        favListView = (ListView) findViewById(R.id.favoritesList);//Список избранного

        registerForContextMenu(favListView);

        favListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("ITEM CLICK " + position + " " + id);
                startTranslateAfterFavorites(dbWorker.getFavoritesFromTextById(id));
            }

        });



        Cursor favCursor = dbWorker.getFavoriteItemsCursor();
        favCursorAdapter = new FavoritesCursorAdapter(this, favCursor);
        favListView.setAdapter(favCursorAdapter);//Сразу выводим то, что в БД

        ImageButton favButton = (ImageButton) findViewById(R.id.button_fv);
        favButton.setEnabled(false);//Обязательно тут задизейблить, инчае не подтянет картинку селектора
    }

    public void onDeleteAllFavorites(MenuItem item) {
        dbWorker.delAllFavorites();
        refreshListView();
    }


    //переопределяем контекстное меню для активити, вью на котором он вызвано передается как параметр
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        System.out.println("Create context " + v);
        menu.add(Menu.NONE, 1, Menu.NONE, getResources().getString(R.string.deleteMenuItem));
    }

    public boolean onContextItemSelected(MenuItem item) {
        long id = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).id;
        dbWorker.deleteFavoriteById(id);
        refreshListView();
        return true;
    }

    public void startTranslateAfterFavorites(String[] inputTexts) {
        Intent historyIntent = new Intent(this, TranslateActivity.class);
        historyIntent.putExtra("favorites_extra", inputTexts);
        startActivity(historyIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshListView();
    }

    public void refreshListView() {
        dbWorker.getHistoryItemsCursor();
        favCursorAdapter.changeCursor(dbWorker.getFavoriteItemsCursor());
        favCursorAdapter.notifyDataSetChanged();
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
