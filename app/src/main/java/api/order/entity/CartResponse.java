package api.order.entity;

import java.util.List;

public class CartResponse {
    private String userId;
    private List<CartItemModel> items;
    private double total;

    public String getUserId() {
        return userId;
    }

    public List<CartItemModel> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }
}
