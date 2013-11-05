package org.zzmfish.MiBoxLauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.NumberPicker;

import java.util.ArrayList;

public class Home extends Activity
{
    private static final String TAG = "MiBoxLauncher";
    private static ArrayList<ApplicationInfo> mApplications;
    private AppGridView mGrid;
    private BroadcastReceiver mApplicationsReceiver = new ApplicationsIntentReceiver();

    //启动应用程序
    private class ApplicationLauncher implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
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
        mApplications = ApplicationInfo.getAll(this);
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
		case R.id.move:
		    DialogFragment newFragment = new MoveToDialog(mGrid.getSelectedItemPosition());
		    newFragment.show(getFragmentManager(), "missiles");
		    loadApplications();
		    showApplications();
			break;
		case R.id.exit:
			finish();
			break;
		}
		return true;
	}
}

/**
 * 移动程序对话框
 */
class MoveToDialog extends DialogFragment {
	int mFromIndex;
	
	public MoveToDialog(int fromIndex) {
		super();
		mFromIndex = fromIndex;
	}
	
	public int GetFromIndex() {
		return mFromIndex;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.move_app, null);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int id) {
    			Dialog myDialog = MoveToDialog.this.getDialog();
    			int toIndex = ((NumberPicker) myDialog.findViewById(R.id.app_pos)).getValue();
    			ApplicationInfo.moveApp(MoveToDialog.this.getActivity(), MoveToDialog.this.GetFromIndex(), toIndex);
    		}
    	});
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int id) {
    		}
    	});
        Dialog dialog = builder.create();
        return dialog;
	}
	
}
