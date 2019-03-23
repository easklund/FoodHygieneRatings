package com.example.foodhygieneratings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Result from you search");

        FHRSID = getIntent().getIntExtra("FHRSID",0);
        onRequestEstablishment();
        
    }

    public void onRequestEstablishment(){
        Log.d(TAG, "onRequestEstablishment: ");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String establishment = "http://api.ratings.food.gov.uk/Establishments/";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, establishment + FHRSID, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "good, onResponse: " + response);
                        try{
                            estabishmentsList(response);
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

    public void estabishmentsList(JSONObject items) throws JSONException {
        Log.d(TAG, "estabishmentsList: started");
        TextView name = findViewById(R.id.NameView);
        TextView type = findViewById(R.id.TypeView);
        TextView id = findViewById(R.id.IDView);
        TextView rate = findViewById(R.id.RateView);
        Log.d(TAG, "estabishmentsList: " + items.getString("BusinessName"));
        try{
            Log.d(TAG, "estabishmentsList: in Try");
            Log.d(TAG, "estabishmentsList: " + items.getString("BusinessName"));
            Log.d(TAG, "estabishmentsList: " + items.getString("BusinessType"));
            Log.d(TAG, "estabishmentsList: " + items.getInt("FHRSID"));
            name.setText("Name: " + items.getString("BusinessName"));
            type.setText("Type: " + items.getString("BusinessType"));
            String s = "ID: " + Integer.toString(items.getInt("FHRSID"));
            id.setText(s);
            rate.setText("Rate: " + items.getString("RatingValue"));

        }catch (JSONException err){
            Log.d(TAG, "estabishmentsList, error: " + err);
        }
        Log.d(TAG, "estabishmentsList: done");
    }
}
