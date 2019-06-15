package com.sahil.farmsbook.Views;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;

import com.sahil.farmsbook.MainActivity;
import com.sahil.farmsbook.R;
import com.sahil.farmsbook.utilities.Server;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;

public class AddProduct_Seller extends AppCompatActivity {

    String name,unit,price,brand;
    EditText mName,mPrice,mBrand;
    Button submit;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product__seller);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add New Product");
        mName= findViewById(R.id.product_name);
        mPrice = findViewById(R.id.product_price);
        mBrand = findViewById(R.id.product_brand);
        submit = findViewById(R.id.product_submit);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.clearCheck();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    unit = rb.getText().toString();
                    Toast.makeText(AddProduct_Seller.this, rb.getText(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 name = mName.getText().toString();
                 price = mPrice.getText().toString();
                 brand = mBrand.getText().toString();
                if( name.equals("") || price.equals("") || unit.equals("") || brand.equals("")){
                    Toast.makeText(AddProduct_Seller.this,"Please fill all the Details", Toast.LENGTH_SHORT).show();
                }else{
                    new AddProduct().execute();
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(AddProduct_Seller.this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    class AddProduct extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            params.put("price",price);
            params.put("name",name);
            params.put("unit",unit);
            params.put("brand",brand);
            params.put("sellerId", SharedPreferenceSingleton.getInstance(AddProduct_Seller.this).getString("_id","User Not Registered"));

            progress=new ProgressDialog(AddProduct_Seller.this);
            progress.setMessage("Adding Product..");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            if (success) {



                    Toast.makeText(AddProduct_Seller.this, "Product Successfully Uploaded", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddProduct_Seller.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

            } else {
                Toast.makeText(AddProduct_Seller.this, R.string.error, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                System.out.println(json);
                result = Server.post(getResources().getString(R.string.newProduct),json);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }



            System.out.println("Result:" + result);
            return result;
        }
    }
}
