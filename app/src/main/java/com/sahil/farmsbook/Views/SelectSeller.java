package com.sahil.farmsbook.Views;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.sahil.farmsbook.MainActivity;
import com.sahil.farmsbook.R;
import com.sahil.farmsbook.adapter.SellerListAdapter;
import com.sahil.farmsbook.model.User;
import com.sahil.farmsbook.utilities.Server;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;

public class SelectSeller extends AppCompatActivity {

    ImageButton searchButton;
    EditText pinCode;
    String pincode;
    SellerListAdapter sellerListAdapter;
    RecyclerView recyclerView;
    ArrayList<User> listSeller = new ArrayList<>();
    String orderid;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SelectSeller.this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Seller");
        setContentView(R.layout.activity_select_seller);
        pinCode = findViewById(R.id.searchTab);
        searchButton = findViewById(R.id.searchButton);
        String pc = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("pincode", "Phone not registered");
        Log.e("pincode",pc);
        pinCode.setText(pc);

        orderid = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("orderId","NA");

        sellerListAdapter = new SellerListAdapter(this,listSeller, orderid);

        recyclerView =findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(sellerListAdapter);


       /*For the default search with the stored pincode*/
        new SearchSeller(pc).execute();


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pincode = pinCode.getText().toString();
                new SearchSeller(pincode).execute();
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    public class SearchSeller extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;
        String pincode;

        SearchSeller(String pc){
            this.pincode = pc;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            params.put("pincode",pincode);
            progress=new ProgressDialog(SelectSeller.this);
            progress.setMessage("Retrieving Seller List..");
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
//                JSONObject jsonObject =new JSONObject(s);
                JSONArray jsonArray = new JSONArray(s);
                listSeller.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject post = jsonArray.getJSONObject(i);
                    User current = new User(post);
                    listSeller.add(current);
                }
                sellerListAdapter.notifyDataSetChanged();
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
                result = Server.post(getResources().getString(R.string.getSellerByPinCode),json);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }
    }


}
