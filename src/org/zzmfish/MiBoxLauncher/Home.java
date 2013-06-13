package org.zzmfish.MiBoxLauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import java.util.Collections;
import java.util.List;

public class Home extends Activity
{
    /** Called when the activity is first created. */
    private static final String TAG = "MiBoxLauncher";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        loadApplications();
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
            for (int i = 0; i < count; i++) {
                ResolveInfo info = apps.get(i);
                Log.d(TAG, "label=" + info.loadLabel(manager));
            }
        }

    }
}
