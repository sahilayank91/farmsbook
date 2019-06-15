package com.sahil.farmsbook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hsalf.smilerating.SmileRating;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.sahil.farmsbook.R;
import com.sahil.farmsbook.interfaces.RCVItemClickListener;
import com.sahil.farmsbook.model.Feedback;
import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;


/**
 * Created by Sahil
 */

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private Context context;
    private List<Feedback> listFeedback;
    private RCVItemClickListener rcvItemClickListener;

    public FeedbackAdapter(Context context, List<Feedback> listFeedback) {
        this.context = context;
        this.listFeedback = listFeedback;
    }

    public void setRcvItemClickListener(RCVItemClickListener rcvItemClickListener) {
        this.rcvItemClickListener = rcvItemClickListener;
    }

    @Override
    public FeedbackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_feedback, parent, false);
        return new FeedbackViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final FeedbackViewHolder holder, int position) {
     final Feedback current = listFeedback.get(position);
//        if(current.getAdmin().get_id()== UserData.getInstance(context).get_id()) {
        Log.e("current",current.toString());
            String firstname = SharedPreferenceSingleton.getInstance(context).getString("firstname", "User Not Registered");
            String lastname = SharedPreferenceSingleton.getInstance(context).getString("lastname", "User Not Registered");

            holder.feedbackAuthorName.setText(firstname  + " " + lastname);
            holder.smileRatingBar.setSelectedSmile(Integer.parseInt(current.getReaction()));
            holder.smileRatingBar.isDirty();
            holder.etFeedbackText.setText(current.getComment());
        String feedbackDate = current.getCreated_at();
        Date date=null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(feedbackDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.feedbackDate.setText(date.toLocaleString().substring(0,12));
    }

    @Override
    public int getItemCount() {
        return listFeedback.size();
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView feedbackAuthorName, feedbackDate, feedbackTitle;
        SmileRating smileRatingBar;
        Button btnSeeActivities;
        TextView etFeedbackText;
        LinearLayout btnHolder;

        FeedbackViewHolder(View itemView) {
            super(itemView);

            feedbackAuthorName = itemView.findViewById(R.id.feedback_user_name);
            etFeedbackText =itemView.findViewById(R.id.et_feedback); //hide this in user moder
            btnHolder =  itemView.findViewById(R.id.btn_holder);
            feedbackDate =  itemView.findViewById(R.id.feedback_date);
            feedbackTitle =  itemView.findViewById(R.id.feedback_title);
            smileRatingBar =  itemView.findViewById(R.id.smile_rating);
        }

        @Override
        public void onClick(View itemView) {
            if (rcvItemClickListener != null) {
                rcvItemClickListener.onItemClick(itemView, getAdapterPosition());
            }
        }
    }
}
