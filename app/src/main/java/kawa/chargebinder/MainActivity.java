package kawa.chargebinder;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {

    MyAdapter adapter;
    MyDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PopulateList();
    }

    @Override protected void onResume() {
        super.onResume();
        PopulateList();
    }

    @Override
    public void onItemClick(View view, int position) {

        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        final Intent intent;
        intent = new Intent(this, BedViewActivity.class);
        intent.putExtra("BedNumber", adapter.getItem(position).bedNumber); //Optional parameters
        intent.putExtra("LastName", adapter.getItem(position).lastName); //Optional parameters
        intent.putExtra("FirstName", adapter.getItem(position).firstName); //Optional parameters
        this.startActivity(intent);
    }

    public void PopulateList(){
        // initialize db
        db = new MyDatabaseHelper(this);

        SQLiteDatabase readableDatabase = db.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                MyDatabaseHelper.KEY_BED,
                MyDatabaseHelper.KEY_LAST_NAME,
                MyDatabaseHelper.KEY_FIRST_NAME
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                MyDatabaseHelper.KEY_BED + " ASC";

        Cursor cursor = readableDatabase.query(
                MyDatabaseHelper.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        // data to populate the RecyclerView with
        ArrayList<RecycleRow> dbPatientArray = new ArrayList<>();

        while(cursor.moveToNext()) {
            String LastName = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.KEY_LAST_NAME));
            String FirstName = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.KEY_FIRST_NAME));
            int BedNumber = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.KEY_BED));
            RecycleRow dbPatient = new RecycleRow();
            dbPatient.firstName = FirstName;
            dbPatient.lastName = LastName;
            dbPatient.bedNumber = BedNumber;
            dbPatientArray.add(dbPatient);
        }
        cursor.close();

        // populate recyclerViewList from db
        ArrayList<RecycleRow> recycleRowPatientList = new ArrayList<>();
        for(int i = 0; i <44; i++){
            boolean foundPatient = false;
            for(int k = 0; k < dbPatientArray.size(); k++){
                if(dbPatientArray.get(k).bedNumber == (i+1)){
                    recycleRowPatientList.add(dbPatientArray.get(k));
                    foundPatient = true;
                    break;
                }
            }
            if (!foundPatient){
                RecycleRow emptyBed = new RecycleRow();
                emptyBed.lastName = "Empty";
                emptyBed.bedNumber = (i+1);
                recycleRowPatientList.add(emptyBed);
            }
        }

        // set up the RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this, recycleRowPatientList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        //db.close();
    }
}

