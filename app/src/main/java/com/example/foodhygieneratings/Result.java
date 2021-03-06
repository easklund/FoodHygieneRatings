package com.example.foodhygieneratings;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class Result extends AppCompatActivity {
    private String TAG = "Result";

    private ArrayList<Establishment> establishments =  new ArrayList<Establishment>();
    private ArrayAdapter establishAdpt;

    private int businessTypeId;
    private int rate;
    private String rateOperator;
    private int regionId;
    private int authorityId;
    private double longitude;
    private double latitude;
    private int radius;
    private String searchText;
    private String sortOption;

    private String url;
    int page;
    int maxPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Result from you search");
        page = 1;
        sortOption = "alpha";

        businessTypeId = getIntent().getIntExtra("BusinessName", 0);
        Log.d(TAG, "onCreate: businessTypeId; " + businessTypeId);
        rate = getIntent().getIntExtra("Rating", -1);
        Log.d(TAG, "onCreate: rate; " + rate);
        rateOperator = getIntent().getStringExtra("RatingOperator");
        Log.d(TAG, "onCreate: rate operator; " + rateOperator);
        regionId = getIntent().getIntExtra("RegionId", -1);
        Log.d(TAG, "onCreate: region id: " + regionId);
        authorityId = getIntent().getIntExtra("AuthorityId", -1);
        Log.d(TAG, "onCreate: authority id: " + authorityId);
        longitude = getIntent().getDoubleExtra("Longitude", 0);
        Log.d(TAG, "onCreate: longitude: " + longitude);
        latitude = getIntent().getDoubleExtra("Latitude", 0);
        Log.d(TAG, "onCreate: latitude: " + latitude);
        radius = getIntent().getIntExtra("Radius", -1);
        Log.d(TAG, "onCreate: raduis: " + radius);
        searchText = getIntent().getStringExtra("SearchText");
        Log.d(TAG, "onCreate: searchText: " + searchText);


        establishAdpt = new ArrayAdapter(this,android.R.layout.simple_selectable_list_item, establishments);
        ListView listview = (ListView)findViewById(R.id.ListViewResult);
        listview.setAdapter(establishAdpt);

        onRequestEstablishment(listview);

        final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Establishment clickedItem = (Establishment) establishAdpt.getItem(position);
                Intent intent = new Intent(Result.this, Details.class);
                intent.putExtra("FHRSID", clickedItem.getFHRSID());
                startActivity(intent);
            }
        };
        listview.setOnItemClickListener(itemClickListener);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ListView listview = (ListView)findViewById(R.id.ListViewResult);
        TextView loading = findViewById(R.id.LoadingView);
        switch (item.getItemId()){
            case R.id.item1:
                loading.setBackgroundColor(Color.parseColor("#A9D0F5"));
                loading.setText("Loading next page");
                sortOption = "Relevance";
                onRequestEstablishment(listview);
                return true;
            case R.id.item2:
                loading.setBackgroundColor(Color.parseColor("#A9D0F5"));
                loading.setText("Loading next page");
                sortOption = "rating";
                onRequestEstablishment(listview);
                return true;
            case R.id.item3:
                loading.setBackgroundColor(Color.parseColor("#A9D0F5"));
                loading.setText("Loading next page");
                sortOption = "desc_rating";
                onRequestEstablishment(listview);
                return true;
            case R.id.item4:
                loading.setBackgroundColor(Color.parseColor("#A9D0F5"));
                loading.setText("Loading next page");
                sortOption = "alpha";
                onRequestEstablishment(listview);
                return true;
            case R.id.item5:
                loading.setBackgroundColor(Color.parseColor("#A9D0F5"));
                loading.setText("Loading next page");
                sortOption = "desc_alpha";
                onRequestEstablishment(listview);
                return true;
            case R.id.item6:
                loading.setBackgroundColor(Color.parseColor("#A9D0F5"));
                loading.setText("Loading next page");
                sortOption = "Distance";
                onRequestEstablishment(listview);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getUrl(){
        url = "http://api.ratings.food.gov.uk/Establishments?pageNumber=" + page +"&pageSize=20";
        if(!searchText.equals("")){
            url = url + "&name=" + searchText;
        }
        if(businessTypeId!=-1){
            url =  url + "&businessTypeId=" + businessTypeId;
        }
        if(rate!=-1){
            url =  url + "&ratingKey=" + rate;

            if(rateOperator!="Equal"){
                url = url + "&ratingOperatorKey=" + rateOperator;
            }
        }
        if(authorityId!=-1){
           url =  url + "&localAuthorityId=" + authorityId;
        }
        if(radius!=-1){
            url =  url + "&longitude=" + longitude + "&latitude=" + latitude + "&maxDistanceLimit=" + radius;
        }
        url = url + "&sortOptionKey="+sortOption;
        return url;
    }

    public void onRequestEstablishment(View view){
        Log.d(TAG, "onRequestEstablishment: ");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String allEstablishments = getUrl();
        Log.d(TAG, "onRequestEstablishment: allEstablishments" + allEstablishments);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, allEstablishments, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "good, onResponse: " + response);
                        try{
                            estabishmentsList(response);
                            getPageNumber(response);
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
    public void getPageNumber(JSONObject meta)throws JSONException {
        JSONObject jObject = meta.getJSONObject("meta");
        try{
            maxPage = jObject.getInt("totalPages");
            TextView pageView = findViewById(R.id.PageView);
            pageView.setText("Page " + page + "/" + maxPage);
        }catch (JSONException err){
            Log.d(TAG, "estabishmentsList, error: " + err);
        }
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
        TextView loading = findViewById(R.id.LoadingView);
        loading.setBackgroundColor(Color.parseColor("#ffffff"));
        loading.setText("");
        establishAdpt.notifyDataSetChanged();
        TextView pageView = findViewById(R.id.PageView);
        pageView.setText("Page " + page + "/" + maxPage);
        Log.d(TAG, "estabishmentsList: done");
    }

    public void nextPage(View view){
        Log.d(TAG, "nextPage: ");
        if(page < maxPage){
            page ++;
            TextView loading = findViewById(R.id.LoadingView);
            loading.setBackgroundColor(Color.parseColor("#A9D0F5"));
            loading.setText("Loading next page");
            onRequestEstablishment((ListView)findViewById(R.id.ListViewResult));
            TextView pageView = findViewById(R.id.PageView);
            pageView.setText("Page " + page + "/" + maxPage);
        }
    }
    public void previousPage(View view){
        Log.d(TAG, "previousPage: ");
        if(page > 1){
            page --;
            TextView loading = findViewById(R.id.LoadingView);
            loading.setBackgroundColor(Color.parseColor("#A9D0F5"));
            loading.setText("Loading previous page");
            onRequestEstablishment((ListView)findViewById(R.id.ListViewResult));
            TextView pageView = findViewById(R.id.PageView);
            pageView.setText("Page " + page + "/" + maxPage);
        }
    }
}
