package com.example.foodhygieneratings;

import android.arch.persistence.room.Room;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Fave extends AppCompatActivity {
    private String TAG = "Fave";
    private FavouriteList db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_fave);
        db = Room.databaseBuilder(getApplicationContext(),FavouriteList.class, "favourites").allowMainThreadQueries().build();
    }

    public void addFavouriteClick(View view){
        Log.d(TAG, "addFavouriteClick: ");
        String fhrsid = ((EditText) findViewById(R.id.EditFHRSID)).getText().toString();
        if(!fhrsid.equals("")){
            String name = ((EditText) findViewById(R.id.EditName)).getText().toString();

            Favourite newFave = new Favourite();
            newFave.setFHRSID(Integer.parseInt(fhrsid));
            newFave.setName(name);
            if(db.favDao().retrieveFavfromFHRSID(newFave.getFHRSID())==null){
                db.favDao().insertFavourite(newFave);
                Log.d(TAG, "addFavouriteClick: Inserted in the db");
            }else{
                Log.d(TAG, "addFavouriteClick: It already exist in the db");
            }
            Log.d(TAG, "addFavouriteClick: " + db.favDao().retrieveAllFave().length);
        }else{
            Log.d(TAG, "addFavouriteClick: You need to enter an ID");
        }
        
    }
}
