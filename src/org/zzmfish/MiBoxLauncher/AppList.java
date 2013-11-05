package org.zzmfish.MiBoxLauncher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

public class AppList {
    static AppList mInstance;
    final String LIST_FILE = "AppList.txt";
    String mAppSortList[] = null;
    
    AppList() {
    	super();
    	mInstance = this;
    }
    
    static AppList getInstance() {
    	return mInstance;
    }
    
    /**
     * 获取应用列表
     * @param activity
     * @return
     */
    ArrayList<AppInfo> getAll(Activity activity)
    {
    	loadList(activity);
    	ArrayList<AppInfo> appList = null;
        PackageManager manager = activity.getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

        if (apps != null) {
        	int count = apps.size();
            appList = new ArrayList<AppInfo>(count);

            for (int i = 0; i < count; i++) {
            	AppInfo appInfo = new AppInfo();
            	ResolveInfo info = apps.get(i);

            	//保存应用信息
            	appInfo.title = info.loadLabel(manager);
            	appInfo.setActivity(new ComponentName(
            			info.activityInfo.applicationInfo.packageName,
            			info.activityInfo.name),
            			Intent.FLAG_ACTIVITY_NEW_TASK
            			| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            	appInfo.icon = info.activityInfo.loadIcon(manager);
            	appInfo.packageName = info.activityInfo.packageName;
            	
            	//插入到对应位置
            	String appUri = appInfo.intent.toUri(0);
            	int pos = getAppPosition(appUri);
            	while (pos < appList.size()
            			&& appList.get(pos) != null
            			&& appList.get(pos).intent != null)
            		pos = pos + 1;
            	while (appList.size() < pos + 1)
            		appList.add(new AppInfo());
            	appList.set(pos, appInfo);
            	Log.d("zhouzm", "appList.set: " + pos + ")");
            }
        }
        saveList(activity, appList);
        return appList;
    }
    
    void loadList(Activity activity)
    {
    	try {
			FileInputStream file = activity.openFileInput(LIST_FILE);
			byte buffer[] = new byte[10240];
			int length = file.read(buffer);
			if (length > 0) {
				String content = new String(buffer, 0, length);
				mAppSortList  = content.split("\n");
			}
			file.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    void saveList(Activity activity, ArrayList<AppInfo> appList)
    {
    	String uriList[] = new String[appList.size()];
		for (int i = 0; i < appList.size(); i ++) {
			uriList[i] = appList.get(i).intent.toUri(0);
		}
		saveList(activity, uriList);
		mAppSortList = uriList;
    }
    
    void saveList(Activity activity, String uriList[])
    {
    	try {
			FileOutputStream file = activity.openFileOutput(LIST_FILE, activity.MODE_PRIVATE);
			for (int i = 0; i < uriList.length; i ++) {
				if (i > 0)
					file.write("\n".getBytes());
				file.write(uriList[i].getBytes());
			}
			file.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    int getAppPosition(String appUri)
    {
    	if (mAppSortList != null) {
    		for (int i = 0; i < mAppSortList.length; i ++) {
    			if (mAppSortList[i].equals(appUri))
    				return i;
    		}
    		return mAppSortList.length;
    	}
    	return 0;
    }
    
    void moveApp(Activity activity, int fromIndex, int toIndex) {
    	int step = (toIndex > fromIndex) ? 1 : -1;
    	String appUri = mAppSortList[fromIndex];
    	for (int i = fromIndex; i != toIndex; i += step) {
    		mAppSortList[i] = mAppSortList[i + step];
    	}
    	mAppSortList[toIndex] = appUri;
    	saveList(activity, mAppSortList);
    }
    
}
