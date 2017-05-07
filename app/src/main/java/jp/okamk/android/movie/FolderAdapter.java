package jp.okamk.android.movie;

import java.util.ArrayList;

import jp.okamk.android.movie.util.FolderList.FolderInfo;
import jp.okamk.android.movie.util.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FolderAdapter extends ArrayAdapter<FolderInfo> {

    private ArrayList<FolderInfo> items;
    private LayoutInflater inflater;
    Context mContext = null;
    String TAG = "FolderAdapter";

    public FolderAdapter(Context context, int textViewResourceId,
                         ArrayList<FolderInfo> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.folderrow, null);
        }
        if (items != null) {
            FolderInfo folderInfo = items.get(position);
            if (folderInfo != null) {
                TextView text_title = (TextView) view
                        .findViewById(R.id.folder_text_title);
                text_title.setText(folderInfo.FolderName);
                TextView text_path = (TextView) view
                        .findViewById(R.id.folder_text_path);
                text_path.setText(folderInfo.FolderPath);
                TextView text_count = (TextView) view
                        .findViewById(R.id.folder_text_count);
                text_count.setText(String.valueOf(folderInfo.NumOfFiles));
                TextView text_size = (TextView) view
                        .findViewById(R.id.folder_text_size);
                text_size.setText(Util.getSize(folderInfo.Size));
            }
        }
        return view;
    }
}
