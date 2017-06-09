package com.roigreenberg.easyshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.roigreenberg.easyshop.MainActivity.ITEMS;

public class AddItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
    }

    public void onClickAddItem(View view) {

        DatabaseReference databaseReference;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("EXTRA_REF");
            databaseReference = FirebaseDatabase.getInstance().getReference().child(value);
            //The key argument here must match that used in the other activity
        } else {
            return;
        }

        String itemName = ((EditText) findViewById(R.id.et_item_name)).getText().toString();
        if (itemName.length() == 0) {
            return;
        }

        String itemBrand = ((EditText) findViewById(R.id.et_item_brand)).getText().toString();
        String itemWeight = ((EditText) findViewById(R.id.et_item_weight)).getText().toString();

        Toast.makeText(this, "add " + itemName + " " + itemBrand + " " + itemWeight, Toast.LENGTH_LONG).show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(ITEMS).push();
        ref.setValue(new Item(ref.getKey(), itemName, itemBrand, itemWeight, null, null, null));
        //ref.child(USERS).child(mUserID).setValue("admin");

        DatabaseReference listRef = databaseReference.child(ITEMS).push();
        listRef.setValue(new ItemInList(ref.getKey(), null, null, null));

        finish();
    }
}
