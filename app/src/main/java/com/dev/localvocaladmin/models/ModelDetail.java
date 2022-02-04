package com.dev.localvocaladmin.models;

public class ModelDetail {
    private String pName, productImage, finalPrice, finalQuantity, productId, productPrice, shopId, timeStamp;

    public ModelDetail() {
    }

    public ModelDetail(String pName, String productImage, String finalPrice, String finalQuantity, String productId, String productPrice, String shopId, String timeStamp) {
        this.pName = pName;
        this.productImage = productImage;
        this.finalPrice = finalPrice;
        this.finalQuantity = finalQuantity;
        this.productId = productId;
        this.productPrice = productPrice;
        this.shopId = shopId;
        this.timeStamp = timeStamp;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(String finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getFinalQuantity() {
        return finalQuantity;
    }

    public void setFinalQuantity(String finalQuantity) {
        this.finalQuantity = finalQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
