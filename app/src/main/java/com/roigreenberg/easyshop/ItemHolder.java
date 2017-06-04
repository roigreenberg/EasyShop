package com.roigreenberg.easyshop;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Roi on 02/06/2017.
 */

public class ItemHolder extends RecyclerView.ViewHolder {

    //private final TextView mID;
    private final TextView mName;
    private final TextView mBrand;
    private final TextView mWeight;
    private final TextView mVolume;
    //private final TextView mBarcode;
    //private final TextView[] mImage;

    public ItemHolder(View itemView) {
        super(itemView);
        //this.mID = (TextView) itemView.findViewById(R.id.tv_list_name);
        this.mName = (TextView) itemView.findViewById(R.id.tv_item_name);
        this.mBrand = (TextView) itemView.findViewById(R.id.tv_item_brand);
        this.mWeight = (TextView) itemView.findViewById(R.id.tv_item_weight_volume);
        this.mVolume = (TextView) itemView.findViewById(R.id.tv_item_weight_volume);
        //this.mBarcode = (TextView) itemView.findViewById(R.id.tv_list_name);
    }

    public void setName(String name) {
        mName.setText(name);
    }
    public void setBrand(String name) { mBrand.setText(name); }
    public void setWeight(String name) {
        if (name != null)
            mWeight.setText(name);
    }
    public void setVolume(String name) {
        if (name != null)
            mVolume.setText(name);
    }
    //public void setID(String name) { mListNameField.setText(name); }
}
