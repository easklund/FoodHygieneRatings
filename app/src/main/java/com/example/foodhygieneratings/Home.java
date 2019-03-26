package com.example.foodhygieneratings;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    private ArrayList<Business> businesses = new ArrayList<Business>();
    private ArrayAdapter businessAdpt;

    private ArrayList<Region> regions = new ArrayList<Region>();
    private ArrayAdapter regionsAdpt;

    private ArrayList<Authority> authorities = new ArrayList<Authority>();
    private ArrayAdapter authoritiesAdpt;

    private String ratingOperator;
    private int rating;

    private double longitude;
    private double latitude;
    private final int FINE_LOCATION_PERMISSION = 1;
    LocationManager locManager;
    LocationListener locListener;

    private int radius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        ratingOperator = "Equal";
        rating = -1;

        Spinner BuissnesSpinner = (Spinner) findViewById(R.id.BuissnesSpinner);
        businessAdpt = new ArrayAdapter(this, android.R.layout.simple_selectable_list_item, businesses);
        businessAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BuissnesSpinner.setAdapter(businessAdpt);
        onRequestBusiness();

        Spinner RegionSpinner = (Spinner) findViewById(R.id.RegionSpinner);
        regionsAdpt = new ArrayAdapter(this, android.R.layout.simple_selectable_list_item, regions);
        regionsAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        RegionSpinner.setAdapter(regionsAdpt);
        onRequestRegions();

        Spinner AuthoritySpinner = (Spinner) findViewById(R.id.AuthoritySpinner);
        authoritiesAdpt = new ArrayAdapter(this, android.R.layout.simple_selectable_list_item, authorities);
        authoritiesAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AuthoritySpinner.setAdapter(authoritiesAdpt);
        onRequestAuthorities();

        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Log.d(TAG, "onLocationChanged: logitude: " + longitude);
                Log.d(TAG, "onLocationChanged: latitude: " + latitude);
                ((TextView) findViewById(R.id.longitude)).setText(String.format("%.2f",
                        location.getLongitude()));
                ((TextView) findViewById(R.id.latitude)).setText(String.format("%.2f",
                        location.getLatitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this).setMessage("The application is about to request acces to your location.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestLocPerms();
                            }
                        }).create().show();
            }
        }else {
            requestLocPerms();
        }

        updateLocationText();

    }
    public void requestLocPerms(){
        ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    attachLocManager();
                }
                return;
            }
        }
    }

    public void attachLocManager() {
        try {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 0, locListener);
            Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
            Log.d(TAG, "attachLocManager: GOT LOCATION");

        } catch (SecurityException err) {
            Log.wtf("LocMan", "LocManager error");
        }
    }
    private void updateLocationText(){
        TextView posLongitude = findViewById(R.id.longitude);
        TextView posLatitude = findViewById(R.id.latitude);
        posLongitude.setText(Double.toString(longitude));
        posLatitude.setText(Double.toString(latitude));

    }
    public void ToFavourites(View view) {
        Intent intent = new Intent(this, Fave.class);
        startActivity(intent);
    }


    public void StartSearch(View view) {
        Intent intent = new Intent(this, Result.class);
        Spinner businessSpinner = (Spinner) findViewById(R.id.BuissnesSpinner);
        Spinner regionSpinner = (Spinner) findViewById(R.id.RegionSpinner);
        Spinner authoritySpinner = (Spinner) findViewById(R.id.AuthoritySpinner);

        int business = businessSpinner.getSelectedItemPosition();
        int region = regionSpinner.getSelectedItemPosition();
        int authority = authoritySpinner.getSelectedItemPosition();

        EditText editRadius = findViewById(R.id.EditRadius);
        if (editRadius.getText().toString().equals("")) {
            radius = -1;
        } else {
            radius = Integer.parseInt(editRadius.getText().toString());
        }

        attachLocManager();

        intent.putExtra("BusinessName", businesses.get(business).getId());
        intent.putExtra("RegionId", regions.get(region).getId());
        intent.putExtra("AuthorityId", authorities.get(authority).getLocalAuthorityId());
        intent.putExtra("Rating", rating);
        intent.putExtra("RatingOperator", ratingOperator);
        intent.putExtra("Longitude", longitude);
        intent.putExtra("Latitude", latitude);
        intent.putExtra("Radius", radius);
        startActivity(intent);
    }


    public void onRadioRatingOperator(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioEqual:
                if (checked)
                    Log.d(TAG, "onRadioRatingOperator: Equal");
                ratingOperator = "Equal";
                break;
            case R.id.radioLess:
                if (checked)
                    Log.d(TAG, "onRadioRatingOperator: LessThanOrEqual");
                ratingOperator = "LessThanOrEqual";
                break;
            case R.id.radioHigher:
                if (checked)
                    Log.d(TAG, "onRadioRatingOperator: GreaterThanOrEqual");
                ratingOperator = "GreaterThanOrEqual";
                break;
        }
    }

    public void onRadioRating(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio1:
                if (checked)
                    Log.d(TAG, "onRadioRating: 1");
                rating = 1;
                break;
            case R.id.radio2:
                if (checked)
                    Log.d(TAG, "onRadioRating: 2");
                rating = 2;
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

    public void onRequestBusiness() {
        Log.d(TAG, "onRequestBusiness: started");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String allBusiness = "http://api.ratings.food.gov.uk/BusinessTypes/basic";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, allBusiness, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "good, onResponse: " + response);
                        try {
                            fillBusinessList(response);
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
        requestQueue.add(getRequest);
    }

    public void fillBusinessList(JSONObject items) throws JSONException {
        Log.d(TAG, "fillBusinessList: started");
        businesses.clear();
        JSONArray jArray = items.getJSONArray("businessTypes");
        try {
            Log.d(TAG, "fillBusinessList: in Try");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jo = jArray.getJSONObject(i);
                JSONArray ja = jo.getJSONArray("links");
                HashMap<String, String> hm = new HashMap<String, String>();
                for (int j = 0; j < ja.length(); j++) {
                    hm.put(ja.getJSONObject(0).getString("rel"), ja.getJSONObject(0).getString("href"));
                }
                businesses.add(new Business(jo.getInt("BusinessTypeId"), jo.getString("BusinessTypeName"), hm));
            }
        } catch (JSONException err) {
            Log.d(TAG, "fillBusinessList, error: " + err);
        }
        businessAdpt.notifyDataSetChanged();
        Log.d(TAG, "fillBusinessList: done");
    }

    private void onRequestRegions() {
        Log.d(TAG, "onRequestRegions: Started");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String allRegions = "http://api.ratings.food.gov.uk/Regions/basic";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, allRegions, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "good, onResponse: " + response);
                        try {
                            fillRegionList(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
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
        requestQueue.add(getRequest);
    }

    public void fillRegionList(JSONObject items) throws JSONException {
        Log.d(TAG, "fillRegionList: Started");
        regions.clear();
        regions.add(new Region(-1, "All regions", "empty"));
        JSONArray jArray = items.getJSONArray("regions");
        try {
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jo = jArray.getJSONObject(i);
                if (!jo.getString("name").equals("Scotland")) {
                    regions.add(new Region(jo.getInt("id"), jo.getString("name"), jo.getString("nameKey")));
                }
            }
        } catch (JSONException err) {
            Log.d(TAG, "fillRegionList: error: " + err);
        }
        regionsAdpt.notifyDataSetChanged();
        Log.d(TAG, "fillRegionList: done");
    }

    private void onRequestAuthorities() {
        Log.d(TAG, "onRequestAuthorities: started");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String allAuthorities = "http://api.ratings.food.gov.uk/Authorities/basic";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, allAuthorities, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "good, onResponse: " + response);
                        try {
                            fillAuthorityList(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
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
        requestQueue.add(getRequest);
    }

    public void fillAuthorityList(JSONObject items) throws JSONException {
        Log.d(TAG, "fillAuthorityList: Started");
        authorities.clear();
        authorities.add(new Authority(-1, "empty", "All Authorities"));
        JSONArray jArray = items.getJSONArray("authorities");
        try {
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jo = jArray.getJSONObject(i);
                authorities.add(new Authority(jo.getInt("LocalAuthorityId"), jo.getString("LocalAuthorityIdCode"), jo.getString("Name")));
            }
        } catch (JSONException err) {
            Log.d(TAG, "fillAuthorityList: error: " + err);
        }
        authoritiesAdpt.notifyDataSetChanged();
        Log.d(TAG, "fillAuthorityList: done");
    }

}
