package com.sahil.farmsbook;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sahil.farmsbook.Views.AddProduct_Seller;
import com.sahil.farmsbook.Views.CompletedOrder;
import com.sahil.farmsbook.Views.ContactActivity;
import com.sahil.farmsbook.Views.FeedbackActivity;
import com.sahil.farmsbook.Views.LoginActivity;
import com.sahil.farmsbook.Views.PickupActivity;
import com.sahil.farmsbook.Views.ProfileActivity;
import com.sahil.farmsbook.Views.SelectSeller;
import com.sahil.farmsbook.Views.TermsAndCondition;
import com.sahil.farmsbook.Views.Admin.UploadImage;
import com.sahil.farmsbook.Views.YourOrders;
import com.sahil.farmsbook.adapter.ManualOrderList;
import com.sahil.farmsbook.adapter.SellerProductListAdapter;
import com.sahil.farmsbook.interfaces.RCVItemClickListener;
import com.sahil.farmsbook.model.Offer;
import com.sahil.farmsbook.model.Product;
import com.sahil.farmsbook.utilities.Server;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;
import android.widget.AdapterView.OnItemSelectedListener;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnItemSelectedListener,RCVItemClickListener {
    private static final int PICK_IMAGE_REQUEST = 234;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    TextInputLayout product , quantity, quantity_with_image,input_layout_brandName;
    private RecyclerView recyclerView;
    private SellerProductListAdapter sellerProductListAdapter;
    private StorageReference storageReference;
    private Uri filePath;
    private Button submit, image_from_gallery, image_capture, add_manually,addItems;
    private RadioGroup radioGroup;
    private TextView navPhoneView,navPinCode;
    CardView cardView;
    AutoCompleteTextView manual_brandName,manual_name,manual_quantity;
    String man_name,man_brand,man_quantity;
    private String unit,download_url;
    EditText dialog_name, dialog_price,search;
    List<String> units = new ArrayList<String>();
    String[] s = {"Kg","L"};
    int flag = -1;
    RelativeLayout relativeLayout, relativeLayout_seller;
    LinearLayout linearLayout;

    ArrayList<Product> listProduct = new ArrayList<>();
    ArrayList<Product> manualListProduct = new ArrayList<>();


    RecyclerView manualOrderList;
    ManualOrderList manualOrderListAdapter;


    Spinner spinner;
    private LinearLayout dotsLayout;
    private TextView[] dots,donation_dots;

    private ViewPager viewPager;
    private ArrayList<String> images = new ArrayList<>();
    private MyViewPagerAdapter myViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        product = findViewById(R.id.input_layout_product);
        quantity = findViewById(R.id.input_layout_price);
        imageView = findViewById(R.id.imageView);


        cardView = findViewById(R.id.view_pager_card);

        dialog_name = findViewById(R.id.dialog_name);
        dialog_price = findViewById(R.id.dialog_price);

        linearLayout = findViewById(R.id.customer);
        relativeLayout_seller = findViewById(R.id.seller);

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        submit = findViewById(R.id.submitImage);
        image_from_gallery = findViewById(R.id.uploadImage);
//        image_capture = findViewById(R.id.captureImage);
        add_manually = findViewById(R.id.addManually);
        input_layout_brandName = findViewById(R.id.input_layout_brandName);
        search = findViewById(R.id.search);

        manual_brandName = findViewById(R.id.brandName);
        manual_quantity = findViewById(R.id.quantity);
        manual_name = findViewById(R.id.productName);
        manualOrderList = findViewById(R.id.orderlist_recycler_view);
        manualOrderList.setLayoutManager(new LinearLayoutManager(this));
        manualOrderListAdapter = new ManualOrderList(this,manualListProduct);
        manualOrderList.setAdapter(manualOrderListAdapter);
        manualOrderList.setItemAnimator(new DefaultItemAnimator());
        addItems = findViewById(R.id.addItems);

        // Spinner element
         spinner = (Spinner) findViewById(R.id.spinner);


//        addItems.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                man_brand = manual_brandName.getText().toString();
//                man_quantity = manual_quantity.getText().toString();
//                man_name = manual_name.getText().toString();
//                Product product = new Product(man_name, man_brand, man_quantity,unit);
//                manualListProduct.add(product);
//                manualOrderListAdapter.notifyDataSetChanged();
//                manual_brandName.setText("");
//                manual_quantity.setText("");
//                manual_name.setText("");
//            }
//        });
//

        //get view pager images
        new getImages().execute();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sellerProductListAdapter = new SellerProductListAdapter(this,listProduct);
        recyclerView.setAdapter(sellerProductListAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


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


        FloatingActionButton fab = findViewById(R.id.addProduct);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddProduct_Seller.class);
                startActivity(intent);
            }
        });


        String role =  SharedPreferenceSingleton.getInstance(MainActivity.this).getString("role","User Not Registered");
        if(role.equals("Seller")){
            cardView.setVisibility(GONE);
            input_layout_brandName.setVisibility(GONE);
            linearLayout.setVisibility(GONE);
            relativeLayout_seller.setVisibility(View.VISIBLE);
            product.setVisibility(GONE);
            quantity.setVisibility(GONE);
            imageView.setVisibility(GONE);
            addItems.setVisibility(GONE);
            viewPager.setVisibility(GONE);
            fab.setVisibility(View.VISIBLE);
            submit.setVisibility(GONE);
            new GetProductList().execute();

        }else if(role.equals("Customer")){
            search.setVisibility(GONE);
            viewPager.setVisibility(View.VISIBLE);
            add_manually.setVisibility(View.VISIBLE);
            manualOrderList.setVisibility(GONE);
            input_layout_brandName.setVisibility(GONE);
            linearLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(GONE);
            fab.setVisibility(GONE);
            product.setVisibility(GONE);
            quantity.setVisibility(GONE);
            imageView.setVisibility(GONE);
            addItems.setVisibility(GONE);
            spinner.setVisibility(GONE);
            image_from_gallery.setVisibility(View.VISIBLE);
//            image_capture.setVisibility(View.GONE);
            add_manually.setVisibility(View.VISIBLE);
        }else{

        }


        image_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 1;
                showFileChooser();
