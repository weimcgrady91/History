package com.weimc.history.utils;

import android.util.Log;

import com.weimc.history.domain.History;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
