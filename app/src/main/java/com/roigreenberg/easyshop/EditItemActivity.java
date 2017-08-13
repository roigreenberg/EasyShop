package com.roigreenberg.easyshop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roigreenberg.easyshop.models.Item;

import static com.roigreenberg.easyshop.MainActivity.ITEMS;

public class EditItemActivity extends AppCompatActivity {

    private static boolean showExtraDetails = false;
    private EditText nameEditText;
    private EditText brandEditText;
    private EditText weightEditText;
    private DatabaseReference databaseReference;
    private Item item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("EXTRA_REF");
            if (value == null)
                return;

            Log.v("RROI", value);

            databaseReference = FirebaseDatabase.getInstance().getReference().child(ITEMS).child(value);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.v("RROI", dataSnapshot.toString());
                    item = (Item) dataSnapshot.getValue(Item.class);
                    nameEditText = (EditText) findViewById(R.id.et_edit_item_name);
                    brandEditText = (EditText) findViewById(R.id.et_edit_item_brand);
                    weightEditText = (EditText) findViewById(R.id.et_edit_item_weight);
                    nameEditText.setText(item.getName());
                    brandEditText.setText(item.getBrand());
                    weightEditText.setText(item.getWeight());

                    nameEditText.requestFocus();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            return;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

//        Spinner spinner = (Spinner) findViewById(R.id.sp_edit_item_quantity);
// Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.item_quantity_numbers, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
//        spinner.setAdapter(adapter);


        //InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT);

    }

    public void onClickEditItem(View view) {



        String itemName = ((EditText) findViewById(R.id.et_edit_item_name)).getText().toString();
        if (itemName.length() == 0) {
            return;
        }
        item.setName(itemName);

        //String itemQuantity = ((Spinner) findViewById(R.id.sp_edit_item_quantity)).getSelectedItem().toString();

        if (showExtraDetails) {
            item.setBrand(((EditText) findViewById(R.id.et_edit_item_brand)).getText().toString());
            item.setWeight(((EditText) findViewById(R.id.et_edit_item_weight)).getText().toString());
        }

        //Toast.makeText(this, "edit " + itemName + " " + itemBrand + " " + itemWeight, Toast.LENGTH_LONG).show();

        databaseReference.setValue(item);
        //ref.child(USERS).child(mUserID).setValue("admin");

        //DatabaseReference listRef = databaseReference.child(ITEMS).push();
        //listRef.setValue(new ItemInList(ref.getKey(), itemQuantity, null, mUsername, itemName));
        finish();
    }

    public void onClickEditItemExtraDetails(View view) {
        if (showExtraDetails) {
            showExtraDetails = false;
            ((ImageView) view.findViewById(R.id.ib_edit_item_extra_details)).setImageResource(android.R.drawable.arrow_down_float);
            this.findViewById(R.id.edit_item_extra_details).setVisibility(View.GONE);
        } else {
            showExtraDetails = true;
            ((ImageView) view.findViewById(R.id.ib_edit_item_extra_details)).setImageResource(android.R.drawable.arrow_up_float);
            this.findViewById(R.id.edit_item_extra_details).setVisibility(View.VISIBLE);

        }
    }
}
