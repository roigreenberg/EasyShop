package com.roigreenberg.easyshop;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

/**
 * Created by Roi on 02/06/2017.
 */

public class ListHolder extends RecyclerView.ViewHolder {

    private final TextView mListNameField;
    private final RecyclerView mListItems;
    private final ImageButton mImageButton;

    public ListHolder(View itemView) {
        super(itemView);
        this.mListNameField = (TextView) itemView.findViewById(R.id.tv_list_name);
        mImageButton = (ImageButton) itemView.findViewById(R.id.ib_add_item);
        mListItems = (RecyclerView) itemView.findViewById(R.id.recyclerview_items);
        mListItems.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
                (itemView.getContext(), LinearLayoutManager.VERTICAL, false);
        mListItems.setLayoutManager(layoutManager);
    }

    public void setName(String name) {
        mListNameField.setText(name);
    }

    public void setListAdapter (FirebaseRecyclerAdapter adapter){
        mListItems.setAdapter(adapter);
    }

    public void setButtonOnClick(View.OnClickListener onClick){
        mImageButton.setOnClickListener(onClick);
    }
}
