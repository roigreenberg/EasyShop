package com.roigreenberg.easyshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String ANONYMOUS = "anonymous";
    private static final String TAG = "EasyShop";
    private RecyclerView mRecyclerView;

    private String mUsername;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private DatabaseReference mUserListsRef;
    private DatabaseReference mUserItemsRef;
    private FirebaseRecyclerAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        //mFirebaseStorage = FirebaseStorage.getInstance();
        //mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_lists);
        mRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);


        //DatabaseReference mUserListsRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    onSignedInInitialize(user.getDisplayName());

                    Log.d(TAG, "onAuthStateChanged:signed_in: " + user.getDisplayName() + " " + user.getUid());


                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                                            )
                                    )
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mUserListsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("OwnLists");

        mListAdapter = new FirebaseRecyclerAdapter<List, ListHolder>(List.class, R.id.tv_list_name, ListHolder.class, mUserListsRef) {
            @Override
            protected void populateViewHolder(ListHolder listHolder, List list, int position) {
                listHolder.setName(list.getListName());

            }
        };*/



        //DatabaseReference mUserListsRef = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mUserListsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("OwnLists");
        Toast.makeText(this, "Set list adapter", Toast.LENGTH_SHORT).show();
        mListAdapter = new FirebaseRecyclerAdapter<List, ListHolder>(List.class, R.layout.list, ListHolder.class, mUserListsRef) {
            @Override
            protected void populateViewHolder(ListHolder listHolder, List list, int position) {
                listHolder.setName(list.getListName());


                mUserItemsRef = mUserListsRef.child(list.getListID()).child("list");
                mListAdapter = new FirebaseRecyclerAdapter<Item, ItemHolder>(Item.class, R.layout.item, ItemHolder.class, mUserItemsRef) {
                    @Override
                    protected void populateViewHolder(ItemHolder itemHolder, Item item, int position) {
                        itemHolder.setName(item.getName());
                        itemHolder.setBrand(item.getBrand());
                        itemHolder.setWeight(item.getWeight());
                        itemHolder.setVolume(item.getVolume());
                    }
                };

                listHolder.setListAdapter(mListAdapter);

                listHolder.setButtonOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent addItemIntent = new Intent(MainActivity.this, AddItemActivity.class);
                        Log.d("ADD_ITEM", mUserItemsRef.toString());
                        Log.d("ADD_ITEM", mUserItemsRef.toString().substring(FirebaseDatabase.getInstance().getReference().toString().length()));
                        addItemIntent.putExtra("EXTRA_REF", mUserItemsRef.toString().substring(FirebaseDatabase.getInstance().getReference().toString().length()));
                        startActivity(addItemIntent);

                    }
                });
            }
        };



        mRecyclerView.setAdapter(mListAdapter);
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        if (mListAdapter != null)
            mListAdapter.cleanup();
        //detachDatabaseReadListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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

        int id = item.getItemId();

        if (id == R.id.add_new_list_menu_menu) {
            showAddingDialog();
            return true;
        }

        switch (item.getItemId()) {
            case R.id.add_new_list_menu_menu:
                showAddingDialog();
                return true;
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAddingDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_add_list_dialog, null, false);
        dialogBuilder.setView(dialogView);

        final EditText editText = (EditText) dialogView.findViewById(R.id.et_add_list);

        dialogBuilder.setTitle("Adding new List");
        dialogBuilder.setMessage("Input a List name");

        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (editText.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "Please input some texts!", Toast.LENGTH_SHORT).show();
                } else {

                    //add new item name to List
                    DatabaseReference ref = mUserListsRef.push();
                    ref.setValue(new List(ref.getKey(), editText.getText().toString().trim()));


                    //update ListView adapter
                    mListAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Adding sucessful!", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        mListAdapter.cleanup();
        //detachDatabaseReadListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListAdapter.cleanup();
    }
}
