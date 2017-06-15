package com.roigreenberg.easyshop;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Roi on 02/06/2017.
 */

public class ListHolder extends RecyclerView.ViewHolder {

    public final TextView mListNameField;

    private final ImageButton mImageButtonShare;

    public ListHolder(View itemView) {
        super(itemView);
        this.mListNameField = (TextView) itemView.findViewById(R.id.tv_list_name);
        mImageButtonShare = (ImageButton) itemView.findViewById(R.id.ib_share);

    }

    public void setName(String name) {
        mListNameField.setText(name);
    }

    public void setNameSize(Float size){
        mListNameField.setTextSize(size + 10);
    }



    public void setShareOnClick(View.OnClickListener onClick){
        mImageButtonShare.setOnClickListener(onClick);
    }

}
