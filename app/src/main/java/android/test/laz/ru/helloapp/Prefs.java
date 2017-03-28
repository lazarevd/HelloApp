package android.test.laz.ru.helloapp;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by z on 26.03.2017.
 */

public class Prefs {

    private transient static Prefs instance;
    private  HashMap<String,String> langDisplayNames = new HashMap<>();
    private  ArrayList<String[]> langPairsList = new ArrayList<>();
    private final String SAVE_FILE_NAME = "prefsJSON.json";
    private ArrayList<SpinnerItem> fromSpinnerItems = new ArrayList<>();
    private ArrayList<SpinnerItem> toSpinnerItems = new ArrayList<>();
    public static String fromLang = "en";
    public static String toLang = "ru";

    private Prefs() {
    }




    public static synchronized Prefs getInstance() {
        if(instance == null) {
            instance = new Prefs();
        }

        return  instance;
    }


    public synchronized void makeJSONfromPrefs(Context context) {
        Gson gson = new Gson();
        String filename = SAVE_FILE_NAME;
        String string = gson.toJson(instance).toString();
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void makePrefsfromJson(Context context) {
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

    public void rearrangeSpinnerArray(String inpLang, LangsPannel.SpinSelect spinSel) {

        ArrayList<SpinnerItem> tmpSpinItems;
        if(spinSel.equals(LangsPannel.SpinSelect.FROM)) {
            tmpSpinItems = new ArrayList<>(fromSpinnerItems);
        } else {
            tmpSpinItems = new ArrayList<>(toSpinnerItems);
        }
            int tmpSiIndx = 0;
            for(int i=0;i < tmpSpinItems.size();i++) {
                if (tmpSpinItems.get(i).getLangShortName().equals(inpLang)) {
                    tmpSiIndx=i;
                }
            }
            SpinnerItem tmpSi = tmpSpinItems.get(tmpSiIndx);
                tmpSpinItems.remove(tmpSiIndx);
                tmpSpinItems.add(0,tmpSi);


        if(spinSel.equals(LangsPannel.SpinSelect.FROM)) {
            fromSpinnerItems = tmpSpinItems;
        } else {
            toSpinnerItems = tmpSpinItems;
        }

            for(SpinnerItem it : fromSpinnerItems) {
                Log.i("Rearrange" + spinSel.toString() , it.getDisplayName());
            }
    }



    public void generateSpinnerArray(Context context, LangsPannel.SpinSelect spinSel) {
        ArrayList<SpinnerItem> siList = new ArrayList<SpinnerItem>();
        HashSet<String> siSet = new HashSet<>();
        if (spinSel.equals(LangsPannel.SpinSelect.FROM)) {
            for(String[] st : langPairsList) {
                siSet.add(st[0]);
            }
        } else {
            for(String[] st : langPairsList) {
                siSet.add(st[1]);
            }
        }

        for(String st : siSet) {
            try {
                siList.add(new SpinnerItem(st, getStringResourceByName(context, st)));
            } catch (Resources.NotFoundException nfex) {
                System.out.println("not found for" + st);
                siList.add(new SpinnerItem(st, st));
            }
        }


        if(spinSel.equals(LangsPannel.SpinSelect.FROM)) {fromSpinnerItems = siList;}
        else {toSpinnerItems = siList;}

        for (SpinnerItem si : siList) {
            System.out.println(si.getLangShortName() + " " + si.getDisplayName());
        }

    }


    public ArrayList<SpinnerItem> getFromSpinnerItems(){return new ArrayList<>(fromSpinnerItems);}

    public ArrayList<SpinnerItem> getToSpinnerItems() {
        return new ArrayList<>(toSpinnerItems);
    }

    private String getStringResourceByName(Context context, String aString) {
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

    public  ArrayList<String[]> getLangPairsList(){
        return new ArrayList<String[]>(instance.langPairsList);
    }

    public void setLangPairsList(ArrayList<String[]> arrList) {
        instance.langPairsList = arrList;
    }



}
