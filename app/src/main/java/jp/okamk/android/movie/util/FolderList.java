package jp.okamk.android.movie.util;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video.Media;
import android.provider.MediaStore.Video.VideoColumns;

public class FolderList {

    Context mContext = null;
    static final Uri mUri = Media.EXTERNAL_CONTENT_URI;
    String TAG = "FolderList";

    public class FolderInfo {
        public FolderInfo() {
            FolderName = "";
            FolderPath = "";
            NumOfFiles = 0;
            Size = 0;
        }

        public String FolderName;
        public String FolderPath;
        public long NumOfFiles;
        public long Size;
    }

    public FolderList(Context context) {
        mContext = context;
    }

    /**
     * 指定パスの情報
     *
     * @param path 　パス
     * @return　FolderInfo
     */
    public FolderInfo getFolderInfo(String path) {
        FolderInfo ret = null;

        final String[] projection = new String[]{MediaColumns.SIZE,
                VideoColumns.BUCKET_DISPLAY_NAME};
        // DATAカラムのパス部分を削除した文字列がDISPLAY_NAMEと一致するrowを取得
        String selection = "REPLACE(" + MediaColumns.DATA + ",'" + path
                + "','') = " + MediaColumns.DISPLAY_NAME;

        Cursor cursor = mContext.getContentResolver().query(mUri, projection,
                selection, null, VideoColumns.BUCKET_DISPLAY_NAME);

        if (cursor != null) {
            ret = new FolderInfo();

            ret.FolderPath = path;
            ret.NumOfFiles = cursor.getCount();

            while (cursor.moveToNext()) {
                int colIndex = cursor
                        .getColumnIndex(VideoColumns.BUCKET_DISPLAY_NAME);
                String folderName = cursor.getString(colIndex);
                ret.FolderName = folderName;

                colIndex = cursor.getColumnIndex(MediaColumns.SIZE);
                Long columnValue = cursor.getLong(colIndex);
                ret.Size += columnValue;
            }
            cursor.close();
        }
        return ret;
    }

    /**
     * メディアのあるフォルダの一覧
     *
     * @return paths contains video file. null if no video file.
     */
    public ArrayList<FolderInfo> getFolders() {
        ArrayList<FolderInfo> ret = null;

        // DATAカラムのパス部分を削除した文字列がDISPLAY_NAMEと一致する、置換後の値を取得
        String projection1 = "REPLACE(" + MediaColumns.DATA + ","
                + MediaColumns.DISPLAY_NAME + ",'')";
        String projection2 = VideoColumns.BUCKET_DISPLAY_NAME;
        // String projection3 = MediaColumns.SIZE;

        // メディアを含むすべてのパスを取得
        Cursor cursor = mContext.getContentResolver().query(mUri,
                new String[]{projection1, projection2}, null, null,
                projection2);
        ArrayList<String> pathList = new ArrayList<String>();
        if (cursor != null) {
            // Cursorは重複を含むので重複を取り除いたリストを作る
            while (cursor.moveToNext()) {
                int colIndex = cursor.getColumnIndex(projection1);
                if (colIndex >= 0) {
                    String path = cursor.getString(colIndex);
                    if (!pathList.contains(path)) {
                        pathList.add(path);
                    }
                }
            }
            cursor.close();

            ret = new ArrayList<FolderInfo>();
            // 各パスを個別に再検索して件数、トータルサイズをとる
            for (String path : pathList) {
                FolderInfo folderInfo = getFolderInfo(path);
                if (folderInfo != null) {
                    ret.add(folderInfo);
                }
            }
        }
        return ret;
    }
}
