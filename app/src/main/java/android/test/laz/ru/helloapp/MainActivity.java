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
    private final String URL = "https://translate.yandex.net/api/v1.5/tr.json";
    private final String KEY = "?key=trnsl.1.1.20170315T111852Z.8e1ce17582bf567d.c36b8c3cf325da51fd6fa504d099559c62fa9102";
    private HashMap<String,String> langDisplayNames;
    public HashSet<String> langPairsSet;
    private RequestQueue reqQueue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_layout);

        NetworkWorker.getInstance(this.getApplicationContext()).getLangs(URL,"/getLangs", KEY);


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
                NetworkWorker.getInstance(MainActivity.this).translate(URL,"/translate", KEY,  inStr);
            }
        });

        ArrayList<String> fromSpinnerList = new ArrayList<String>();
        fromSpinnerList.add("Rus");
        fromSpinnerList.add("Engl");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, fromSpinnerList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.fromSpinner);
        sItems.setAdapter(adapter);

    }


    private HashMap<String,String> fillDisplayRusNames() {
        HashMap<String,String> ret = new HashMap<String,String>();
        ret.put("ru", "Русский");
        ret.put("en", "Английский");
        ret.put("fr", "Французский");
        ret.put("de", "Немецкий");

        return ret;
    }

}