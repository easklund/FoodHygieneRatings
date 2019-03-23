package com.example.foodhygieneratings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {
    private String TAG = "Home";

    private ArrayList<Business> businesses =  new ArrayList<Business>();
    private ArrayAdapter businessAdpt;
    private String ratingOperator;
    private int rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        ratingOperator = "Equal";
        rating = -1;

        Spinner spinner = (Spinner) findViewById(R.id.BuissnesSpinner);
        businessAdpt = new ArrayAdapter(this,android.R.layout.simple_selectable_list_item, businesses);
        businessAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(businessAdpt);
        onRequestBusiness();

    }

    public void onRadioRatingOperator(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioEqual:
                if (checked)
                    Log.d(TAG, "onRadioRatingOperator: Equal");
                    ratingOperator = "Equal";
                    break;
            case R.id.radioLess:
                if (checked)
                    Log.d(TAG, "onRadioRatingOperator: LessThanOrEqual");
                    ratingOperator  = "LessThanOrEqual";
                    break;
            case R.id.radioHigher:
                if (checked)
                    Log.d(TAG, "onRadioRatingOperator: GreaterThanOrEqual");
                    ratingOperator  = "GreaterThanOrEqual";
                    break;
        }
    }
    public void onRadioRating(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio1:
                if (checked)
                    Log.d(TAG, "onRadioRating: 1");
                    rating = 1;
                    break;
            case R.id.radio2:
                if (checked)
                    Log.d(TAG, "onRadioRating: 2");
                    rating =2;
                    break;
            case R.id.radio3:
                if (checked)
                    Log.d(TAG, "onRadioRating: 3");
                    rating = 3;
                    break;
            case R.id.radio4:
                if (checked)
                    Log.d(TAG, "onRadioRating: 4");
                    rating = 4;
                    break;
            case R.id.radio5:
                if (checked)
                    Log.d(TAG, "onRadioRating: 5");
                    rating = 5;
                    break;
        }

    }

    public void StartSearch(View view){
        Intent intent = new Intent(this, Result.class);
        Spinner spinner = (Spinner) findViewById(R.id.BuissnesSpinner);
        int a = spinner.getSelectedItemPosition();
        intent.putExtra("BusinessName", businesses.get(a).getId());
        intent.putExtra("Rating", rating);
        intent.putExtra("RatingOperator", ratingOperator);
        startActivity(intent);
    }

    public void onRequestBusiness(){
        Log.d(TAG, "onRequestBusiness: started");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String allBusiness = "http://api.ratings.food.gov.uk/BusinessTypes/basic";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, allBusiness, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "good, onResponse: " + response);
                        try{
                            fillBusinessList(response);
                        }catch (JSONException err){}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "bad, onErrorResponse: " + error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-api-version", "2");
                params.put("accept", "application/json");
                return params;
            }
        };
        requestQueue.add(getRequest);
    }

    public void fillBusinessList(JSONObject items) throws JSONException {
        Log.d(TAG, "fillBusinessList: started");
        businesses.clear();
        JSONArray jArray = items.getJSONArray("businessTypes");
        try{
            Log.d(TAG, "fillBusinessList: in Try");
            for(int i = 0; i < jArray.length(); i++){
                JSONObject jo = jArray.getJSONObject(i);
                JSONArray ja = jo.getJSONArray("links");
                HashMap<String, String> hm = new HashMap<String, String>();
                for(int j = 0; j < ja.length(); j++){
                    hm.put(ja.getJSONObject(0).getString("rel"), ja.getJSONObject(0).getString("href"));
                }
                businesses.add(new Business(jo.getInt("BusinessTypeId"), jo.getString("BusinessTypeName"), hm));
            }
        }catch (JSONException err){
            Log.d(TAG, "fillBusinessList, error: " + err);
        }
        businessAdpt.notifyDataSetChanged();
        Log.d(TAG, "fillBusinessList: done");
    }

}
