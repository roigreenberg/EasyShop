package com.roigreenberg.easyshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.roigreenberg.easyshop.MainActivity.ITEMS;
import static com.roigreenberg.easyshop.MainActivity.LISTS;
import static com.roigreenberg.easyshop.MainActivity.ShareList;
import static com.roigreenberg.easyshop.MainActivity.mUserID;

public class ListActivity extends AppCompatActivity implements ItemAdapter.ItemHolder.ClickListener{

    private RecyclerView mRecyclerView;
    private DatabaseReference listRef;
    private ItemAdapter itemAdapter;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_items_2);
        mRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager ownLayoutManager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        ownLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(ownLayoutManager);

        String  listID;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            listID = extras.getString("EXTRA_REF");
        } else {
            return;
        }

        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        listRef = FirebaseDatabase.getInstance().getReference().child(LISTS).child(listID);
        itemAdapter = new ItemAdapter(listRef.child(ITEMS), this);

        mRecyclerView.setAdapter(itemAdapter);
        listRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final SList listData = dataSnapshot.getValue(SList.class);
                setTitle(listData.getListName());
                //listData.setItemTouchHelper(listHolder, LISTS);
                //listHolder.setNameSize(mTextSize);
                //listHolder.setShareOnClick(new MainActivity.ShareOnClickListener(mUserID, listID, listData.getListName()));



            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                String itemId = (String) viewHolder.itemView.getTag();

                ((ItemAdapter.ItemHolder) viewHolder).setNameCond(ItemAdapter.ItemHolder.BOUGHT);
                clearView(mRecyclerView,viewHolder);

                /*FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(mUserID)
                        .child(list)
                        .child(mListID)
                        .child("list")
                        .child(itemId).setValue(null);*/


            }

        }).attachToRecyclerView(mRecyclerView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addItemIntent = new Intent(ListActivity.this, AddItemActivity.class);
                addItemIntent.putExtra("EXTRA_REF", listRef.toString().substring(
                        FirebaseDatabase.getInstance().getReference().toString().length()));
                startActivity(addItemIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        switch (item.getItemId()) {
            case R.id.share_list_menu:
                ShareList(this, mUserID, listRef.getKey());


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }

    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);

        return true;
    }

    /**
     * Toggle the selection state of an item.
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        itemAdapter.toggleSelection(position);
        int count = itemAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.selected_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_remove:
                    // TODO: actually remove items
                    Log.d(TAG, "menu_remove");
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            itemAdapter.clearSelection();
            actionMode = null;
        }
    }
}
