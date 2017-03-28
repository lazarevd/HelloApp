package android.test.laz.ru.helloapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static String LOG_TAG = "my_log";
    public static TextView fromText;
    public static TextView toText;
    public LangsPannel langsPannel;
    public Button btn;
    public static final String URL = "https://translate.yandex.net/api/v1.5/tr.json";
    public static final String KEY = "?key=trnsl.1.1.20170315T111852Z.8e1ce17582bf567d.c36b8c3cf325da51fd6fa504d099559c62fa9102";
    public static final String TRANLSATE_URL = "/translate";
    public static final String GETLANGS_URL = "/getLangs";
    private RequestQueue reqQueue;
    private Prefs prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_layout);
        prefs = Prefs.getInstance();
        prefs.makePrefsfromJson(this);
        langsPannel = new LangsPannel(this);
        fromText = (TextView) findViewById(R.id.fromText);
        NetworkWorker.getInstance(this).getLangs(URL,GETLANGS_URL, KEY);
        fromText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fromText.getText().equals(getResources().getString(R.string.enterText)));
                //fromText.setText("");
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
                String inStr = fromText.getText().toString();
                NetworkWorker.getInstance(MainActivity.this).translate(URL,TRANLSATE_URL, KEY,  inStr, Prefs.getInstance().fromLang, Prefs.getInstance().toLang);
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


}