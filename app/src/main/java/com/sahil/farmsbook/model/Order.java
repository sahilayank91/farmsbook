package com.sahil.farmsbook.model;

import org.json.JSONException;
import org.json.JSONObject;


public class Order {

    private String _id;
    private String orderdate;
    private String status;
    private String address;
    private String create_time;
    private String total;
    private User seller;
    private User customer;
    private String delivered_otp;
    private String type;
    private String comment;
    private String order;
    private String longitude;
    private String latitude;
    private String image_url;
    private String time;

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    private String slot;
    private String payment_method;
    private String payment_status;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    private String customerId;



    private String locality;


    private String rating;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    private String paymentId;



    private String credit;


    private String discount;

    public Order(JSONObject order) throws JSONException {
        if(order.has("_id"))this._id = order.getString("_id");
        if(order.has("created_at"))this.create_time = order.getString("created_at");
        if(order.has("status"))this.status = order.getString("status");
        if(order.has("total"))this.total = order.getString("total");
        if(order.has("customerId"))this.customer = new User(new JSONObject(order.getString("customerId")));
//        if(order.has("customerId"))this.customerId = order.getString("customerId");
        if(order.has("delivered_otp"))this.delivered_otp= order.getString("delivered_otp");
        if(order.has("type"))this.type= order.getString("type");
        if(order.has("comment"))this.comment = order.getString("comment");
        if(order.has("order"))this.order = order.getString("order");
        if(order.has("latitude"))this.latitude = order.getString("latitude");
        if(order.has("longitude"))this.longitude = order.getString("longitude");
        if(order.has("time"))this.time = order.getString("time");
        if(order.has("locality"))this.locality = order.getString("locality");
        if(order.has("slot"))this.slot = order.getString("slot");
        if(order.has("payment_method"))this.payment_method = order.getString("payment_method");
        if(order.has("payment_status"))this.payment_status = order.getString("payment_status");
        if(order.has("paymentId"))this.paymentId = order.getString("paymentId");
        if(order.has("discount"))this.discount = order.getString("discount");
        if(order.has("credit"))this.credit = order.getString("credit");
    }
    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }
    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }


    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDelivered_otp() {
        return delivered_otp;
    }

    public void setDelivered_otp(String delivered_otp) {
        this.delivered_otp = delivered_otp;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderservice() {
        return orderservice;
    }

    public void setOrderservice(String orderservice) {
        this.orderservice = orderservice;
    }

    private String orderservice;


    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
    }



    public String getOrderstatus() {
        return status;
    }

    public void setOrderstatus(String orderstatus) {
        this.status = orderstatus;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String offer,code;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }


}
