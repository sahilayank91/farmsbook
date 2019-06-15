package com.sahil.farmsbook.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.adapter.SellerProductListAdapter;
import com.sahil.farmsbook.interfaces.RCVItemClickListener;
import com.sahil.farmsbook.model.Product;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;

public class CartActivity extends AppCompatActivity implements RCVItemClickListener {

    private Integer total = 0;
    ArrayList<Product> listProduct = new ArrayList<>();
    private SellerProductListAdapter sellerProductListAdapter;
    private RecyclerView recyclerView;
    LinearLayout cart, emptycart, checkoutButton;
    TextView text_items, text_total;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(CartActivity.this,CustomerActivity.class);
                startActivity(intent);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cart");

        text_items = findViewById(R.id.items);
        text_total = findViewById(R.id.total);
        checkoutButton = findViewById(R.id.checkoutbutton);

        try {
            getCartItems();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        cart  = findViewById(R.id.cart);
        emptycart = findViewById(R.id.emptycart);


        if(listProduct.size()>0){
            cart.setVisibility(View.VISIBLE);
            emptycart.setVisibility(View.GONE);



        }else{
            cart.setVisibility(View.GONE);
            emptycart.setVisibility(View.VISIBLE);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sellerProductListAdapter = new SellerProductListAdapter(this,listProduct,"Cart");
        recyclerView.setAdapter(sellerProductListAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        sellerProductListAdapter.notifyDataSetChanged();



        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(total<10){
                    Toast.makeText(CartActivity.this, "The purchasing total should be more than Rs 10!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(total>9999){
                    Toast.makeText(CartActivity.this, "The purchasing total should be less than Rs 10000!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    checkout();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void checkout() throws JSONException {
        ArrayList<Product> cartItems  = sellerProductListAdapter.listProduct;
        Gson gson = new Gson();

        total = 0;
        for (int i=0;i<cartItems.size();i++) {
            Product product = cartItems.get(i);
            total+=Integer.parseInt(product.getTotal());
        }

        Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
        intent.putExtra("total",String.valueOf(total));
        startActivity(intent);
        finish();
    }
    public void updateTotal(){
        ArrayList<Product> cartItems  = sellerProductListAdapter.listProduct;
        Gson gson = new Gson();

        total = 0;
        for (int i=0;i<cartItems.size();i++) {
            Product product = cartItems.get(i);
            total+=Integer.parseInt(product.getTotal());
        }
        text_items.setText(cartItems.size() +  " Item");
        text_total.setText(String.valueOf(total));

    }

    private void getCartItems() throws JSONException {

        Gson gson = new Gson();

        String cartString = SharedPreferenceSingleton.getInstance(CartActivity.this).getString("cart","Can't find the value");

        Log.e("cartmkhkj:",cartString);
        if(cartString!=null && !cartString.equals("Can't find the value")) {
            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            HashMap<String, String> cartmap = gson.fromJson(cartString, type);

            for (HashMap.Entry<String, String> entry : cartmap.entrySet()) {
                Product product = new Product(new JSONObject(entry.getValue()));


                product.set_id(entry.getKey());


                listProduct.add(product);
                Log.e("total:",product.getTotal());

                if (!TextUtils.isEmpty(product.getTotal()) && TextUtils.isDigitsOnly(product.getTotal())) {
                    total += Integer.parseInt(product.getTotal());
                } else {
                    total = 0;
                }
//                total += Integer.parseInt(product.getTotal());
            }

            if (listProduct.size() > 0) {
                text_items.setText(cartmap.size() + " Item");
                text_total.setText(String.valueOf(total));
            } else {
                text_items.setText(cartmap.size() + " Item");
                text_total.setText("Rs 0");
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if(view.getId()==R.id.addItems){

        }
    }
}
