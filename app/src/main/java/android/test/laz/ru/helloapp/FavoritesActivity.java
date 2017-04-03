package android.test.laz.ru.helloapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites_layout);
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
