package com.example.together.models;

public class ModelProduct {
    private String productId, productTitle, productDescription,
            productCategory, productPrice, timestamp, uid, productIcon, sellerId;

    private Long productQuantity;



    public ModelProduct(String productId, String productTitle, String productDescription, String productCategory, Long productQuantity,String productIcon, String productPrice, String timestamp, String uid) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.timestamp = timestamp;
        this.uid = uid;
        this.productIcon = productIcon;
        this.sellerId = uid;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductQuantity() {
        return String.valueOf(productQuantity);
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = Long.valueOf(productQuantity);
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String uid) {
        this.productIcon = productIcon;
    }

    public ModelProduct() {
    }
}
