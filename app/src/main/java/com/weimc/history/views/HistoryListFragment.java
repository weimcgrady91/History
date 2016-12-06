package com.weimc.history.views;

import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weiqun on 2016/12/6 0006.
 */

public class HistoryListFragment extends Fragment {
    private static final String TAG = "HistoryListFragment";
    private List<History> mHistories;
    private RecyclerView mRecyclerView;
    private HistoryAdapter mAdapter;
    private FetchHistoryTask mFetchHistoryTask;

    public static HistoryListFragment newInstance() {
        Bundle args = new Bundle();
        HistoryListFragment fragment = new HistoryListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFetchHistoryTask = new FetchHistoryTask();
        String url = Uri.parse("http://api.juheapi.com/japi/toh")
                .buildUpon()
                .appendQueryParameter("v", "1.0")
                .appendQueryParameter("month", "10")
                .appendQueryParameter("day", "1")
                .appendQueryParameter("key", FetchHistories.KEY)
                .build().toString();
        Log.e(TAG,"url=" + url);
        mFetchHistoryTask.execute(url);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_list, null, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFetchHistoryTask.cancel(true);
    }

    private void updateUI() {
        if (isAdded()) {
            if (mAdapter == null) {
                mAdapter = new HistoryAdapter(mHistories);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setHistories(mHistories);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class FetchHistoryTask extends AsyncTask<String, Void, List<History>> {
        @Override
        protected List<History> doInBackground(String... voids) {
            List<History> histories = new ArrayList<>();
            try {
                String result = FetchHistories.getStringUrl(voids[0]);
                LogUtil.e(TAG, "FetchHistoryTask result = " + result);
                histories.addAll(FetchHistories.parseHistories(result));
                return histories;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, " FetchHistoryTask result wrong ! and e = ", e);
            }
            return histories;
        }

        @Override
        protected void onPostExecute(List<History> histories) {
            mHistories = histories;
            updateUI();
            super.onPostExecute(histories);
        }
    }

    private class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mLunar;
        private TextView mDes;
        private ImageView mPic;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mLunar = (TextView) itemView.findViewById(R.id.lunar);
            mDes = (TextView) itemView.findViewById(R.id.des);
            mPic = (ImageView) itemView.findViewById(R.id.pic);
        }

        public void bindHolder(History history) {
            String picUrl = history.getPic();
            if(TextUtils.isEmpty(picUrl)) {
                mPic.setImageDrawable(null);
            } else {
                Picasso.with(getActivity()).load(picUrl).into(mPic);
            }
            mTitle.setText(history.getTitle());
            mLunar.setText(history.getLunar());
            mDes.setText(history.getDes());
        }
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
        private List<History> mHistories;

        public HistoryAdapter(List<History> histories) {
            mHistories = histories;
        }

        public void setHistories(List<History> histories) {
            mHistories = histories;
        }

        @Override
        public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_history_list,parent,false);
            HistoryViewHolder viewHolder = new HistoryViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(HistoryViewHolder holder, int position) {
            holder.bindHolder(mHistories.get(position));
        }

        @Override
        public int getItemCount() {
            return mHistories.size();
        }
    }
}
