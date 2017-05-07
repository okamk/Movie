package jp.okamk.android.movie;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MovieActivity extends Activity implements OnCompletionListener {

    static final String TAG = "MOVIE";
    private VideoView mVideoView;
    Cursor mCursor = null;
    static final int DIALOG_RESUME = 0;
    int resumePos = 0;
    SharedPreferences sharedPreferences = null;
    boolean use_resume = false;
    boolean show_toast = false;
    boolean noneedBackupResume = false;
    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Movie");
        setContentView(R.layout.main);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        mVideoView = (VideoView) findViewById(R.id.VideoView01);
        MediaController mMediaController = new MediaController(this);
        mMediaController.setPrevNextListeners(next, prev);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setOnCompletionListener(this);
        mCursor = CursorHolder.getCursor();
        if (mCursor == null) {
            noneedBackupResume = true;
            finish();
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        use_resume = sharedPreferences.getBoolean(Const.USE_RESUME_KEY, false);
        show_toast = sharedPreferences.getBoolean(Const.SHOW_TOAST_KEY, false);

        resumePos = 0;
        if (use_resume) {
            resumePos = RestoreResumeData(getPath());
        }
        if (resumePos > 0) {
            showDialog(DIALOG_RESUME);
        } else {
            playback(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!noneedBackupResume) {
            BackupResumeData(getPath(), mVideoView.getCurrentPosition());
        }
        mVideoView.setKeepScreenOn(false);
    }

    OnClickListener next = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            next();
        }
    };

    OnClickListener prev = new OnClickListener() {
        @Override
        public void onClick(View v) {
            prev();
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        RemoveResumeData(getPath());
        if (mCursor.isLast()) {
            noneedBackupResume = true;
            finish();
        } else {
            mCursor.moveToNext();
            int pos = 0;
            if (use_resume) {
                pos = RestoreResumeData(getPath());
            }
            playback(pos);
        }
    }

    void next() {
        Runnable r = new Runnable() {
            public void run() {
                BackupResumeData(getPath(), mVideoView.getCurrentPosition());
                if (mCursor.isLast()) {
                    mCursor.moveToFirst();
                } else {
                    mCursor.moveToNext();
                }

                int pos = 0;
                if (use_resume) {
                    pos = RestoreResumeData(getPath());
                }
                playback(pos);
            }
        };
        handler.post(r);
    }

    void prev() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                int currentPos = mVideoView.getCurrentPosition();
                // 今のファイル、位置をバックアップ
                BackupResumeData(getPath(), currentPos);

                // 再生位置が3秒未満以内なら前のファイル、それ以上ならそのファイルの先頭に戻すだけ
                if (currentPos < 3000) {
                    // 前のファイルへ
                    if (mCursor.isFirst()) {
                        mCursor.moveToLast();
                    } else {
                        mCursor.moveToPrevious();
                    }
                    // ポジションをリストアして再生
                    int pos = 0;
                    if (use_resume) {
                        pos = RestoreResumeData(getPath());
                    }
                    playback(pos);

                } else {
                    // 今のファイルのまま、先頭から再生
                    playback(0);
                }
            }
        };
        handler.post(r);
    }

    void pause() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                }
            }
        };
        handler.post(r);
    }

    void play() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (!mVideoView.isPlaying()) {
                    mVideoView.start();
                }
            }
        };
        handler.post(r);
    }

    void firstforward() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (mVideoView.isPlaying()) {
                    int currentPos = mVideoView.getCurrentPosition();
                    currentPos += 15000;
                    mVideoView.seekTo(currentPos);
                }
            }
        };
        handler.post(r);
    }

    void rewind() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (mVideoView.isPlaying()) {
                    int currentPos = mVideoView.getCurrentPosition();
                    currentPos -= 5000;
                    mVideoView.seekTo(currentPos);
                }
            }
        };
        handler.post(r);
    }

    protected Dialog onCreateDialog(int dialogId) {
        Dialog ret = null;
        switch (dialogId) {
            case DIALOG_RESUME:
                ret = new ResumeCheckDialog(this).create();
                break;
        }
        return ret;
    }

    String getPath() {
        return mCursor.getString(mCursor.getColumnIndex(MediaColumns.DATA));
    }

    String getMediaTitle() {
        return mCursor.getString(mCursor.getColumnIndex(MediaColumns.TITLE));
    }

    void playback(int msec) {
        mVideoView.setKeepScreenOn(true);

        String path = getPath();

        String msg = "now playing : " + path + " (position = "
                + mCursor.getPosition() + ")";
        Log.v(TAG, msg);

        if (path == null || path == "") {
            noneedBackupResume = true;
            finish();
        } else {
            mVideoView.setVideoPath(getPath());
            mVideoView.seekTo(msec);
            mVideoView.requestFocus();
            mVideoView.start();
            String title = getMediaTitle();

            if (show_toast && title != null) {
                Toast t = Toast.makeText(this, title, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP, 0, 0);
                t.show();
            }
        }
    }

    void BackupResumeData(String path, int msec) {
        Log.v(TAG, "BackupResumeData(" + path + ", " + msec + ")");

        int id = -1;
        Cursor cursor = getContentResolver().query(ResumeProvider.CONTENT_URI,
                null, ResumeProvider.DATA + "='" + path + "'", null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
            }
            cursor.close();
        }
        if (id > 0) {
            Uri uri = ContentUris
                    .withAppendedId(ResumeProvider.CONTENT_URI, id);
            ContentValues values = new ContentValues();
            values.clear();
            values.put(ResumeProvider.DATA, path);
            values.put(ResumeProvider.POSITION, msec);
            getContentResolver().update(uri, values, null, null);
        } else {
            ContentValues values = new ContentValues();
            values.put(ResumeProvider.DATA, path);
            values.put(ResumeProvider.POSITION, msec);

            @SuppressWarnings("unused")
            Uri uri = getContentResolver().insert(ResumeProvider.CONTENT_URI,
                    values);
        }
    }

    int RestoreResumeData(String path) {
        int position = 0;
        Cursor cursor = getContentResolver().query(ResumeProvider.CONTENT_URI,
                null, ResumeProvider.DATA + "='" + path + "'", null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                position = cursor.getInt(cursor
                        .getColumnIndex(ResumeProvider.POSITION));
                Log.v(TAG, "RestoreResumeData(" + path
                        + ") : stored data found = " + position);
            }
            cursor.close();
        }
        Log.v(TAG, "RestoreResumeData(" + path + ") = " + position);
        return position;
    }

    void RemoveResumeData(String path) {
        int num_of_rows_deleted = getContentResolver().delete(
                ResumeProvider.CONTENT_URI,
                ResumeProvider.DATA + "='" + path + "'", null);
        Log.v(TAG, "RemoveResumeData(" + path + ") = " + num_of_rows_deleted);
    }

    class ResumeCheckDialog {
        AlertDialog.Builder dialog;

        public ResumeCheckDialog(Context context) {
            dialog = new AlertDialog.Builder(context);
            dialog.setIcon(android.R.drawable.ic_dialog_info);
            dialog.setTitle(context
                    .getString(R.string.pref_resume_dialog_title));
            dialog.setMessage(context
                    .getString(R.string.pref_resume_dialog_message));
            dialog.setPositiveButton(
                    context.getString(R.string.pref_resume_dialog_yes),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            playback(resumePos);
                        }
                    });
            dialog.setNegativeButton(
                    context.getString(R.string.pref_resume_dialog_no),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            dialog.cancel();
                            playback(0);
                        }
                    });
        }

        public Dialog create() {
            return dialog.create();
        }
    }
}