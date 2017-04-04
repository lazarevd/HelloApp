package android.test.laz.ru.translateapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.HashMap;

/**
 * Created by laz on 28.03.17.
 */

public class LangsPannel {
    private HashMap<String, Spinner> langSpinners = new HashMap<>();
    private Spinner fromSpinner;
    private Spinner toSpinner;
    public Context context;

    public enum SpinSelect {FROM,TO};


    public LangsPannel(final Context context) {
        this.context = context;
        fromSpinner = (Spinner) ((Activity) context).findViewById(R.id.fromSpinner);
        toSpinner = (Spinner) ((Activity) context).findViewById(R.id.toSpinner);

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerItem si = (SpinnerItem) fromSpinner.getSelectedItem();
                Prefs.getInstance().fromLang = si.getLangShortName();
                Prefs.getInstance().rearrangeSpinnerArray(Prefs.getInstance().fromLang, SpinSelect.FROM); //перераспределяем элементы в спиннере, двигаем вверх выбранный
                NetworkWorker.getInstance(context).translateString();
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
                Prefs.getInstance().rearrangeSpinnerArray(Prefs.getInstance().toLang, SpinSelect.TO);
                NetworkWorker.getInstance(context).translateString();
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
                    redrawSpinner(SpinSelect.FROM);//тут применяем к спиннеру перераспределенные элементы, т.е. список формируется в момент клика по нему, иначе проваливаемся в бесконечный цикл
                }
                return false;
            }
        });

        toSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    redrawSpinner(SpinSelect.TO);
                }
                return false;
            }
        });
        langSpinners.put("fromSpinner", fromSpinner);
        langSpinners.put("toSpinner", toSpinner);


        //Кнопка смены языка
        Button switchBtn = (Button) ((Activity) context).findViewById(R.id.switchBtn);
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs.getInstance().switchLangs();
                redrawSpinner(SpinSelect.FROM);
                redrawSpinner(SpinSelect.TO);
            }
        });
    }

    public void redrawSpinner(SpinSelect spinSel) {
        if(spinSel.equals(SpinSelect.FROM)) {//Затем в зависимости от спиннера применяем элементы к спиннеру
            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, Prefs.getInstance().getFromSpinnerItems());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fromSpinner.setAdapter(adapter);
        } else {
            ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, Prefs.getInstance().getToSpinnerItems());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            toSpinner.setAdapter(adapter);
        }

        /*
        for(int i=0;i<Prefs.getInstance().getFromSpinnerItems().size() && i<Prefs.getInstance().getToSpinnerItems().size();i++) {
            Log.i("redrawSpinner", Prefs.getInstance().getFromSpinnerItems().get(i).getDisplayName() + " " + Prefs.getInstance().getToSpinnerItems().get(i).getDisplayName());
        }
        */
    }


}

