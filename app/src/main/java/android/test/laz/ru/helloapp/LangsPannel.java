package android.test.laz.ru.helloapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by laz on 28.03.17.
 */

public class LangsPannel {
    private HashMap<String, Spinner> langSpinners = new HashMap<>();
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private Context context;

    public enum SpinSelect {FROM,TO};


    public LangsPannel(Context context) {
        this.context = context;
        fromSpinner = (Spinner) ((Activity) context).findViewById(R.id.fromSpinner);
        toSpinner = (Spinner) ((Activity) context).findViewById(R.id.toSpinner);

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerItem si = (SpinnerItem) fromSpinner.getSelectedItem();
                Prefs.getInstance().fromLang = si.getLangShortName();
                Log.i("onItemSelected fromSpi ", si.getDisplayName() + " " + si.getLangShortName() + " pos: " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerItem si = (SpinnerItem) toSpinner.getSelectedItem();
                Prefs.getInstance().toLang = si.getLangShortName();
                Log.i("onItemSelected toSpi", si.getDisplayName() + " " + si.getLangShortName() + " pos: " + position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        fromSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Prefs.getInstance().rearrangeSpinnerArray(Prefs.getInstance().fromLang, SpinSelect.FROM);
                    redrawSpinner(SpinSelect.FROM);
                }
                return false;
            }
        });

        toSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Prefs.getInstance().rearrangeSpinnerArray(Prefs.getInstance().toLang, SpinSelect.TO);
                    redrawSpinner(SpinSelect.TO);
                }
                return false;
            }
        });
        langSpinners.put("fromSpinner", fromSpinner);
        langSpinners.put("toSpinner", toSpinner);
    }

    public void redrawSpinner(SpinSelect spinSel) {
        if(spinSel.equals(SpinSelect.FROM)) {
            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, Prefs.getInstance().getFromSpinnerItems());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fromSpinner.setAdapter(adapter);
        } else {
            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, Prefs.getInstance().getToSpinnerItems());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            toSpinner.setAdapter(adapter);
        }
    }


}

