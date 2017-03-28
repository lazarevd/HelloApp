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
        if(Prefs.getInstance().getLangPairsList().size() <1) {
            NetworkWorker.getInstance(this).getLangs(Prefs.getInstance().URL, Prefs.getInstance().GETLANGS_URL, Prefs.getInstance().KEY);
        } else {
            langsPannel.redrawSpinner(LangsPannel.SpinSelect.FROM);
            langsPannel.redrawSpinner(LangsPannel.SpinSelect.TO);
        }
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
                NetworkWorker.getInstance(MainActivity.this).translate(Prefs.getInstance().URL,Prefs.getInstance().TRANLSATE_URL, Prefs.getInstance().KEY,  inStr, Prefs.getInstance().fromLang, Prefs.getInstance().toLang);
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