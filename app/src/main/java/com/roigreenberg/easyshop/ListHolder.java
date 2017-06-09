package com.roigreenberg.easyshop;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

/**
 * Created by Roi on 02/06/2017.
 */

public class ListHolder extends RecyclerView.ViewHolder {

    public final TextView mListNameField;
    private final RecyclerView mListItemsRecyclerView;
    private final ImageButton mImageButtonAddList;
    private final ImageButton mImageButtonShare;

    public ListHolder(View itemView) {
        super(itemView);
        this.mListNameField = (TextView) itemView.findViewById(R.id.tv_list_name);
        mImageButtonAddList = (ImageButton) itemView.findViewById(R.id.ib_add_item);
        mImageButtonShare = (ImageButton) itemView.findViewById(R.id.ib_share);
        mListItemsRecyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerview_items);
        mListItemsRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
                (itemView.getContext(), LinearLayoutManager.VERTICAL, false);
        mListItemsRecyclerView.setLayoutManager(layoutManager);
    }

    public void setName(String name) {
        mListNameField.setText(name);
    }

    public void setNameSize(Float size){
        mListNameField.setTextSize(size + 10);
    }

    public void setListAdapter (FirebaseRecyclerAdapter adapter){
        mListItemsRecyclerView.setAdapter(adapter);
    }

    public void setAddItemOnClick(View.OnClickListener onClick){
        mImageButtonAddList.setOnClickListener(onClick);
    }

    public void setShareOnClick(View.OnClickListener onClick){
        mImageButtonShare.setOnClickListener(onClick);
    }

    public RecyclerView getListItemsRecyclerView() { return mListItemsRecyclerView; }
}
