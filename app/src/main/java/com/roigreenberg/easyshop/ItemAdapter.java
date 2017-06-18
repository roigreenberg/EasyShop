package com.roigreenberg.easyshop;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.roigreenberg.easyshop.MainActivity.ITEMS;

/**
 * Created by Roi on 16/06/2017.
 */

public  class ItemAdapter extends SelectableItemAdapter {

    private ItemHolder.ClickListener clickListener;

    public ItemAdapter(DatabaseReference ref, ItemHolder.ClickListener clickListener) {
        super(ItemInList.class,
                R.layout.item,
                ItemAdapter.ItemHolder.class,
                ref);
        this.clickListener = clickListener;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ItemHolder(view, clickListener);
    }

    @Override
    protected void populateViewHolder(final ItemHolder itemHolder, final ItemInList item, final int position) {
        final String itemID = item.getItemID();

        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child(ITEMS).child(itemID);

        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Item itemData = dataSnapshot.getValue(Item.class);
                itemHolder.bindItem(itemData, item.getAssignee(), /*mTextSize*/15, isSelected(position));
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
        //private final TextView mBarcode;
        //private final TextView[] mImage;

        private ClickListener mClickListener;

        public ItemHolder(View itemView, ClickListener listener) {
            super(itemView);
            //this.mID = (TextView) itemView.findViewById(R.id.tv_list_name);
            this.mName = (TextView) itemView.findViewById(R.id.tv_item_name);
            this.mBrand = (TextView) itemView.findViewById(R.id.tv_item_brand);
            this.mWeight = (TextView) itemView.findViewById(R.id.tv_item_weight_volume);
            this.mVolume = (TextView) itemView.findViewById(R.id.tv_item_weight_volume);
            //this.mBarcode = (TextView) itemView.findViewById(R.id.tv_list_name);
            this.mAssignee = (TextView) itemView.findViewById(R.id.tv_item_assignee);

            this.mClickListener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }



        public void bindItem(Item item, String assignee, float textSize, boolean isSelected){
            Log.d("RROI", "" + item.getName());
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
            if (isSelected)
                setWeight("Chosen");

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
