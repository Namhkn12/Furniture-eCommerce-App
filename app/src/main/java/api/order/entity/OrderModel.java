package api.order.entity;

import java.util.List;

public class OrderModel {
    private String id;
    private String userId;
    private String orderDate;
    private String status;
    private List<OrderItemModel> items;
    private double total;

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderItemModel> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }
}
