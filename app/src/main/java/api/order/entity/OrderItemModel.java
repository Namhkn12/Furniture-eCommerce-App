package api.order.entity;

public class OrderItemModel {
    private int id;
    private int productId;
    private String name;
    private String imageUrl;
    private double price;
    private int quantity;

    public int getId() {
        return id;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
