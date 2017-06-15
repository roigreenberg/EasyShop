package com.roigreenberg.easyshop;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Roi on 02/06/2017.
 */

public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener  {

    public static final int BOUGHT = 0;
    //private final TextView mID;
    private final TextView mName;
    private final TextView mBrand;
    private final TextView mWeight;
    private final TextView mVolume;
    private final TextView mAssignee;
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
        this.mAssignee = (TextView) itemView.findViewById(R.id.tv_item_assignee);

        itemView.setOnClickListener(this);
    }

    public void bindItem(Item item, String assignee, float textSize){
        setName(item.getName());
        setBrand(item.getBrand());
        setWeight(item.getWeight());
        setVolume(item.getVolume());
        setAssignee(assignee);
        itemView.setTag(item.getID());
        setNameSize(textSize);
        setBrandSize(textSize);
        setVolumeSize(textSize);
        setAssigneeSize(textSize);

    }


    public void setName(String name) {
        mName.setText(name);
    }
    public void setBrand(String name) {
        if (name == "")
            mBrand.setVisibility(View.GONE);
        else {
            mBrand.setVisibility(View.VISIBLE);
            mBrand.setText(name);
        }
    }
    public void setWeight(String name) {
        if (name == "")
            mBrand.setVisibility(View.GONE);
        else if (name != null) {
            mWeight.setVisibility(View.VISIBLE);
            mWeight.setText(name);
        }
    }
    public void setVolume(String name) {
        if (name != null)
            mVolume.setText(name);
    }

    public void setAssignee(String name) {
        if (name != null)
            mAssignee.setText(name);
    }

    public void setNameSize(float size) {
        mName.setTextSize(size + 5);
    }

    public void setNameCond(int cond) {

        switch (cond) {
            case (BOUGHT):
                mName.setPaintFlags(mName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);;
        }
    }

    public void setBrandSize(Float size) {
        mBrand.setTextSize(size);
    }

    public void setVolumeSize(Float size) {
        mVolume.setTextSize(size);
    }

    public void setAssigneeSize(Float size) {
        mAssignee.setTextSize(size);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
    //public void setID(String name) { mListNameField.setText(name); }
}
