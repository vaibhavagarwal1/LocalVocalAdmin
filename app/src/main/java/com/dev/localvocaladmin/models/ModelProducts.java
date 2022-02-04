package com.dev.localvocaladmin.models;

public class ModelProducts {

    private String uid, productName, productDesc, productCategory, productQuantity, productPrice, productImage, productId, timestamp, discountAvailable, productDiscount, productDiscountNote, shopId, shopCity;

    public ModelProducts() {
    }

    public ModelProducts(String uid, String productName, String productDesc, String productCategory, String productQuantity, String productPrice, String productImage, String productId, String timestamp, String discountAvailable, String productDiscount, String productDiscountNote, String shopId, String shopCity) {
        this.uid = uid;
        this.productName = productName;
        this.productDesc = productDesc;
        this.productCategory = productCategory;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.productId = productId;
        this.timestamp = timestamp;
        this.discountAvailable = discountAvailable;
        this.productDiscount = productDiscount;
        this.productDiscountNote = productDiscountNote;
        this.shopId = shopId;
        this.shopCity = shopCity;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public String getProductDiscountNote() {
        return productDiscountNote;
    }

    public void setProductDiscountNote(String productDiscountNote) {
        this.productDiscountNote = productDiscountNote;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopCity() {
        return shopCity;
    }

    public void setShopCity(String shopCity) {
        this.shopCity = shopCity;
    }
}
