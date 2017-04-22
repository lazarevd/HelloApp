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
                 int cutWords = 20;
            TextView fromTextView = (TextView) view.findViewById(R.id.histFromText);//не забываем указать view, а то на первом элементе свалится
            String fromTxt = cursor.getString(cursor.getColumnIndex(DBContract.HistoryEntry.FROM_TEXT));
                    if (fromTxt.length() > cutWords) {
                        fromTextView.setText(fromTxt.substring(0, cutWords-1) + "...(" + fromTxt.length()+")");
                    } else {
                            fromTextView.setText(fromTxt);
                        }
                        TextView toTextView = (TextView) view.findViewById(R.id.histToText);
                        String toTxt = cursor.getString(cursor.getColumnIndex(DBContract.HistoryEntry.TO_TEXT));
                        if (toTxt.length() > cutWords) {
                             toTextView.setText(toTxt.substring(0, cutWords-1) + "...");
                         } else {
                            toTextView.setText(toTxt);
                        TextView dateText = (TextView) view.findViewById(R.id.histDate);
                            dateText.setText(cursor.getString(cursor.getColumnIndex(DBContract.HistoryEntry.DATE)));

                            TextView dirText = (TextView) view.findViewById(R.id.histDir);
                            dirText.setText(cursor.getString(cursor.getColumnIndex(DBContract.HistoryEntry.DIR)));
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

    //Создаем меню в экшнбаре
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//Создаем меню в тайтлбаре
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return super.onCreateOptionsMenu(menu);

    }
    public void onDeleteAllHistory(MenuItem item) {
        dbWorker.delAllHistory();
        refreshListView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.history_activity));
        setContentView(R.layout.history_layout);

        dbWorker = DBWorker.getInstance(this);
        histListView = (ListView) findViewById(R.id.historyList);//Список избранного

        //при создании регистриуем контекстное меню.
        //потом View - параметр этого метода передается в метод активити onCreateContextMenu(View view)
        //это позволяет делать разные меню для разных вью

        registerForContextMenu(histListView);



        histListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("ITEM CLICK " + position + " " + id);
                startTranslateAfterHistory(dbWorker.getHistoryFromTextById(id));
            }

        });

        Cursor histCursor = dbWorker.getHistoryItemsCursor();
        histCursorAdapter = new HistoryCursorAdapter(this, histCursor);
        histListView.setAdapter(histCursorAdapter);//Сразу выводим то, что в БД

        ImageButton historyButton = (ImageButton) findViewById(R.id.button_hs);
        historyButton.setEnabled(false);//Обязательно тут задизейблить, инчае не подтянет картинку селектора

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
        dbWorker.deleteHistoryById(id);
        refreshListView();
        return true;
    }


    public void startTranslateAfterHistory(String[] fromText) {
        Intent historyIntent = new Intent(this, TranslateActivity.class);
        historyIntent.putExtra("transfer_history", fromText);
        startActivity(historyIntent);
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
