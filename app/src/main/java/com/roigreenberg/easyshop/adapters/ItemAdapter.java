package com.roigreenberg.easyshop.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.roigreenberg.easyshop.R;
import com.roigreenberg.easyshop.models.Item;
import com.roigreenberg.easyshop.models.ItemInList;

import static com.roigreenberg.easyshop.ListActivity.isSelectionMode;
import static com.roigreenberg.easyshop.MainActivity.ITEMS;

/**
 * Created by Roi on 16/06/2017.
 */

public  class ItemAdapter extends SelectableItemAdapter {

    private ItemHolder.ClickListener clickListener;
    private boolean done;
    private Context context;

    public ItemAdapter(Context context, Query query, ItemHolder.ClickListener clickListener, boolean done) {
        super(ItemInList.class,
                R.layout.item,
                ItemAdapter.ItemHolder.class,
                query);
        this.clickListener = clickListener;
        this.done = done;
        this.context = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ItemHolder(context, view, clickListener);
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
                itemHolder.bindItem(dataSnapshot, itemInListRef, item, done, /*mTextSize*/15, isSelected(position));

                itemHolder.mImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (done) {
                            Log.e("RROI", "cb->uncheck "  + itemInListRef.toString());
                            itemInListRef.getParent().getParent().child("Items").push().setValue(item);
                            itemInListRef.removeValue();

                        } else {
                            Log.e("RROI", "cb->check " + itemInListRef.toString());
                            itemInListRef.getParent().getParent().child("DoneItems").push().setValue(item);
                            itemInListRef.removeValue();
                        }
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
        private final ImageButton mImageButton;
        private boolean showExtraDetails = false;
        private boolean doneList;
        private boolean isBrand, isWeight;

        private ClickListener mClickListener;

        private DatabaseReference itemsRef, itemInListRef;
        private Context context;
        private int current_quantity;

        public ItemHolder(final Context context, final View itemView, ClickListener listener) {
            super(itemView);
            this.context = context;
            //this.mID = (TextView) itemView.findViewById(R.id.tv_list_name);
            this.mName = (TextView) itemView.findViewById(R.id.tv_item_name);
            this.mName.setSelected(true);
            this.mBrand = (TextView) itemView.findViewById(R.id.tv_item_brand);
            this.mWeight = (TextView) itemView.findViewById(R.id.tv_item_weight_volume);
            this.mVolume = (TextView) itemView.findViewById(R.id.tv_item_weight_volume);
            //this.mBarcode = (TextView) itemView.findViewById(R.id.tv_list_name);
            this.mAssignee = (TextView) itemView.findViewById(R.id.tv_item_assignee);
            this.mQuantity = (TextView) itemView.findViewById(R.id.tv_item_quantity);
            this.mQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showQuantityDialog();
                }
            });
            this.mExtraDetails = (TextView) itemView.findViewById(R.id.tv_item_extra_details);
            this.mExtraDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("RROI", "extra on click " + showExtraDetails);
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
            this.mImageButton = (ImageButton) itemView.findViewById(R.id.ib_item);
            this.mClickListener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }



        public void bindItem(DataSnapshot dataSnapshot, DatabaseReference itemInListRef, ItemInList itemInList, boolean done, float textSize, boolean isSelected){
            itemsRef = dataSnapshot.getRef();
            Item item = dataSnapshot.getValue(Item.class);
            doneList = done;
            this.itemInListRef = itemInListRef;
            Log.d("RROI", "" + item.getName());
            setName(item.getName());
            isBrand = setBrand(item.getBrand());
            isWeight = setWeight(item.getWeight());

            setVolume(item.getVolume());
            setAssignee(itemInList.getAssignee());
            if (itemInList.getQuantity() == null)
                itemInList.setQuantity("1");
            current_quantity = Integer.parseInt(itemInList.getQuantity());
            setQuantity(itemInList.getQuantity());
            itemView.setTag(item.getID());
            setNameSize(textSize);
            setBrandSize(textSize);
            setVolumeSize(textSize);
            setAssigneeSize(textSize);
            if (isSelected) {
                itemView.setBackgroundResource(R.color.selectedItem);
            } else {
                itemView.setBackgroundResource(R.color.transparent);
            }

            setSelectedMode();



        }

        public void setSelectedMode() {
            if (isSelectionMode()) {
                if (mExtraDetails.getVisibility() != View.GONE) {
                    mExtraDetails.setVisibility(View.GONE);
                }
                itemView.findViewById(R.id.extra_details).setVisibility(View.GONE);
                mImageButton.setVisibility(View.GONE);
            } else {
                if (isBrand || isWeight){
                    if (mExtraDetails.getVisibility() != View.VISIBLE) {
                        mExtraDetails.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mExtraDetails.getVisibility() != View.GONE) {
                        mExtraDetails.setVisibility(View.GONE);
                    }
                }
                Log.e("RROI", "bind " + showExtraDetails);
                if (showExtraDetails) {
                    mExtraDetails.setText(R.string.tv_collaps_extra_details);
                    itemView.findViewById(R.id.extra_details).setVisibility(View.VISIBLE);
                } else {
                    mExtraDetails.setText(R.string.tv_expand_extra_details);
                    itemView.findViewById(R.id.extra_details).setVisibility(View.GONE);
                }
                mImageButton.setVisibility(View.VISIBLE);
                if (doneList) {
                    mImageButton.setContentDescription(mImageButton.getContext().getString(R.string.add_to_list));
                    mImageButton.setBackgroundResource(R.drawable.ic_add_shopping_cart_black_24dp);
                } else {
                    mImageButton.setContentDescription(mImageButton.getContext().getString(R.string.remove_from_list));
                    mImageButton.setBackgroundResource(R.drawable.ic_remove_shopping_cart_black_24dp);
                }
            }
        }

        public void setName(String value) {
            mName.setText(value);
            if (doneList)
                mName.setPaintFlags(mName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
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
                mClickListener.onItemClicked(doneList, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if (mClickListener != null) {
                return mClickListener.onItemLongClicked(doneList, getPosition());
            }

            return false;
        }
        //public void setID(String name) { mListNameField.setText(name); }

        public interface ClickListener {
            public void onItemClicked(boolean done, int position);
            public boolean onItemLongClicked(boolean done, int position);
        }

        private void showQuantityDialog() {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            final View dialogView = inflater.inflate(R.layout.layout_edit_item_quantity_dialog, null, false);
            dialogBuilder.setView(dialogView);

            final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.np_item_quantity);
            numberPicker.setMaxValue(100); // max value 100
            numberPicker.setMinValue(1);   // min value 0
            numberPicker.setWrapSelectorWheel(false);

            numberPicker.setValue(current_quantity);



            dialogBuilder.setTitle("Item quantity");
            dialogBuilder.setMessage("Choose item quantity:");

            dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //add new item name to ShoppingList
                    DatabaseReference ref = itemInListRef.child("quantity");
                    ref.setValue(Long.toString(numberPicker.getValue()));

                    //update ListView adapter
                    //mOwnListAdapter.notifyDataSetChanged();
                    //Toast.makeText(MainActivity.this, "Adding sucessful!", Toast.LENGTH_SHORT).show();

                }
            });
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do nothing, just close this dialog
                }
            });
            AlertDialog b = dialogBuilder.create();
            b.show();
        }
    }
}
