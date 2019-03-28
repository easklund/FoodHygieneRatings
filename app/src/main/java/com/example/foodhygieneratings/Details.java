package com.example.foodhygieneratings;

import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Details extends AppCompatActivity {
    private String TAG = "Details";
    private int FHRSID;
    private String name;
    private String rating;
    private FavouriteList db;
    Favourite newFave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Detail view");

        FHRSID = getIntent().getIntExtra("FHRSID", 0);
        newFave = new Favourite();
        newFave.setFHRSID(FHRSID);
        onRequestEstablishment();

        db = Room.databaseBuilder(getApplicationContext(), FavouriteList.class, "favourites").allowMainThreadQueries().build();
        setButtonText();
    }

    public void onRequestEstablishment() {
        Log.d(TAG, "onRequestEstablishment: ");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String establishment = "http://api.ratings.food.gov.uk/Establishments/";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, establishment + FHRSID, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "good, onResponse: " + response);
                        try {
                            estabishmentsList(response);
                        } catch (JSONException err) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "bad, onErrorResponse: " + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-api-version", "2");
                params.put("accept", "application/json");
                return params;
            }
        };
        getRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 800000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 800000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(getRequest);
    }

    public void setRatingBar() {
        RatingBar rateBar = findViewById(R.id.ratingBar);
        rateBar.setRating(Integer.parseInt(rating));
    }
    public void setRatingBarZero(){
        RatingBar rateBar = findViewById(R.id.ratingBar);
        rateBar.setRating(0);
    }

    public void estabishmentsList(JSONObject items) throws JSONException {
        Log.d(TAG, "estabishmentsList: started");
        TextView name = findViewById(R.id.NameView);
        TextView type = findViewById(R.id.TypeView);
        TextView id = findViewById(R.id.IDView);
        TextView rate = findViewById(R.id.RateView);
        TextView adress = findViewById(R.id.IDAdress);
        TextView postCode = findViewById(R.id.IDPostCode);
        Log.d(TAG, "estabishmentsList: " + items.getString("BusinessName"));
        try {
            Log.d(TAG, "estabishmentsList: in Try");
            Log.d(TAG, "estabishmentsList: " + items.getString("BusinessName"));
            Log.d(TAG, "estabishmentsList: " + items.getString("BusinessType"));
            Log.d(TAG, "estabishmentsList: " + items.getInt("FHRSID"));
            this.name = items.getString("BusinessName").toString();
            getSupportActionBar().setTitle(this.name);
            name.setText("Name: " + items.getString("BusinessName"));
            type.setText("Type: " + items.getString("BusinessType"));
            adress.setText("Adress: " + items.getString("AddressLine1"));
            postCode.setText("Post code: " + items.getString("PostCode"));
            String s = "ID: " + Integer.toString(items.getInt("FHRSID"));
            id.setText(s);
            rating = items.getString("RatingValue");
            rate.setText("Rate: " + rating);
            if(rating.equals("1")||rating.equals("2")||rating.equals("3")||rating.equals("4")||rating.equals("5")||rating.equals("0")){
                setRatingBar();
            }else{
                setRatingBarZero();
            }
            newFave.setName(this.name);

        } catch (JSONException err) {
            Log.d(TAG, "estabishmentsList, error: " + err);
        }
        Log.d(TAG, "estabishmentsList: done");
    }

    public void AddToFavourites(View view) {
        Log.d(TAG, "AddToFavourites: ");
        if (db.favDao().retrieveFavfromFHRSID(newFave.getFHRSID()) == null) {
            db.favDao().insertFavourite(newFave);
            Log.d(TAG, "AddToFavourites: Inserted in the db");
        } else {
            db.favDao().deleteFavFRomFHRSID(newFave.getFHRSID());
            Log.d(TAG, "AddToFavourites: It already exist in the db");
        }
        Log.d(TAG, "AddToFavourites: " + db.favDao().retrieveAllFave().length);
        setButtonText();
    }
    private void setButtonText(){
        Log.d(TAG, "setButtonText: ");
        Button button = findViewById(R.id.AddToFavourites);
        if (db.favDao().retrieveFavfromFHRSID(newFave.getFHRSID()) == null) {
            Log.d(TAG, "setButtonText: Inserted in the db");
            button.setText("Add to favourites");
        } else {
            Log.d(TAG, "setButtonText: It already exist in the db");
            button.setText("Remove from favourites");
        }
    }
}
