package com.dev.localvocaladmin.models;

import java.util.HashMap;

public class ModelOrders {
    private String deliveryFee, deliveryType, itemCount, orderId, orderStatus, ownerUid, paymentMethod, productsCost, shippingAddress, shopId, totalPrice, uid;
    private HashMap<String, String> Items;

    public ModelOrders() {
    }

    public ModelOrders(String deliveryFee, String deliveryType, String itemCount, String orderId, String orderStatus, String ownerUid, String paymentMethod, String productsCost, String shippingAddress, String shopId, String totalPrice, String uid, HashMap<String, String> items) {
        this.deliveryFee = deliveryFee;
        this.deliveryType = deliveryType;
        this.itemCount = itemCount;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.ownerUid = ownerUid;
        this.paymentMethod = paymentMethod;
        this.productsCost = productsCost;
        this.shippingAddress = shippingAddress;
        this.shopId = shopId;
        this.totalPrice = totalPrice;
        this.uid = uid;
        this.Items = items;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getProductsCost() {
        return productsCost;
    }

    public void setProductsCost(String productsCost) {
        this.productsCost = productsCost;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
