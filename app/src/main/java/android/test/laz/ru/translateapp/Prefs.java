package android.test.laz.ru.translateapp;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.test.laz.ru.network.NetworkWorker;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by z on 26.03.2017.
 */

public class Prefs {

    private transient static Prefs instance;
    public transient static boolean switchOnAuto = true;//каждый раз сбрасывается при выходе из приложения
    private transient Handler activateAutoDetect;
    private transient Runnable activateAutoRunnable;



    private  HashMap<String,String> langDisplayNames = new HashMap<>();
    private  ArrayList<String[]> langPairsList = new ArrayList<>();
    private final String SAVE_FILE_NAME = "prefsJSON.json";
    private ArrayList<SpinnerItem> fromSpinnerItems = new ArrayList<>();
    private ArrayList<SpinnerItem> toSpinnerItems = new ArrayList<>();
    public  String fromLang = "en";
    public  String toLang = "ru";
    public static final String URL = "https://translate.yandex.net/api/v1.5/tr.json";
    public static final String KEY = "?key=trnsl.1.1.20170315T111852Z.8e1ce17582bf567d.c36b8c3cf325da51fd6fa504d099559c62fa9102";
    public static final String TRANLSATE_URL = "/translate";
    public static final String GETLANGS_URL = "/getLangs";
    public static final String DETECT_URL = "/detect";
    public static String lastTranslateString = "";
    public static int SWITCH_ON_AUTO_TIMEOUT = 10000;
    public static int SAVE_HISTORY_TIMEOUT = 3000;
    private static Context context = null;
    //настройки приложения
    public static boolean allowAutoswitch = true; //настройка





    private Prefs() {
    }




    public static synchronized Prefs getInstance() {
        if(instance == null) {
            instance = new Prefs();
        }

        return  instance;
    }

    public void init(Context context) {
        this.context = context;
        NetworkWorker.getInstance(this.context).getLangs(URL, GETLANGS_URL, KEY);
        generateSpinnerArray(LangsPannel.SpinSelect.FROM);
        generateSpinnerArray(LangsPannel.SpinSelect.TO);
    }



    public void setLangsFromString(String input) {

        if (input != null && input.length() == 5) {
            String[] getStr = input.split("-");
            fromLang = getStr[0];
            toLang = getStr[1];
        }

    }


