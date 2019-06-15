package com.sahil.farmsbook.Views;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hsalf.smilerating.SmileRating;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sahil.farmsbook.MainActivity;
import com.sahil.farmsbook.R;
import com.sahil.farmsbook.adapter.FeedbackAdapter;
import com.sahil.farmsbook.interfaces.RCVItemClickListener;
import com.sahil.farmsbook.model.Feedback;
import com.sahil.farmsbook.utilities.Server;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackActivity extends AppCompatActivity implements RCVItemClickListener {

    private RecyclerView recyclerView;
    private FeedbackAdapter feedbackAdapter;

    private ProgressBar progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    View view;
    private List<Feedback> feedbackList=new ArrayList<>();

    TextView feedbackAuthorName, feedbackDate, feedbackTitle;
    SmileRating smileRatingBar;
    Button btnSubmit;
    Button btnSeeActivities;
    EditText etFeedbackText;
    LinearLayout btnHolder;

    public FeedbackActivity() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_feedback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Feedback");
        recyclerView = findViewById(R.id.feedback_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(FeedbackActivity.this));
        feedbackAdapter = new FeedbackAdapter(this, feedbackList);
        recyclerView.setAdapter(feedbackAdapter);
        feedbackAdapter.setRcvItemClickListener(this);

        feedbackAuthorName = findViewById(R.id.feedback_user_name);
        etFeedbackText =findViewById(R.id.et_feedback); //hide this in user moder
//        etFeedbackText.setFocusedByDefault(false);

        btnSubmit = findViewById(R.id.btn_submit_feedback);
        btnHolder =  findViewById(R.id.btn_holder);
        feedbackDate =  findViewById(R.id.feedback_date);
        feedbackTitle = findViewById(R.id.feedback_title);
        smileRatingBar =  findViewById(R.id.smile_rating);
        String name;
        String firstname = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("firstname", "User Not Registered");
        String lastname = SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("lastname", "User Not Registered");
        name = firstname + " " + lastname;
        feedbackAuthorName.setText(name);
        feedbackTitle.setText("Please select the rating smile");
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateFeedback(smileRatingBar.getSelectedSmile(),etFeedbackText.getText().toString()).execute();
            }
        });
        prepareFeedbackItems();
    }



    private void prepareFeedbackItems() {

        new GetFeedbackList().execute();

    }


    @Override
    public void onItemClick(View view, int position) {

        switch (view.getId()) {
            case R.id.btn_submit_feedback:
                //handle feedback submission
//
//                SmileRating reaction = view.getRootView().findViewById(R.id.smile_rating);
//                EditText comment= view.getRootView().findViewById(R.id.et_feedback);
//                new UpdateFeedback(position,reaction.getSelectedSmile(),comment.getText().toString()).execute();

                break;

        }

    }
    @SuppressLint("StaticFieldLeak")
    class GetFeedbackList extends AsyncTask<String,String,String>{
        boolean success=false;
        HashMap<String,String>params=new HashMap<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);
            params.put("userid",SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("_id", "User Not Registered"));

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("fksjdf",s);
            if(success){
                try {

                    JSONArray jsonArray=new JSONArray(s);
                    feedbackList.clear();
                    for (int i = 0; i <jsonArray.length() ; i++) {
                        Feedback feedback=new Feedback(jsonArray.getJSONObject(i));
                        feedbackList.add(feedback);
                    }
                    feedbackAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            progressBar.setVisibility(View.GONE);
//            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            String response="";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                response = Server.post(getResources().getString(R.string.get_feedback),json);
                success=true;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(FeedbackActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    class UpdateFeedback extends AsyncTask<String,String,String>{
        boolean success=false;
        ProgressDialog progress = new ProgressDialog(FeedbackActivity.this);
        int position;
        HashMap<String,String>params=new HashMap<>();

        UpdateFeedback(int reaction, String comment){
            this.position=position;
//            params.put("feedback_id",String.valueOf(feedbackList.get(position).getFeedback_id()));
            params.put("reaction", String.valueOf(reaction));
            params.put("comment",comment);
            params.put("userid",SharedPreferenceSingleton.getInstance(getApplicationContext()).getString("_id", "User Not Registered"));
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Submitting Feedback..");
//            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();



        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            etFeedbackText.setText("");
            progress.dismiss();

                Intent intent = new Intent(FeedbackActivity.this,MainActivity.class);
                startActivity(intent);
                finish();


        }

        @Override
        protected String doInBackground(String... strings) {
            String response="";
            try {
                Gson gson = new Gson();
                String json = gson.toJson(params);
                response = Server.post(getResources().getString(R.string.update_feedback),json);
                success=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }

}
