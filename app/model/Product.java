

package com.sahil.farmsbook.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private String name;
    private String created_at;
    private String price;
    private String sellerId;
    private String quantity;
    private String unit;
    private List<Unit> unitlist = new ArrayList<>();
    private String _id;
    private User seller;
    private String twofiftygram;
    private String fivehundredgram;
    private String onekg;
    private String hindiname;
    private String imageurl;
    private String list;
    private String total;
    private String brand;


    private String outofstock;

    public Product(String name, String brand, String quantity,String unit){
        this.name = name;
        this.brand = brand;
        this.quantity = quantity;
        this.unit = unit;
    }


    private String units;
    public Product(JSONObject order) throws JSONException {
        if(order.has("_id"))this._id = order.getString("_id");
        if(order.has("created_at"))this.created_at = order.getString("created_at");
        if(order.has("sellerId"))this.sellerId = order.getString("sellerId");
        if(order.has("twofiftygram"))this.twofiftygram = order.getString("twofiftygram");
        if(order.has("fivehundredgram"))this.fivehundredgram = order.getString("fivehundredgram");
        if(order.has("onekg"))this.onekg = order.getString("onekg");
        if(order.has("hindiname"))this.hindiname = order.getString("hindiname");
        if(order.has("name"))this.name = order.getString("name");
        if(order.has("unitQuantity"))this.quantity = order.getString("unitQuantity");
        if(order.has("unitlist")){
            setUnits(order.getString("unitlist"));
        }
        if (order.has("units")) {
            setUnits(order.getString("units"));

        }
        if(order.has("unit"))this.unit = order.getString("unit");
        if(order.has("imageurl"))this.imageurl = order.getString("imageurl");
        if(order.has("quantity"))this.quantity = order.getString("quantity");
        if(order.has("price"))this.price = order.getString("price");
        if(order.has("brand"))this.brand = order.getString("brand");
        if(order.has("total"))this.total = order.getString("total");
        if(order.has("outofstock"))this.outofstock = order.getString("outofstock");
    }
    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }



    public String getOutofstock() {
        return outofstock;
    }

    public void setOutofstock(String outofstock) {
        this.outofstock = outofstock;
    }
    public List<Unit> getUnitlist() {
        return unitlist;
    }

    public void setUnitlist(List<Unit> unitlist) {
        this.unitlist = unitlist;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTwofiftygram() {
        return twofiftygram;
    }

    public void setTwofiftygram(String twofiftygram) {
        this.twofiftygram = twofiftygram;
    }

    public String getFivehundredgram() {
        return fivehundredgram;
    }

    public void setFivehundredgram(String fivehundredgram) {
        this.fivehundredgram = fivehundredgram;
    }
    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getOnekg() {
        return onekg;
    }

    public void setOnekg(String onekg) {
        this.onekg = onekg;
    }

    public String getHindiname() {
        return hindiname;
    }

    public void setHindiname(String hindiname) {
        this.hindiname = hindiname;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getUnitQuantity() {
        return quantity;
    }

    public void setUnitQuantity(String unitQuantity) {
        this.quantity = unitQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

}
