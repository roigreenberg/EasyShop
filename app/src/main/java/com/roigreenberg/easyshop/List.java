package com.roigreenberg.easyshop;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Roi on 04/06/2017.
 */

public class List {

    private String mListID;
    private String mListName;

    public List() {
    }

    public List(String mListID, String mListName) {
        this.mListID = mListID;
        this.mListName = mListName;
    }


    public String getListID() {
        return mListID;
    }

    public void setListID(String mListID) {
        this.mListID = mListID;
    }

    public String getListName() {
        return mListName;
    }

    public void setListName(String mListName) { this.mListName = mListName; }

    public void setItemTouchHelper(final ListHolder listHolder, final String list) {
        /*
                 Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
                 An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
                 and uses callbacks to signal when a user is performing these actions.
                 */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                String itemId = (String) viewHolder.itemView.getTag();

                ((ItemHolder) viewHolder).setNameCond(ItemHolder.BOUGHT);
                clearView(listHolder.getListItemsRecyclerView(),viewHolder);

                /*FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(mUserID)
                        .child(list)
                        .child(mListID)
                        .child("list")
                        .child(itemId).setValue(null);*/


            }

        }).attachToRecyclerView(listHolder.getListItemsRecyclerView());
    }

}
