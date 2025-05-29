package api.order.entity;

import java.util.List;

public class OrderRequest {
    private String userId;
    private List<CartItemModel> items;
    private String shippingAddress;
    private String paymentMethod;

    public OrderRequest(String userId, List<CartItemModel> items, String shippingAddress, String paymentMethod) {
        this.userId = userId;
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
    }
}
