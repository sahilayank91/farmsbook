package com.sahil.farmsbook.Views;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.adapter.SellerProductListAdapter;
import com.sahil.farmsbook.model.Product;
import com.sahil.farmsbook.utilities.CountDrawable;
import com.sahil.farmsbook.utilities.Server;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;

public class ListActivity extends AppCompatActivity {
    ArrayList<Product> listProduct = new ArrayList<>();
    private SellerProductListAdapter sellerProductListAdapter;
    private RecyclerView recyclerView;
    private Menu menu;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ListActivity.this,CustomerActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.action_cart:
                Intent inte = new Intent(ListActivity.this, CartActivity.class);
                startActivity(inte);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_cart, menu);



        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
             /*   if(list.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }*/
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        Gson gson = new Gson();

        String cart = SharedPreferenceSingleton.getInstance(ListActivity.this).getString("cart",null);

        if(cart==null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setCount(this, "0", menu);
            }
        }else{
            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            HashMap<String, String> cartmap = gson.fromJson(cart, type);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setCount(this, String.valueOf(cartmap.size()), menu);
            }
        }
        return true;
    }

    public void update(){
        Gson gson = new Gson();

        Integer total = 0;
        String cart = SharedPreferenceSingleton.getInstance(ListActivity.this).getString("cart",null);

        if(cart==null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setCount(this, "0", this.menu);
            }
        }else{
            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            HashMap<String, String> cartmap = gson.fromJson(cart, type);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setCount(this, String.valueOf(cartmap.size()), this.menu);
            }
        }


    }


    public void setCount(Context context, String count, Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_cart);
        VectorDrawable icons = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            icons = (VectorDrawable) menuItem.getIcon();
        }
        LayerDrawable icon = new LayerDrawable(new Drawable [] { icons });
        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_group_count);
        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_group_count, badge);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);



        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String type = getIntent().getStringExtra("type");

        getSupportActionBar().setTitle("List of " + type);


        Spinner spinner = (Spinner) findViewById(R.id.spinner);




        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sellerProductListAdapter = new SellerProductListAdapter(this,listProduct, "List");
        recyclerView.setAdapter(sellerProductListAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        new GetProductList(type).execute();


    }
    @SuppressLint("StaticFieldLeak")
    public class GetProductList extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;
        String type;
        GetProductList(String types){
            type = types;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            params.put("type",type);
            progress=new ProgressDialog(ListActivity.this);
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
                result = Server.post(getResources().getString(R.string.getProductByType),json);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }



            System.out.println("Result:" + result);
            return result;
        }
    }


}
