package com.sahil.farmsbook.Views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.Views.Admin.AddItem;
import com.sahil.farmsbook.Views.Admin.UploadImage;
import com.sahil.farmsbook.interfaces.RCVItemClickListener;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;

public class SellerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener, RCVItemClickListener {
    private StorageReference storageReference;
    private TextView navPhoneView,navPinCode;
    Button order, completed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        storageReference = FirebaseStorage.getInstance().getReference();

        order = findViewById(R.id.orders);
        completed = findViewById(R.id.completedOrder);



        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerActivity.this, PickupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerActivity.this, CompletedOrder.class);
                startActivity(intent);
                finish();
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_Menu = navigationView.getMenu();

        String role =  SharedPreferenceSingleton.getInstance(SellerActivity.this).getString("role","User Not Registered");

        switch (role) {
            case "Customer":
                nav_Menu.findItem(R.id.nav_seller_order).setVisible(false);
                nav_Menu.findItem(R.id.nav_add_image).setVisible(false);
                nav_Menu.findItem(R.id.nav_add_item).setVisible(false);
                nav_Menu.findItem(R.id.nav_add_seller).setVisible(false);
                break;
            case "Seller":
                nav_Menu.findItem(R.id.nav_order).setVisible(false);
                nav_Menu.findItem(R.id.nav_add_seller).setVisible(false);
                nav_Menu.findItem(R.id.nav_wallet).setVisible(false);
                break;
            case "Admin":
                nav_Menu.findItem(R.id.nav_add_seller).setVisible(true);
                nav_Menu.findItem(R.id.nav_add_image).setVisible(true);
                nav_Menu.findItem(R.id.nav_wallet).setVisible(false);

                break;
        }




        View header = navigationView.getHeaderView(0);
        TextView navEmailView = header.findViewById(R.id.nav_header_email);
        TextView navNameView = header.findViewById(R.id.nav_header_name);
        navPhoneView = header.findViewById(R.id.nav_header_phone);
        navPinCode = header.findViewById(R.id.nav_header_pincode);
        String name;
        String firstname = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("firstname", "User Not Registered");

        String lastname = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("lastname", "User Not Registered");
        name = firstname ;

//        navEmailView.setText(SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("email", "User Not Registered"));
        navNameView.setText(name);
        navPhoneView.setText(SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("phone", "Phone not registered"));
        navPinCode.setText(SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("pincode", "Pincode not registered"));


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
            Intent intent = new Intent(SellerActivity.this, ProfileActivity.class);
            startActivity(intent);
        }else if(id==R.id.nav_contactus){
            Intent intent = new Intent(SellerActivity.this, ContactActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_logout) {
            getApplicationContext().getSharedPreferences(SharedPreferenceSingleton.SETTINGS_NAME, MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(SellerActivity.this, LoginActivity.class));
            finish();
        }else if (id == R.id.nav_share) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "FarmsBook");
            String message = "\nDownload FarmsBook \n https://play.google.com/store/apps/details?id=com.sahil.farmsbook\n\n";
            i.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(i, "Choose Sharing Method"));
        }else if(id==R.id.nav_order){
            Intent intent = new Intent(SellerActivity.this, YourOrders.class);
            startActivity(intent);
        }else if (id==R.id.nav_seller_order){
            Intent intent = new Intent(SellerActivity.this, PickupActivity.class);
            startActivity(intent);
        }else if(id==R.id.nav_terms){
            Intent intent = new Intent(SellerActivity.this, TermsAndCondition.class);
            String role =  SharedPreferenceSingleton.getInstance(SellerActivity.this).getString("role","User Not Registered");
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
            Intent intent = new Intent(SellerActivity.this, UploadImage.class);
            startActivity(intent);
        }else if(id==R.id.nav_completed_order){
            Intent intent = new Intent(SellerActivity.this,CompletedOrder.class);
            startActivity(intent);
        }else if(id==R.id.nav_feedback){
            Intent intent = new Intent(SellerActivity.this, FeedbackActivity.class);
            startActivity(intent);
        }else if (id==R.id.nav_add_item){
            Intent intent = new Intent(SellerActivity.this, AddItem.class);
            startActivity(intent);
        }else if(id==R.id.nav_add_seller){
            Intent intent = new Intent(SellerActivity.this, SellerRegisterActivity.class);
            startActivity(intent);
            finish();
        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(View view, int position) {

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

}
