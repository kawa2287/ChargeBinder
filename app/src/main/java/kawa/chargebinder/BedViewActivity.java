package kawa.chargebinder;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class BedViewActivity extends AppCompatActivity {

    MyDatabaseHelper db;

    TextView BedTitle;
    TextInputEditText firstNameInput;
    TextInputEditText lastNameInput;

    int bedNumber;
    String lastName;
    String firstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        Intent intent = getIntent();
        setContentView(R.layout.activity_bed_view);


        BedTitle = (TextView) this.findViewById(R.id.tvBedNumber);
        firstNameInput = (TextInputEditText) this.findViewById(R.id.inputFirstName);
        lastNameInput = (TextInputEditText) this.findViewById(R.id.inputLastName);

        bedNumber = intent.getIntExtra("BedNumber", 1);
        lastName = intent.getStringExtra("LastName");
        firstName = intent.getStringExtra("FirstName");

        BedTitle.setText("Bed Number " + bedNumber);
        if (!lastName.equals("Empty")) {lastNameInput.setText(lastName);}
        firstNameInput.setText(firstName);

        RetrieveData();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // Checks whether a hardware keyboard is available
        View b = findViewById(R.id.btmButtonLayout);
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO)
        {
            b.setVisibility(View.GONE);

        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            b.setVisibility(View.VISIBLE);
        }
    }

    public RecycleRow RetrieveData(){
        RecycleRow dataPackage = new RecycleRow();

        // initialize db
        db = new MyDatabaseHelper(this);
        SQLiteDatabase database = db.getReadableDatabase();

        // How you want the results sorted in the resulting Cursor
        Cursor cursor = db.getBedData(bedNumber,database);

        if (cursor != null && cursor.getCount() != 0) {
            Log.d("made it here ", "made it here");
            Log.d("count of cursor ", String.valueOf(cursor.getCount()));
            cursor.moveToFirst();
            String temp;
            for (int i = 0; i <cursor.getCount(); i++){
                if(cursor.getString(i) == null){
                    temp = "empty";
                } else {
                    temp = cursor.getString(i);
                }
                Log.d("columnName ", cursor.getColumnName(i));
                Log.d("value ", temp);
            }
        }
        cursor.close();



        return dataPackage;
    }

    public void AddPatient(String firstName, String lastName, int bedNumber){

        // initialize db
        db = new MyDatabaseHelper(this);
        SQLiteDatabase database = db.getWritableDatabase();

        //check if bed exists
        String[] projection = { MyDatabaseHelper.KEY_BED };
        Cursor cursor = database.query(MyDatabaseHelper.TABLE_NAME, projection, null, null, null, null, null);
        boolean exists = false;

        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                if (cursor.getInt(cursor.getColumnIndex(MyDatabaseHelper.KEY_BED)) == bedNumber)
                {

                    //UPDATE DATABASE
                    // New value for one column
                    ContentValues updateValues = new ContentValues();
                    updateValues.put(MyDatabaseHelper.KEY_FIRST_NAME, firstName);
                    updateValues.put(MyDatabaseHelper.KEY_LAST_NAME, lastName);

                    // Which row to update, based on the title
                    String nameSelection = MyDatabaseHelper.KEY_BED + " LIKE ?";
                    String[] nameSelectionArgs = { (Integer.toString(bedNumber)) };

                    database.update(
                            MyDatabaseHelper.TABLE_NAME,
                            updateValues,
                            nameSelection,
                            nameSelectionArgs);

                    exists = true;
                    break;
                }
                cursor.moveToNext();
            }
        }
        cursor.close();

        if( exists == false){
            ContentValues values = new ContentValues();
            values.put(MyDatabaseHelper.KEY_LAST_NAME, lastName);
            values.put(MyDatabaseHelper.KEY_FIRST_NAME, firstName);
            values.put(MyDatabaseHelper.KEY_BED, bedNumber);

            // Insert the new row, returning the primary key value of the new row
            database.insert(MyDatabaseHelper.TABLE_NAME, null, values);
        }

        //database.close();
        Toast.makeText(this, "Database Saved ", Toast.LENGTH_SHORT).show();
    }



    public void SaveChanges(View view)
    {
        AddPatient(firstNameInput.getText().toString(), lastNameInput.getText().toString(), bedNumber);
        finish();

    }

    public void Cancel(View view)
    {
        finish();
    }

    public void ClearInfo(View view)
    {
        finish();
    }
}
