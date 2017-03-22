package android.test.laz.ru.helloapp;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.net.HttpURLConnection;


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


    public void getLangs(String... params)

    {
        // получаем данные с внешнего ресурса
        String url = params[0] + params[1] + params[2];
        StringRequest sReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("RESP " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        rQueue.add(sReq);
    }


    public void translate(String... params) {
        String url = params[0]+params[1]+params[2]+"&text="+params[3]+"&lang=en-ru";
        System.out.println("TRANSLATE");
        StringRequest sReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("RESPONSE TRANSLATE " + response);

                JSONObject resJsonObj = null;
                String resultText = "";

                try {
                    resJsonObj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (resJsonObj!=null) {
                    try {
                        resultText = resJsonObj.getString("text");
                        resultText = resultText.substring(2,resultText.length()-2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
