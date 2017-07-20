package com.roigreenberg.easyshop;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.roigreenberg.easyshop.MainActivity.ITEMS;
import static com.roigreenberg.easyshop.MainActivity.LISTS;
import static com.roigreenberg.easyshop.MainActivity.ShareList;
import static com.roigreenberg.easyshop.MainActivity.USERS;
import static com.roigreenberg.easyshop.MainActivity.mUserID;

public class ListActivity extends AppCompatActivity implements ItemAdapter.ItemHolder.ClickListener{

    private RecyclerView mRecyclerView, mDoneRecyclerView;
    private DatabaseReference listRef;
    private Query query;
    private ItemAdapter itemAdapter, doneItemAdapter;
    private ActionModeCallback actionModeCallback = new ActionModeCallback(this);
    private ActionMode actionMode;
    private static boolean selectionMode = false;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String  listID;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            listID = extras.getString("EXTRA_REF");
        } else {
            return;
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_items);
        mRecyclerView.setNestedScrollingEnabled(false);
       // mRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager ownLayoutManager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        ownLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(ownLayoutManager);

        listRef = FirebaseDatabase.getInstance().getReference().child(LISTS).child(listID);
        itemAdapter = new ItemAdapter(this, listRef.child(ITEMS).orderByChild("name"), this, false);

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

        mDoneRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_done_items);
        //mDoneRecyclerView.setHasFixedSize(false);
        mDoneRecyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager doneLayoutManager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        doneLayoutManager.setAutoMeasureEnabled(true);
        mDoneRecyclerView.setLayoutManager(doneLayoutManager);

        listRef = FirebaseDatabase.getInstance().getReference().child(LISTS).child(listID);
        doneItemAdapter = new ItemAdapter(this, listRef.child("DoneItems").orderByChild("name"), this, true);

        mDoneRecyclerView.setAdapter(doneItemAdapter);

