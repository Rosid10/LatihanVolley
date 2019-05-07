package com.rosid.latihanvolley;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String id, name, username, email,street,suite,city,zipcode,addr,lat,lng,ge;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RecyclerView recycleView;
    private UsserAdapter adapter;
    private ArrayList<Usser> usersArrayList;
    private Button button;
    HttpURLConnection connection = null;
    BufferedReader reader = null;
    ProgressDialog progressDialog;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RequestQueue rq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rq = Volley.newRequestQueue(MainActivity.this);
        usersArrayList = new ArrayList<>();

        button = (Button) findViewById(R.id.btn_data);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
               makeJsonArrayRequest();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               makeJsonArrayRequest();
            }
        });

    }
    public void makeJsonArrayRequest(){
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String reqURL = "http://jsonplaceholder.typicode.com/users";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, reqURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject userDetail = response.getJSONObject(i);
                        id = userDetail.getString("id");
                        name = userDetail.getString("name");
                        username = userDetail.getString("username");
                        email = userDetail.getString("email");

                        JSONObject address = userDetail.getJSONObject("address");
                        street = address.getString("street");
                        suite = address.getString("suite");
                        city = address.getString("city");
                        zipcode = address.getString("zipcode");

                        JSONObject geo = address.getJSONObject("geo");
                        lat = geo.getString("lat");
                        lng = geo.getString("lng");

                        addr = street + ", " + suite + ", " + city + ", " + zipcode;
                        ge = lat + ", " + lng;

                        usersArrayList.add(new Usser(id, name, username, email, addr, ge));

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSwipeRefreshLayout.setRefreshing(false);
                progressDialog.dismiss();
                recycleView = (RecyclerView) findViewById(R.id.recycle_view);

                adapter = new UsserAdapter(usersArrayList);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);

                recycleView.setLayoutManager(layoutManager);

                recycleView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                progressDialog.dismiss();
                Log.i("Volley error :", String.valueOf(error));
            }
        });
        rq.add(jsonArrayRequest);

    }
    }