package com.sahil.farmsbook.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Offer {
    private String url;
    private String percentage;
    private String code;
    private String service;


    private String _id;


  public Offer(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("url"))this.url = jsonObject.getString("url");

    }
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
