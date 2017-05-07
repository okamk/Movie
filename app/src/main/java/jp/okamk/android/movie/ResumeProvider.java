package jp.okamk.android.movie;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

public class ResumeProvider extends ContentProvider {

    public static final String AUTHORITY = "jp.okamk.android.movie";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + "resume");

    public static final String DATA = "data";
    public static final String POSITION = "position";

    private static final int CODE_NUMBERS = 1;
    private static final int CODE_NUMBER = 2;

    /**
     * ディレクトリのMIMEタイプ
     */
    private static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + AUTHORITY;

    /**
     * 単一のMIMEタイプ
     */
    private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + AUTHORITY;

    private UriMatcher uriMatcher_;
    DatabaseHelper databaseHelper;
    String TAG = "ResumeProvider";

    @Override
    public boolean onCreate() {
        // 何にもマッチしないものを作成
        uriMatcher_ = new UriMatcher(UriMatcher.NO_MATCH);
        // マッチ対象にcom.suddenAngerSytem.randomNumber/numbersを追加
        uriMatcher_.addURI(AUTHORITY, "resume", CODE_NUMBERS);
        // マッチ対象にcom.suddenAngerSytem.randomNumber/numbers/xxxxを追加(xxxxは数字)
        uriMatcher_.addURI(AUTHORITY, "resume/#", CODE_NUMBER);
        // DBを初期化(必要であれば、勝手にテーブルを作ってくれる)
        databaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher_.match(uri)) {
            case CODE_NUMBERS:
                return CONTENT_TYPE;
            case CODE_NUMBER:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        final int deleteCount;
        switch (uriMatcher_.match(uri)) {
            case CODE_NUMBERS:
                deleteCount = db.delete("resumelist", selection, selectionArgs);
                break;
            case CODE_NUMBER:
                final long id = Long.parseLong(uri.getPathSegments().get(1));
                final String idPlusSelection = android.provider.BaseColumns._ID
                        + "=" + Long.toString(id)
                        + (selection == null ? "" : "AND (" + selection + ")");
                deleteCount = db.delete("resumelist", idPlusSelection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return deleteCount;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        if (uriMatcher_.match(uri) != CODE_NUMBERS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        final long id = db.insertOrThrow("resumelist", null, values);

        // 変更を通知する
        final Uri newUri = ContentUris.withAppendedId(CONTENT_URI, id);
        getContext().getContentResolver().notifyChange(newUri, null);

        return newUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        if (uriMatcher_.match(uri) == CODE_NUMBER) {
            final long id = Long.parseLong(uri.getPathSegments().get(1));
            selection = android.provider.BaseColumns._ID + "="
                    + Long.toString(id)
                    + (selection == null ? "" : "AND (" + selection + ")");
        }
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.query("resumelist", projection, selection,
                selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();
        final int updateCount;
        switch (uriMatcher_.match(uri)) {
            case CODE_NUMBERS:
                updateCount = db.update("resumelist", values, selection,
                        selectionArgs);
                break;
            case CODE_NUMBER:
                final long id = Long.parseLong(uri.getPathSegments().get(1));
                final String idPlusSelection = android.provider.BaseColumns._ID
                        + "=" + Long.toString(id)
                        + (selection == null ? "" : "AND (" + selection + ")");
                updateCount = db.update("resumelist", values, idPlusSelection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // 変更を通知する
        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "resumetable.db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE resumelist (" + BaseColumns._ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + "data TEXT,"
                    + "position INTEGER" + ");");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // 削除してまた作成
            db.execSQL("DROP TABLE IF EXISTS resumelist");
            onCreate(db);
        }
    }
}
