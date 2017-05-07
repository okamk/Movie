package jp.okamk.android.movie;

import jp.okamk.android.movie.util.Util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video.VideoColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class FileAdapter extends CursorAdapter {
    static final String TAG = "FileAdapter";
    private LayoutInflater inflater;

    public FileAdapter(Context context, Cursor c) {
        super(context, c);
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null) {
            String title = cursor.getString(cursor
                    .getColumnIndex(MediaColumns.TITLE));
            String path = cursor.getString(cursor
                    .getColumnIndex(MediaColumns.DATA));
            Long duration = cursor.getLong(cursor
                    .getColumnIndex(VideoColumns.DURATION));
            Long size = cursor
                    .getLong(cursor.getColumnIndex(MediaColumns.SIZE));

            TextView text_title = (TextView) view
                    .findViewById(R.id.file_text_title);
            text_title.setText(title);
            TextView text_path = (TextView) view
                    .findViewById(R.id.file_text_path);
            text_path.setText(path);
            TextView text_duration = (TextView) view
                    .findViewById(R.id.file_text_duration);
            text_duration.setText(Util.getDuration(duration));
            TextView text_size = (TextView) view
                    .findViewById(R.id.file_text_size);
            text_size.setText(Util.getSize(size));
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.filerow, null);
        return view;
    }

}
