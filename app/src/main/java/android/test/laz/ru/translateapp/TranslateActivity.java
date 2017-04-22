package android.test.laz.ru.translateapp;

import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.test.laz.ru.db.DBWorker;
import android.test.laz.ru.network.NetworkWorker;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
        setTitle(getResources().getString(R.string.translate_activity));
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

        /*Сохранять запись в историю будем по событию смены текста в поле ввода и
        * после ожидания в пару секунд, чтобы не плодить туда записи. Для тайм-аута  используем Хендлер*/
        final Handler saveToHistoryHandler = new Handler();
        final Runnable saveToHistoryRunnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("RUN!");
                String fromTxt = fromText.getText().toString();
                String toTxt = toText.getText().toString();
                DBWorker dbw = DBWorker.getInstance(TranslateActivity.this);
                if (!dbw.isInHistory(fromTxt))
                {System.out.println("STARTING PRINT HISTORY");
                    String dir = Prefs.getInstance().fromLang + "-" + Prefs.getInstance().toLang;
                    dbw.addHistory(fromTxt, toTxt, dir);
                     }
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
                //сохраняем в историю, после того как текст не менялся некторое время
                //если текст именился, обнуляем очередь заданий в хендлере и закидывем новое с текущим текстом
                saveToHistoryHandler.removeCallbacksAndMessages(null);
                //после того как язык сменили вручную, ждем некоторое время и не применяем автоопределение
                if (fromTxt != null && fromTxt.length() > 1 && !fromTxt.equals(getResources().getString(R.string.enterText))) {//Проверяем, что слова написаны целиком (в конце пробел) и после этого сохраняем их
                    saveToHistoryHandler.postDelayed(saveToHistoryRunnable, Prefs.getInstance().SAVE_HISTORY_TIMEOUT);//запускаем таймер, если за это время текст не изменится, сохраним его в историю
                    if (Prefs.getInstance().allowAutoswitch && Prefs.getInstance().isSwitchOnAuto()) {//если настройкаи разрешено, то делаем автодетект
                        NetworkWorker.getInstance(TranslateActivity.this).detectLanguage(fromTxt);
                    }
                }

            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if (extras.containsKey("favorites_extra")) {
                    String[] intentString = intent.getStringArrayExtra("favorites_extra");
                        fromText.setText(intentString[0]);
                        toText.setText(intentString[1]);
                        Prefs.getInstance().setLangsFromString(intentString[2]);
                    Prefs.getInstance().rearrangeSpinnerArray(Prefs.getInstance().fromLang, LangsPannel.SpinSelect.FROM);
                    Prefs.getInstance().rearrangeSpinnerArray(Prefs.getInstance().toLang, LangsPannel.SpinSelect.TO);
                    langsPannel.redrawSpinner(LangsPannel.SpinSelect.FROM);
                    langsPannel.redrawSpinner(LangsPannel.SpinSelect.TO);

                } else if (extras.containsKey("transfer_history")) {
                    String[] intentString = intent.getStringArrayExtra("transfer_history");
                    fromText.setText(intentString[0]);
                    Prefs.getInstance().setLangsFromString(intentString[1]);
                    Prefs.getInstance().rearrangeSpinnerArray(Prefs.getInstance().fromLang, LangsPannel.SpinSelect.FROM);
                    Prefs.getInstance().rearrangeSpinnerArray(Prefs.getInstance().toLang, LangsPannel.SpinSelect.TO);
                    langsPannel.redrawSpinner(LangsPannel.SpinSelect.FROM);
                    langsPannel.redrawSpinner(LangsPannel.SpinSelect.TO);
                }
            }
        }

        toText = (TextView) findViewById(R.id.toText);//Определяем поле перевода
        toText.setKeyListener(null);


        ImageButton translateButton = (ImageButton) findViewById(R.id.button_tr);
        translateButton.setEnabled(false);//Обязательно тут задизейблить, инчае не подтянет картинку селектора

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

public void addToFavorites(View view) {
    DBWorker dbw = DBWorker.getInstance(TranslateActivity.this);
    String dir = Prefs.getInstance().fromLang + "-" + Prefs.getInstance().toLang;
    dbw.addFavorite(fromText.getText().toString(), toText.getText().toString(), dir);
    Toast toast = Toast.makeText(this,R.string.toast_added_fav,Toast.LENGTH_SHORT);
    toast.show();
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