package android.test.laz.ru.helloapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;


/**
 * Created by z on 21.03.2017.
 */

public class NetworkWorker {



    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String resultJson = "";
    private RequestQueue rQueue;
    private Cache cache;
    private static NetworkWorker netWorker;
    private Context context;


    private NetworkWorker(Context cnxt) {
        this.context = cnxt;
        rQueue = getRequestQueue();
    }


    public static synchronized NetworkWorker getInstance(Context cnxt) {
        if (netWorker == null) {
            netWorker = new NetworkWorker(cnxt);
        }
        return netWorker;
    }


    public RequestQueue getRequestQueue() {
        if (rQueue == null) {
            rQueue = Volley.newRequestQueue(this.context.getApplicationContext());
        }
        return rQueue;
    }


    public <T> void addToRequestQueue(Request<T> req) {
        rQueue.add(req);
    }


    public void getLangs(String... params) {
        // получаем данные с внешнего ресурса
        String url = params[0] + params[1] + params[2];
        StringRequest sReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("RESP " + response);
                JSONObject resJsonObj = null;
                ArrayList<String[]> arrList = new ArrayList<>();
                try {
                    resJsonObj = new JSONObject(response);
                    JSONArray resJsonArr = resJsonObj.getJSONArray("dirs");
                    for (int i=0;i<resJsonArr.length();i++) {
                        arrList.add(resJsonArr.getString(i).split("-"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Prefs.getInstance().setLangPairsList(arrList);
                Prefs.getInstance().generateSpinnerArray(context, LangsPannel.SpinSelect.FROM);
                Prefs.getInstance().generateSpinnerArray(context, LangsPannel.SpinSelect.TO);
                Prefs.getInstance().makeJSONfromPrefs(context);
                MainActivity ma = (MainActivity) context;
                ma.langsPannel.redrawSpinner(LangsPannel.SpinSelect.FROM);
                ma.langsPannel.redrawSpinner(LangsPannel.SpinSelect.TO);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        rQueue.add(sReq);
    }


    public void translateString() {
        translate(Prefs.getInstance().URL,Prefs.getInstance().TRANLSATE_URL, Prefs.getInstance().KEY,  MainActivity.fromText.getText().toString(), Prefs.getInstance().fromLang, Prefs.getInstance().toLang);
    }


    private void translate(String... params) {
        //String url = params[0]+params[1]+params[2]+"&text="+params[3]+"&lang=en-ru";
        String url = params[0]+params[1]+params[2]+"&text="+params[3]+"&lang=" + params[4] + "-" + params[5];
        url=url.replace(" ", "%20");
        Log.i("URL ", url);
        StringRequest sReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("RESPONSE TRANSLATE: " + response);

                JSONObject resJsonObj = null;
                String resultText = "";

                try {
                    resJsonObj = new JSONObject(response);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (resJsonObj!=null) {
                    try {
                        JSONArray resJsonArr = resJsonObj.getJSONArray("text");
                        for (int i=0;i<resJsonArr.length();i++) {
                            resultText = resultText + " " + resJsonArr.getString(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("LOGG:", resultText);
                }
                MainActivity.toText.setText(resultText);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        rQueue.add(sReq);

    }



}