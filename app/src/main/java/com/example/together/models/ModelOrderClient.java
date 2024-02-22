package com.example.together.models;

public class ModelOrderClient {

    String orderId, orderDate,orderStatus,orderCost,orderBy;

    public ModelOrderClient() {
    }

    public ModelOrderClient(String orderId, String orderDate, String orderStatus, String orderCost, String orderBy) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.orderCost = orderCost;
        this.orderBy = orderBy;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(String orderCost) {
        this.orderCost = orderCost;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
