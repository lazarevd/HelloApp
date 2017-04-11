package android.test.laz.ru.translateapp;

import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.test.laz.ru.db.DBWorker;
import android.text.Editable;
import android.text.InputType;
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
    private int SAVE_HISTORY_TIMEOUT = 3000;
    private Prefs prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_layout);
        prefs = Prefs.getInstance();
        prefs.init(this);
        prefs.makePrefsfromJsonFile();
        langsPannel = new LangsPannel(this);
        if(Prefs.getInstance().getLangPairsList().size() <1) {
        } else {
            langsPannel.redrawSpinner(LangsPannel.SpinSelect.FROM);
            langsPannel.redrawSpinner(LangsPannel.SpinSelect.TO);
        }
        try {
            File httpCacheDir = new File(this.getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i("CACHE", "HTTP response cache installation failed:" + e);
        }





        fromText = (TextView) findViewById(R.id.fromText);//Определяем поле исходного текста


        fromText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromText.getText().toString().equals(getResources().getString(R.string.enterText)))
                {
                    fromText.setText("");
                }
            }
        });

        /*Сохранять запись в историю будем по событию смены текста в пле ввода и
        * после ожидания в пару секунд, чтобы не плодить туда записи. Для тайм-аута используем Хендлер*/
        final Handler saveToHistoryHandler = new Handler();
        final Runnable saveToHistoryRunnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("RUN!");
                String fromTxt = fromText.getText().toString();
                String toTxt = toText.getText().toString();
                DBWorker dbw = DBWorker.getInstance(TranslateActivity.this);
                dbw.addHistory(fromTxt, toTxt);
            }
        };


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
                String fromTxt = fromText.getText().toString();
                if (fromTxt != null && fromTxt.length() > 0) {
                    NetworkWorker.getInstance(TranslateActivity.this).translateString(fromTxt);
                }
                saveToHistoryHandler.removeCallbacksAndMessages(null);//Всякий раз как текст меняется, очищаем очередь в хендлере


                //System.out.println("MATCH \n\n\n " + m.find() + ", Text: " + s.toString() +".");
                if (fromTxt != null && fromTxt.length() > 1 && !fromTxt.equals(getResources().getString(R.string.enterText))) {//Проверяем, что слова написаны целиком (в конце пробел) и после этого сохраняем их
                    saveToHistoryHandler.postDelayed(saveToHistoryRunnable, SAVE_HISTORY_TIMEOUT);//запускаем таймер, если за это время текст не изменится, сохраним его в историю
                }
            }
        });



        toText = (TextView) findViewById(R.id.toText);//Определяем поле перевода
        toText.setInputType(InputType.TYPE_NULL);

    }


@Override
    public void onPause() {
        super.onPause();
        Prefs.getInstance().makeJSONfromPrefs();
    }

    @Override
    public void onResume() {
        super.onResume();
        Prefs.getInstance().makePrefsfromJsonFile();
        String lastTranslated = Prefs.getInstance().lastTranslateString;
        if(lastTranslated.length() > 0) {//Проверяем, чтобы урл был хоть какой-то длинны
            fromText.setText(lastTranslated);
            NetworkWorker.getInstance(this).translateString(lastTranslated);
        }
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
        Intent historyIntent = new Intent(this, FavoritesActivity.class);
        startActivity(historyIntent);
    }

}