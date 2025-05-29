package model.product;

import java.io.Serializable;

public class Selling implements Serializable {
    private Shop shop;

    private Product product;

    private int amount;

    public Selling(Shop shop, Product product, int amount) {
        this.shop = shop;
        this.product = product;
        this.amount = amount;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
