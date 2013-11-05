package org.zzmfish.MiBoxLauncher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;

import java.util.ArrayList;

public class Home extends Activity
{
    private static final String TAG = "MiBoxLauncher";
    private static ArrayList<AppInfo> mApplications;
    private AppGridView mGrid;
    private BroadcastReceiver mApplicationsReceiver = new ApplicationsIntentReceiver();
    private AppList mAppList = new AppList(this);

    //启动应用程序
    private class ApplicationLauncher implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            AppInfo app = (AppInfo) parent.getItemAtPosition(position);
            Log.d(TAG, "startActivity: " + app.intent.getPackage());
            startActivity(app.intent);
        }
    } 
    
	//应用更新通知
	private class ApplicationsIntentReceiver extends BroadcastReceiver {
    	@Override
        public void onReceive(Context context, Intent intent) {
            loadApplications();
    	}
    }
	
	private void registerIntentReceivers() {
		IntentFilter filter;
		filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		registerReceiver(mApplicationsReceiver, filter);
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        mGrid = (AppGridView) findViewById(R.id.all_apps);
        mGrid.setOnItemClickListener(new ApplicationLauncher()); 
        loadApplications();
        registerIntentReceivers();
    }

    private void loadApplications()
    {
        mApplications = mAppList.getApps();
        mGrid.setAdapter(new AppAdapter(this, mApplications));
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
		
	}
	
	//卸载应用程序
	private void uninstallApplication() {
		AppInfo appInfo = (AppInfo) mGrid.getSelectedItem();
		if (appInfo == null)
			return;
		Uri uri = Uri.fromParts("package", appInfo.packageName, null);
		Intent it = new Intent(Intent.ACTION_DELETE, uri);
		startActivity(it);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.uninstall:
			uninstallApplication();
			break;
		case R.id.move:
		    MoveAppDialog dialog = new MoveAppDialog();
		    dialog.setFromIndex(mGrid.getSelectedItemPosition());
		    dialog.show(getFragmentManager(), "missiles");
		    loadApplications();
			break;
		case R.id.exit:
			finish();
			break;
		}
		return true;
	}
}


