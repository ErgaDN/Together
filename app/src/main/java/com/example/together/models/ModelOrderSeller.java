package com.example.together.models;

public class ModelOrderSeller {

    String nameClient;
    String orderStatus;
    String phoneClient;
    String productId;
    String productPrice;
    String productPriceEach;
    String productQuantity;
    String productTitle, orderId, orderDate, clientId;

    String totalPrice;



    public ModelOrderSeller(String orderId, String nameClient, String orderStatus, String phoneClient, String productId, String productPrice, String productPriceEach, String productQuantity, String productTitle , String orderDate, String clientId, String totalPrice) {
        this.orderId = orderId;
        this.nameClient = nameClient;
        this.orderStatus = orderStatus;
        this.phoneClient = phoneClient;
        this.productId = productId;
        this.productPrice = productPrice;
        this.productPriceEach = productPriceEach;
        this.productQuantity = productQuantity;
        this.productTitle = productTitle;
        this.orderDate = orderDate;
        this.clientId = clientId;
        this.totalPrice = totalPrice;
    }
    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public ModelOrderSeller() {
        // Default constructor required for Firestore
    }

    // Getters and setters for all fields

    public String getNameClient() {
        return nameClient;
    }

    public void setNameClient(String nameClient) {
        this.nameClient = nameClient;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPhoneClient() {
        return phoneClient;
    }

    public void setPhoneClient(String phoneClient) {
        this.phoneClient = phoneClient;
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

    public String getProductPriceEach() {
        return productPriceEach;
    }

    public void setProductPriceEach(String productPriceEach) {
        this.productPriceEach = productPriceEach;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }


}






//public class ModelOrderSeller {
//
//    String orderId, orderTime, orderStatus, orderCost, orderBy;
//
//    public ModelOrderSeller() {
//    }
//
//    public ModelOrderSeller(String orderId, String orderTime, String orderStatus, String orderCost, String orderBy) {
//        this.orderId = orderId;
//        this.orderTime = orderTime;
//        this.orderStatus = orderStatus;
//        this.orderCost = orderCost;
//        this.orderBy = orderBy;
//    }
//
//    public String getOrderId() {
//        return orderId;
//    }
//
//    public void setOrderId(String orderId) {
//        this.orderId = orderId;
//    }
//
//    public String getOrderTime() {
//        return orderTime;
//    }
//
//    public void setOrderTime(String orderTime) {
//        this.orderTime = orderTime;
//    }
//
//    public String getOrderStatus() {
//        return orderStatus;
//    }
//
//    public void setOrderStatus(String orderStatus) {
//        this.orderStatus = orderStatus;
//    }
//
//    public String getOrderCost() {
//        return orderCost;
//    }
//
//    public void setOrderCost(String orderCost) {
//        this.orderCost = orderCost;
//    }
//
//    public String getOrderBy() {
//        return orderBy;
//    }
//
//    public void setOrderBy(String orderBy) {
//        this.orderBy = orderBy;
//    }
//
//
//
//}
