package com.example.together.models;

public class ModelCartItem {

    String id, pId, name, price, cost, quantity, sellerId;
//    double allTotalPrice;

    public ModelCartItem() {
    }

    public ModelCartItem(String id, String pId, String name, String price, String cost, String quantity) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.price = price;
        this.cost = cost;
        this.quantity = quantity;
//        this.sellerId = sellerId
//        this.allTotalPrice = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }
//    public Double getAllTotalPrice(){
//        return this.allTotalPrice;
//    }

//    public void setAllTotalPrice(Double num){
//        this.allTotalPrice += num;
//    }

    public void setName(String name) {
        this.name = name;
    }

//    public void setSellerId(String sellerId){
//        this.sellerId = sellerId;
//    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}