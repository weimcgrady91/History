package com.weimc.history.views;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class HistoryActivity extends SingleFragmentActivity {
    private static final String EXTRA_HISTORY_ID =
            "com.weimc.history.history_id";

    public static Intent newIntent(Context context, String id) {
        Intent intent = new Intent(context, HistoryActivity.class);
        intent.putExtra(EXTRA_HISTORY_ID, id);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        String id = getIntent().getStringExtra(EXTRA_HISTORY_ID);
        return HistoryFragment.newInstance(id);
    }
}
