package jp.okamk.android.movie;

import jp.okamk.android.movie.util.FileList;
import jp.okamk.android.movie.util.FolderList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class FileActivity extends ListActivity {

    static final String TAG = "FileActivity";
    String mPath = "";
    FileAdapter fa = null;
    Cursor fileListCursor = null;
    static final int DIALOG_SORT_SETTING = 1;
    SharedPreferences sharedPreferences = null;
    static int sort_order_key_default = 0;
    static String sort_order_default = Const.SORT_ORDER_DATA;
    static String sort_order2_default = Const.SORT_ORDER_ASC;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            mPath = intent.getStringExtra(Const.EXTRA_FOLDER_PATH);
            if (mPath == null) {
                mPath = "";
            }
        }

        FolderList.FolderInfo folderInfo = new FolderList(FileActivity.this)
                .getFolderInfo(mPath);
        String folderName = "";
        if (folderInfo != null) {
            folderName = folderInfo.FolderName;
        }

        this.setTitle(getString(R.string.app_name) + " - " + folderName);
        setContentView(R.layout.fileactivity);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor cursor = (Cursor) l.getItemAtPosition(position);
        if (cursor != null) {
            CursorHolder.setCursor(cursor);

            Intent intent = new Intent(FileActivity.this, MovieActivity.class);
            startActivityForResult(intent, Const.REQUEST_CODE_MOVIE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        int checkedItem = sharedPreferences.getInt(Const.SORT_ORDER_KEY,
                sort_order_key_default);
        FileList fileList = new FileList(FileActivity.this);
        setSortOrder(fileList, checkedItem);
        fileListCursor = fileList.getFiles(mPath);
        fa = new FileAdapter(FileActivity.this, fileListCursor);
        setListAdapter(fa);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.REQUEST_CODE_MOVIE) {
            if (requestCode == Activity.RESULT_OK) {
            }
        }
    }

    private static final int MENU_ID_MENU1 = Menu.FIRST;
    private static final int MENU_ID_MENU2 = Menu.FIRST + 1;

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // メニューアイテムを追加します
        MenuItem item1 = menu.add(Menu.NONE, MENU_ID_MENU1, Menu.NONE,
                getString(R.string.menu_sort));
        item1.setIcon(android.R.drawable.ic_menu_sort_by_size);
        MenuItem item2 = menu.add(Menu.NONE, MENU_ID_MENU2, Menu.NONE,
                getString(R.string.menu_settings));
        item2.setIcon(android.R.drawable.ic_menu_preferences);
        return super.onCreateOptionsMenu(menu);
    }

    // TODO もう少しきれいに書く
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_ID_MENU1) {
            showDialog(DIALOG_SORT_SETTING);
        } else if (item.getItemId() == MENU_ID_MENU2) {
            Intent intent = new Intent();
            intent.setClass(FileActivity.this, SettingActivity.class);
            startActivity(intent);
        }
        return true;
    }

    protected Dialog onCreateDialog(int dialogId) {
        Dialog ret = null;
        switch (dialogId) {
            case DIALOG_SORT_SETTING:
                ret = new SortSettingDialog(this).create();
                break;
        }
        return ret;
    }

    void setSortOrder(FileList fileList, int which) {
        String order = sort_order_default;
        String order2 = sort_order2_default;
        switch (which) {
            case 0:
                order = Const.SORT_ORDER_TITLE;
                order2 = Const.SORT_ORDER_ASC;
                break;
            case 1:
                order = Const.SORT_ORDER_TITLE;
                order2 = Const.SORT_ORDER_DESC;
                break;
            case 2:
                order = Const.SORT_ORDER_DATA;
                order2 = Const.SORT_ORDER_ASC;
                break;
            case 3:
                order = Const.SORT_ORDER_DATA;
                order2 = Const.SORT_ORDER_DESC;
                break;
            case 4:
                order = Const.SORT_ORDER_SIZE;
                order2 = Const.SORT_ORDER_ASC;
                break;
            case 5:
                order = Const.SORT_ORDER_SIZE;
                order2 = Const.SORT_ORDER_DESC;
                break;
            case 6:
                order = Const.SORT_ORDER_DURATION;
                order2 = Const.SORT_ORDER_ASC;
                break;
            case 7:
                order = Const.SORT_ORDER_DURATION;
                order2 = Const.SORT_ORDER_DESC;
                break;
            case 8:
                order = Const.SORT_ORDER_DATE_ADDED;
                order2 = Const.SORT_ORDER_ASC;
                break;
            case 9:
                order = Const.SORT_ORDER_DATE_ADDED;
                order2 = Const.SORT_ORDER_DESC;
                break;
            case 10:
                order = Const.SORT_ORDER_DATE_MODIFIED;
                order2 = Const.SORT_ORDER_ASC;
                break;
            case 11:
                order = Const.SORT_ORDER_DATE_MODIFIED;
                order2 = Const.SORT_ORDER_DESC;
                break;
        }
        fileList.setSortOrder(order, order2);
    }

    class SortSettingDialog {
        AlertDialog.Builder dialog;

        public SortSettingDialog(Context context) {

            int checkedItem = sharedPreferences.getInt(Const.SORT_ORDER_KEY,
                    sort_order_key_default);

            dialog = new AlertDialog.Builder(context);
            dialog.setTitle(context.getString(R.string.menu_sort_dialog_title));
            dialog.setSingleChoiceItems(new String[]{
                            getString(R.string.menu_sort_title_asc),
                            getString(R.string.menu_sort_title_desc),
                            getString(R.string.menu_sort_data_asc),
                            getString(R.string.menu_sort_data_desc),
                            getString(R.string.menu_sort_size_asc),
                            getString(R.string.menu_sort_size_desc),
                            getString(R.string.menu_sort_duration_asc),
                            getString(R.string.menu_sort_duration_desc),
                            getString(R.string.menu_sort_date_added_asc),
                            getString(R.string.menu_sort_date_added_desc),
                            getString(R.string.menu_sort_date_modified_asc),
                            getString(R.string.menu_sort_date_modified_desc)},
                    checkedItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FileList fileList = new FileList(FileActivity.this);
                            setSortOrder(fileList, which);
                            fileListCursor = fileList.getFiles(mPath);
                            fa = new FileAdapter(FileActivity.this,
                                    fileListCursor);
                            FileActivity.this.setListAdapter(fa);

                            SharedPreferences.Editor editor = sharedPreferences
                                    .edit();
                            editor.putInt("sort_order_key", which);
                            editor.commit();

                            dialog.dismiss();
                        }
                    });
            dialog.setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(Activity.RESULT_CANCELED);
                        }
                    });
        }

        public Dialog create() {
            return dialog.create();
        }
    }
}
