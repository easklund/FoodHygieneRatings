package com.example.foodhygieneratings;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class Fave extends AppCompatActivity {
    private String TAG = "Fave";

    private ArrayList<Favourite> favourites =  new ArrayList<Favourite>();
    private ArrayAdapter favAdpt;

    private FavouriteList db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_fave);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favourites");
        db = Room.databaseBuilder(getApplicationContext(),FavouriteList.class, "favourites").allowMainThreadQueries().build();

        favAdpt = new ArrayAdapter(this,android.R.layout.simple_selectable_list_item, favourites);
        ListView listview = (ListView)findViewById(R.id.ListViewFavourites);
        listview.setAdapter(favAdpt);

        fillFavouriteList();
        final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Favourite clickedItem = (Favourite) favAdpt.getItem(position);
                Intent intent = new Intent(Fave.this, Details.class);
                intent.putExtra("FHRSID", clickedItem.getFHRSID());
                startActivity(intent);
            }
        };
        listview.setOnItemClickListener(itemClickListener);
    }
    private void fillFavouriteList(){
        Favourite[] favList = db.favDao().retrieveAllFave();
        Log.d(TAG, "fillFavouriteList: " + favList);
        for(int i = 0; i < favList.length; i++){
            favourites.add(favList[i]);
        }
        favAdpt.notifyDataSetChanged();

    }
    public void addFavouriteClick(View view){
        Log.d(TAG, "addFavouriteClick: ");
        String fhrsid = "";//((EditText) findViewById(R.id.EditFHRSID)).getText().toString();
        if(!fhrsid.equals("")){
            String name = "";//((EditText) findViewById(R.id.EditName)).getText().toString();

            Favourite newFave = new Favourite();
            newFave.setFHRSID(Integer.parseInt(fhrsid));
            newFave.setName(name);
            if(db.favDao().retrieveFavfromFHRSID(newFave.getFHRSID())==null){
                db.favDao().insertFavourite(newFave);
                Log.d(TAG, "addFavouriteClick: Inserted in the db");
            }else{
                db.favDao().deleteFavFRomFHRSID(newFave.getFHRSID());
                Log.d(TAG, "addFavouriteClick: It already exist in the db");
            }
            Log.d(TAG, "addFavouriteClick: " + db.favDao().retrieveAllFave().length);
        }else{
            Log.d(TAG, "addFavouriteClick: You need to enter an ID");
        }
    }
}
