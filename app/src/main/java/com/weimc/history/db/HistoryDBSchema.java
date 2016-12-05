package com.weimc.history.db;

/**
 * Created by Administrator on 2016/12/6.
 */

public class HistoryDBSchema {
    public static final int DB_VERSION = 1;

    public static final class HistoryTable {
        public static final String TABLE_NAME = "t_history";
        public static final class Cols {
            public static final String day = "day";
            public static final String des = "des";
            public static final String id = "id";
            public static final String lunar = "lunar";
            public static final String month = "month";
            public static final String pic = "pic";
            public static final String title = "title";
            public static final String year = "year";
            public static final String content = "content";
        }
    }
}
