package android.test.laz.ru.helloapp;

import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;


public class TranslateActivity extends AppCompatActivity {

    public static String LOG_TAG = "my_log";
    public static TextView fromText;
    public static TextView toText;
    public LangsPannel langsPannel;
    private Prefs prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_layout);
        prefs = Prefs.getInstance();
        prefs.makePrefsfromJson(this);


        try {
            File httpCacheDir = new File(this.getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i("CACHE", "HTTP response cache installation failed:" + e);
        }



        langsPannel = new LangsPannel(this);
        fromText = (TextView) findViewById(R.id.fromText);
        if(Prefs.getInstance().getLangPairsList().size() <1) {
            NetworkWorker.getInstance(this).getLangs(Prefs.getInstance().URL, Prefs.getInstance().GETLANGS_URL, Prefs.getInstance().KEY);
        } else {
            langsPannel.redrawSpinner(LangsPannel.SpinSelect.FROM);
            langsPannel.redrawSpinner(LangsPannel.SpinSelect.TO);
        }
        fromText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromText.getText().toString().equals(getResources().getString(R.string.enterText)))
                {
                    Log.i("ERASE!!!! ", (getResources().getString(R.string.enterText)) + "      " + fromText.getText().toString());
                    fromText.setText("");
                } else {
                    Log.i("NOERASE!!!! ", (getResources().getString(R.string.enterText)) + "      " + fromText.getText().toString());
                }
            }
        });


        fromText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("Text changed");
                NetworkWorker.getInstance(TranslateActivity.this).translateString();
                //prefs.makeJSONfromPrefs();
            }
        });

        toText = (TextView) findViewById(R.id.toText);
    }


@Override
    public void onPause() {
    super.onPause();
    Prefs.getInstance().makeJSONfromPrefs(this);
}



    @Override
    protected void onStop() {
    super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

public  void startHistory(View view) {
    Intent historyIntent = new Intent(this, HistoryActivity.class);
    startActivity(historyIntent);
}
    public void startFavorites(View view) {
        Intent historyIntent = new Intent(this, HistoryActivity.class);
        startActivity(historyIntent);
    }

}