    public synchronized void makeJSONfromPrefs() {
        Gson gson = new Gson();
        String filename = SAVE_FILE_NAME;
        String jsonString = gson.toJson(instance).toString();
        Log.i("Save to JSON ", jsonString);
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(jsonString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void makePrefsfromJsonFile() {
        Gson gson = new Gson();
        String jsonRes  = "";
        try {
            FileInputStream inStream = context.openFileInput(SAVE_FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            String jsonStr;
            while ((jsonStr = reader.readLine()) != null) {
                jsonRes = jsonRes + jsonStr;
            }

            instance = gson.fromJson(jsonRes, Prefs.class);
            Log.i("PREFS_from JSON ", jsonRes);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void switchLangs() {
        rearrangeSpinnerArray(toLang, LangsPannel.SpinSelect.FROM);
        rearrangeSpinnerArray(fromLang, LangsPannel.SpinSelect.TO);
    }

    public void rearrangeSpinnerArray(String inpLang, LangsPannel.SpinSelect spinSel) {
        ArrayList<SpinnerItem> tmpSpinItems;
        if(spinSel.equals(LangsPannel.SpinSelect.FROM)) {
            tmpSpinItems = new ArrayList<>(fromSpinnerItems);
        } else {
            tmpSpinItems = new ArrayList<>(toSpinnerItems);
        }
            int tmpSiIndex = 0;
            for(int i=0;i < tmpSpinItems.size();i++) {
                if (tmpSpinItems.get(i).getLangShortName().equals(inpLang)) {
                    tmpSiIndex=i;
                }
            }
            SpinnerItem tmpSi = tmpSpinItems.get(tmpSiIndex);
                tmpSpinItems.remove(tmpSiIndex);
                tmpSpinItems.add(0,tmpSi);


        if(spinSel.equals(LangsPannel.SpinSelect.FROM)) {
            fromSpinnerItems = tmpSpinItems;
        } else {
            toSpinnerItems = tmpSpinItems;
        }

        for(int i=0;i<fromSpinnerItems.size() && i<toSpinnerItems.size();i++) {
                Log.i("Rear", fromSpinnerItems.get(i).getDisplayName() + " " + toSpinnerItems.get(i).getDisplayName());
        }
    }



    public void generateSpinnerArray(LangsPannel.SpinSelect spinSel) {//Создаем массивы для заполнения спиннеров
        ArrayList<SpinnerItem> siList = new ArrayList<>();
        HashSet<String> siSet = new HashSet<>();
        if (spinSel.equals(LangsPannel.SpinSelect.FROM)) {
            //Заполняем сеты для спиннеров из массива языковых пар.
            for(String[] st : langPairsList) {
                siSet.add(st[0]);
                //Азиатские языки при запросе сервиса getLangs почему-то не выводятся, добавим их в ручнную, ведь они есть в описании и перевод то работает
                siSet.add("zh");
                siSet.add("ja");
                siSet.add("ko");

            }
        } else {
            for(String[] st : langPairsList) {
                siSet.add(st[1]);
                siSet.add("zh");
                siSet.add("ja");
                siSet.add("ko");
            }
        }
        for(String st : siSet) {//для каждого сокращенного имени создаем объект, содержащий и полное имя на локальном языке
            try {
                siList.add(new SpinnerItem(st, getStringResourceByName(st)));
            } catch (Resources.NotFoundException nfex) {
                System.out.println("not found for" + st);
                siList.add(new SpinnerItem(st, st));
            }
        }
        if(spinSel.equals(LangsPannel.SpinSelect.FROM)) {fromSpinnerItems = siList;}
        else {toSpinnerItems = siList;}
/*
        for (SpinnerItem si : siList) {
            System.out.println(si.getLangShortName() + " " + si.getDisplayName());
        }
        */

    }

    public static String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy KK:mm");
        return sdf.format(calendar.getTime());
    }


    public ArrayList<SpinnerItem> getFromSpinnerItems(){return new ArrayList<>(fromSpinnerItems);}

    public ArrayList<SpinnerItem> getToSpinnerItems() {
        return new ArrayList<>(toSpinnerItems);
    }

    private String getStringResourceByName(String aString) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(aString, "string", packageName);
        return context.getString(resId);
    }


    public  HashMap<String,String> getLangDisplayNames(){
        return new HashMap<>(instance.langDisplayNames);
    }

    public void setLangDisplayNames(HashMap<String,String > langMap) {
        instance.langDisplayNames = langMap;
    }

    public synchronized  ArrayList<String[]> getLangPairsList(){
        return new ArrayList<String[]>(instance.langPairsList);
    }

    public synchronized void  setLangPairsList(ArrayList<String[]> arrList) {
        instance.langPairsList = arrList;
    }



    public synchronized void setPauseForAutodetect () {//пауза дял автоопределени языка
        System.out.println("SETTING PAUSE");
        Prefs.getInstance().setSwitchOnAuto(false);
        activateAutoDetect = new Handler();
        activateAutoRunnable = new Runnable() {
            @Override
            public void run() {
                setSwitchOnAuto(true);
                System.out.println("switched auto " + true);
            }
        };
        System.out.println("activateAutoDetect " + activateAutoDetect + " " + "activateAutoRunnable" + activateAutoRunnable);
            activateAutoDetect.removeCallbacksAndMessages(null);
            activateAutoDetect.postDelayed(activateAutoRunnable, SWITCH_ON_AUTO_TIMEOUT);
    }


    public synchronized static boolean isSwitchOnAuto() {
        return switchOnAuto;
    }

    public synchronized static void setSwitchOnAuto(boolean switchOnAuto) {
        Prefs.switchOnAuto = switchOnAuto;
    }


}
