package api.order.entity;


import java.util.List;

public class Order {
    private String id;
    private String date;
    private String status;
    private List<OrderProduct> products;
    private double total;

    public Order(String id, String date, String status, List<OrderProduct> products, double total) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.products = products;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderProduct> getProducts() {
        return products;
    }

    public double getTotal() {
        return total;
    }
}
