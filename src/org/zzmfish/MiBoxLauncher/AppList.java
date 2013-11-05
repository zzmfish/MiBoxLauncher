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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

public class AppList {
    static AppList mInstance;
    final String LIST_FILE = "AppList.txt";
    String mSortList[] = null;
    Activity mActivity;
    
    AppList(Activity activity) {
    	super();
    	mInstance = this;
    	mActivity = activity;
    }
    
    static AppList getInstance() {
    	return mInstance;
    }
    
    /**
     * 获取应用列表
     */
    ArrayList<AppInfo> getApps()
    {
    	loadSortList();
    	ArrayList<AppInfo> appList = null;
        PackageManager manager = mActivity.getPackageManager();

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
            	int pos = getSortIndex(appUri);
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
        saveSortList(appList);
        return appList;
    }
    
    /**
     * 加载排序列表
     */
    void loadSortList()
    {
    	try {
			FileInputStream file = mActivity.openFileInput(LIST_FILE);
			byte buffer[] = new byte[10240];
			int length = file.read(buffer);
			if (length > 0) {
				String content = new String(buffer, 0, length);
				mSortList  = content.split("\n");
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
    
    /**
     * 保存排序列表
     */
    void saveSortList(ArrayList<AppInfo> appList)
    {
    	String uriList[] = new String[appList.size()];
		for (int i = 0; i < appList.size(); i ++) {
			uriList[i] = appList.get(i).intent.toUri(0);
		}
		saveSortList(uriList);
		mSortList = uriList;
    }
    
    /**
     * 保存排序列表2
     */
    void saveSortList(String uriList[])
    {
    	try {
			FileOutputStream file = mActivity.openFileOutput(LIST_FILE, Context.MODE_PRIVATE);
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
    
    /**
     * 获取位置
     */
    int getSortIndex(String appUri)
    {
    	if (mSortList != null) {
    		for (int i = 0; i < mSortList.length; i ++) {
    			if (mSortList[i].equals(appUri))
    				return i;
    		}
    		return mSortList.length;
    	}
    	return 0;
    }
    
    /**
     * 移动位置
     */
    void move(int fromIndex, int toIndex) {
    	int step = (toIndex > fromIndex) ? 1 : -1;
    	String appUri = mSortList[fromIndex];
    	for (int i = fromIndex; i != toIndex; i += step) {
    		mSortList[i] = mSortList[i + step];
    	}
    	mSortList[toIndex] = appUri;
    	saveSortList(mSortList);
    }
    
}
