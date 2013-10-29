package org.zzmfish.MiBoxLauncher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import java.util.Collections;
import java.util.List;

public class Home extends Activity
{
    private static final String TAG = "MiBoxLauncher";
    //private static final String MIBOX_PACKAGE = "com.duokan.duokantv";
    //private static final String MIBOX_PACKAGE = "com.android.browser";
    //private boolean mStartMibox = true;
    private static ArrayList<ApplicationInfo> mApplications;
    private AppGridView mGrid;
    private BroadcastReceiver mApplicationsReceiver = new ApplicationsIntentReceiver();



    /**
     * Starts the selected activity/application in the grid view.
     */
    private class ApplicationLauncher implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
            Log.d(TAG, "startActivity: " + app.intent.getPackage());
            startActivity(app.intent);
        }
    } 
    
	//应用更新通知
	private class ApplicationsIntentReceiver extends BroadcastReceiver {
    	@Override
        public void onReceive(Context context, Intent intent) {
            loadApplications();
            showApplications();
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
        showApplications();
        registerIntentReceivers();
    }

    private void showApplications()
    {
        mGrid.setAdapter(new ApplicationsAdapter(this, mApplications));
    }

    private void loadApplications()
    {
        PackageManager manager = getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

        if (apps != null) {
            final int count = apps.size();

            if (mApplications == null) {
                mApplications = new ArrayList<ApplicationInfo>(count);
            }
            mApplications.clear();

            for (int i = 0; i < count; i++) {
                ApplicationInfo application = new ApplicationInfo();
                ResolveInfo info = apps.get(i);
                Log.d(TAG, "label=" + info.loadLabel(manager) + ", package=" + info.activityInfo.packageName);

                application.title = info.loadLabel(manager);
                application.setActivity(new ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name),
                        Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                application.icon = info.activityInfo.loadIcon(manager);
                application.packageName = info.activityInfo.packageName;

                /*if (info.activityInfo.packageName.equals(MIBOX_PACKAGE)) {
                	mApplications.add(0, application);
                	if (mStartMibox) {
                		mStartMibox = false;
                		Log.d(TAG, "startActivity: " + application.intent.getPackage());
                		startActivity(application.intent);
                	}
                }
                else*/
                	mApplications.add(application);

            }
        }
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
	
	private void uninstallApplication() {
		//获取应用信息
		ApplicationInfo appInfo = (ApplicationInfo) mGrid.getSelectedItem();
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
		}
		return true;
	}


    
}