//                image_capture.setVisibility(View.GONE);
                add_manually.setVisibility(View.VISIBLE);
                addItems.setVisibility(GONE);
                product.setVisibility(GONE);
                quantity.setVisibility(GONE);
                input_layout_brandName.setVisibility(GONE);
                imageView.setVisibility(View.VISIBLE);
                manualOrderList.setVisibility(GONE);
            }
        });

//        image_capture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                flag = 2;
//                addItems.setVisibility(View.GONE);
//                image_from_gallery.setVisibility(View.GONE);
//                add_manually.setVisibility(View.GONE);
//                input_layout_brandName.setVisibility(View.GONE);
//                product.setVisibility(View.GONE);
//                quantity.setVisibility(View.GONE);
//                captureImage();
//                manualOrderList.setVisibility(View.GONE);
//                imageView.setVisibility(View.VISIBLE);
//            }
//        });

        add_manually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 3;

                add_manually.setVisibility(GONE);
                image_from_gallery.setVisibility(GONE);
//                image_capture.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);

                product.setVisibility(View.VISIBLE);
                quantity.setVisibility(View.VISIBLE);
                input_layout_brandName.setVisibility(View.VISIBLE);
                addItems.setVisibility(View.VISIBLE);
                imageView.setVisibility(GONE);
                manualOrderList.setVisibility(View.VISIBLE);

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_Menu = navigationView.getMenu();


        switch (role) {
            case "Customer":
                nav_Menu.findItem(R.id.nav_seller_order).setVisible(false);
                nav_Menu.findItem(R.id.nav_add_image).setVisible(false);
                break;
            case "Seller":
                nav_Menu.findItem(R.id.nav_order).setVisible(false);
                nav_Menu.findItem(R.id.nav_add_image).setVisible(false);
                break;
            case "Admin":
                break;
        }

        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                man_brand = manual_brandName.getText().toString();
                man_quantity = manual_quantity.getText().toString();
                man_name = manual_name.getText().toString();

                if(TextUtils.isEmpty(man_brand) || TextUtils.isEmpty(man_quantity)  || TextUtils.isEmpty(man_name)){
                    Toast.makeText(MainActivity.this,"Please fill all the Details",Toast.LENGTH_SHORT).show();

                }else{
                    Product product = new Product(man_name,man_brand,man_quantity,unit);
                    manualListProduct.add(product);
                    manualOrderListAdapter.notifyDataSetChanged();
                    manual_name.setText("");
                    manual_brandName.setText("");
                    manual_quantity.setText("");
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==1 || flag == 2){
                    uploadImage();
                }else if(flag==3){
                    //TODO: create Order Here
                    if(manualListProduct.size()==0){
                        Toast.makeText(MainActivity.this, "Add atleast one item in the list", Toast.LENGTH_SHORT).show();
                    }else{
                        new AddOrder().execute();

                    }

                }else{
                    Toast.makeText(MainActivity.this,"Please select any option", Toast.LENGTH_SHORT).show();
                }
            }
        });

        View header = navigationView.getHeaderView(0);
        TextView navEmailView = header.findViewById(R.id.nav_header_email);
        TextView navNameView = header.findViewById(R.id.nav_header_name);
        navPhoneView = header.findViewById(R.id.nav_header_phone);
        navPinCode = header.findViewById(R.id.nav_header_pincode);
        String name;
        String firstname = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("firstname", "User Not Registered");

        String lastname = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("lastname", "User Not Registered");
        name = firstname + " " + lastname;

        navEmailView.setText(SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("email", "User Not Registered"));
        navNameView.setText(name);
        navPhoneView.setText(SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("phone", "Phone not registered"));
        navPinCode.setText(SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("pincode", "Pincode not registered"));



        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        List<String> unit = new ArrayList<String>();
        unit.add("Kg");
        unit.add("L");
        unit.add("g");
        unit.add("ml");
        unit.add("Packet");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, unit);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);



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
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void uploadImage() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            final StorageReference riversRef = storageReference.child("customerProductImages/" + filePath.getLastPathSegment());
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.e("Tuts+", "uri: " + uri.toString());
                                     download_url = uri.toString();
                                    progressDialog.dismiss();

                                    Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                                    new AddOrder().execute();

                                    //Handle whatever you're going to do with the URL here
                                }
                            });
                            //and displaying a success toast
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }

    }



    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK ) {
            Bundle extras = data.getExtras();
            filePath = data.getData();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            getImageUri(MainActivity.this,imageBitmap);
            imageView.setImageBitmap(imageBitmap);
//            Log.e("filepath:",getImageUri(this,imageBitmap).toString());
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        Log.e("path:",path);
        return Uri.parse(path);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }else if(id==R.id.nav_contactus){
            Intent intent = new Intent(MainActivity.this, ContactActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_logout) {
            getApplicationContext().getSharedPreferences(SharedPreferenceSingleton.SETTINGS_NAME, MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }else if (id == R.id.nav_share) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Kisaan Hub");
            String message = "\nDownload Kisaan Hub \n https://play.google.com/store/apps/details?id=com.sahil.farmsbook\n\n";
            i.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(i, "Choose Sharing Method"));
        }else if(id==R.id.nav_order){
            Intent intent = new Intent(MainActivity.this, YourOrders.class);
            startActivity(intent);
        }else if (id==R.id.nav_seller_order){
            Intent intent = new Intent(MainActivity.this, PickupActivity.class);
            startActivity(intent);
        }else if(id==R.id.nav_terms){
            Intent intent = new Intent(MainActivity.this, TermsAndCondition.class);
            String role =  SharedPreferenceSingleton.getInstance(MainActivity.this).getString("role","User Not Registered");
            if(role.equals("Seller")){
                intent.putExtra("type","Seller");
            }else{
                intent.putExtra("type","Login");
            }
            startActivity(intent);
        }else if (id==R.id.nav_visit_website){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.farmsbook.com"));
            startActivity(browserIntent);
        }else if(id==R.id.nav_add_image){
            Intent intent = new Intent(MainActivity.this,UploadImage.class);
            startActivity(intent);
        }else if(id==R.id.nav_completed_order){
            Intent intent = new Intent(MainActivity.this,CompletedOrder.class);
            startActivity(intent);
        }else if(id==R.id.nav_feedback){
            Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
            startActivity(intent);
        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
         unit = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + unit, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        if(id==R.id.deleteButton){
            Log.e("flasfas",listProduct.get(position).get_id()) ;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @SuppressLint("StaticFieldLeak")
    public class GetProductList extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            params.put("sellerId", SharedPreferenceSingleton.getInstance(MainActivity.this).getString("_id","User Not Registered"));
            progress=new ProgressDialog(MainActivity.this);
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
    class AddOrder extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            params.put("customerId",SharedPreferenceSingleton.getInstance(MainActivity.this).getString("_id","Jaipur"));

            params.put("status","Recieved");

            if(flag==3){

                JSONArray jsonArray = new JSONArray(manualListProduct);
                Gson gson = new Gson();
                params.put("order",gson.toJson(manualListProduct));
                params.put("type","Manual");
            }else{
                params.put("image_url",download_url);
                params.put("type","Image");
            }


            progress=new ProgressDialog(MainActivity.this);
            progress.setMessage("Adding Order..");
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            if (success) {

                Intent intent = new Intent(MainActivity.this, SelectSeller.class);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                    if(jsonObject.has("data")){
                        JSONObject data = jsonObject.getJSONObject("data");
                        if(data.has("_id")){
//                            intent.putExtra("_id",data.getString("_id"));
                            SharedPreferenceSingleton.getInstance(getApplicationContext()).put("orderId", data.getString("_id"));

                        }
                    }
                    Toast.makeText(MainActivity.this, "Order Submitted..Please Select Seller!!", Toast.LENGTH_LONG).show();

                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {

            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                System.out.println(json);
                result = Server.post(getResources().getString(R.string.newOrder),json);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }



            Log.e("result.....:",result);
            return result;
        }
    }


    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }



    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = MainActivity.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        MyViewPagerAdapter() {
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.viewpager_image, container, false);
            ImageView imageView= view.findViewById(R.id.imageOffer);
