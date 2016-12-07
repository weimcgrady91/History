package com.weimc.history.views;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.weimc.history.R;
import com.weimc.history.domain.History;
import com.weimc.history.utils.FetchHistories;
import com.weimc.history.utils.LogUtil;

import java.io.IOException;

/**
 * Created by weiqun on 2016/12/7 0007.
 */

public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";
    private ImageView mPic;
    private TextView mContent;
    private FetchHistoryTask mFetchHistoryTask;
    private static final String ARG_ID = "id";

    public static HistoryFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = getArguments().getString(ARG_ID);
        mFetchHistoryTask = new FetchHistoryTask();
        String url = FetchHistories.buildUrl(FetchHistories.METHOD_HISTORY, id, null);
        Log.e(TAG, "url=" + url);
        mFetchHistoryTask.execute(url);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, null, false);
        mPic = (ImageView) view.findViewById(R.id.pic);
        mContent = (TextView) view.findViewById(R.id.content);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFetchHistoryTask != null) {
            mFetchHistoryTask.cancel(true);
        }
    }

    private class FetchHistoryTask extends AsyncTask<String, Void, History> {

        @Override
        protected History doInBackground(String... strings) {
            try {
                String result = FetchHistories.getStringUrl(strings[0]);
                LogUtil.e(TAG, "FetchHistoryTask result = " + result);
                History history = FetchHistories.parseHistory(result);
                return history;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(History history) {
            super.onPostExecute(history);
            if (history != null) {
                updateUI(history);
            }
        }
    }

    private void updateUI(History history) {
        if(isAdded()) {
            if(TextUtils.isEmpty(history.getPic())) {
                mPic.setImageDrawable(null);
            } else {
                Picasso.with(getActivity()).load(history.getPic()).into(mPic);
            }
            mContent.setText(history.getContent());
        }
    }

}
