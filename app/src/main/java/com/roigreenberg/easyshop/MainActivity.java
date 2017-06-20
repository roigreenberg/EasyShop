package com.roigreenberg.easyshop;

import android.content.Context;
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
import android.widget.ProgressBar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    public static final String ITEMS = "Items";
    public static final String LISTS = "Lists";
    public static final String USERS = "Users";
    public static final String SHARED_LISTS = "SharedLists";
    private RecyclerView mOwnListsRecyclerView, mSharedListsRecyclerView;

    public static String mUsername;
    public static String mUserID;

    public Float mTextSize;

    // Firebase instance variables
    private static FirebaseDatabase mFirebaseDatabase = null;
    private static DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private ProgressBar mLoadingIndicator;

    private DatabaseReference mUserListsRef;
    private DatabaseReference mUserItemsRef;
    private FirebaseRecyclerAdapter mOwnListAdapter;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsername = ANONYMOUS;

        mTextSize = Float.parseFloat(getString(R.string.pref_size_default));

        if (mFirebaseDatabase == null) {
            Toast.makeText(this, "here", Toast.LENGTH_LONG);
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseReference = mFirebaseDatabase.getReference();
        }
        // Initialize Firebase components

        mFirebaseAuth = FirebaseAuth.getInstance();
        //mFirebaseStorage = FirebaseStorage.getInstance();
        //mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mOwnListsRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_lists);
        mOwnListsRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager ownLayoutManager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        ownLayoutManager.setAutoMeasureEnabled(true);
        mOwnListsRecyclerView.setLayoutManager(ownLayoutManager);

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
                    mUsername = user.getDisplayName();
                    Log.d("RROI", mUsername);
                    onSignedInInitialize();

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
                        if (result.getStatus().isSuccess()) {
                            // Extract information from the intent
                            Intent intent = result.getInvitationIntent();
                            String deepLink = AppInviteReferral.getDeepLink(intent);
                            AppInviteReferral.getInvitationId(intent);

                            // Because autoLaunchDeepLink = true we don't have to do anything
                            // here, but we could set that to false and manually choose
                            // an Activity to launch to handle the deep link here.
                            // ...
                            try {
                                deepLink = URLDecoder.decode(deepLink, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Uri uri = Uri.parse(deepLink);
                            String userID = uri.getQueryParameter("UserID"); //TODO is it needed?
                            String listID = uri.getQueryParameter("ListID"); //TODO change name
                            if (userID == null || listID == null)
                                return;
                            Toast.makeText(MainActivity.this, "UserID= " +userID + "ListID= " + listID, Toast.LENGTH_LONG).show();


                            //add new item name to SList
                            DatabaseReference userRef = mDatabaseReference.child(USERS).child(mUserID).child(LISTS).push();
                            userRef.setValue(new ListForUser(listID));

                            DatabaseReference ref = mDatabaseReference.child(LISTS).child(listID);
                            ref.child(USERS).child(mUserID).setValue("user");

                            //update ListView adapter
                            mOwnListAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Adding sucessful!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //DatabaseReference mUserListsRef = FirebaseDatabase.getInstance().getReference().child("Users");

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

    private void onSignedInInitialize() {
        showLoading();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mUserID = user.getUid();

        mDatabaseReference.child(USERS).child(mUserID).child("Name").setValue(mUsername);

        mUserListsRef = mDatabaseReference
                .child(USERS)
                .child(mUserID)
                .child(LISTS);

        mUserItemsRef = mDatabaseReference
                .child(USERS)
                .child(mUserID)
                .child(ITEMS);

        Toast.makeText(this, "Set list adapter", Toast.LENGTH_SHORT).show();
        mOwnListAdapter = new FirebaseRecyclerAdapter<ListForUser, ListHolder>(
                ListForUser.class,
                R.layout.list,
                ListHolder.class,
                mUserListsRef) {
            @Override
            protected void populateViewHolder(final ListHolder listHolder, ListForUser list, int position) {

                final String listID = list.getListID();

                final DatabaseReference listsRef = mDatabaseReference.child(LISTS).child(listID);

                listsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final SList listData = dataSnapshot.getValue(SList.class);
                        listHolder.setName(listData.getListName());
                        listHolder.setNameSize(mTextSize);
                        listHolder.setShareOnClick(new ShareOnClickListener(mUserID, listID, listData.getListName()));
                        listHolder.mListNameField.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent ListIntent = new Intent(MainActivity.this, ListActivity.class);
                                ListIntent.putExtra("EXTRA_REF", listID);
                                startActivity(ListIntent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mOwnListsRecyclerView.setAdapter(mOwnListAdapter);
        showListDataView();

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

        dialogBuilder.setTitle("Adding new SList");
        dialogBuilder.setMessage("Input a SList name");

        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (editText.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "Please input some texts!", Toast.LENGTH_SHORT).show();
                } else {

                    //add new item name to SList
                    DatabaseReference ref = mDatabaseReference.child(LISTS).push();
                    ref.setValue(new SList(ref.getKey(), editText.getText().toString().trim()));
                    ref.child(USERS).child(mUserID).setValue("admin");

                    DatabaseReference userRef = mDatabaseReference.child(USERS).child(mUserID).child(LISTS).push();
                    userRef.setValue(new ListForUser(ref.getKey()));


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
        if (mOwnListAdapter != null)
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
        //.setMinSizeScale(minSize);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_size_key))) {
            mTextSize = Float.parseFloat(sharedPreferences.getString(getString(R.string.pref_size_key), "1.0"));
            if (mOwnListAdapter != null)
                mOwnListAdapter.notifyDataSetChanged();
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
                    mDatabaseReference.toString().length()));
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
            ShareList(getBaseContext(), userId, listId);
        }

    };

    public static void ShareList(Context context, String userId, String listId){
        Intent sendIntent = new Intent();

        Uri BASE_URI = Uri.parse("https://easyshop/roigreenberg.com/add_new_list");

        Uri APP_URI = BASE_URI.buildUpon().appendQueryParameter("UserID", userId).
                appendQueryParameter("ListID", listId).build();


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
        context.startActivity(sendIntent);
    }




    /**
     * This method will make the View for the weather data visible and hide the error message and
     * loading indicator.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showListDataView() {
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the weather data is visible */
        mOwnListsRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the loading indicator visible and hide the weather View and error
     * message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showLoading() {
        /* Then, hide the weather data */
        mOwnListsRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }



}