//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            // Called when a user swipes left or right on a ViewHolder
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//
//                String itemId = (String) viewHolder.itemView.getTag();
//
//                ((ItemAdapter.ItemHolder) viewHolder).setNameCond(ItemAdapter.ItemHolder.BOUGHT);
//                clearView(mRecyclerView,viewHolder);
//
//                /*FirebaseDatabase.getInstance().getReference()
//                        .child("Users")
//                        .child(mUserID)
//                        .child(list)
//                        .child(mListID)
//                        .child("list")
//                        .child(itemId).setValue(null);*/
//
//
//            }
//
//        }).attachToRecyclerView(mRecyclerView);

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
    protected void onStart() {
        super.onStart();

        mAdView = (AdView) findViewById(R.id.av_list);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("AB840131C8782DD57088B9B95755DBDB")
                .build();
        mAdView.loadAd(adRequest);
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
    public void onItemClicked(boolean doneList, int position) {
        if (actionMode != null) {
            toggleSelection(doneList, position);
        }

    }

    @Override
    public boolean onItemLongClicked(boolean doneList, int position) {
        if (actionMode == null) {
            selectionMode = true;
            itemAdapter.notifyDataSetChanged();
            doneItemAdapter.notifyDataSetChanged();
            actionMode = startSupportActionMode(actionModeCallback);

        }

        toggleSelection(doneList, position);

        return true;
    }

    public static boolean isSelectionMode() {
        return selectionMode;
    }

    /**
     * Toggle the selection state of an item.
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(boolean doneList, int position) {
        if (doneList)
            doneItemAdapter.toggleSelection(position);
        else
            itemAdapter.toggleSelection(position);
        int count = itemAdapter.getSelectedItemCount() +
                doneItemAdapter.getSelectedItemCount();

        if (count == 0) {
            selectionMode = false;
            itemAdapter.notifyDataSetChanged();
            doneItemAdapter.notifyDataSetChanged();
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();
        private List<String> users;
        private Context context;

        ActionModeCallback(Context context) {
            this.context = context;
        }

        @Override
        public boolean onCreateActionMode(final ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.selected_menu, menu);
            Log.d(TAG, listRef.child(USERS).toString());
            listRef.child(USERS).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Toast.makeText(ListActivity.this, dataSnapshot.getKey().toString(),Toast.LENGTH_LONG);
                    // Is better to use a List, because you don't know the size
                    // of the iterator returned by dataSnapshot.getChildren() to
                    // initialize the array
                    users = new ArrayList<String>();

                    final DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child(USERS);
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        //String userName = userSnapshot.child("Name").getValue(String.class);
                        String userID = userSnapshot.getKey();
                        Log.d("RROI", "onCreateActionMode " + userID);
                        usersDatabaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String userName = dataSnapshot.child("Name").getValue(String.class);
                                if (userName != null) {
                                    Log.d("RROI", "onCreateActionMode " + userName);
                                    users.add(userName);
                                }
                                else
                                    Log.d("RROI", "onCreateActionMode " + "fail to get user name");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            /*final Spinner userSpinner = (Spinner) menu.findItem(R.id.menu_users_spinner).getActionView();
            if (userSpinner == null)
                Log.e("RROI", "userSpinner in null");
            listRef.child(USERS).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Toast.makeText(ListActivity.this, dataSnapshot.getKey().toString(),Toast.LENGTH_LONG);
                    // Is better to use a List, because you don't know the size
                    // of the iterator returned by dataSnapshot.getChildren() to
                    // initialize the array
                    final List<String> users = new ArrayList<String>();

                    final DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child(USERS);
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        //String userName = userSnapshot.child("Name").getValue(String.class);
                        String userID = userSnapshot.getKey();
                        Log.d("RROI", userID);
                        usersDatabaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String userName = dataSnapshot.child("Name").getValue(String.class);
                                if (userName != null) {
                                    Log.d("RROI", userName);
                                    users.add(userName);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    ArrayAdapter<String> usersAdapter = new ArrayAdapter<String>(ListActivity.this, android.R.layout.simple_spinner_item, users);
                    usersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    userSpinner.setAdapter(usersAdapter);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("RROI", "on spinner");
                    for (int i = mRecyclerView.getAdapter().getItemCount() - 1; i >= 0; i--) {
                        ItemAdapter.ItemHolder itemHolder = (ItemAdapter.ItemHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
                        if (((ItemAdapter) mRecyclerView.getAdapter()).isSelected(i)){

                            Log.d("RROI", ((ItemAdapter) mRecyclerView.getAdapter()).getRef(i).toString());
                        }
                    }
                    Log.d(TAG, "menu_spinner");
                    mode.finish();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
*/

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_remove:
                    // TODO: actually remove items
                    final DatabaseReference items = FirebaseDatabase.getInstance().getReference().child(ITEMS);
                    for (int i = mRecyclerView.getAdapter().getItemCount() - 1; i >= 0; i--) {
                        ItemAdapter.ItemHolder itemHolder = (ItemAdapter.ItemHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
                        if (itemAdapter.isSelected(i)){
                            itemAdapter.getRef(i).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("RROI", "itemID " + dataSnapshot.child("itemID").getValue().toString());
                                    items.child(dataSnapshot.child("itemID").getValue().toString()).removeValue();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            itemAdapter.getRef(i).setValue(null);
                        }
                    }
                    for (int i = mDoneRecyclerView.getAdapter().getItemCount() - 1; i >= 0; i--) {
                        ItemAdapter.ItemHolder itemHolder = (ItemAdapter.ItemHolder) mDoneRecyclerView.findViewHolderForAdapterPosition(i);
                        if (doneItemAdapter.isSelected(i)){
                            doneItemAdapter.getRef(i).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("RROI", "itemID " + dataSnapshot.child("itemID").getValue().toString());
                                    items.child(dataSnapshot.child("itemID").getValue().toString()).removeValue();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            //items.child(doneItemAdapter.getRef(i).child("itemID")).removeValue();
                            doneItemAdapter.getRef(i).setValue(null);
                        }
                    }
                    selectionMode = false;
                    itemAdapter.notifyDataSetChanged();
                    doneItemAdapter.notifyDataSetChanged();
                    mode.finish();
                    return true;
                case R.id.menu_users_picker:
                    showAssigningDialog(mode);
                    return true;

                default:
                    return false;
            }
        }

        private void showAssigningDialog(final ActionMode mode) {

            final CharSequence[] charSequenceItems = users.toArray(new CharSequence[users.size()]);
            Log.d("RROI", charSequenceItems[0].toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Assign items to:");
            builder.setItems(charSequenceItems, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.e("RROI", "on picker");
                    for (int i = itemAdapter.getItemCount() - 1; i >= 0; i--) {
                        ItemAdapter.ItemHolder itemHolder = (ItemAdapter.ItemHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
                        if (itemAdapter.isSelected(i)){
                            Log.e("RROI", "Before " + itemAdapter.getRef(i).child("assignee").toString());
                            itemAdapter.getRef(i).child("assignee").setValue(charSequenceItems[which].toString());
                            itemAdapter.notifyItemChanged(i);
                            Log.e("RROI", "After " + itemAdapter.getRef(i).child("assignee").toString());
                        }
                    }
                    for (int i = doneItemAdapter.getItemCount() - 1; i >= 0; i--) {
                        ItemAdapter.ItemHolder itemHolder = (ItemAdapter.ItemHolder) mDoneRecyclerView.findViewHolderForAdapterPosition(i);
                        if (doneItemAdapter.isSelected(i)){
                            Log.e("RROI", "Before " + doneItemAdapter.getRef(i).child("assignee").toString());
                            doneItemAdapter.getRef(i).child("assignee").setValue(charSequenceItems[which].toString());
                            doneItemAdapter.notifyItemChanged(i);
                            Log.e("RROI", "After " + doneItemAdapter.getRef(i).child("assignee").toString());
                        }
                    }
                    Log.d(TAG, "menu_users_picker");
                    //itemAdapter.notifyDataSetChanged();
                   //doneItemAdapter.notifyDataSetChanged();
                    mode.finish();
                }
            });
            AlertDialog b = builder.create();
            b.show();
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            itemAdapter.clearSelection();
            doneItemAdapter.clearSelection();
            selectionMode = false;
            itemAdapter.notifyDataSetChanged();
            doneItemAdapter.notifyDataSetChanged();
            actionMode = null;
        }
    }
}
