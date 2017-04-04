package android.test.laz.ru.db;

import android.provider.BaseColumns;

/**
 * Created by Dmitry Lazarev on 04.04.2017.
 */

public class DBContract {

    private DBContract(){}

    public static class HistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_NAME_FROM_TEXT = "fromText";
        public static final String COLUMN_NAME_TO_TEXT = "toText";
        public static final String COLUMN_NAME_DATE = "date";
    }

    public static class FavoritesEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_NAME_FROM_TEXT = "fromText";
        public static final String COLUMN_NAME_DATE = "date";
    }
}
