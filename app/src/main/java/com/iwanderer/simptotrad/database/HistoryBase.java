package com.iwanderer.simptotrad.database;

/**
 * Created by Leslie on 2016/6/9.
 */
public class HistoryBase {
    public HistoryBase() {
    }

    public static class Pair {
        public static final String TABLE_NAME = "history";
        public static final String SEARCH_WORD = "searchWord";
        public static final String RESULT = "result";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + Pair.TABLE_NAME + " (" +
                    Pair.SEARCH_WORD + TEXT_TYPE + COMMA_SEP +
                    Pair.RESULT + TEXT_TYPE +
            " )";

    public static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + Pair.TABLE_NAME;

}
