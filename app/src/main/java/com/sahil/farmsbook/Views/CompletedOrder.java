package com.sahil.farmsbook.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sahil.farmsbook.MainActivity;
import com.sahil.farmsbook.R;
import com.sahil.farmsbook.adapter.OrderAdapter;
import com.sahil.farmsbook.adapter.SellerOrderAdapter;
import com.sahil.farmsbook.model.Order;
import com.sahil.farmsbook.utilities.Server;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;


public class CompletedOrder extends AppCompatActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public static ArrayList<Order> listOrders = new ArrayList<>();
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private SellerOrderAdapter sellerOrderAdapter;
    private ProgressBar progressBar;
    LinearLayout emptyorder;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                String role =  SharedPreferenceSingleton.getInstance(CompletedOrder.this).getString("role","User Not Registered");
                if(role.equals("Customer")){
                    Intent intent = new Intent(CompletedOrder.this,CustomerActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(CompletedOrder.this,SellerActivity.class);
                    startActivity(intent);
                    finish();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_orders);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Completed Orders");
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // Refresh items
               prepareOrderItems();
            }
        });


        emptyorder = findViewById(R.id.emptyorder);





        String role =  SharedPreferenceSingleton.getInstance(CompletedOrder.this).getString("role","User Not Registered");


        if(role.equals("Customer")){
            recyclerView = findViewById(R.id.order_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            adapter = new OrderAdapter(this, listOrders);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            new GetOrders().execute();
        }else if(role.equals("Seller")){
            recyclerView = findViewById(R.id.order_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            sellerOrderAdapter = new SellerOrderAdapter(this, listOrders);
            recyclerView.setAdapter(sellerOrderAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            new GetOrders().execute();
        }


    }
    private void prepareOrderItems() {
         new GetOrders().execute();
    }



    @SuppressLint("StaticFieldLeak")
    class GetOrders extends AsyncTask<String, String, String> {
        HashMap<String, String> params = new HashMap<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String userid = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("_id","User Not Registered");
            String role = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("role","User Not Registered");

            params.put("role",role);

            if(role.equals("Customer")){
                params.put("customerId",userid);
            }else{
                params.put("sellerId",userid);
            }
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                result = Server.post(getResources().getString(R.string.getCompletedOrders),json);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String role = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("role","User Not Registered");

            mSwipeRefreshLayout.setRefreshing(false);


            try {

                JSONObject jsonObject =new JSONObject(s);

                String success = jsonObject.getString("success");
                if (!success.equals("true")) {

                    Toast.makeText(CompletedOrder.this,"Some error occured in getting Data..Please check your internet connection",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CompletedOrder.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    listOrders.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject post = jsonArray.getJSONObject(i);
                        Order current = new Order(post);
                        listOrders.add(current);

                        if(role.equals("Customer")){
                            adapter.notifyDataSetChanged();
                        }else{
                            sellerOrderAdapter.notifyDataSetChanged();
                        }

                    }
                    if(jsonArray.length()==0){

                        emptyorder.setVisibility(View.VISIBLE);
                        Toast.makeText(CompletedOrder.this,"No order currently in the list",Toast.LENGTH_SHORT).show();

                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


    }


}
