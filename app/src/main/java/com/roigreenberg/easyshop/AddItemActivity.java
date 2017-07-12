package com.roigreenberg.easyshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.roigreenberg.easyshop.MainActivity.ITEMS;
import static com.roigreenberg.easyshop.MainActivity.mUsername;

public class AddItemActivity extends AppCompatActivity {

    private static boolean showExtraDetails = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Spinner spinner = (Spinner) findViewById(R.id.sp_item_quantity);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.item_quantity_numbers, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        EditText nameEditText= (EditText) findViewById(R.id.et_item_name);
        nameEditText.requestFocus();
        //InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT);

    }

    public void onClickAddItem(View view) {

        DatabaseReference databaseReference;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("EXTRA_REF");
            if (value == null)
                return;

            databaseReference = FirebaseDatabase.getInstance().getReference().child(value);
            //The key argument here must match that used in the other activity
        } else {
            return;
        }

        String itemName = ((EditText) findViewById(R.id.et_item_name)).getText().toString();
        if (itemName.length() == 0) {
            return;
        }

        String itemQuantity = ((Spinner) findViewById(R.id.sp_item_quantity)).getSelectedItem().toString();

        String itemBrand = null;
        String itemWeight = null;
        if (showExtraDetails) {
            itemBrand = ((EditText) findViewById(R.id.et_item_brand)).getText().toString();
            itemWeight = ((EditText) findViewById(R.id.et_item_weight)).getText().toString();
        }

        Toast.makeText(this, "add " + itemName + " " + itemBrand + " " + itemWeight, Toast.LENGTH_LONG).show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(ITEMS).push();
        ref.setValue(new Item(ref.getKey(), itemName, itemBrand, itemWeight, null, null, null));
        //ref.child(USERS).child(mUserID).setValue("admin");

        DatabaseReference listRef = databaseReference.child(ITEMS).push();
        listRef.setValue(new ItemInList(ref.getKey(), itemQuantity, null, mUsername, itemName));

        finish();
    }

    public void onClickAddItemExtraDetails(View view) {
        if (showExtraDetails) {
            showExtraDetails = false;
            ((ImageView) view.findViewById(R.id.ib_add_item_extra_details)).setImageResource(android.R.drawable.arrow_down_float);
            this.findViewById(R.id.add_item_extra_details).setVisibility(View.GONE);
        } else {
            showExtraDetails = true;
            ((ImageView) view.findViewById(R.id.ib_add_item_extra_details)).setImageResource(android.R.drawable.arrow_up_float);
            this.findViewById(R.id.add_item_extra_details).setVisibility(View.VISIBLE);

        }
    }
}
