package api.order.entity;

import java.util.List;

public class OrderListResponse {
    private List<OrderModel> orders;

    public List<OrderModel> getOrders() {
        return orders;
    }
}
