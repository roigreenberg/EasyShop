package com.roigreenberg.easyshop;

import android.util.SparseBooleanArray;

/**
 * Created by Roi on 12/06/2017.
 */

public class SelectionArray {

    private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();
    private boolean mIsSelectable = false;

    public void setSelected(int position, boolean isSelected) {
        mSelectedPositions.put(position, isSelected);
    }

    public boolean isSelected(int position) {
        return mSelectedPositions.get(position);
    }

    public void setSelectable(boolean selectable) {
        mIsSelectable = selectable;
    }

    public boolean isSelectable() {
        return mIsSelectable;
    }
}
