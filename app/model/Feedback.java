package com.sahil.farmsbook.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Feedback {
	private String _id;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getReaction() {
		return reaction;
	}

	public void setReaction(String reaction) {
		this.reaction = reaction;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	private String userid;
	private String reaction;
	private  String comment;
	private String created_at;


	public Feedback(JSONObject feedback) throws JSONException {
		if(feedback.has("created_at"))setCreated_at(feedback.getString("created_at"));
		if(feedback.has("_id"))set_id(feedback.getString("_id"));
//		if(feedback.has("userid"))setUser(new User(feedback.getJSONObject("user")));
//		if(feedback.has("admin"))setAdmin(new User(feedback.getJSONObject("admin")));
		if(feedback.has("reaction"))setReaction(feedback.getString("reaction"));
		if(feedback.has("comment"))setComment(feedback.getString("comment"));
//		if(feedback.has("completed"))setCompleted(feedback.getBoolean("completed"));
//        if(feedback.has("text"))setText(feedback.getString("text"));
	}


}
