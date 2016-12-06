package com.weimc.history.views;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.weimc.history.R;

public class HistoryListActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return HistoryListFragment.newInstance();
    }
}
