package com.architecture.order.model;

public class Order {

    private String orderId;
    private String status;
    private String productName;

    public Order() {}

    public Order(String orderId, String status, String productName) {
        this.orderId = orderId;
        this.status = status;
        this.productName = productName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
