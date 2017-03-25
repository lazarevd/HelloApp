package android.test.laz.ru.helloapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import java.util.HashMap;
import java.util.HashSet;


public class MainActivity extends AppCompatActivity {

    public static String LOG_TAG = "my_log";
    public static TextView fromText;
    public static TextView toText;
    public Button btn;
    public Spinner fromSpinner;
    public Spinner toSpinner;
    public static final String URL = "https://translate.yandex.net/api/v1.5/tr.json";
    public static final String KEY = "?key=trnsl.1.1.20170315T111852Z.8e1ce17582bf567d.c36b8c3cf325da51fd6fa504d099559c62fa9102";
    public static final String TRANLSATE_URL = "/translate";
    public static final String GETLANGS_URL = "/getLangs";
    public static String fromLang = "en";
    public static String toLang = "ru";
    private HashMap<String,String> langDisplayNames;
    public HashSet<String> langPairsSet;
    private RequestQueue reqQueue;

    @Override
    protected void onStart() {
    super.onStart();
        NetworkWorker.getInstance(this).getLangs(URL,GETLANGS_URL, KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_layout);

        ArrayList<String[]> langPairs = new ArrayList<String[]>();
        fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        toSpinner = (Spinner) findViewById(R.id.toSpinner);
        langDisplayNames = new HashMap<String,String>();
        langPairsSet = new HashSet<String>();
        fromText = (TextView) findViewById(R.id.fromText);
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
                System.out.println("Text chenged");
                String inStr = fromText.getText().toString();
                NetworkWorker.getInstance(MainActivity.this).translate(URL,TRANLSATE_URL, KEY,  inStr, fromLang, toLang);
            }
        });

        toText = (TextView) findViewById(R.id.toText);
    }


    public void setSpinners(boolean isFrom) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Language.fillFromSpinnerList(isFrom));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);





        if(isFrom) {
            fromSpinner.setAdapter(adapter);
            fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    fromLang = fromSpinner.getSelectedItem().toString();
                    System.out.println("Spin from " + fromLang);
                    Log.i("LANG:from ", "");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            toSpinner.setAdapter(adapter);
            toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    toLang = toSpinner.getSelectedItem().toString();
                    System.out.println("Spin to " + fromLang);
                    Log.i("LANG:from ", "");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }


        fromSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setSpinners(true);
                }
                return false;
            }
        });



        toSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setSpinners(false);
                }
                return false;
            }
        });

    }


    public class NetworkChangeReceiver extends BroadcastReceiver {
        Context mContext;

        @Override
        public void onReceive(Context context, Intent intent) {
            mContext = context;
            String status = NetworkUtil.getConnectivityStatusString(context);

            Log.e("Receiver ", "" + status);

            if (status.equals("Not connected to Internet")) {
                Log.e("Receiver ", "not connction");// your code when internet lost


            } else {
                Log.e("Receiver ", "connected to internet");//your code when internet connection come back
            }

        }


}