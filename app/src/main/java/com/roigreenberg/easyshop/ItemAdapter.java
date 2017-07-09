package com.roigreenberg.easyshop;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.roigreenberg.easyshop.MainActivity.ITEMS;

/**
 * Created by Roi on 16/06/2017.
 */

public  class ItemAdapter extends SelectableItemAdapter {

    private ItemHolder.ClickListener clickListener;
    private boolean done;

    public ItemAdapter(Query query, ItemHolder.ClickListener clickListener, boolean done) {
        super(ItemInList.class,
                R.layout.item,
                ItemAdapter.ItemHolder.class,
                query);
        this.clickListener = clickListener;
        this.done = done;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ItemHolder(view, clickListener);
    }

    @Override
    protected void populateViewHolder(final ItemHolder itemHolder, final ItemInList item, final int position) {
        final String itemID = item.getItemID();
        final DatabaseReference itemInListRef = this.getRef(position);
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child(ITEMS).child(itemID);

        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Item itemData = dataSnapshot.getValue(Item.class);
                itemHolder.bindItem(dataSnapshot, item, done, /*mTextSize*/15, isSelected(position));

                itemHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!done && isChecked) {
                            Log.e("RROI", "cb->check " + itemInListRef.toString());
                            itemInListRef.getParent().getParent().child("DoneItems").push().setValue(item);
                            itemInListRef.removeValue();
                        } else if (done && !isChecked) {
                            Log.e("RROI", "cb->uncheck "  + itemInListRef.toString());
                            itemInListRef.getParent().getParent().child("Items").push().setValue(item);
                            itemInListRef.removeValue();
                        }
                        buttonView.setTag(null);
                        //notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener  {

        public static final int BOUGHT = 0;
        //private final TextView mID;
        private final TextView mName;
        private final TextView mBrand;
        private final TextView mWeight;
        private final TextView mVolume;
        private final TextView mAssignee;
        private final TextView mQuantity;
        //private final TextView mBarcode;
        //private final TextView[] mImage;
        private final TextView mExtraDetails;
        private final CheckBox mCheckBox;
        private boolean showExtraDetails = false;

        private ClickListener mClickListener;

        private DatabaseReference itemsRef;

        public ItemHolder(final View itemView, ClickListener listener) {
            super(itemView);
            //this.mID = (TextView) itemView.findViewById(R.id.tv_list_name);
            this.mName = (TextView) itemView.findViewById(R.id.tv_item_name);
            this.mBrand = (TextView) itemView.findViewById(R.id.tv_item_brand);
            this.mWeight = (TextView) itemView.findViewById(R.id.tv_item_weight_volume);
            this.mVolume = (TextView) itemView.findViewById(R.id.tv_item_weight_volume);
            //this.mBarcode = (TextView) itemView.findViewById(R.id.tv_list_name);
            this.mAssignee = (TextView) itemView.findViewById(R.id.tv_item_assignee);
            this.mQuantity = (TextView) itemView.findViewById(R.id.tv_item_quantity);
            this.mExtraDetails = (TextView) itemView.findViewById(R.id.tv_item_extra_details);
            this.mExtraDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (showExtraDetails) {
                        showExtraDetails = false;
                        mExtraDetails.setText(R.string.tv_expand_extra_details);
                        itemView.findViewById(R.id.extra_details).setVisibility(View.GONE);
                    } else {
                        showExtraDetails = true;
                        mExtraDetails.setText(R.string.tv_collaps_extra_details);
                        itemView.findViewById(R.id.extra_details).setVisibility(View.VISIBLE);

                    }
                }
            });
            this.mCheckBox = (CheckBox) itemView.findViewById(R.id.cb_item);
            this.mClickListener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }



        public void bindItem(DataSnapshot dataSnapshot, ItemInList itemInList, boolean done, float textSize, boolean isSelected){
            itemsRef = dataSnapshot.getRef();
            Item item = dataSnapshot.getValue(Item.class);
            Log.d("RROI", "" + item.getName());
            setName(item.getName());
            boolean isBrand = setBrand(item.getBrand());
            boolean isWeight = setWeight(item.getWeight());
            if (isBrand || isWeight){
                if (mExtraDetails.getVisibility() != View.VISIBLE) {
                    mExtraDetails.setVisibility(View.VISIBLE);
                }
            } else {
                if (mExtraDetails.getVisibility() != View.GONE) {
                    mExtraDetails.setVisibility(View.GONE);
                }
            }
            setVolume(item.getVolume());
            setAssignee(itemInList.getAssignee());
            setQuantity(itemInList.getQuantity());
            itemView.setTag(item.getID());
            setNameSize(textSize);
            setBrandSize(textSize);
            setVolumeSize(textSize);
            setAssigneeSize(textSize);
            if (isSelected) {
                itemView.setBackgroundResource(R.color.ripple_material_light);
            } else {
                itemView.setBackgroundResource(R.color.transparent);
            }
            mCheckBox.setChecked(done);



        }


        public void setName(String value) {
            mName.setText(value);
        }
        public boolean setBrand(String value) {
            if (value == null || value.equals("")) {
                mBrand.setVisibility(View.INVISIBLE);
                return false;
            } else {
                mBrand.setVisibility(View.VISIBLE);
                mBrand.setText(value);
                return true;
            }
        }
        public boolean setWeight(String value) {
            if (value == null || value.equals("")) {
                mWeight.setVisibility(View.INVISIBLE);
                return false;
            } else {
                mWeight.setVisibility(View.VISIBLE);
                mWeight.setText(value);
                return true;
            }
        }
        public void setVolume(String value) {
            if (value != null)
                mVolume.setText(value);
        }

        public void setAssignee(String value) {
            if (value != null)
                mAssignee.setText(value);
        }

        public void setQuantity(String value) {
            if (value != null)
                mQuantity.setText(value);
            else
                mQuantity.setText("1");
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
            if (mClickListener != null) {
                mClickListener.onItemClicked(getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if (mClickListener != null) {
                return mClickListener.onItemLongClicked(getPosition());
            }

            return false;
        }
        //public void setID(String name) { mListNameField.setText(name); }

        public interface ClickListener {
            public void onItemClicked(int position);
            public boolean onItemLongClicked(int position);
        }
    }
}