//            Glide.with(getContext()).load(images.get(position)).transition(DrawableTransitionOptions.withCrossFade()).into(imageView);
            Glide.with(MainActivity.this).load(images.get(position)).apply(new RequestOptions().placeholder(R.drawable.placeholder_image).fitCenter()).into(imageView);

            imageView.setScaleType(ImageView.ScaleType.FIT_XY);


            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }





    private void addBottomDots(int currentPage) {
        if(!images.isEmpty()) {
            dots = new TextView[images.size()];
            dotsLayout.removeAllViews();
            for (int i = 0; i < images.size(); i++) {
                dots[i] = new TextView(MainActivity.this);
                dots[i].setText(Html.fromHtml("&#8226;"));
                dots[i].setTextSize(35);
                dots[i].setTextColor(getResources().getColor(R.color.white));
                dotsLayout.addView(dots[i]);
            }

            if (dots.length > 0)
                dots[currentPage].setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }




//    public void getAllImages(){
//        DatabaseReference refs = FirebaseDatabase.getInstance().getReference("Offerimages");
//        refs.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
//                images.add(dataSnapshot.getValue().toString());
//                myViewPagerAdapter.notifyDataSetChanged();
//                addBottomDots(0);
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
//                addBottomDots(0);
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                images.remove(dataSnapshot.getValue().toString());
//                myViewPagerAdapter.notifyDataSetChanged();
//                addBottomDots(0);
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
//                addBottomDots(0);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        });
//    }

    @SuppressLint("StaticFieldLeak")
    class getImages extends AsyncTask<String, String, String> {
        boolean success = false;
        HashMap<String, String> params = new HashMap<>();



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (success) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                images.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject post = null;
                    try {
                        post = jsonArray.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Offer current = null;
                    try {
                        current = new Offer(post);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    images.add(current.getUrl());
                }
                myViewPagerAdapter.notifyDataSetChanged();


            } else {
                Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                result = Server.post(getResources().getString(R.string.getOffer),json);
                success = true;
            } catch (Exception e){
                e.printStackTrace();
            }

            System.out.println("Result:" + result);
            return result;
        }
    }

}
