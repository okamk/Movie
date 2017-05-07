package jp.okamk.android.movie;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class ClearResumeDialog extends DialogPreference {
    String TAG = "ClearResumeDialog";
    Context mContext = null;

    public ClearResumeDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        if (which == Dialog.BUTTON_POSITIVE) {
            try {
                /* int deleted = */
                mContext.getContentResolver().delete(
                        ResumeProvider.CONTENT_URI, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
