package com.weimc.history.views;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.weimc.history.R;
import com.weimc.history.domain.History;
import com.weimc.history.utils.FetchHistories;
import com.weimc.history.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by weiqun on 2016/12/6 0006.
 */

public class HistoryListFragment extends Fragment {
    private static final String TAG = "HistoryListFragment";
    private List<History> mHistories;
    private RecyclerView mRecyclerView;
    private HistoryAdapter mAdapter;
    private FetchHistoriesTask mFetchHistoriesTask;

    public static HistoryListFragment newInstance() {
        Bundle args = new Bundle();
        HistoryListFragment fragment = new HistoryListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fetchHistories(Calendar.getInstance());
    }

    private void fetchHistories(Calendar calendar) {
        mFetchHistoriesTask = new FetchHistoriesTask();
        String url = FetchHistories.buildUrl(FetchHistories.METHOD_HISTORIES, null, calendar);
        Log.e(TAG, "url=" + url);
        mFetchHistoriesTask.execute(url);
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
        mFetchHistoriesTask.cancel(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_history_list, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.queryHint));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LogUtil.e(TAG, "onQueryTextSubmit query =" + query);
                searchView.clearFocus();  //可以收起键盘
                searchView.onActionViewCollapsed();
                parseQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void parseQuery(String query) {
        String[] arr = query.split("/");
        if (arr.length > 1) {
            LogUtil.e(TAG, "a=" + arr[0] + ",b=" + arr[1]);
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(arr[0]));
                calendar.set(Calendar.MONTH,Integer.parseInt(arr[1])-1);
                fetchHistories(calendar);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(),R.string.queryError,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI() {
        if (isAdded()) {
            if (mAdapter == null) {
                mAdapter = new HistoryAdapter(mHistories);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mRecyclerView.scrollToPosition(0);
                mAdapter.setHistories(mHistories);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class FetchHistoriesTask extends AsyncTask<String, Void, List<History>> {
        @Override
        protected List<History> doInBackground(String... voids) {
            List<History> histories = new ArrayList<>();
            try {
                String result = FetchHistories.getStringUrl(voids[0]);
                LogUtil.e(TAG, "FetchHistoriesTask result = " + result);
                histories.addAll(FetchHistories.parseHistories(result));
                return histories;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, " FetchHistoriesTask result wrong ! and e = ", e);
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

    private class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitle;
        private TextView mLunar;
        private TextView mDes;
        private ImageView mPic;
        private History mHistory;
        private TextView mContent;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mLunar = (TextView) itemView.findViewById(R.id.lunar);
            mDes = (TextView) itemView.findViewById(R.id.des);
            mPic = (ImageView) itemView.findViewById(R.id.pic);
            mContent = (TextView) itemView.findViewById(R.id.content);
            mContent.setOnClickListener(this);
        }

        public void bindHolder(History history) {
            mHistory = history;
            String picUrl = history.getPic();
            if (TextUtils.isEmpty(picUrl)) {
                mPic.setImageDrawable(null);
            } else {
                Picasso.with(getActivity()).load(picUrl).into(mPic);
            }
            mTitle.setText(history.getTitle());
            mLunar.setText(history.getLunar());
            mDes.setText(history.getDes());
        }

        @Override
        public void onClick(View view) {
            Intent intent = HistoryActivity.newIntent(getActivity(), mHistory.getId());
            startWithTransition(getActivity(), intent, mPic);
        }
    }

    public static void startWithTransition(Activity activity, Intent intent, View sourceView) {
        ViewCompat.setTransitionName(sourceView, "image");
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sourceView, "image");
        activity.startActivity(intent, options.toBundle());
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
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_history_list, parent, false);
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
