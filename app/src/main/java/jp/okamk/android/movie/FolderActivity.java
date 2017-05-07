package jp.okamk.android.movie;

import java.util.ArrayList;

import jp.okamk.android.movie.util.FolderList;
import jp.okamk.android.movie.util.FolderList.FolderInfo;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class FolderActivity extends ListActivity {

    static final String TAG = "FolderActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<FolderInfo> folders = new FolderList(this).getFolders();
        FolderAdapter fa = new FolderAdapter(this, R.layout.folderrow, folders);
        setListAdapter(fa);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FolderInfo folderInfo = (FolderInfo) l.getItemAtPosition(position);

        Intent intent = new Intent(FolderActivity.this, FileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Const.EXTRA_FOLDER_PATH, folderInfo.FolderPath);
        startActivityForResult(intent, Const.REQUEST_CODE_FILELIST);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        if (requestCode == Const.REQUEST_CODE_FILELIST) {
            if (requestCode == Activity.RESULT_OK) {
            }
        }
    }

    private static final int MENU_ID_SETTINGS = Menu.FIRST;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item1 = menu.add(Menu.NONE, MENU_ID_SETTINGS, Menu.NONE,
                getString(R.string.menu_settings));
        item1.setIcon(android.R.drawable.ic_menu_preferences);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = true;
        switch (item.getItemId()) {
            default:
                ret = super.onOptionsItemSelected(item);
                break;
            case MENU_ID_SETTINGS:
                Intent intent = new Intent();
                intent.setClass(FolderActivity.this, SettingActivity.class);
                startActivity(intent);
                ret = true;
                break;
        }
        return ret;
    }
}
