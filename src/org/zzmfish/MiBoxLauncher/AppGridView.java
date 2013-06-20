package org.zzmfish.MiBoxLauncher;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

class AppGridView extends GridView {
    private int mSelectedIndex = 0;
    
    public AppGridView(Context context)
    {
        super(context);
    }
    public AppGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public AppGridView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    
    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return true;
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {        
        int childCount = getChildCount();
        int columns = getNumColumns();
        int rows = (childCount + columns - 1) / columns;
        int x = mSelectedIndex % columns;
        int y = mSelectedIndex / columns;
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_LEFT:
            x = (x - 1 + columns) % columns;
            break;
        case KeyEvent.KEYCODE_DPAD_RIGHT:
            x = (x + 1) % columns;
            break;
        case KeyEvent.KEYCODE_DPAD_UP:
            y = (y - 1 + rows) % rows;
            break;
        case KeyEvent.KEYCODE_DPAD_DOWN:
            y = (y + 1) % rows;
            break;
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER:
            ApplicationInfo app = (ApplicationInfo) getItemAtPosition(mSelectedIndex);
            this.getContext().startActivity(app.intent);
            break;
        default:
            break;
        }
        int index = y * columns + x;
        if (index != mSelectedIndex) {
            if (index >= childCount) { //处理落到最后一行的空位的情况
                switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    index = childCount - 1; //最后一个
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    index = columns * (rows - 1); //最后一行第一个
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    index -= columns; //倒数第二行
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    index = index % columns; //第一行
                    break;
                
                }
            }
            select(index);
        }
        return true;
    }
    
    private void select(int index) {
        int oldIndex = mSelectedIndex;
        int newIndex = index % getChildCount();
        if (newIndex != oldIndex) {
            TextView child = (TextView) getChildAt(newIndex);
            if (child != null) {
                child.setTextColor(0xff00ffff);
                TextView oldChild = (TextView) getChildAt(oldIndex);
                if (oldChild != null)
                    oldChild.setTextColor(0xffffffff);
                mSelectedIndex = newIndex;
            }
        }
    }
}
