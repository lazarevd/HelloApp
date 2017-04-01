package android.test.laz.ru.helloapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
    }

    public void startTranslate(View view) {
        Intent historyIntent = new Intent(this, MainActivity.class);
        startActivity(historyIntent);
    }
}
