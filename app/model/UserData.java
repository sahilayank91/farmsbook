package com.sahil.farmsbook.model;

import android.content.Context;

import org.json.JSONException;

import com.sahil.farmsbook.utilities.SharedPreferenceSingleton;

/**
 * Created by Sahil.
 */
public class UserData {
    private static UserData ourInstance = new UserData();
    private String email;
    private String phone;
    private String firstname;
    private String lastname;
    private String address;
    private String _id;
    private Context context;
    private String pincode;
    private String latitude;
    private String longitude;



    private String shop;
    private String ifsc;
    private String gst;



    private String role;


    private String flataddress;

    private UserData() {
    }

    public static UserData getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new UserData();
            ourInstance.getUserData(context);
        }

        try {
            String id= ourInstance.get_id();
            if(id==null)throw new Exception();
        } catch (Exception e) {
            ourInstance = null;
            ourInstance = new UserData();
            ourInstance.getUserData(context);
        }

        return ourInstance;
    }
//
//        public void initUserData(String data, Context context) throws Exception {
//        this.context = context;
//        JSONObject userdata = new JSONObject(data);
//
//        setUser_id(userdata.getLong("id"));
//        setUsername(userdata.getString("username"));
//        setEmail(userdata.getString("email"));
//        setContact(userdata.getString("contact"));
//
//    }
    public void initUserData(User user,Context context)  {
        this.context = context;
        setCity(user.getCity());
        setRole(user.getRole());
        if(user.getRole().equals("Seller")){
            setShop(user.getShop());
        }
        setUser_id(user.get_id());
        setFirstname(user.getFirstname());
        setLastname(user.getLastname());
        setEmail(user.getEmail());
        setPhone(user.getPhone());
        setAddress(user.getAddress());
        setLatitude(user.getLatitude());
        setLongitude(user.getLongitude());
        setFlataddress(user.getFlataddress());
        setPincode(user.getPincode());

    }

    public boolean getUserData(Context context) {
        this.context = context;
        try {

            this._id = SharedPreferenceSingleton.getInstance(context).getString("_id");
            this.firstname = SharedPreferenceSingleton.getInstance(context).getString("firstname");
            this.phone = SharedPreferenceSingleton.getInstance(context).getString("phone");
            this.email = SharedPreferenceSingleton.getInstance(context).getString("email");
            this.lastname=SharedPreferenceSingleton.getInstance(context).getString("lastname");
            this.address=SharedPreferenceSingleton.getInstance(context).getString("address");
            this.pincode = SharedPreferenceSingleton.getInstance(context).getString("pincode");
            this.shop = SharedPreferenceSingleton.getInstance(context).getString("shop");
            this.pincode = SharedPreferenceSingleton.getInstance(context).getString("pincode");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if(this._id==null)return false;
        return true;

    }

    public User getUser() throws JSONException {
        User user=new User();
        user.setEmail(this.email);
        user.set_id(this._id);
        user.setFirstname(this.firstname);
        user.setPhone(this.phone);
        user.setLastname(this.lastname);
        user.setAddress(this.address);
        user.setLatitude(this.latitude);
        user.setLongitude(this.longitude);
        user.setCity(this.city);
        user.setPincode(this.pincode);
        user.setRole(this.role);
        user.setShop(this.shop);
        return user;
    }


    public void setUser_id(String user_id) {
        this._id = user_id;
        SharedPreferenceSingleton.getInstance(context).put("_id", user_id);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        SharedPreferenceSingleton.getInstance(context).put("email", email);
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
        SharedPreferenceSingleton.getInstance(context).put("firstname", firstname);

    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
        SharedPreferenceSingleton.getInstance(context).put("lastname", lastname);

    }

    public String getFlataddress() {
        return flataddress;
    }

    public void setFlataddress(String flataddress) {
        this.flataddress = flataddress;
        SharedPreferenceSingleton.getInstance(context).put("flataddress", flataddress);

    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        SharedPreferenceSingleton.getInstance(context).put("address", address);

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        SharedPreferenceSingleton.getInstance(context).put("phone", phone);
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        SharedPreferenceSingleton.getInstance(context).put("city", city);

    }

    private String city;

    public String getLatitude() {
        return latitude;
    }

    private void setLatitude(String latitude) {
        this.latitude = latitude;
        SharedPreferenceSingleton.getInstance(context).put("latitude", latitude);

    }

    public String getLongitude() {
        return longitude;
    }

    private void setLongitude(String longitude) {
        this.longitude = longitude;
        SharedPreferenceSingleton.getInstance(context).put("longitude", longitude);

    }


    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        SharedPreferenceSingleton.getInstance(context).put("pincode", pincode);

        this.pincode = pincode;
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
        SharedPreferenceSingleton.getInstance(context).put("role", role);

    }

    public String getShop() {
        return shop;

    }

    public void setShop(String shop) {
        this.shop = shop;
        SharedPreferenceSingleton.getInstance(context).put("shop", shop);

    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
        SharedPreferenceSingleton.getInstance(context).put("ifsc", ifsc);

    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
        SharedPreferenceSingleton.getInstance(context).put("gst", gst);

    }
}
