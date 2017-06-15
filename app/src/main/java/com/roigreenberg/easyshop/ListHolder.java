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
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Roi on 02/06/2017.
 */

public class ListHolder extends RecyclerView.ViewHolder {

    public final TextView mListNameField;
    private final RecyclerView mListItemsRecyclerView;
    private final ImageButton mImageButtonAddList;
    private final ImageButton mImageButtonShare;
    private final ImageButton mImageButtonDeleteItems;


    public ListHolder(View itemView) {
        super(itemView);
        this.mListNameField = (TextView) itemView.findViewById(R.id.tv_list_name);
        mImageButtonAddList = (ImageButton) itemView.findViewById(R.id.ib_add_item);
        mImageButtonShare = (ImageButton) itemView.findViewById(R.id.ib_share);
        mImageButtonDeleteItems = (ImageButton) itemView.findViewById(R.id.ib_delete_items);
        mImageButtonDeleteItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = mListItemsRecyclerView.getAdapter().getItemCount() - 1; i >= 0; i--) {
                    ItemHolder itemHolder = (ItemHolder) mListItemsRecyclerView.findViewHolderForAdapterPosition(i);
                    if (itemHolder.isCheck()){

                        ((FirebaseRecyclerAdapter) mListItemsRecyclerView.getAdapter()).getRef(i).setValue(null);
                    }
                }
            }
        });

        mListItemsRecyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerview_items);
        mListItemsRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
                (itemView.getContext(), LinearLayoutManager.VERTICAL, false);
        mListItemsRecyclerView.setLayoutManager(layoutManager);
        mListNameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListItemsRecyclerView.getVisibility() == View.GONE)
                    mListItemsRecyclerView.setVisibility(View.VISIBLE);
                else
                    mListItemsRecyclerView.setVisibility(View.GONE);
            }
        });
        mListNameField.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mImageButtonDeleteItems.getVisibility() == View.GONE)
                    mImageButtonDeleteItems.setVisibility(View.VISIBLE);
                else
                    mImageButtonDeleteItems.setVisibility(View.GONE);

                for (int i = mListItemsRecyclerView.getAdapter().getItemCount() - 1; i >= 0; i--) {
                    ItemHolder itemHolder = (ItemHolder) mListItemsRecyclerView.findViewHolderForAdapterPosition(i);
                    itemHolder.setCheckBoxVisibility(!itemHolder.isCheckBoxVisibile());
                }

                return true;
            }
        });
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

    public void setDeleteItemsOnClick(View.OnClickListener onClick){
        mImageButtonDeleteItems.setOnClickListener(onClick);
    }

    public RecyclerView getListItemsRecyclerView() { return mListItemsRecyclerView; }

}
