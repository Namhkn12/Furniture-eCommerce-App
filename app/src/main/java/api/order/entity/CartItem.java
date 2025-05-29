package api.order.entity;



public class CartItem {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private String imageResource;

    public CartItem(int id, String name, double price, int quantity, String imageResource) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageResource = imageResource;
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

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageResource() {
        return imageResource;
    }
}
