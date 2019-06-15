package com.sahil.farmsbook.Views;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import com.sahil.farmsbook.R;
import com.sahil.farmsbook.adapter.SellerProductListAdapter;
import com.sahil.farmsbook.model.Product;
import com.sahil.farmsbook.utilities.Server;

public class SellerProfile extends AppCompatActivity {
    ArrayList<Product> listProduct = new ArrayList<>();
    private SellerProductListAdapter sellerProductListAdapter;
    private RecyclerView recyclerView;
    private Button sendnow;
    private String sellerName, sellerId, orderId, shop;
    private TextView sellername;
    EditText search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Seller Profile");
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sellerProductListAdapter = new SellerProductListAdapter(this,listProduct);
        recyclerView.setAdapter(sellerProductListAdapter);

        sellername = findViewById(R.id.sellerName);

        sellerName = getIntent().getStringExtra("name");
        sellerId = getIntent().getStringExtra("sellerId");
        orderId = getIntent().getStringExtra("orderId");
        shop = getIntent().getStringExtra("shop");

        sellername.setText(shop);

        sendnow = findViewById(R.id.sendNow);

        search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });

        new GetProductList().execute();


        sendnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: update order
                new AddSeller().execute();
            }
        });

    }
    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<Product> filterdNames = new ArrayList<>();

        for(int i=0;i<listProduct.size();i++){
            if (listProduct.get(i).getName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(listProduct.get(i));
            }
        }

        //calling a method of the adapter class and passing the filtered list
        sellerProductListAdapter.filterList(filterdNames);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SellerProfile.this,SelectSeller.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    public class GetProductList extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            params.put("sellerId",sellerId);

            progress=new ProgressDialog(SellerProfile.this);
            progress.setMessage("Retrieving Product List..");
            progress.setIndeterminate(true);
            progress.setProgress(ProgressDialog.STYLE_HORIZONTAL);
            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            try {
                Log.e("s:",s);
                JSONObject jsonObject =new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                listProduct.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject post = jsonArray.getJSONObject(i);
                    Product current = new Product(post);
                    listProduct.add(current);
                }
                sellerProductListAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                System.out.println(json);
                result = Server.post(getResources().getString(R.string.getProductBySellerId),json);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }



            System.out.println("Result:" + result);
            return result;
        }
    }


    @SuppressLint("StaticFieldLeak")
    public class AddSeller extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            params.put("_id",orderId);
            params.put("sellerId",sellerId);

            progress=new ProgressDialog(SellerProfile.this);
            progress.setMessage("Adding Seller for the Order..");
            progress.setIndeterminate(true);
            progress.setProgress(ProgressDialog.STYLE_HORIZONTAL);
            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                if(jsonObject.has("success")){
                    if(jsonObject.getString("success").equals("true")){
                        Toast.makeText(SellerProfile.this,"Seller Added for the Order",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SellerProfile.this,YourOrders.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(SellerProfile.this,"Error occured while updating the seller",Toast.LENGTH_SHORT).show();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                System.out.println(json);
                result = Server.post(getResources().getString(R.string.addSeller),json);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }



            System.out.println("Result:" + result);
            return result;
        }
    }




}
