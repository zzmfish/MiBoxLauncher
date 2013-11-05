package org.zzmfish.MiBoxLauncher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * 移动程序对话框
 */
public class MoveAppDialog extends DialogFragment {
	private int mFromIndex = 0;

	public void setFromIndex(int fromIndex) {
		mFromIndex = fromIndex;
	}
	
	int GetFromIndex() {
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
    			Dialog myDialog = MoveAppDialog.this.getDialog();
    			int toIndex = ((NumberPicker) myDialog.findViewById(R.id.app_pos)).getValue();
    			AppList.getInstance().move(MoveAppDialog.this.GetFromIndex(), toIndex);
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
