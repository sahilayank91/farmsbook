package com.sahil.farmsbook.Views.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sahil.farmsbook.adapter.SellerOrderAdapter;
import com.sahil.farmsbook.model.Order;
import com.sahil.farmsbook.utilities.Server;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;


/**
 * Provides UI for the view with Cards.
 */
public class TodayFragment extends Fragment {
    public static ArrayList<Order> listOrders = new ArrayList<>();
    private SellerOrderAdapter adapter;
    RecyclerView recyclerView;
    View view;
    CardView cardView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null) view = inflater.inflate(R.layout.fragment_today, container, false);
        else return view;
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // Refresh items

                new GetOrders().execute();
            }
        });
//        cardView = view.findViewById(R.id.orderview);
        recyclerView = view.findViewById(R.id.order_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SellerOrderAdapter(getContext(), listOrders);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        new GetOrders().execute();
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    class GetOrders extends AsyncTask<String, String, String> {
        HashMap<String, String> params = new HashMap<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String userid = SharedPreferenceSingleton.getInstance(getContext()).getString("_id","User Not Registered");

        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);

                result = Server.post(getResources().getString(R.string.getUpcomingOrders),json);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);



            try {

                JSONObject jsonObject =new JSONObject(s);

                String success = jsonObject.getString("success");
                if (!success.equals("true")) {
                    Toast.makeText(getContext(),"Some error occured in getting Data..Please check your internet connection",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getContext(),MainActivity.class);
                    startActivity(intent);
                }else{
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    Log.e("array:" ,jsonArray.toString());
                    listOrders.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject post = jsonArray.getJSONObject(i);
                        Log.e("post:",post.toString());
                        Order current = new Order(post);
                        listOrders.add(current);


                    }

//                    if(listOrders.isEmpty()){
//                        cardView.setVisibility(View.VISIBLE);
//                    }
                    adapter.notifyDataSetChanged();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


    }


}