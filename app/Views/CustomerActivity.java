package com.sahil.farmsbook.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.Views.Admin.AddItem;
import com.sahil.farmsbook.Views.Admin.UploadImage;
import com.sahil.farmsbook.interfaces.RCVItemClickListener;
import com.sahil.farmsbook.model.Offer;
import com.sahil.farmsbook.utilities.CountDrawable;
import com.sahil.farmsbook.utilities.Server;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;

public class CustomerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener, RCVItemClickListener {
    private ViewPager viewPager;
    private ArrayList<String> images = new ArrayList<>();
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView[] dots,donation_dots;
    private LinearLayout dotsLayout;
    CardView vegetable, fruit, grain;

    private StorageReference storageReference;

    private TextView navPhoneView,navPinCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.view_pager);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        dotsLayout = findViewById(R.id.layoutDots);

        new getImages().execute();

        storageReference = FirebaseStorage.getInstance().getReference();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_Menu = navigationView.getMenu();

        String role =  SharedPreferenceSingleton.getInstance(CustomerActivity.this).getString("role","User Not Registered");

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
                break;
            case "Admin":
                nav_Menu.findItem(R.id.nav_add_seller).setVisible(true);
                nav_Menu.findItem(R.id.nav_add_image).setVisible(true);

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
        name = firstname + " " + lastname;

        navEmailView.setText(SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("email", "User Not Registered"));
        navNameView.setText(name);
        navPhoneView.setText(SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("phone", "Phone not registered"));
        navPinCode.setText(SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("pincode", "Pincode not registered"));


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(CustomerActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        fab.setRippleColor(Color.CYAN);


        vegetable = findViewById(R.id.vegetable);
        fruit = findViewById(R.id.fruit);
        grain = findViewById(R.id.grain);


       vegetable.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(CustomerActivity.this, ListActivity.class);
               intent.putExtra("type","Vegetable");
               startActivity(intent);
           }
       });

       fruit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(CustomerActivity.this, ListActivity.class);
               intent.putExtra("type","Fruit");
               startActivity(intent);
           }
       });

       grain.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent intent = new Intent(CustomerActivity.this, ListActivity.class);
               intent.putExtra("type","Grain");
               startActivity(intent);
           }
       });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
            Intent intent = new Intent(CustomerActivity.this, ProfileActivity.class);
            startActivity(intent);
        }else if(id==R.id.nav_contactus){
            Intent intent = new Intent(CustomerActivity.this, ContactActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_logout) {
            getApplicationContext().getSharedPreferences(SharedPreferenceSingleton.SETTINGS_NAME, MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(CustomerActivity.this, LoginActivity.class));
            finish();
        }else if (id == R.id.nav_share) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "FarmsBook");
            String message = "\nDownload FarmsBook \n https://play.google.com/store/apps/details?id=com.sahil.farmsbook\n\n";
            i.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(i, "Choose Sharing Method"));
        }else if(id==R.id.nav_order){
            Intent intent = new Intent(CustomerActivity.this, YourOrders.class);
            startActivity(intent);
        }else if (id==R.id.nav_seller_order){
            Intent intent = new Intent(CustomerActivity.this, PickupActivity.class);
            startActivity(intent);
        }else if(id==R.id.nav_terms){
            Intent intent = new Intent(CustomerActivity.this, TermsAndCondition.class);
            String role =  SharedPreferenceSingleton.getInstance(CustomerActivity.this).getString("role","User Not Registered");
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
            Intent intent = new Intent(CustomerActivity.this, UploadImage.class);
            startActivity(intent);
        }else if(id==R.id.nav_completed_order){
            Intent intent = new Intent(CustomerActivity.this,CompletedOrder.class);
            startActivity(intent);
        }else if(id==R.id.nav_feedback){
            Intent intent = new Intent(CustomerActivity.this, FeedbackActivity.class);
            startActivity(intent);
        }else if (id==R.id.nav_add_item){
            Intent intent = new Intent(CustomerActivity.this, AddItem.class);
            startActivity(intent);
        }else if(id==R.id.nav_add_seller){
            Intent intent = new Intent(CustomerActivity.this, SellerRegisterActivity.class);
            startActivity(intent);
            finish();
        }






        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;    }

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        Gson gson = new Gson();

        String cart = SharedPreferenceSingleton.getInstance(CustomerActivity.this).getString("cart",null);

        if(cart==null){
            setCount(this, "0", menu);
        }else{
            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            HashMap<String, String> cartmap = gson.fromJson(cart, type);
            setCount(this, String.valueOf(cartmap.size()), menu);
        }
        return true;
    }


    public void setCount(Context context, String count, Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_cart);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

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
        if (id == R.id.action_cart) {
            Intent intent  = new Intent(CustomerActivity.this,CartActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            layoutInflater = (LayoutInflater) CustomerActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.viewpager_image, container, false);
            ImageView imageView= view.findViewById(R.id.imageOffer);
//            Glide.with(getContext()).load(images.get(position)).transition(DrawableTransitionOptions.withCrossFade()).into(imageView);
            Glide.with(CustomerActivity.this).load(images.get(position)).apply(new RequestOptions().placeholder(R.drawable.placeholder_image).fitCenter()).into(imageView);

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


    private void addBottomDots(int currentPage) {
        if(!images.isEmpty()) {
            dots = new TextView[images.size()];
            dotsLayout.removeAllViews();
            for (int i = 0; i < images.size(); i++) {
                dots[i] = new TextView(this);
                dots[i].setText(Html.fromHtml("&#8226;"));
                dots[i].setTextSize(35);
                dots[i].setTextColor(getResources().getColor(R.color.white));
                dotsLayout.addView(dots[i]);
            }

            if (dots.length > 0)
                dots[currentPage].setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }



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
                Toast.makeText(CustomerActivity.this, R.string.error, Toast.LENGTH_LONG).show();
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
