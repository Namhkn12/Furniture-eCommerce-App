package api.order.entity;


public class OrderProduct {
    private int id;
    private int productId;
    private String name;
    private double price;
    private int quantity;
    private String imageResource;

    public OrderProduct(int id, int productId, String name, double price, int quantity, String imageResource) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageResource = imageResource;
    }

    public int getProductId() {
        return productId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImageResource() {
        return imageResource;
    }
}
