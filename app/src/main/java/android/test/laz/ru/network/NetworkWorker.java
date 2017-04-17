package android.test.laz.ru.network;

import android.content.Context;
import android.test.laz.ru.translateapp.LangsPannel;
import android.test.laz.ru.translateapp.Prefs;
import android.test.laz.ru.translateapp.TranslateActivity;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * Created by z on 21.03.2017.
 */

public class NetworkWorker {
//синглтон, обслуживающий связь с сервером

    private RequestQueue rQueue;
    private static NetworkWorker netWorker;
    private Context context;


    private class CacheRequest extends Request<NetworkResponse> {

        /*Класс, расширяющий базовый класс Request библиотеки Volley с целью в ручную
        контролировать время кэширования запроса т.к. по умолчанию ожидается, что данные для кэширования отдаст сервер
         */
        private final Response.Listener<NetworkResponse> mListener;
        private final Response.ErrorListener mErrorListener;

        public CacheRequest(int method, String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.mListener = listener;
            this.mErrorListener = errorListener;
        }

        @Override//переопределяем метод обработки ответа от сервера, вручную указываем как и сколько времени хранить кэш
        protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
            Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
            if (cacheEntry == null) {
                cacheEntry = new Cache.Entry();
            }
            final long cacheHitButRefreshed = 60 * 60 * 1000; // кэш до его обновления используем час
            final long cacheExpired = 72 * 60 * 60 * 1000; // ..а через 72 часа он истекает полностью и больше не может быть использован
            long now = System.currentTimeMillis();
            final long softExpire = now + cacheHitButRefreshed;
            final long ttl = now + cacheExpired;
            cacheEntry.data = response.data;
            cacheEntry.softTtl = softExpire;
            cacheEntry.ttl = ttl;
            String headerValue;
            headerValue = response.headers.get("Date");
            if (headerValue != null) {
                cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
            }
            headerValue = response.headers.get("Last-Modified");
            if (headerValue != null) {
                cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
            }
            cacheEntry.responseHeaders = response.headers;
            return Response.success(response, cacheEntry);
        }

        @Override
        protected void deliverResponse(NetworkResponse response) {
            mListener.onResponse(response);
        }

        @Override
        protected VolleyError parseNetworkError(VolleyError volleyError) {
            return super.parseNetworkError(volleyError);
        }

        @Override
        public void deliverError(VolleyError error) {
            mErrorListener.onErrorResponse(error);
        }
    }


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
        String url = params[0] + params[1] + params[2];
        StringRequest sReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            //Запрашиваем языковые пары
            @Override
            public void onResponse(String response) {
                System.out.println("RESP " + response);
                JSONObject resJsonObj;
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
                if (Prefs.getInstance().getFromSpinnerItems().size() < 1 && Prefs.getInstance().getFromSpinnerItems().size() < 1) {//Если в спиннерах ничего нет, рефрешим их
                    Prefs.getInstance().generateSpinnerArray(LangsPannel.SpinSelect.FROM);
                    Prefs.getInstance().generateSpinnerArray(LangsPannel.SpinSelect.TO);
                    TranslateActivity ma = (TranslateActivity) context;
                    ma.langsPannel.redrawSpinner(LangsPannel.SpinSelect.FROM);
                    ma.langsPannel.redrawSpinner(LangsPannel.SpinSelect.TO);
                    Prefs.getInstance().makeJSONfromPrefs();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        rQueue.add(sReq);//запускаем реквест
    }


    public void detectLanguage(String detectString) {
        detectLang(Prefs.getInstance().URL, Prefs.getInstance().DETECT_URL, Prefs.getInstance().KEY, detectString);
    }

    public void detectLang(String... args) {


        String url = null;
        try {
            url = args[0] + args[1] + args[2] + "&text="+ URLEncoder.encode(args[3], "utf-8") + "&hint=ru,en,fr,de";//скорее всего это русский или английский
        } catch (UnsupportedEncodingException e) {
            url = args[0] + args[1] + args[2] + "&text="+ args[2] + "&hint=ru,en";
        }
        System.out.println("DETECTOR URL " + url);
        StringRequest detectReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resLang;
                JSONObject resJsonObj;
                System.out.println("DETECT RESPONSE " + response);
                try {
                    resJsonObj = new JSONObject(response);
                    System.out.println("DETECT JSON " + resJsonObj);
                    resLang = resJsonObj.getString("lang");
                    System.out.println("Detected " + resLang + " " + response);
                    if (resLang != null && resLang.length()> 1) {
                        TranslateActivity ta = (TranslateActivity) context;

                        if(resLang.equals(Prefs.getInstance().toLang))
                        {Prefs.getInstance().switchLangs();
                            ta.langsPannel.redrawSpinner(LangsPannel.SpinSelect.FROM);
                            ta.langsPannel.redrawSpinner(LangsPannel.SpinSelect.TO);}
                        else {
                        Prefs.getInstance().fromLang = resLang;
                            Prefs.getInstance().rearrangeSpinnerArray(resLang, LangsPannel.SpinSelect.FROM);
                        ta.langsPannel.redrawSpinner(LangsPannel.SpinSelect.FROM);}
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {//всегда надо переопределять в аргументах листнера
            }
        });
   rQueue.add(detectReq);//запускаем реквест


    }


    public void translateString(String translateTxt) {
        translate(Prefs.getInstance().URL,Prefs.getInstance().TRANLSATE_URL, Prefs.getInstance().KEY,  translateTxt, Prefs.getInstance().fromLang, Prefs.getInstance().toLang);
    }


    private void translate(String... params) {
        String url = null;
        try {//Кодируем URL
            url = params[0]+params[1]+params[2]+"&text="+URLEncoder.encode(params[3], "utf-8")+"&lang=" + params[4] + "-" + params[5];
       } catch (UnsupportedEncodingException e) {
            url = params[0]+params[1]+params[2]+"&text="+params[3]+"&lang=" + params[4] + "-" + params[5];
         e.printStackTrace();
        }
        Prefs.getInstance().lastTranslateString = params[3];//Припомним текст последнего запроса на случай восстановления приклада из паузы

        Log.i("URL ", url);

        CacheRequest sReq = new CacheRequest(0, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                System.out.println("RESPONSE TRANSLATE: " + response);
                JSONObject resJsonObj = null;
                String resultText = "";
                try {
                    final String jsonString = new String(response.data,HttpHeaderParser.parseCharset(response.headers));
                    resJsonObj = new JSONObject(jsonString);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
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
                TranslateActivity.toText.setText(resultText);//В итоге применяем полученный перевод к текстовому полю


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        rQueue.add(sReq);

    }



}