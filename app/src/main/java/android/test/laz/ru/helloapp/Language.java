package android.test.laz.ru.helloapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Created by z on 25.03.2017.
 */

public class Language {

    private static ArrayList<String[]> langPairs = new ArrayList<String[]>();


    public static synchronized ArrayList<String[]> getLangArray() {
        return new ArrayList<>(langPairs);
    }

    public static synchronized void setLangArray(ArrayList<String[]> langArr) {
        langPairs = langArr;
    }

    public static ArrayList<String> fillFromSpinnerList(boolean isFrom) {
        ArrayList<String[]> arrSrgs = getLangArray();
        LinkedHashSet<String> ret = new LinkedHashSet<String>();
        if (isFrom) {
            for (String[] stArr : arrSrgs) {//Заполняем переводимый язык?
                ret.add(stArr[0]);
            }
        } else {
            for (String[] stArr : arrSrgs) {
                    ret.add(stArr[1]);
            }
        }

        for(String st : ret) {
            Log.i("FILL LIST ", isFrom + " " + st);
        }
        return new ArrayList<String>(ret);
    }

}
