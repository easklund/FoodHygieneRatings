package com.example.foodhygieneratings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class Result extends AppCompatActivity {
    private String TAG = "Result";

    private ArrayList<Establishment> establishments =  new ArrayList<Establishment>();
    public ArrayAdapter establishAdpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Result from you search");

        establishAdpt = new ArrayAdapter(this,android.R.layout.simple_selectable_list_item, establishments);
        ListView listview = (ListView)findViewById(R.id.ListViewResult);
        listview.setAdapter(establishAdpt);

        onRequestEstablishment(listview);
    }


    public void onRequestEstablishment(View view){
        Log.d(TAG, "onRequestEstablishment: ");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String allEstablishments = "http://api.ratings.food.gov.uk/Establishments/basic";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, allEstablishments, null,
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
        requestQueue.add(getRequest);
    }

    public void estabishmentsList(JSONObject items) throws JSONException {
        Log.d(TAG, "estabishmentsList: started");
        establishments.clear();
        Log.d(TAG, "estabishmentsList: cleared");
        JSONArray jArray = items.getJSONArray("establishments");
        Log.d(TAG, "estabishmentsList: before try");
        try{
            Log.d(TAG, "estabishmentsList: in Try");
            for(int i = 0; i < jArray.length(); i++){
                JSONObject jo = jArray.getJSONObject(i);
                JSONArray ja = jo.getJSONArray("links");
                HashMap<String, String> hm = new HashMap<String, String>();
                for(int j = 0; j < ja.length(); j++){
                    hm.put(ja.getJSONObject(0).getString("rel"), ja.getJSONObject(0).getString("href"));
                }
                establishments.add(new Establishment(jo.getInt("FHRSID"), jo.getString("LocalAuthorityBusinessID"), jo.getString("BusinessName"), jo.getString("BusinessType"), jo.getString("RatingValue"), jo.getString("RatingDate"), hm));
            }
        }catch (JSONException err){
            Log.d(TAG, "estabishmentsList, error: " + err);
        }
        establishAdpt.notifyDataSetChanged();
        Log.d(TAG, "estabishmentsList: done");
    }
}
