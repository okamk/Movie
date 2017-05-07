package jp.okamk.android.movie;

import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video.VideoColumns;

public class Const {
    public static final String EXTRA_FOLDER_PATH = "FOLDER_PATH";
    // public static final String EXTRA_FOLDER_NAME = "FOLDER_NAME";
    // public static final String EXTRA_FILE_PATH = "FILE_PATH";
    public static final int REQUEST_CODE_FILELIST = 1;
    public static final int REQUEST_CODE_MOVIE = 2;

    public static final String SORT_ORDER_DATA = MediaColumns.DATA;
    public static final String SORT_ORDER_DISPLAY_NAME = MediaColumns.DISPLAY_NAME;
    public static final String SORT_ORDER_SIZE = MediaColumns.SIZE;
    public static final String SORT_ORDER_DATE_ADDED = MediaColumns.DATE_ADDED;
    public static final String SORT_ORDER_DATE_MODIFIED = MediaColumns.DATE_MODIFIED;
    public static final String SORT_ORDER_TITLE = MediaColumns.TITLE;
    public static final String SORT_ORDER_DURATION = VideoColumns.DURATION;

    public static final String SORT_ORDER_ASC = "ASC";
    public static final String SORT_ORDER_DESC = "DESC";

    public static String SORT_ORDER_KEY = "sort_order_key";
    public static String USE_RESUME_KEY = "use_resume_key";
    public static String SHOW_TOAST_KEY = "show_toast_key";

    public static boolean isValidOrder(String order1, String order2) {
        if ((order1.equals(SORT_ORDER_DATA)
                || order1.equals(SORT_ORDER_DISPLAY_NAME)
                || order1.equals(SORT_ORDER_SIZE)
                || order1.equals(SORT_ORDER_DATE_ADDED)
                || order1.equals(SORT_ORDER_DATE_MODIFIED)
                || order1.equals(SORT_ORDER_TITLE) || order1
                .equals(SORT_ORDER_DURATION))
                && (order2.equals(SORT_ORDER_ASC) || order2
                .equals(SORT_ORDER_DESC))) {
            return true;
        }
        return false;
    }
}
