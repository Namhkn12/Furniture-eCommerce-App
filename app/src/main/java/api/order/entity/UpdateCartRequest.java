package api.order.entity;


public class UpdateCartRequest {
    private String userId;
    private int productId;
    private int quantity;

    public UpdateCartRequest(String userId, int productId, int quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
