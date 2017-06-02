package com.roigreenberg.easyshop;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Roi on 02/06/2017.
 */

public class ListHolder extends RecyclerView.ViewHolder {

    private final TextView mListNameField;

    public ListHolder(View itemView) {
        super(itemView);
        this.mListNameField = itemView.findViewById(R.id.list_name);
    }
}
