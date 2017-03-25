package android.test.laz.ru.helloapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

//sava

public class MainActivity extends AppCompatActivity {

    public static String LOG_TAG = "my_log";
    public static TextView fromText;
    public static TextView toText;
    public Button btn;
    public static final String URL = "https://translate.yandex.net/api/v1.5/tr.json";
    public static final String KEY = "?key=trnsl.1.1.20170315T111852Z.8e1ce17582bf567d.c36b8c3cf325da51fd6fa504d099559c62fa9102";
    public static final String TRANLSATE_URL = "/translate";
    public static final String GETLANGS_URL = "/getLangs";
    private HashMap<String,String> langDisplayNames;
    public HashSet<String> langPairsSet;
    private RequestQueue reqQueue;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_layout);

        ArrayList<String[]> langPairs = new ArrayList<String[]>();
        NetworkWorker.getInstance(this).getLangs(URL,GETLANGS_URL, KEY);


        langDisplayNames = new HashMap<String,String>();
        langPairsSet = new HashSet<String>();
        //new FillLangsTask().execute(URL,"/getLangs", KEY);
        fromText = (TextView) findViewById(R.id.fromText);
        toText = (TextView) findViewById(R.id.toText);
        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inStr = fromText.getText().toString();
                System.out.println("PRESSED");
                NetworkWorker.getInstance(MainActivity.this).translate(URL,TRANLSATE_URL, KEY,  inStr);
            }
        });


    }


    public void setSpinner(int spinId, boolean isFrom) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Language.fillFromSpinnerList(isFrom));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(spinId);
        spinner.setAdapter(adapter);
    }


}