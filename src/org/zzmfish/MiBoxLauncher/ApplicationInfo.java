/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zzmfish.MiBoxLauncher;

import java.io.File;
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
import android.graphics.drawable.Drawable;
import android.util.Log;

class ApplicationInfo {
    CharSequence title;
    Intent intent;
    Drawable icon;
    String packageName;
    boolean filtered;
    static final String LIST_FILE = "AppList.txt";

    /**
     * Creates the application intent based on a component name and various launch flags.
     *
     * @param className the class name of the component representing the intent
     * @param launchFlags the launch flags
     */
    final void setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationInfo)) {
            return false;
        }

        ApplicationInfo that = (ApplicationInfo) o;
        return title.equals(that.title) &&
                intent.getComponent().getClassName().equals(
                        that.intent.getComponent().getClassName());
    }

    @Override
    public int hashCode() {
        int result;
        result = (title != null ? title.hashCode() : 0);
        final String name = intent.getComponent().getClassName();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
    
    /**
     * 获取应用列表
     * @param activity
     * @return
     */
    static ArrayList<ApplicationInfo> getAll(Activity activity)
    {
    	loadList(activity);
    	ArrayList<ApplicationInfo> appList = null;
        PackageManager manager = activity.getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

        if (apps != null) {
        	int count = apps.size();
            appList = new ArrayList<ApplicationInfo>(count);

            for (int i = 0; i < count; i++) {
            	ApplicationInfo application = new ApplicationInfo();
            	ResolveInfo info = apps.get(i);

            	//保存应用信息
            	application.title = info.loadLabel(manager);
            	application.setActivity(new ComponentName(
            			info.activityInfo.applicationInfo.packageName,
            			info.activityInfo.name),
            			Intent.FLAG_ACTIVITY_NEW_TASK
            			| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            	application.icon = info.activityInfo.loadIcon(manager);
            	application.packageName = info.activityInfo.packageName;
            	appList.add(application);
            }
        }
        saveList(activity, appList);
        return appList;
    }
    
    static void loadList(Activity activity)
    {
    	try {
			FileInputStream file = activity.openFileInput(LIST_FILE);
			byte buffer[] = new byte[10240];
			int length = file.read(buffer);
			String content = new String(buffer, 0, length);
			Log.d("zhouzm", "content=" + content);
			String appList[]  = content.split(",");
			file.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    static void saveList(Activity activity, ArrayList<ApplicationInfo> appList)
    {
    	Log.d("zhouzm", "saveList");
    	try {
			FileOutputStream file = activity.openFileOutput(LIST_FILE, activity.MODE_PRIVATE);
			for (int i = 0; i < appList.size(); i ++) {
				if (i > 0)
					file.write(",".getBytes());
				file.write(appList.get(i).packageName.getBytes());
			}
			file.close();
			Log.d("zhouzm", "saveList END");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
