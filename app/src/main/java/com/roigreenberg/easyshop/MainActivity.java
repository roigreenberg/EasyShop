package com.roigreenberg.easyshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
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
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int RC_SIGN_IN = 1;
    private static final String ANONYMOUS = "anonymous";
    private static final String TAG = "EasyShop";
    private static final String LINK = "https://dwt9e.app.goo.gl/qL6j";
    public static final String OWN_LISTS = "OwnLists";
    public static final String SHARED_LISTS = "SharedLists";
    private RecyclerView mOwnListsRecyclerView, mSharedListsRecyclerView;

    private String mUsername;
    private String mUserID;

    private Float mTextSize;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private DatabaseReference mUserListsRef, mSharedListsRef;
    private DatabaseReference mUserItemsRef, mSharedItemsRef;
    private FirebaseRecyclerAdapter mOwnListAdapter, mSharedListAdapter;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;

        mTextSize = Float.parseFloat(getString(R.string.pref_size_default));

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        //mFirebaseStorage = FirebaseStorage.getInstance();
        //mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mOwnListsRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_own_lists);
        mOwnListsRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager ownLayoutManager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        ownLayoutManager.setAutoMeasureEnabled(true);
        mOwnListsRecyclerView.setLayoutManager(ownLayoutManager);

        mSharedListsRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_shared_lists);
        mSharedListsRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager sharedLayoutManager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        sharedLayoutManager.setAutoMeasureEnabled(true);
        mSharedListsRecyclerView.setLayoutManager(sharedLayoutManager);

        // Create an auto-managed GoogleApiClient with access to App Invites.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, this)
                .build();


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

        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, true).setResultCallback(
                new ResultCallback<AppInviteInvitationResult>() {
                    @Override
                    public void onResult(AppInviteInvitationResult result) {
                        Log.d(TAG, "getInvitation:onResult:" + result.getStatus());
                        if (result.getStatus().isSuccess()) {
                            // Extract information from the intent
                            Intent intent = result.getInvitationIntent();
                            String deepLink = AppInviteReferral.getDeepLink(intent);
                            AppInviteReferral.getInvitationId(intent);
                            Log.d("RROI", "0: " + deepLink);

                            // Because autoLaunchDeepLink = true we don't have to do anything
                            // here, but we could set that to false and manually choose
                            // an Activity to launch to handle the deep link here.
                            // ...
                            try {
                                deepLink = URLDecoder.decode(deepLink, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                Log.d("RROI", "1: error");
                                e.printStackTrace();
                            }
                            Log.d("RROI", "1: " + deepLink);
                            Uri uri = Uri.parse(deepLink);
                            String userId = uri.getQueryParameter("UserId");
                            String linkId = uri.getQueryParameter("LinkId");
                            String linkName = uri.getQueryParameter("LinkName");
                            Log.d("RROI", "1: " + "UserId= " + userId + "LinkId= " + linkId + "LinkName= " + linkName);
                            Toast.makeText(MainActivity.this, "UserId= " +userId + "LinkId= " + linkId + "LinkName= " + linkName, Toast.LENGTH_LONG).show();


                            //add new item name to List
                            DatabaseReference ref = mSharedListsRef.push();
                            ref.setValue(new List(userId, linkId, linkName));


                            //update ListView adapter
                            mSharedListAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Adding sucessful!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mUserListsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child(OWN_LISTS);

        mOwnListAdapter = new FirebaseRecyclerAdapter<List, ListHolder>(List.class, R.id.tv_list_name, ListHolder.class, mUserListsRef) {
            @Override
            protected void populateViewHolder(ListHolder listHolder, List list, int position) {
                listHolder.setName(list.getListName());

            }
        };*/



        //DatabaseReference mUserListsRef = FirebaseDatabase.getInstance().getReference().child("Users");
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setupSharedPreferences();
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
        mUserID = user.getUid();

        mUserListsRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(mUserID)
                .child(OWN_LISTS);
        Toast.makeText(this, "Set list adapter", Toast.LENGTH_SHORT).show();
        mOwnListAdapter = new FirebaseRecyclerAdapter<List, ListHolder>(
                List.class,
                R.layout.list,
                ListHolder.class,
                mUserListsRef) {
            @Override
            protected void populateViewHolder(ListHolder listHolder, List list, int position) {
                listHolder.setName(list.getListName());
                listHolder.setNameSize(mTextSize);

                mUserItemsRef = mUserListsRef.child(list.getListID()).child("list");
                mOwnListAdapter = new FirebaseRecyclerAdapter<Item, ItemHolder>(
                        Item.class,
                        R.layout.item,
                        ItemHolder.class,
                        mUserItemsRef) {
                    @Override
                    protected void populateViewHolder(ItemHolder itemHolder, Item item, int position) {
                        itemHolder.setName(item.getName());
                        itemHolder.setBrand(item.getBrand());
                        itemHolder.setWeight(item.getWeight());
                        itemHolder.setVolume(item.getVolume());
                        Log.d("RROI", item.getID());
                        itemHolder.itemView.setTag(item.getID());
                        itemHolder.setNameSize(mTextSize);
                        itemHolder.setBrandSize(mTextSize);
                        itemHolder.setVolumeSize(mTextSize);
                    }
                };

                listHolder.setListAdapter(mOwnListAdapter);
                listHolder.setAddItemOnClick(new AddItemOnClickListener(mUserItemsRef));

                listHolder.setShareOnClick(new ShareOnClickListener(list.getUserID(), list.getListID(), list.getListName()));

                list.setItemTouchHelper(listHolder, OWN_LISTS);
            }
        };

        mOwnListsRecyclerView.setAdapter(mOwnListAdapter);

        mSharedListsRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(mUserID)
                .child(SHARED_LISTS);
        Toast.makeText(this, "Set list adapter", Toast.LENGTH_SHORT).show();
        mSharedListAdapter = new FirebaseRecyclerAdapter<List, ListHolder>(
                List.class,
                R.layout.list,
                ListHolder.class,
                mSharedListsRef) {
            @Override
            protected void populateViewHolder(ListHolder listHolder, List list, int position) {
                listHolder.setName(list.getListName());
                listHolder.setNameSize(mTextSize);

                mSharedItemsRef = FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(list.getUserID())
                        .child(OWN_LISTS)
                        .child(list.getListID())
                        .child("list");
                mSharedListAdapter = new FirebaseRecyclerAdapter<Item, ItemHolder>(
                        Item.class,
                        R.layout.item,
                        ItemHolder.class,
                        mSharedItemsRef) {
                    @Override
                    protected void populateViewHolder(ItemHolder itemHolder, Item item, int position) {
                        itemHolder.setName(item.getName());
                        itemHolder.setBrand(item.getBrand());
                        itemHolder.setWeight(item.getWeight());
                        itemHolder.setVolume(item.getVolume());
                        itemHolder.itemView.setTag(item.getID());
                        itemHolder.setNameSize(mTextSize);
                        itemHolder.setBrandSize(mTextSize);
                        itemHolder.setVolumeSize(mTextSize);
                    }
                };

                listHolder.setListAdapter(mSharedListAdapter);

                listHolder.setAddItemOnClick(new AddItemOnClickListener(mSharedItemsRef));

                listHolder.setShareOnClick(new ShareOnClickListener(list.getUserID(), list.getListID(), list.getListName()));

                list.setItemTouchHelper(listHolder, SHARED_LISTS);
            }
        };

        mSharedListsRecyclerView.setAdapter(mSharedListAdapter);
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        if (mOwnListAdapter != null)
            mOwnListAdapter.cleanup();
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



        switch (item.getItemId()) {
            case R.id.add_new_list_menu_menu:
                showAddingDialog();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
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
                    ref.setValue(new List(mUserID, ref.getKey(), editText.getText().toString().trim()));


                    //update ListView adapter
                    mOwnListAdapter.notifyDataSetChanged();
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
        //mOwnListAdapter.cleanup();
        //detachDatabaseReadListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOwnListAdapter.cleanup();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadSizeFromSharedPreferences(sharedPreferences);

        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void loadSizeFromSharedPreferences(SharedPreferences sharedPreferences) {
        mTextSize = Float.parseFloat(sharedPreferences.getString(getString(R.string.pref_size_key),
                getString(R.string.pref_size_default)));
        if (mOwnListAdapter != null)
            mOwnListAdapter.notifyDataSetChanged();
        if (mSharedListAdapter != null)
            mSharedListAdapter.notifyDataSetChanged();
        //.setMinSizeScale(minSize);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_size_key))) {
            mTextSize = Float.parseFloat(sharedPreferences.getString(getString(R.string.pref_size_key), "1.0"));
            if (mOwnListAdapter != null)
                mOwnListAdapter.notifyDataSetChanged();
            if (mSharedListAdapter != null)
                mSharedListAdapter.notifyDataSetChanged();
        }
    }

    public class AddItemOnClickListener implements View.OnClickListener
    {

        final DatabaseReference ref;
        public AddItemOnClickListener(final DatabaseReference ref) {
            this.ref = ref;
        }

        @Override
        public void onClick(View v)
        {
            Intent addItemIntent = new Intent(MainActivity.this, AddItemActivity.class);
            addItemIntent.putExtra("EXTRA_REF", ref.toString().substring(
                    FirebaseDatabase.getInstance().getReference().toString().length()));
            startActivity(addItemIntent);
        }

    };

    public class ShareOnClickListener implements View.OnClickListener
    {

        final String userId, listId, listName;
        public ShareOnClickListener(final String userId, final String listId, final String listName) {

            this.userId = userId;
            this.listId = listId;
            this.listName = listName;
        }

        @Override
        public void onClick(View v)
        {
            Log.d("RROI", "onClick ShareOnClick");

            Intent sendIntent = new Intent();

            Uri BASE_URI = Uri.parse("https://easyshop/roigreenberg.com/add_new_list");

            Uri APP_URI = BASE_URI.buildUpon().appendQueryParameter("UserId", userId).
                    appendQueryParameter("LinkId", listId).
                    appendQueryParameter("LinkName", listName).build();


            String encodedUri = null;
            try {
                encodedUri = URLEncoder.encode(APP_URI.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Uri deepLink = Uri.parse("https://dwt9e.app.goo.gl/?link="+encodedUri+"&apn=com.roigreenberg.easyshop");

            String msg = "Hey, check this out: " + deepLink.toString();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }

    };

}
