package com.sahil.farmsbook.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Unit {
    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private String quantity;
    private String price;
    private String unit;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    private String _id;

    public Unit(JSONObject json) throws JSONException {
        if(json.has("quantity"))this.quantity = json.getString("quantity");
        if(json.has("_id"))this._id = json.getString("_id");
        if(json.has("price"))this.price = json.getString("price");
        if(json.has("unit"))this.unit = json.getString("unit");
    }
}
