package jp.okamk.android.movie;

import android.database.Cursor;

public class CursorHolder {
    static String TAG = "Holder";
    static Cursor mCursor = null;
    static int mPosition = -1;

    public static void setCursor(Cursor cursor) {
        mCursor = cursor;
        mPosition = mCursor.getPosition();
    }

    public static Cursor getCursor() {
        mCursor.moveToPosition(mPosition);
        return mCursor;
    }
}
