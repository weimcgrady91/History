package com.weimc.history.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.weimc.history.domain.History;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by weiqun on 2016/12/6 0006.
 */

public class FetchHistories {
    public static final String KEY = "8a629d4c05172390f5864cf8e15b62e6";
    public static final String TAG = "FetchHistories";
    private static final String ERROR_CODE = "error_code";
    private static final String RESULT = "result";
    private static final String _ID = "_id";
    private static final String TITLE = "title";
    private static final String PIC = "pic";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String DES = "des";
    private static final String LUNAR = "lunar";
    private static final String CONTENT = "content";

    public static final String METHOD_HISTORIES = "method_histories";
    public static final String METHOD_HISTORY = "method_history";

    public static String buildUrl(String method, String id, Calendar calendar) {
        String url = null;
        if (METHOD_HISTORIES.equals(method)) {
            url = Uri.parse("http://api.juheapi.com/japi/toh")
                    .buildUpon()
                    .appendQueryParameter("v", "1.0")
                    .appendQueryParameter("month", calendar.get(Calendar.MONTH) + 1 + "")
                    .appendQueryParameter("day", calendar.get(Calendar.DAY_OF_MONTH) + "")
                    .appendQueryParameter("key", FetchHistories.KEY)
                    .build().toString();
        } else if (METHOD_HISTORY.equals(method)) {
            url = Uri.parse("http://api.juheapi.com/japi/tohdet")
                    .buildUpon()
                    .appendQueryParameter("v", "1.0")
                    .appendQueryParameter("id", id)
                    .appendQueryParameter("key", FetchHistories.KEY)
                    .build().toString();
        }
        return url;
    }

    public static byte[] getByteUrl(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            byte[] buff = new byte[1024];
            int len = 0;
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LogUtil.e(TAG, " connection responseCode = " + connection.getResponseCode());
                return null;
            }
            InputStream is = connection.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = is.read(buff)) > 0) {
                bos.write(buff, 0, len);
            }
            bos.close();
            return bos.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public static String getStringUrl(String urlSpec) throws IOException {
        return new String(getByteUrl(urlSpec));
    }

    public static History parseHistory(String result) {
        History history = new History();
        try {
            JSONObject resultObject = new JSONObject(result);
            if (0 == resultObject.getInt(ERROR_CODE)) {
                JSONArray jsonArray = resultObject.getJSONArray(RESULT);
                if (jsonArray.length() > 0) {
                    JSONObject object = jsonArray.getJSONObject(0);
                    history.setId(object.getString(_ID));
                    history.setTitle(object.getString(TITLE));
                    history.setPic(object.getString(PIC));
                    history.setYear(object.getString(YEAR));
                    history.setMonth(object.getString(MONTH));
                    history.setDay(object.getString(DAY));
                    history.setDes(object.getString(DES));
                    history.setLunar(object.getString(LUNAR));
                    history.setContent(object.getString(CONTENT));
                    return history;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static List<History> parseHistories(String result) {
        List<History> histories = new ArrayList<>();
        try {
            JSONObject resultObject = new JSONObject(result);
            if (0 == resultObject.getInt(ERROR_CODE)) {
                JSONArray jsonArray = resultObject.getJSONArray(RESULT);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    History history = new History();
                    history.setId(object.getString(_ID));
                    history.setTitle(object.getString(TITLE));
                    history.setPic(object.getString(PIC));
                    history.setYear(object.getString(YEAR));
                    history.setMonth(object.getString(MONTH));
                    history.setDay(object.getString(DAY));
                    history.setDes(object.getString(DES));
                    history.setLunar(object.getString(LUNAR));
                    histories.add(history);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "parseHistories is wrong and e = ", e);
        } finally {
            return histories;
        }

    }
}
