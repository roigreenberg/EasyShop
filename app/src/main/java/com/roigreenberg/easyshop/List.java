package com.roigreenberg.easyshop;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.roigreenberg.easyshop.MainActivity.OWN_LISTS;

/**
 * Created by Roi on 04/06/2017.
 */

public class List {

    private String mUserID;
    private String mListID;
    private String mListName;

    public List() {
    }

    public List(String mUserID, String mListID, String mListName) {
        this.mUserID= mUserID;
        this.mListID = mListID;
        this.mListName = mListName;
    }
    public String getUserID() { return mUserID; }

    public void setUserID(String mUserID) { this.mUserID = mUserID; }

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

    public void setItemTouchHelper(ListHolder listHolder, final String list) {
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
                // Here is where you'll implement swipe to delete

                // COMPLETED (1) Construct the URI for the item to delete
                //[Hint] Use getTag (from the adapter code) to get the id of the swiped item
                // Retrieve the id of the task to delete
                String itemId = (String) viewHolder.itemView.getTag();
                Log.d("RROI", itemId);
                        FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(mUserID)
                        .child(list)
                        .child(mListID)
                        .child("list")
                        .child(itemId).setValue(null);
                Log.d("RROI", FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(mUserID)
                        .child(list)
                        .child(mListID)
                        .child("list")
                        .child(itemId).toString());


            }
        }).attachToRecyclerView(listHolder.getListItemsRecyclerView());
    }

}
