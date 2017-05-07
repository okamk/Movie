package jp.okamk.android.movie.util;

import jp.okamk.android.movie.Const;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video.Media;
import android.provider.MediaStore.Video.VideoColumns;

public class FileList {

    Context mContext = null;
    static final String TAG = "FileList";
    static final Uri mUri = Media.EXTERNAL_CONTENT_URI;
    static final String[] mProjection = new String[]{MediaColumns._ID,
            MediaColumns.DATA, MediaColumns.DISPLAY_NAME, MediaColumns.SIZE,
            MediaColumns.DATE_ADDED, MediaColumns.DATE_MODIFIED,
            MediaColumns.TITLE, VideoColumns.DURATION};
    String mSortOrder1 = Const.SORT_ORDER_DATA;
    String mSortOrder2 = "ASC";

    public FileList(Context context) {
        mContext = context;
    }

    public Cursor getFiles(String path) {
        String selection = "REPLACE(" + MediaColumns.DATA + ",'" + path
                + "','') = " + MediaColumns.DISPLAY_NAME;
        if (path == null || path == "") {
            selection = null;
        }

        Cursor cursor = mContext.getContentResolver().query(mUri, mProjection,
                selection, null, mSortOrder1 + " " + mSortOrder2);
        return cursor;
    }

    // TODO もう少しきれいに書く
    public boolean setSortOrder(String order1, String order2) {
        if (Const.isValidOrder(order1, order2)) {
            mSortOrder1 = order1;
            mSortOrder2 = order2;
            return true;
        }
        return false;
    }
}
