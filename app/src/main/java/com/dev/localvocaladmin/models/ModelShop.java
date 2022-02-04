package com.dev.localvocaladmin.models;

public class ModelShop {

    private String uid, shopname, shopemail, shopphone, yourname, deliveryfee;
    private String shopaddress, shopcategory, shopcity, shopimage, shopid, cartValue;
    private String deliveryLocation, longitude, latitude;

    public ModelShop() {
    }

    public ModelShop(String uid, String shopname, String shopemail, String shopphone, String yourname, String deliveryfee, String shopaddress, String shopcategory, String shopcity, String shopimage, String shopid, String cartValue, String deliveryLocation, String longitude, String latitude) {
        this.uid = uid;
        this.shopname = shopname;
        this.shopemail = shopemail;
        this.shopphone = shopphone;
        this.yourname = yourname;
        this.deliveryfee = deliveryfee;
        this.shopaddress = shopaddress;
        this.shopcategory = shopcategory;
        this.shopcity = shopcity;
        this.shopimage = shopimage;
        this.shopid = shopid;
        this.cartValue = cartValue;
        this.deliveryLocation = deliveryLocation;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getShopemail() {
        return shopemail;
    }

    public void setShopemail(String shopemail) {
        this.shopemail = shopemail;
    }

    public String getShopphone() {
        return shopphone;
    }

    public void setShopphone(String shopphone) {
        this.shopphone = shopphone;
    }

    public String getYourname() {
        return yourname;
    }

    public void setYourname(String yourname) {
        this.yourname = yourname;
    }

    public String getDeliveryfee() {
        return deliveryfee;
    }

    public void setDeliveryfee(String deliveryfee) {
        this.deliveryfee = deliveryfee;
    }

    public String getShopaddress() {
        return shopaddress;
    }

    public void setShopaddress(String shopaddress) {
        this.shopaddress = shopaddress;
    }

    public String getShopcategory() {
        return shopcategory;
    }

    public void setShopcategory(String shopcategory) {
        this.shopcategory = shopcategory;
    }

    public String getShopcity() {
        return shopcity;
    }

    public void setShopcity(String shopcity) {
        this.shopcity = shopcity;
    }

    public String getShopimage() {
        return shopimage;
    }

    public void setShopimage(String shopimage) {
        this.shopimage = shopimage;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getCartValue() {
        return cartValue;
    }

    public void setCartValue(String cartValue) {
        this.cartValue = cartValue;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
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
}
